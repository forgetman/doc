package reader.network.api

import eth.annotation.Host
import eth.annotation.Retry
import eth.annotation.method.Get
import eth.annotation.param.Path
import kotlinx.coroutines.flow.Flow
import reader.model.pack.PackTop
import reader.network.RETRY_COUNT
import reader.network.RETRY_DELAY
import reader.network.URL

/**
 * 分类
 * PS: 命名都是根据接口定义的
 */
enum class Category(val desc: String) {
    COMMEND("推荐"), // 推荐
    HOT("最热"), // 最新
    NEW("最新"), // 新书
    COLLECT("收藏"), // 收藏
    VOTE("评分"), // 评分
    OVER("完结") // 完结
}

/**
 * 排行榜种类
 * PS: 命名都是根据接口定义的
 */
enum class LeaderboardType(val desc: String) {
    WEEK("周榜"),
    MONTH("月榜"),
    TOTAL("总榜")
}

interface LeaderboardApi {

    /**
     * 排行榜
     */
    @Host(URL.HOST_TOP)
    @Retry(count = RETRY_COUNT, delay = RETRY_DELAY)
    @Get("top/man/top/{type}/{listType}/{page}.html")
    fun leaderboard(
        @Path("type") type: String?,
        @Path("listType") listType: String?,
        @Path("page") id: Int
    ): Flow<PackTop>
}
