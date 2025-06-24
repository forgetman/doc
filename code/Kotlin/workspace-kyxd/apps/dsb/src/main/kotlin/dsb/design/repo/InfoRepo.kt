package dsb.design.repo

import dsb.model.unpackList
import dsb.network.api.InfoApi
import lib.base.model.Page
import lib.base.network.createApi
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/17
 */
class InfoRepo @Inject constructor() {

    fun fetchInfo(categoryId: Int, page: Page) =
        createApi<InfoApi>()
            .list(categoryId, page.num)
            .unpackList()

    fun fetchCategory() =
        createApi<InfoApi>()
            .category()
            .unpackList()
}