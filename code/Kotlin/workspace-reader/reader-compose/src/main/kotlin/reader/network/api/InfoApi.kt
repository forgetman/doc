package reader.network.api

import eth.annotation.Host
import eth.annotation.Retry
import eth.annotation.method.Get
import eth.annotation.param.Path
import kotlinx.coroutines.flow.Flow
import reader.model.Book
import reader.network.RETRY_COUNT
import reader.network.RETRY_DELAY
import reader.network.URL

/**
 * @author yuansui
 * @since 2019-07-11
 */
interface InfoApi {
    /**
     * 书本详细信息
     * https://shuapi.jiaston.com/info/360660.html
     */
    @Host(URL.HOST_INFO)
    @Retry(count = RETRY_COUNT, delay = RETRY_DELAY)
    @Get("info/{bookId}.html")
    fun info(@Path("bookId") bookId: String?): Flow<Book>
}