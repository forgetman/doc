package fund

import eth.annotation.network.Field
import eth.annotation.network.Query
import eth.annotation.network.method.Get
import eth.annotation.network.method.Post
import eth.annotation.network.method.Upload
import fund.model.ListMsg
import io.reactivex.Observable
import lib.base.model.Form
import lib.base.model.Page
import lib.base.model.User

/**
 * @author yuansui
 * @since 2018/7/28 0028
 */

@Suppress("ConstantConditionIf")
object URL {
    private const val HOST_DEBUG = "http://testapi.luobohr.com/dsbapi/"
    private const val HOST = "http://api.dashebao.com/dsbapi/"
    private const val HOST_H5_DEBUG = "http://testm.luobohr.com/dsbapi/"
    private const val HOST_H5 = "http://m.dashebao.com/dsbapi/"

    val host: String
        get() = if (BuildConfig.DEBUG_LOG) URL.HOST_DEBUG else URL.HOST

    val h5Host: String
        get() = if (BuildConfig.DEBUG_LOG) URL.HOST_H5_DEBUG else URL.HOST_H5
}

interface HomeApi {
    /**
     * 公积金查询
     */
    @Get("v1/search/index")
    fun query(
        @Query("page") page: Int,
        @Query("limit") limit: Int = Page.LIMIT
    ): Observable<MutableList<Form>>
}

interface UserApi {
    @Post("v5/user/getVerificationCode")
    fun captcha(@Query("mobile") phone: String, @Query("send_type") type: Int): Observable<String>

    @Post("v1/wxlogin/bindTel")
    fun bindPhone(
        @Query("mobile") phone: String,
        @Query("code") captcha: String,
        @Query("wxuid") wxUid: String
    ): Observable<User>

    /**
     * 登录
     */
    @Post("v4/user/login")
    fun login(@Query("mobile") phone: String, @Query("code") captcha: String): Observable<User>

    @Post("v1/wxlogin/login")
    fun loginWechat(@Query("wxinfo") info: String): Observable<User>
}

interface DeviceApi {
    /**
     * 设备唯一id
     */
    @Post("v1/device/registerDevice")
    fun id(@Query("token") token: String?): Observable<String>

    /**
     * 上传jpush唯一ID
     */
    @Post("v5/device/upPushToken")
    fun upJPushToken(@Query("jpush_id") id: String): Observable<String>
}

interface MessageApi {

    @Post("v4/message/lists")
    fun detail(@Query("page_num") page: Int, @Query("page_limit") limit: Int): Observable<ListMsg>
}

interface CommonApi {

    @Upload("v4/upload/uploadImg")
    fun upload(@Query("type") type: String, @Field("image") image: String): Observable<String>
}
