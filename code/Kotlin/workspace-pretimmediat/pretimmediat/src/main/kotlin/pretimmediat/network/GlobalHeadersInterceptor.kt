package pretimmediat.network

import eth.interceptor.PreInterceptor
import eth.model.Request
import pretimmediat.manager.AccountManager
import vector.util.DeviceIdUtil
import vector.util.PackageUtil

class GlobalHeadersInterceptor : PreInterceptor {

    override fun intercept(chain: PreInterceptor.Chain): Request {
        val builder = chain.request().newBuilder()
            // headers
            .header(ParamsName.CLIENT_ID_1, ParamsValue.CLIENT_ID)
            .header(ParamsName.CLIENT_ID_2, ParamsValue.CLIENT_ID)
            .header(ParamsName.TOKEN, AccountManager.token)
            .header(ParamsName.CHANNEL, ParamsValue.CHANNEL)
            .header(ParamsName.VERSION_NAME, PackageUtil.appVersionName)
            .header(ParamsName.VERSION_CODE, PackageUtil.appVersionCode)
            .header(ParamsName.DEVICE_ID_1, DeviceIdUtil.id)
            .header(ParamsName.DEVICE_ID_2, DeviceIdUtil.id)
            .header(ParamsName.DEVICE_ID_3, DeviceIdUtil.id)
            .header(ParamsName.IMEI, DeviceIdUtil.id)
            .header(ParamsName.MUL_FLAG, "1")

        val oldVFlag1 = chain.request().headers?.get(ParamsName.V_FLAG_1)
        if (oldVFlag1 == null) {
            // 不需要登录的接口传的是true, 在Api声明那边配置, 如果没有配置, 则认为是需要登录的接口
            builder.header(ParamsName.V_FLAG_1, false)
                .header(ParamsName.V_FLAG_2, false)
        } else {
            builder.header(ParamsName.V_FLAG_2, oldVFlag1)
        }

        // 多产品时传的是子userId, 如果没有则传当前登录的userId
        val oldUserId = chain.request().headers?.get(ParamsName.USER_ID)
        if (oldUserId == null) {
            builder.header(ParamsName.USER_ID, AccountManager.account)
                .header(ParamsName.CURR_USER_ID, AccountManager.account)
        } else {
            builder.header(ParamsName.CURR_USER_ID, oldUserId)
        }

        val oldAppSsid1 = chain.request().headers?.get(ParamsName.APP_SSID_1)
        if (oldAppSsid1 == null) {
            builder.header(ParamsName.CLIENT_ID_1, ParamsValue.CLIENT_ID)
                .header(ParamsName.CLIENT_ID_2, ParamsValue.CLIENT_ID)
        } else {
            builder.header(ParamsName.CLIENT_ID_1, oldAppSsid1)
                .header(ParamsName.CLIENT_ID_2, oldAppSsid1)
        }

        return chain.proceed(builder.build())
    }
}