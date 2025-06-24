package dsb.util

import dsb.App
import dsb.network.api.MessageApi
import eth.ext.bind
import kotlinx.coroutines.flow.map
import lib.base.network.createApi

/**
 * @author yuansui
 * @since 2019/2/28
 */
object NetUtil {

    fun refreshUnreadNumber() {
        createApi<MessageApi>()
            .unread()
            .map { it.num }
            .bind(App.unreadCount)
            .launch()
    }
}