package lib.base

import eth.annotation.Retry
import eth.annotation.method.Post
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow
import lib.base.model.User

/**
 * @author yuansui
 * @since 2019/1/26
 */
interface UserApi {
    @Post("v5/user/getVerificationCode")
    fun captcha(
        @Query("mobile") phone: String?,
        @Query("send_type") type: Int
    ): Flow<String>

    @Post("v1/wxlogin/bindTel")
    fun bindPhone(
        @Query("mobile") phone: String?,
        @Query("code") captcha: String?,
        @Query("wxuid") wxUid: String?
    ): Flow<User>

    /**
     * 登录
     */
    @Post("v4/user/login")
    fun login(
        @Query("mobile") phone: String?,
        @Query("code") captcha: String?
    ): Flow<User>

    @Post("v1/wxlogin/login")
    fun loginWechat(@Query("wxinfo") info: String): Flow<User>
}

interface DeviceApi {
    /**
     * 设备唯一id
     */
    @Post("v1/device/registerDevice")
    fun id(): Flow<String>

    /**
     * 上传jpush唯一ID
     */
    @Retry(count = 5, delay = 1000)
    @Post("v5/device/upPushToken")
    fun uploadJPushId(@Query("jpush_id") id: String?): Flow<String>
}