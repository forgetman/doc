package catroom.network.api

import eth.annotation.method.Download
import eth.annotation.param.Url
import eth.convertor.DownloadResult
import kotlinx.coroutines.flow.Flow

/**
 * @author yuansui
 * @since 2024/9/14
 */
interface TestApi {

    @Download
    fun checkNetworkSpeed(@Url url: String): Flow<DownloadResult>
}