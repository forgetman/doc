package pretimmediat.network

import eth.interceptor.PreInterceptor
import eth.model.Request
import pretimmediat.manager.AccountManager
import pretimmediat.property.Properties
import vector.datastore.preference.sync
import vector.util.DeviceIdUtil
import vector.util.PackageUtil

class GlobalParamsInterceptor : PreInterceptor {

    override fun intercept(chain: PreInterceptor.Chain): Request {
        val builder = chain.request().newBuilder()
            .param(ParamsName.LBS, Properties.location)
            .param(ParamsName.VERSION_NAME, PackageUtil.appVersionName)
            .param(ParamsName.VERSION_CODE, PackageUtil.appVersionCode)
            .param(ParamsName.DEVICE_ID_3, DeviceIdUtil.id)
            .param(ParamsName.IMEI, DeviceIdUtil.id)
            .param(ParamsName.IP, Properties.ip)
            .param(ParamsName.CHANNEL, ParamsValue.CHANNEL)
            .param(ParamsName.SYSTEM_MODE, android.os.Build.MODEL)
            .param(ParamsName.GOOGLE_MOBILE_NO, Properties.accountPhoneNumber)
            .param(ParamsName.GOOGLE_GAID, Properties.gaid)
            .param(ParamsName.GOOGLE_USER_AGENT, System.getProperty("http.agent"))

        val language = Properties.language.sync().getOrNull()
        if (language == "en") {
            // 把en转成zh
            builder.param(ParamsName.LANGUAGE, "zh")
        } else {
            builder.param(ParamsName.LANGUAGE, language)
        }

        // 多产品时传的是子userId, 如果没有则传当前登录的userId
        val oldUserId = chain.request().headers?.get(ParamsName.USER_ID)
        if (oldUserId == null) {
            builder.param(ParamsName.USER_ID, AccountManager.account)
        } else {
            builder.param(ParamsName.USER_ID, oldUserId)
        }

        val oldAppSsid1 = chain.request().headers?.get(ParamsName.APP_SSID_1)
        if (oldAppSsid1 == null) {
            builder.param(ParamsName.APP_SSID_1, ParamsValue.CLIENT_ID)
                .param(ParamsName.APP_SSID_2, ParamsValue.CLIENT_ID)
        } else {
            builder.param(ParamsName.APP_SSID_1, oldAppSsid1)
                .param(ParamsName.APP_SSID_2, oldAppSsid1)
        }

        return chain.proceed(builder.build())
    }
}