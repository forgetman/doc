package pretimmediat.service

import android.content.Intent
import com.appsflyer.AFInAppEventType
import com.facebook.appevents.AppEventsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import coroutine.flow.launchIn
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import logger.L
import pretimmediat.ext.ensureUserIdFlow
import pretimmediat.network.api.InputPieceApi
import pretimmediat.network.createApi
import pretimmediat.stats.Stats
import vector.service.ServiceEx

/**
 * 检测是否首次填写进件页信息
 */
@Creator
class CheckFirstService : ServiceEx() {

    companion object {
        private const val LOG_TAG = "CheckFirstService"
    }

    @Extra(true)
    var userId: String? = null

    @Extra(true)
    var ssid: String? = null

    @Extra(true)
    var pageType = -1

    override fun onHandleIntent(intent: Intent) {
        when (pageType) {
            InputPieceApi.PAGE_TYPE_BANK,
            InputPieceApi.PAGE_TYPE_ID_CARD -> {
                ensureUserIdFlow(userId, ssid) {
                    createApi<InputPieceApi>().checkFirst(userId, ssid, ssid, pageType)
                }.flowOn(Dispatchers.IO).catch { e ->
                    L.e(LOG_TAG, "checkFirst", e)
                }.onEach {
                    if (it.isIdCardFirst()) {
                        Stats.faceBook.onEvent(AppEventsConstants.EVENT_NAME_SUBSCRIBE)
                        Stats.firebase.onEvent(FirebaseAnalytics.Event.ADD_SHIPPING_INFO)
                        Stats.flyer.onEvent(AFInAppEventType.SUBSCRIBE)
                    }
                    if (it.isBankFirst()) {
                        Stats.faceBook.onEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART)
                        Stats.firebase.onEvent(FirebaseAnalytics.Event.ADD_TO_CART)
                        Stats.flyer.onEvent(AFInAppEventType.ADD_TO_CART)
                    }
                    stopSelf()
                }.launchIn(this)
            }
        }
    }
}