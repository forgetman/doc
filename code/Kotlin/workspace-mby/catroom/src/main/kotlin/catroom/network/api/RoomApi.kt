package catroom.network.api

import catroom.model.CatRoomInfo
import catroom.model.Upgrade
import eth.annotation.method.Download
import eth.annotation.method.Post
import eth.annotation.method.Upload
import eth.annotation.param.File
import eth.annotation.param.Query
import eth.annotation.param.Url
import eth.convertor.DownloadResult
import kotlinx.coroutines.flow.Flow

/**
 * @author yuansui
 * @since 2024/7/6
 */
interface RoomApi {

    @Post("/device/index/saveDeviceInfo")
    fun info(
        @Query("client_id") clientId: String,
        @Query("longitude") longitude: String,
        @Query("latitude") latitude: String
    ): Flow<CatRoomInfo>

    @Download
    fun download(@Url url: String): Flow<DownloadResult>

    @Post("/device/index/remoteUpgradation")
    fun checkUpgrade(
        @Query("client_id") clientId: String,
        @Query("version") version: Int
    ): Flow<Upgrade>

    @Post("/device/index/getPushLiveFlow")
    fun reportTraffic(
        @Query("client_id") clientId: String,
        @Query("recevice_flow") received: String,
        @Query("transmit_flow") transmitted: String,
    ): Flow<List<Unit>>

    @Upload("/device/upload/xlogFile")
    fun uploadLog(
        @Query("client_id") clientId: String,
        @File("file") logFile: String
    ): Flow<List<Unit>>
}