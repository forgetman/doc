package pretimmediat.network.api

import eth.annotation.BooleanHeaders
import eth.annotation.BooleanMap
import eth.annotation.Charset
import eth.annotation.ContentType
import eth.annotation.method.Post
import eth.annotation.param.Body
import eth.annotation.param.Header
import eth.annotation.param.Query
import eth.model.CharsetValue
import eth.model.ContentTypeValue
import kotlinx.coroutines.flow.Flow
import pretimmediat.model.AppConfig
import pretimmediat.model.ApplicationSettings
import pretimmediat.model.CopyInfo
import pretimmediat.model.GooglePlayToggle
import pretimmediat.network.ParamsName

interface GlobalApi {

    @Post("/tightThirst/divideBusyKey")
    @BooleanHeaders(
        BooleanMap(ParamsName.V_FLAG_1, true)
    )
    fun appSetting(): Flow<ApplicationSettings>

    @Post("/tightThirst/praiseSureCab")
    @BooleanHeaders(
        BooleanMap(ParamsName.V_FLAG_1, true)
    )
    fun appConfig(@Query("cubicHobbySweatYard") type: String): Flow<List<AppConfig>>

    @Post("/door/manageTiredTrunk")
    @ContentType(ContentTypeValue.JSON_FAKE)
    @Charset(CharsetValue.UTF8)
    fun uploadBigJson(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Body json: String
    ): Flow<String>

    /**
     * @param ogAppssid 主产品号 288写死, 不变
     * @param ssid 子产品号, 不能使用单独接口的参数, 会报错, 只能用公参的参数
     */
    @Post("/flour/suggestAboveArea")
    @BooleanHeaders(
        BooleanMap(ParamsName.V_FLAG_1, true)
    )
    fun copyInfo(
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("darkMonkeyNecessaryFlesh") ogAppssid: String = "288"
    ): Flow<CopyInfo>

    @Post("/tightThirst/describeHealthyBasement")
    fun checkGooglePlayToggle(): Flow<GooglePlayToggle>
}