package fund.design.repo

import eth.NLiveList
import eth.bind
import fund.HomeApi
import lib.base.NET
import lib.base.model.Form
import lib.base.model.Page

/**
 * @author yuansui
 * @since 2018/7/28 0028
 */
class HomeRepo {

    val data = NLiveList<Form>()

    fun query(page: Page) =
        createApi(HomeApi::class)
            .query(page.num)
            .bind(data, page.refresh())
}