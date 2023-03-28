package com.shounakmulay.telephony.mms

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.shounakmulay.telephony.PermissionsController
import com.shounakmulay.telephony.utils.MMSActionType
import com.shounakmulay.telephony.utils.Constants
import com.shounakmulay.telephony.utils.Constants.ADDRESS
import com.shounakmulay.telephony.utils.Constants.BACKGROUND_HANDLE
import com.shounakmulay.telephony.utils.Constants.CALL_REQUEST_CODE
import com.shounakmulay.telephony.utils.Constants.DEFAULT_CONVERSATION_PROJECTION
import com.shounakmulay.telephony.utils.Constants.DEFAULT_SMS_PROJECTION
import com.shounakmulay.telephony.utils.Constants.FAILED_FETCH
import com.shounakmulay.telephony.utils.Constants.GET_STATUS_REQUEST_CODE
import com.shounakmulay.telephony.utils.Constants.ILLEGAL_ARGUMENT
import com.shounakmulay.telephony.utils.Constants.LISTEN_STATUS
import com.shounakmulay.telephony.utils.Constants.MESSAGE_BODY
import com.shounakmulay.telephony.utils.Constants.PERMISSION_DENIED
import com.shounakmulay.telephony.utils.Constants.PERMISSION_DENIED_MESSAGE
import com.shounakmulay.telephony.utils.Constants.PERMISSION_REQUEST_CODE
import com.shounakmulay.telephony.utils.Constants.PHONE_NUMBER
import com.shounakmulay.telephony.utils.Constants.PROJECTION
import com.shounakmulay.telephony.utils.Constants.SELECTION
import com.shounakmulay.telephony.utils.Constants.SELECTION_ARGS
import com.shounakmulay.telephony.utils.Constants.SETUP_HANDLE
import com.shounakmulay.telephony.utils.Constants.SHARED_PREFERENCES_NAME
import com.shounakmulay.telephony.utils.Constants.SHARED_PREFS_DISABLE_BACKGROUND_EXE
import com.shounakmulay.telephony.utils.Constants.SMS_BACKGROUND_REQUEST_CODE
import com.shounakmulay.telephony.utils.Constants.MMS_DELIVERED
import com.shounakmulay.telephony.utils.Constants.SMS_QUERY_REQUEST_CODE
import com.shounakmulay.telephony.utils.Constants.SMS_SEND_REQUEST_CODE
import com.shounakmulay.telephony.utils.Constants.MMS_SENT
import com.shounakmulay.telephony.utils.Constants.SORT_ORDER
import com.shounakmulay.telephony.utils.Constants.WRONG_METHOD_TYPE
import com.shounakmulay.telephony.utils.MMSContentUri
import com.shounakmulay.telephony.utils.MMSAction
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry


