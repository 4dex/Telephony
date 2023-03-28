package com.shounakmulay.telephony.utils

import android.Manifest
import android.provider.Telephony

object Constants {
  
  // Channels
  const val CHANNEL_SMS = "plugins.shounakmulay.com/foreground_sms_channel"
  const val CHANNEL_SMS_BACKGROUND = "plugins.shounakmulay.com/background_sms_channel"
  const val CHANNEL_MMS = "plugins.shounakmulay.com/foreground_mms_channel"
  const val CHANNEL_MMS_BACKGROUND = "plugins.shounakmulay.com/background_mms_channel"

  // Intent Actions
  const val ACTION_SMS_SENT = "plugins.shounakmulay.intent.ACTION_SMS_SENT"
  const val ACTION_SMS_DELIVERED = "plugins.shounakmulay.intent.ACTION_SMS_DELIVERED"
  const val ACTION_MMS_SENT = "plugins.shounakmulay.intent.ACTION_MMS_SENT"
  const val ACTION_MMS_DELIVERED = "plugins.shounakmulay.intent.ACTION_MMS_DELIVERED"

  // Permissions
  val SMS_PERMISSIONS = listOf(Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.RECEIVE_MMS)
  // NOTE: SMS_PERMISSIONS also contains mms permissions now.
  val PHONE_PERMISSIONS = listOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE)
  val SERVICE_STATE_PERMISSIONS = listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE)

  // Request Codes
  const val SMS_QUERY_REQUEST_CODE = 1
  const val SMS_SEND_REQUEST_CODE = 2
  const val SMS_SENT_BROADCAST_REQUEST_CODE = 21
  const val SMS_DELIVERED_BROADCAST_REQUEST_CODE = 22
  const val SMS_BACKGROUND_REQUEST_CODE = 31
  const val GET_STATUS_REQUEST_CODE = 41
  const val PERMISSION_REQUEST_CODE = 51
  const val CALL_REQUEST_CODE = 61
  const val MMS_RECEIEVE_REQUEST_CODE = 3

  // Methods
  const val ON_MESSAGE = "onMessage"
  const val HANDLE_BACKGROUND_MESSAGE = "handleBackgroundMessage"
  const val SMS_SENT = "smsSent"
  const val SMS_DELIVERED = "smsDelivered"
  const val MMS_SENT = "mmsSent"
  const val MMS_DELIVERED = "mmsDelivered"
  
  // Invoke Method Arguments
  const val HANDLE = "handle"
  const val MESSAGE = "message"

  // Method Call Arguments
  const val PROJECTION = "projection"
  const val SELECTION = "selection"
  const val SELECTION_ARGS = "selection_args"
  const val SORT_ORDER = "sort_order"
  const val MESSAGE_BODY = "message_body"
  const val ADDRESS = "address"
  const val LISTEN_STATUS = "listen_status"
  const val SERVICE_CENTER_ADDRESS = "service_center"

  const val TIMESTAMP = "timestamp"
  const val ORIGINATING_ADDRESS = "originating_address"
  const val STATUS = "status"

  const val SETUP_HANDLE = "setupHandle"
  const val BACKGROUND_HANDLE = "backgroundHandle"

  const val PHONE_NUMBER = "phoneNumber"

  // Projections
  val DEFAULT_SMS_PROJECTION = listOf(Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE)
  // https://developer.android.com/reference/android/provider/Telephony.BaseMmsColumns 
  // TODO change MESSAGE_BOX to the body of each mms part.
  val DEFAULT_MMS_PROJECTION = listOf(Telephony.Mms._ID, Telephony.Mms.Addr.ADDRESS, Telephony.Mms.MESSAGE_BOX, Telephony.Mms.DATE) // GRANTDO figure out if there are other columns needed here.
  // NOTE: the addr class also holds a contact id column which I may want eventually.
  // Per this thread: https://stackoverflow.com/questions/36001339/find-and-interate-all-sms-mms-messages-in-android you have to iterate over all message parts of an mms message.
  // Circling back to that once I can actually read mms from hub.
  // GRANTDO ^^^
  val DEFAULT_CONVERSATION_PROJECTION = listOf(Telephony.Sms.Conversations.THREAD_ID ,Telephony.Sms.Conversations.SNIPPET, Telephony.Sms.Conversations.MESSAGE_COUNT)
  

  // Strings
  const val PERMISSION_DENIED = "permission_denied"
  const val PERMISSION_DENIED_MESSAGE = "Permission Request Denied By User."
  const val FAILED_FETCH = "failed_to_fetch_sms"
  const val ILLEGAL_ARGUMENT = "illegal_argument"
  const val WRONG_METHOD_TYPE = "Incorrect method called on channel."
  const val MESSAGE_OR_ADDRESS_CANNOT_BE_NULL = "Message body or Address cannot be null or blank."

  const val SMS_TO = "smsto:"
  const val SMS_BODY = "sms_body"

  const val MMS_TO = "mmsto:"
  const val MMS_BODY = "mms_body"
  
  // Shared Preferences
  const val SHARED_PREFERENCES_NAME = "com.shounakmulay.android_telephony_plugin"
  const val SHARED_PREFS_BACKGROUND_SETUP_HANDLE = "background_setup_handle"
  const val SHARED_PREFS_BACKGROUND_MESSAGE_HANDLE = "background_message_handle"
  const val SHARED_PREFS_DISABLE_BACKGROUND_EXE = "disable_background"

}