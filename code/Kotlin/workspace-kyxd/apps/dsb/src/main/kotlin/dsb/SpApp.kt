package dsb

import dsb.model.City
import vector.store.SpEx

object SpApp : SpEx() {
    const val KEY_GPS = "gps_city"
    const val SHOW_NEW_TIP_ON_230 = "show_new_tips_230" // 是否有显示过新版本提示

    override val fileName: String
        get() = "sp_app"

    fun getGpsCity() = getObject<City>(KEY_GPS)
}