class MMSMethodCallHandler(
    private val context: Context,
    private val mmsController: MMSController,
    private val permissionsController: PermissionsController
) : PluginRegistry.RequestPermissionsResultListener,
    MethodChannel.MethodCallHandler,
    BroadcastReceiver() {

  private lateinit var result: MethodChannel.Result
  private lateinit var action: MMSAction
  private lateinit var foregroundChannel: MethodChannel
  private lateinit var activity: Activity

  private var projection: List<String>? = null
  private var selection: String? = null
  private var selectionArgs: List<String>? = null
  private var sortOrder: String? = null

  private lateinit var messageBody: String
  private lateinit var address: String
  private var listenStatus: Boolean = false

  private var setupHandle: Long = -1
  private var backgroundHandle: Long = -1

  private lateinit var phoneNumber: String

  private var requestCode: Int = -1

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    this.result = result

    action = MMSAction.fromMethod(call.method)

    if (action == MMSAction.NO_SUCH_METHOD) {
      result.notImplemented()
      return
    }

    when (action.toActionType()) {
      MMSActionType.GET_MMS -> {
        projection = call.argument(PROJECTION)
        selection = call.argument(SELECTION)
        selectionArgs = call.argument(SELECTION_ARGS)
        sortOrder = call.argument(SORT_ORDER)

        handleMethod(action, SMS_QUERY_REQUEST_CODE)
      }
      MMSActionType.GET_SMS -> {
        projection = call.argument(PROJECTION)
        selection = call.argument(SELECTION)
        selectionArgs = call.argument(SELECTION_ARGS)
        sortOrder = call.argument(SORT_ORDER)

        handleMethod(action, SMS_QUERY_REQUEST_CODE)
      }
      MMSActionType.SEND_MMS -> {
        if (call.hasArgument(MESSAGE_BODY)
            && call.hasArgument(ADDRESS)) {
          val messageBody = call.argument<String>(MESSAGE_BODY)
          val address = call.argument<String>(ADDRESS)
          if (messageBody.isNullOrBlank() || address.isNullOrBlank()) {
            result.error(ILLEGAL_ARGUMENT, Constants.MESSAGE_OR_ADDRESS_CANNOT_BE_NULL, null)
            return
          }

          this.messageBody = messageBody
          this.address = address

          listenStatus = call.argument(LISTEN_STATUS) ?: false
        }
        handleMethod(action, SMS_SEND_REQUEST_CODE)
      }
      MMSActionType.SEND_SMS -> {
        if (call.hasArgument(MESSAGE_BODY)
            && call.hasArgument(ADDRESS)) {
          val messageBody = call.argument<String>(MESSAGE_BODY)
          val address = call.argument<String>(ADDRESS)
          if (messageBody.isNullOrBlank() || address.isNullOrBlank()) {
            result.error(ILLEGAL_ARGUMENT, Constants.MESSAGE_OR_ADDRESS_CANNOT_BE_NULL, null)
            return
          }

          this.messageBody = messageBody
          this.address = address

          listenStatus = call.argument(LISTEN_STATUS) ?: false
        }
        handleMethod(action, SMS_SEND_REQUEST_CODE)
      }
      // MMSActionType.BACKGROUND -> {
      //   if (call.hasArgument(SETUP_HANDLE)
      //       && call.hasArgument(BACKGROUND_HANDLE)) {
      //     val setupHandle = call.argument<Long>(SETUP_HANDLE)
      //     val backgroundHandle = call.argument<Long>(BACKGROUND_HANDLE)
      //     if (setupHandle == null || backgroundHandle == null) {
      //       result.error(ILLEGAL_ARGUMENT, "Setup handle or background handle missing", null)
      //       return
      //     }

      //     this.setupHandle = setupHandle
      //     this.backgroundHandle = backgroundHandle
      //   }
      //   handleMethod(action, SMS_BACKGROUND_REQUEST_CODE)
      // }
      MMSActionType.GET -> handleMethod(action, GET_STATUS_REQUEST_CODE)
      MMSActionType.PERMISSION -> handleMethod(action, PERMISSION_REQUEST_CODE)
      MMSActionType.CALL -> {
        if (call.hasArgument(PHONE_NUMBER)) {
          val phoneNumber = call.argument<String>(PHONE_NUMBER)

          if (!phoneNumber.isNullOrBlank()) {
            this.phoneNumber = phoneNumber
          }

          handleMethod(action, CALL_REQUEST_CODE)
        }
      }
    }
  }

  /**
   * Called by [handleMethod] after checking the permissions.
   *
   * #####
   *
   * If permission was not previously granted, [handleMethod] will request the user for permission
   *
   * Once user grants the permission this method will be executed.
   *
   * #####
   */
  private fun execute(MMSAction: MMSAction) {
    try {
      when (MMSAction.toActionType()) {
        MMSActionType.GET_MMS -> handleGetMMSActions(MMSAction)
        MMSActionType.SEND_MMS -> handleSendMMSActions(MMSAction)
        MMSActionType.GET_SMS -> handleGetMMSActions(MMSAction)
        MMSActionType.SEND_SMS -> handleSendMMSActions(MMSAction)
        // MMSActionType.BACKGROUND -> handleBackgroundActions(MMSAction)
        MMSActionType.GET -> handleGetActions(MMSAction)
        MMSActionType.PERMISSION -> result.success(true)
        MMSActionType.CALL -> handleCallActions(MMSAction)
      }
    } catch (e: IllegalArgumentException) {
      result.error(ILLEGAL_ARGUMENT, WRONG_METHOD_TYPE, null)
    } catch (e: RuntimeException) {
      result.error(FAILED_FETCH, e.message, null)
    }
  }

  private fun handleGetMMSActions(mmsAction: MMSAction) {
    if (projection == null) {
      projection = if (mmsAction == MMSAction.GET_CONVERSATIONS) DEFAULT_CONVERSATION_PROJECTION else DEFAULT_SMS_PROJECTION
    }
    val contentUri = when (mmsAction) {
      MMSAction.GET_INBOX -> MMSContentUri.INBOX
      MMSAction.GET_SENT -> MMSContentUri.SENT
      MMSAction.GET_DRAFT -> MMSContentUri.DRAFT
      // MMSAction.GET_CONVERSATIONS -> MMSContentUri.CONVERSATIONS
      else -> throw IllegalArgumentException()
    }
    val messages = mmsController.getMessages(contentUri, projection!!, selection, selectionArgs, sortOrder)
    result.success(messages)
  }

  private fun handleSendMMSActions(mmsAction: MMSAction) {
    if (listenStatus) {
      val intentFilter = IntentFilter().apply {
        addAction(Constants.ACTION_SMS_SENT)
        addAction(Constants.ACTION_SMS_DELIVERED)
      }
      context.applicationContext.registerReceiver(this, intentFilter)
    }
    when (mmsAction) {
      MMSAction.SEND_MMS -> mmsController.sendMMS(address, messageBody, listenStatus)
      MMSAction.SEND_MULTIPART_MMS -> mmsController.sendMultipartMMS(address, messageBody, listenStatus)
      MMSAction.SEND_MMS_INTENT -> mmsController.sendMMSIntent(address, messageBody)
      else -> throw IllegalArgumentException()
    }
    result.success(null)
  }

