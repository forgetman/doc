package lib.baidu

import android.content.Context
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import logger.L

typealias OnLocationChanged = LocationResult.() -> Unit

/**
 * 定位者
 * @author yuansui
 * @since 2019/1/23
 */
class Locator(context: Context) {

    companion object {
        private const val COOR_TYPE = "bd09ll"
    }

    private var client: LocationClient = LocationClient(context)
    private var started = false
    var listener: OnLocationChanged? = null
    private val localListener = LocatorListener()

    init {
        setOption()
        client.registerLocationListener(localListener)
    }

    fun unRegisterLocationListener() {
        client.unRegisterLocationListener(localListener)
    }

    private fun setOption() {
        val option = LocationClientOption().apply {
            locationMode = LocationClientOption.LocationMode.Hight_Accuracy
            setCoorType(COOR_TYPE) // 返回的定位结果是百度经纬度,默认值gcj02
            isOpenGps = false
            setWifiCacheTimeOut(5 * 60 * 1000);
            scanSpan = 1000// 设置发起定位请求的间隔时间
            setIsNeedAddress(true)// 返回的定位结果包含地址信息
            setNeedDeviceDirect(false)// 返回的定位结果包含手机机头的方向
        }
        client.locOption = option
    }

    inner class LocatorListener : BDAbstractLocationListener() {

        override fun onReceiveLocation(location: BDLocation?) {
            if (!started) {
                // 如果结束了, 不进行通知
                return
            }

            val result = LocationResult()

            L.d("onReceiveLocation1 = " + location?.city)
            L.d("onReceiveLocation2 = " + location?.locType)

            if (location == null || location.locType != 161) {
                L.d("onReceiveLocation: location failed")
                result.success = false
                listener?.invoke(result)
                return
            }

            stop()

            result.success = true
            result.gps = Gps().apply {
                val a = location.address

                longitude = location.longitude.toString()
                latitude = location.latitude.toString()
                province = a.province
                city = a.city
                district = a.district
            }

            listener?.invoke(result)
        }

    }

    fun start() {
        if (!started) {
            started = true
            client.start()
        }
    }

    fun stop() {
        if (started) {
            started = false
            client.stop()
        }
    }
}