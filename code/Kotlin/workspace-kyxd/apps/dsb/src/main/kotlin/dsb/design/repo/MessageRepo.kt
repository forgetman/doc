package dsb.design.repo

import dsb.model.InfoMessage
import dsb.model.unpackList
import dsb.network.api.MessageApi
import kotlinx.coroutines.flow.map
import lib.base.model.Page
import lib.base.network.createApi
import java.util.*
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/23
 */
class MessageRepo @Inject constructor() {

    fun fetchHomeMessage() = createApi<MessageApi>().home()

    fun fetchDetailMessage(page: Page) =
        createApi<MessageApi>()
            .detail(page.num)
            .unpackList()

    fun fetchInfoMessage() =
        createApi<MessageApi>()
            .info()
            .map { map ->
                val list = mutableListOf<InfoMessage>()

                map.forEach {
                    when (it.key.toUpperCase(Locale.getDefault())) {
                        InfoMessage.Type.CUSTOMER.name -> {
                            it.value.type = InfoMessage.Type.CUSTOMER
                            list.add(it.value)
                        }
                        InfoMessage.Type.SYSTEM.name -> {
                            it.value.type = InfoMessage.Type.SYSTEM
                            list.add(it.value)
                        }
                    }
                }

                list
            }
}