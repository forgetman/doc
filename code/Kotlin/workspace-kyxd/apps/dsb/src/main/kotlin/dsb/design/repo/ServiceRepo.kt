package dsb.design.repo

import dsb.App
import dsb.model.Service
import dsb.network.api.MeApi
import eth.ext.bind
import eth.model.Nive
import kotlinx.coroutines.flow.map
import lib.base.network.createApi
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2020-06-23
 */
class ServiceRepo @Inject constructor() {

    fun fetchData() =
        createApi<MeApi>()
            .list(App.currCity?.id)
            .map {
                val list = mutableListOf<Service>()
                list.add(Service())
                list.add(Service())
                list.add(Service())
                list
            }
}