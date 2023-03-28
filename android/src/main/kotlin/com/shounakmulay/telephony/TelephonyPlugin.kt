package com.shounakmulay.telephony

import android.content.Context
import androidx.annotation.NonNull
import com.shounakmulay.telephony.sms.IncomingSmsHandler
// import com.shounakmulay.telephony.mms.IncomingMMSHandler
// import com.shounakmulay.telephony.mms.IncomingMMSReceiver
// import com.shounakmulay.telephony.mms.MMSMethodCallHandler
// import com.shounakmulay.telephony.mms.MMSController
import com.shounakmulay.telephony.utils.Constants.CHANNEL
import com.shounakmulay.telephony.sms.IncomingSmsReceiver
import com.shounakmulay.telephony.sms.SmsController
import com.shounakmulay.telephony.sms.SmsMethodCallHandler
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.*


class TelephonyPlugin : FlutterPlugin, ActivityAware {

  private lateinit var smsChannel: MethodChannel

  private lateinit var smsMethodCallHandler: SmsMethodCallHandler

  private lateinit var smsController: SmsController

  private lateinit var binaryMessenger: BinaryMessenger

  private lateinit var permissionsController: PermissionsController

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    if (!this::binaryMessenger.isInitialized) {
      binaryMessenger = flutterPluginBinding.binaryMessenger
    }

    setupPlugin(flutterPluginBinding.applicationContext, binaryMessenger)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    tearDownPlugin()
  }

  override fun onDetachedFromActivity() {
    tearDownPlugin()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    IncomingSmsReceiver.foregroundSmsChannel = smsChannel
    smsMethodCallHandler.setActivity(binding.activity)
    binding.addRequestPermissionsResultListener(smsMethodCallHandler)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }

  private fun setupPlugin(context: Context, messenger: BinaryMessenger) {
    permissionsController = PermissionsController(context)

    smsController = SmsController(context)
    
    smsMethodCallHandler = SmsMethodCallHandler(context, smsController, permissionsController)
    // TODO: the permission controller here is asking for extras that the app didn't expect.
    // mmsMethodCallHandler = MMSMethodCallHandler(context, mmsController, permissionsController)

    smsChannel = MethodChannel(messenger, CHANNEL)
    smsChannel.setMethodCallHandler(smsMethodCallHandler)
    smsMethodCallHandler.setForegroundChannel(smsChannel)


    // TODO: the telephony plugin is exposing a getAllInboxMMS method on the channel. but the channel doesn't implement it.
    // Start combining the MMS methods back into the smsMethodCallHandler so that it has both mms and sms methods. For now, just need to get inbox mmss.
    // mmsChannel = MethodChannel(messenger, CHANNEL)
    // mmsChannel.setMethodCallHandler(mmsMethodCallHandler)
    // mmsMethodCallHandler.setForegroundChannel(mmsChannel)
  }

  private fun tearDownPlugin() {
    IncomingSmsReceiver.foregroundSmsChannel = null
    smsChannel.setMethodCallHandler(null)
    smsChannel.setMethodCallHandler(null)
  }

}
