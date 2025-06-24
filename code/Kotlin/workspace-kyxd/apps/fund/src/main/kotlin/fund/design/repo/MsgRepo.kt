package fund.design.repo

import eth.NLiveList
import eth.bind
import fund.MessageApi
import fund.model.Msg
import lib.base.NET
import vector.listConfig


/**
 * @author yuansui
 * @since 2018/8/11 0011
 */
class MsgRepo {

    val detail = NLiveList<Msg>()

    fun getDetail(page: Int, limit: Int = listConfig.limit) =
        createApi(MessageApi::class).detail(page, limit)
            .map {
                it.list ?: mutableListOf()
            }.bind(detail)

}