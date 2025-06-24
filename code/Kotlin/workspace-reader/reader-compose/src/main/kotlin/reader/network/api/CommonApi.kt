package reader.network.api

import eth.annotation.Retry
import eth.annotation.method.Get
import eth.annotation.param.Path
import kotlinx.coroutines.flow.Flow
import reader.model.pack.PackContent
import reader.model.pack.PackSet
import reader.network.RETRY_COUNT
import reader.network.RETRY_DELAY

interface CommonApi {

    /**
     * 获取章节
     */
    @Get("{id}/")
    @Retry(count = RETRY_COUNT, delay = RETRY_DELAY)
    fun chapters(@Path("id") id: String?): Flow<PackSet>

    /**
     * 章节内容
     */
    @Get("{bookId}/{chapterId}.html")
    @Retry(count = RETRY_COUNT, delay = RETRY_DELAY)
    fun content(
        @Path("bookId") bookId: String?,
        @Path("chapterId") chapterId: String?
    ): Flow<PackContent>
}
