package com.shounakmulay.telephony.utils

// import android.net.Uri
// import android.provider.Telephony

// enum class MMSAction(private val methodName: String) {
//   GET_INBOX("getAllInboxMMS"),
//   GET_SENT("getAllSentMMS"),
//   GET_DRAFT("getAllDraftMMS"),
//   SEND_MMS("sendMMS"),
//   SEND_MMS_INTENT("sendMMSIntent"),
//   REQUEST_PHONE_AND_MMS_PERMISSIONS("requestPhoneAndMMSPermissions"),
//   GET_CONVERSATIONS("getAllConversations"),
//   START_BACKGROUND_SERVICE("startBackgroundService"),
//   DISABLE_BACKGROUND_SERVICE("disableBackgroundService"),
//   // BACKGROUND_SERVICE_INITIALIZED("backgroundServiceInitialized"),
//   IS_SMS_CAPABLE("isSmsCapable"),
//   GET_CELLULAR_DATA_STATE("getCellularDataState"),
//   GET_CALL_STATE("getCallState"),
//   GET_DATA_ACTIVITY("getDataActivity"),
//   GET_NETWORK_OPERATOR("getNetworkOperator"),
//   GET_NETWORK_OPERATOR_NAME("getNetworkOperatorName"),
//   GET_DATA_NETWORK_TYPE("getDataNetworkType"),
//   GET_PHONE_TYPE("getPhoneType"),
//   GET_SIM_OPERATOR("getSimOperator"),
//   GET_SIM_OPERATOR_NAME("getSimOperatorName"),
//   GET_SIM_STATE("getSimState"),
//   GET_SERVICE_STATE("getServiceState"),
//   GET_SIGNAL_STRENGTH("getSignalStrength"),
//   IS_NETWORK_ROAMING("isNetworkRoaming"),
//   REQUEST_SMS_PERMISSIONS("requestSMSPermissions"),
//   REQUEST_PHONE_PERMISSIONS("requestPhonePermissions"),
//   OPEN_DIALER("openDialer"),
//   DIAL_PHONE_NUMBER("dialPhoneNumber"),
//   NO_SUCH_METHOD("noSuchMethod");

//   companion object {
//     fun fromMethod(method: String): MMSAction {
//       for (action in values()) {
//         if (action.methodName == method) {
//           return action
//         }
//       }
//       return NO_SUCH_METHOD
//     }
//   }

//   fun toActionType(): MMSActionType {
//     return when (this) {
//       GET_INBOX,
//       GET_SENT,
//       GET_DRAFT,
//       GET_CONVERSATIONS -> MMSActionType.GET_MMS
//       SEND_MMS,
//       SEND_MULTIPART_MMS,
//       SEND_MMS_INTENT,
//       NO_SUCH_METHOD -> MMSActionType.SEND_MMS
//       START_BACKGROUND_SERVICE,
//       DISABLE_BACKGROUND_SERVICE,
//       // BACKGROUND_SERVICE_INITIALIZED -> MMSActionType.BACKGROUND
//       IS_SMS_CAPABLE,
//       GET_CELLULAR_DATA_STATE,
//       GET_CALL_STATE,
//       GET_DATA_ACTIVITY,
//       GET_NETWORK_OPERATOR,
//       GET_NETWORK_OPERATOR_NAME,
//       GET_DATA_NETWORK_TYPE,
//       GET_PHONE_TYPE,
//       GET_SIM_OPERATOR,
//       GET_SIM_OPERATOR_NAME,
//       GET_SIM_STATE,
//       GET_SERVICE_STATE,
//       GET_SIGNAL_STRENGTH,
//       IS_NETWORK_ROAMING -> MMSActionType.GET
//       REQUEST_SMS_PERMISSIONS,
//       REQUEST_PHONE_PERMISSIONS,
//       REQUEST_PHONE_AND_MMS_PERMISSIONS -> MMSActionType.PERMISSION
//       OPEN_DIALER,
//       DIAL_PHONE_NUMBER -> MMSActionType.CALL
//     }
//   }
// }

// // NOTE added GET_MMS and SEND_MMS and removed BACKGROUND
// enum class MMSActionType {
//   GET_SMS, SEND_SMS, GET, PERMISSION, CALL, GET_MMS, SEND_MMS
// }

// // http://android.cn-mirrors.com/reference/android/provider/Telephony.Mms.html
// enum class MMSContentUri(val uri: Uri) {
//   INBOX(Telephony.Mms.Inbox.CONTENT_URI),
//   SENT(Telephony.Mms.Sent.CONTENT_URI),
//   DRAFT(Telephony.Mms.Draft.CONTENT_URI),
//   // CONVERSATIONS(Telephony.Mms.Conversations.CONTENT_URI); // TODO: figure out why conversations don't exist for mms
// }