// GRANTDO this is part of incoming MMS Handler
  // private fun handleBackgroundActions(mmsAction: MMSAction) {
  //   when (mmsAction) {
  //     MMSAction.START_BACKGROUND_SERVICE -> {
  //       val preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
  //       preferences.edit().putBoolean(SHARED_PREFS_DISABLE_BACKGROUND_EXE, false).apply()
  //       IncomingMMSHandler.setBackgroundSetupHandle(context, setupHandle)
  //       IncomingMMSHandler.setBackgroundMessageHandle(context, backgroundHandle)
  //     }
  //     MMSAction.BACKGROUND_SERVICE_INITIALIZED -> {
  //       IncomingMMSHandler.onChannelInitialized(context.applicationContext)
  //     }
  //     MMSAction.DISABLE_BACKGROUND_SERVICE -> {
  //       val preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
  //       preferences.edit().putBoolean(SHARED_PREFS_DISABLE_BACKGROUND_EXE, true).apply()
  //     }
  //     else -> throw IllegalArgumentException()
  //   }
  // }

  @SuppressLint("MissingPermission")
  private fun handleGetActions(mmsAction: MMSAction) {
    mmsController.apply {
      val value: Any = when (mmsAction) {
        MMSAction.IS_SMS_CAPABLE -> isSmsCapable()
        MMSAction.GET_CELLULAR_DATA_STATE -> getCellularDataState()
        MMSAction.GET_CALL_STATE -> getCallState()
        MMSAction.GET_DATA_ACTIVITY -> getDataActivity()
        MMSAction.GET_NETWORK_OPERATOR -> getNetworkOperator()
        MMSAction.GET_NETWORK_OPERATOR_NAME -> getNetworkOperatorName()
        MMSAction.GET_DATA_NETWORK_TYPE -> getDataNetworkType()
        MMSAction.GET_PHONE_TYPE -> getPhoneType()
        MMSAction.GET_SIM_OPERATOR -> getSimOperator()
        MMSAction.GET_SIM_OPERATOR_NAME -> getSimOperatorName()
        MMSAction.GET_SIM_STATE -> getSimState()
        MMSAction.IS_NETWORK_ROAMING -> isNetworkRoaming()
        MMSAction.GET_SIGNAL_STRENGTH -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getSignalStrength()
                ?: result.error("SERVICE_STATE_NULL", "Error getting service state", null)

          } else {
            result.error("INCORRECT_SDK_VERSION", "getServiceState() can only be called on Android Q and above", null)
          }
        }
        MMSAction.GET_SERVICE_STATE -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getServiceState()
                ?: result.error("SERVICE_STATE_NULL", "Error getting service state", null)
          } else {
            result.error("INCORRECT_SDK_VERSION", "getServiceState() can only be called on Android O and above", null)
          }
        }
        else -> throw IllegalArgumentException()
      }
      result.success(value)
    }
  }

  @SuppressLint("MissingPermission")
  private fun handleCallActions(mmsAction: MMSAction) {
    when (mmsAction) {
      MMSAction.OPEN_DIALER -> mmsController.openDialer(phoneNumber)
      MMSAction.DIAL_PHONE_NUMBER -> mmsController.dialPhoneNumber(phoneNumber)
      else -> throw IllegalArgumentException()
    }
  }


  /**
   * Calls the [execute] method after checking if the necessary permissions are granted.
   *
   * If not granted then it will request the permission from the user.
   */
  private fun handleMethod(mmsAction: MMSAction, requestCode: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkOrRequestPermission(mmsAction, requestCode)) {
      execute(mmsAction)
    }
  }

  /**
   * Check and request if necessary for all the SMS permissions listed in the manifest
   */
  @RequiresApi(Build.VERSION_CODES.M)
  fun checkOrRequestPermission(mmsAction: MMSAction, requestCode: Int): Boolean {
    this.action = mmsAction
    this.requestCode = requestCode
    when (mmsAction) {
      MMSAction.GET_INBOX,
      MMSAction.GET_SENT,
      MMSAction.GET_DRAFT,
      MMSAction.GET_CONVERSATIONS,
      MMSAction.SEND_MMS,
      MMSAction.SEND_MULTIPART_MMS,
      MMSAction.SEND_MMS_INTENT,
      MMSAction.START_BACKGROUND_SERVICE,
      // MMSAction.BACKGROUND_SERVICE_INITIALIZED,
      MMSAction.DISABLE_BACKGROUND_SERVICE,
      MMSAction.REQUEST_MMS_PERMISSIONS -> {
        val permissions = permissionsController.getMMSPermissions()
        return checkOrRequestPermission(permissions, requestCode)
      }
      MMSAction.GET_DATA_NETWORK_TYPE,
      MMSAction.OPEN_DIALER,
      MMSAction.DIAL_PHONE_NUMBER,
      MMSAction.REQUEST_PHONE_PERMISSIONS -> {
        val permissions = permissionsController.getPhonePermissions()
        return checkOrRequestPermission(permissions, requestCode)
      }
      MMSAction.GET_SERVICE_STATE -> {
        val permissions = permissionsController.getServiceStatePermissions()
        return checkOrRequestPermission(permissions, requestCode)
      }
      MMSAction.REQUEST_PHONE_AND_MMS_PERMISSIONS -> {
        val permissions = listOf(permissionsController.getMMSPermissions(), permissionsController.getPhonePermissions()).flatten()
        return checkOrRequestPermission(permissions, requestCode)
      }
      MMSAction.IS_SMS_CAPABLE,
      MMSAction.GET_CELLULAR_DATA_STATE,
      MMSAction.GET_CALL_STATE,
      MMSAction.GET_DATA_ACTIVITY,
      MMSAction.GET_NETWORK_OPERATOR,
      MMSAction.GET_NETWORK_OPERATOR_NAME,
      MMSAction.GET_PHONE_TYPE,
      MMSAction.GET_SIM_OPERATOR,
      MMSAction.GET_SIM_OPERATOR_NAME,
      MMSAction.GET_SIM_STATE,
      MMSAction.IS_NETWORK_ROAMING,
      MMSAction.GET_SIGNAL_STRENGTH,
      MMSAction.NO_SUCH_METHOD -> return true
    }
  }

  fun setActivity(activity: Activity) {
    this.activity = activity
  }

  @RequiresApi(Build.VERSION_CODES.M)
  private fun checkOrRequestPermission(permissions: List<String>, requestCode: Int): Boolean {
    permissionsController.apply {
      
      if (!::activity.isInitialized) {
        return hasRequiredPermissions(permissions)
      }
      
      if (!hasRequiredPermissions(permissions)) {
        requestPermissions(activity, permissions, requestCode)
        return false
      }
      return true
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean {

    permissionsController.isRequestingPermission = false

    val deniedPermissions = mutableListOf<String>()
    if (requestCode != this.requestCode && !this::action.isInitialized) {
      return false
    }

    val allPermissionGranted = grantResults.foldIndexed(true) { i, acc, result ->
      if (result == PackageManager.PERMISSION_DENIED) {
        permissions.let { deniedPermissions.add(it[i]) }
      }
      return@foldIndexed acc && result == PackageManager.PERMISSION_GRANTED
    }

    return if (allPermissionGranted) {
      execute(action)
      true
    } else {
      onPermissionDenied(deniedPermissions)
      false
    }
  }

  private fun onPermissionDenied(deniedPermissions: List<String>) {
    result.error(PERMISSION_DENIED, PERMISSION_DENIED_MESSAGE, deniedPermissions)
  }

  fun setForegroundChannel(channel: MethodChannel) {
    foregroundChannel = channel
  }

  override fun onReceive(ctx: Context?, intent: Intent?) {
    if (intent != null) {
      when (intent.action) {
        Constants.ACTION_MMS_SENT -> foregroundChannel.invokeMethod(MMS_SENT, null)
        Constants.ACTION_MMS_DELIVERED -> {
          foregroundChannel.invokeMethod(MMS_DELIVERED, null)
          context.unregisterReceiver(this)
        }
      }
    }
  }
}
