package vector.compat.notch.api

import android.app.Activity
import android.graphics.Rect
import vector.compat.notch.DisplayCutoutMode
import vector.compat.notch.manufacturer.ApiImplHUAWEI
import vector.compat.notch.manufacturer.ApiImplMIUI
import vector.compat.notch.manufacturer.ApiImplOPPO
import vector.compat.notch.manufacturer.ApiImplUnKnow
import vector.compat.notch.manufacturer.ApiImplVIVO
import vector.util.RomUtil

/**
 * @author yuansui
 * @since 2020/5/11
 */
internal class ApiImpl : Api {

    private val api: Api =
        when {
            RomUtil.isHUAWEI -> ApiImplHUAWEI()
            RomUtil.isMIUI -> ApiImplMIUI()
            RomUtil.isVIVO -> ApiImplVIVO()
            RomUtil.isOPPO -> ApiImplOPPO()
            else -> ApiImplUnKnow()
        }

    override fun fetchRect(activity: Activity, listener: NotchInScreenListener) {
        api.fetchRect(activity, listener)
    }

    override fun setDisplayCutout(activity: Activity, mode: DisplayCutoutMode) {
        api.setDisplayCutout(activity, mode)
    }

    override fun getNotchRect(activity: Activity): Rect? {
        return api.getNotchRect(activity)
    }
}