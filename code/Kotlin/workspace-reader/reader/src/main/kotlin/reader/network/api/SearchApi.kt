package reader.network.api

import eth.annotation.Host
import eth.annotation.method.Get
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow
import reader.model.Book
import reader.network.URL

/**
 * @author yuansui
 * @since 2019-07-11
 */
interface SearchApi {
    /**
     * 搜索书籍
     */
    @Host(URL.HOST_SEARCH)
    @Get("search.aspx")
    fun search(
        @Query("key") keyword: String?,
        @Query("page") page: Int,
        @Query("siteid") siteId: String = "app2"
    ): Flow<List<Book>>
}