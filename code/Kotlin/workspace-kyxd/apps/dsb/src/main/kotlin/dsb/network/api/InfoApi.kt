package dsb.network.api

import dsb.model.Category
import dsb.model.Info
import dsb.model.PackList
import dsb.network.LIMIT
import dsb.network.PAGE
import eth.annotation.method.Post
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow
import lib.base.model.Page

interface InfoApi {

    @Post("v5/article/getArticleCategory")
    fun category(): Flow<PackList<Category>>

    @Post("v5/article/getArticleList")
    fun list(
        @Query("category_id") id: Int,
        @Query(PAGE) page: Int,
        @Query(LIMIT) limit: Int = Page.LIMIT
    ): Flow<PackList<Info>>
}