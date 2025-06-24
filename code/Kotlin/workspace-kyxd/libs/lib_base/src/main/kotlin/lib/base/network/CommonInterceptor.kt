package lib.base.network

import eth.interceptor.PreInterceptor
import eth.model.Request
import lib.base.Sp
import vector.util.DeviceIdUtil
import vector.util.PackageUtil

private object ParamValue {
    const val PLATFORM = "android"
    const val APP_NAME = "DSB2016"
    const val DID = "10001"
    const val KEY_CHANNEL_NAME = "channel_name"
}

private object BaseParam {
    const val PLATFORM = "platform"
    const val USER_TOKEN = "user_token"
    const val APP_VERSION = "app_version"
    const val APP_NAME = "app_name"
    const val DEVICE_ID = "device_id"
    const val CHANNEL = "channel"
    const val DID = "dsb_did"
}

class CommonInterceptor : PreInterceptor {
    override fun intercept(chain: PreInterceptor.Chain): Request {
        val request = chain.request().newBuilder()
            .param(BaseParam.APP_VERSION, PackageUtil.appVersionName)
            .param(BaseParam.APP_NAME, ParamValue.APP_NAME)
            .param(BaseParam.DEVICE_ID, DeviceIdUtil.id)
            .param(BaseParam.PLATFORM, ParamValue.PLATFORM)
            .param(BaseParam.DID, Sp.getDid() ?: ParamValue.DID)
            .param(BaseParam.CHANNEL, PackageUtil.getMetaValue(ParamValue.KEY_CHANNEL_NAME))
            .param(BaseParam.USER_TOKEN, Sp.getToken())
            .build()
        return chain.proceed(request)
    }
}