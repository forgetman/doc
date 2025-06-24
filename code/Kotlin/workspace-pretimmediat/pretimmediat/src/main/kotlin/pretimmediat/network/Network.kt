package pretimmediat.network

import eth.Eth
import eth.api.impl.OkHttpService
import eth.interceptor.LogInterceptor

object URL {
    const val HOST_TEST = "https://test.pretimmediatpi.com/pretimmediat"
    const val PROTOCOL = "https://www.pretimmediatpi.com/pretimmediatpis/privacy.html"
}

object ParamsName {
    const val CLIENT_ID_1 = "looseSentenceFrontAttitude"
    const val CLIENT_ID_2 = "strictSorrowPeacefulHoneyPhysicalMethod"
    const val TOKEN = "emptyConservationPoliticalNailCivilHaircut"
    const val USER_ID = "roughPoorGlue"
    const val CURR_USER_ID = "spanishScissorsPageShallowTent"
    const val CHANNEL = "flamingInlandDeepFriend"
    const val VERSION_NAME = "electricAdventureCarefulFairNeighbour"
    const val VERSION_CODE = "smokeCustomerDiscountMercifulBeast"
    const val DEVICE_ID_1 = "seniorTaxiClassicalCondition"
    const val DEVICE_ID_2 = "painfulPoisonousJapan"
    const val DEVICE_ID_3 = "surroundingShallowCloseNeighbour"
    const val IMEI = "gentleFingernail"
    const val MUL_FLAG = "luckyBirdEdge"
    const val V_FLAG_1 = "conservativeCubicThread"
    const val V_FLAG_2 = "paleCongratulation"
    const val APP_SSID_1 = "convenientEverythingChairEducator"
    const val APP_SSID_2 = "flatSuite"
    const val LBS = "lazyBathtubForeignRecord"
    const val LANGUAGE = "terminalEverydayTrunk"
    const val IP = "considerateRealBooth"
    const val SYSTEM_MODE = "plasticNormalStairRoughNotebook"
    const val GOOGLE_MOBILE_NO = "folkEveryDecoration" // 登录手机号
    const val GOOGLE_GAID = "softSickness" // 谷歌广告id
    const val GOOGLE_USER_AGENT = "americanCornMember" // 用户代理
}

object ParamsValue {
    const val CLIENT_ID = "288"
    const val CHANNEL = "googleplay"
}

private val NetworkClient: Eth by lazy {
    Eth.builder()
        .baseUrl(URL.HOST_TEST) // TODO: 发布时要换成正式的
        .service(OkHttpService.builder().build())
        .addInterceptor(GlobalHeadersInterceptor())
        .addInterceptor(GlobalParamsInterceptor())
        .addConverter(CommonConverter())
        .addInterceptor(LogInterceptor())
        .build()
}

internal inline fun <reified T : Any> createApi(): T = NetworkClient.create(T::class)


private val NetworkClientWithoutGlobal: Eth by lazy {
    Eth.builder()
        .baseUrl(URL.HOST_TEST) // TODO: 发布时要换成正式的
        .service(OkHttpService.builder().build())
        .addInterceptor(GlobalHeadersInterceptor())
        .addConverter(CommonConverter())
        .addInterceptor(LogInterceptor())
        .build()
}

internal inline fun <reified T : Any> createApiWithoutGlobal(): T = NetworkClientWithoutGlobal.create(T::class)
