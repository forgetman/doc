package dsb.network.api

import dsb.model.*
import dsb.network.LIMIT
import dsb.network.PAGE
import eth.annotation.method.Post
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow
import lib.base.model.Page

interface MessageApi {
    /**
     * 二级消息
     */
    @Post("v4/message/lists")
    fun detail(
        @Query(PAGE) page: Int,
        @Query(LIMIT) limit: Int = Page.LIMIT
    ): Flow<PackList<DetailMessage>>

    /**
     * 一级消息
     */
    @Post("v4/message/info")
    fun info(): Flow<Map<String, InfoMessage>>

    /**
     * 首页弹屏消息
     */
    @Post("v4/message/index")
    fun home(): Flow<HomeMessage>

    /**
     * 消息未读数
     */
    @Post("v4/message/read")
    fun unread(): Flow<UnreadMessage>
}