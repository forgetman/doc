package dsb.serv

import android.content.Intent
import dsb.Bus
import dsb.EventId
import dsb.model.GpsCity
import dsb.model.PackGpsCity
import dsb.network.api.CityApi
import eth.ext.bind
import eth.model.Nive
import lib.baidu.Locator
import lib.base.network.createApi
import vector.service.ServiceEx

/**
 * @author yuansui
 * @since 2019/1/23
 */
class LocationServ : ServiceEx() {

    private val locator = Locator(this)

    private val city = Nive<PackGpsCity>()

    override fun onHandleIntent(intent: Intent) {
        locator.listener = {
            if (success) {
                createApi<CityApi>()
                    .gps(gps?.latitude, gps?.longitude)
                    .bind(city)
                    .launch(this@LocationServ)
            } else {
                locateFailed()
            }
        }

        city.observe(this) {
            GpsCity.id = it.city?.id
            GpsCity.name = it.city?.name
            GpsCity.save()

            Bus.get().send(EventId.LOCATION_CITY, it.city)
            stopSelf()
        }

        city.onError(this) {
            locateFailed()
        }

        locator.start()
    }

    private fun locateFailed() {
        Bus.get().send(EventId.LOCATION, "定位失败")
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        locator.unRegisterLocationListener()
        locator.stop()
    }
}