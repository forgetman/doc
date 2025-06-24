package vector.compat.notch.manufacturer

import android.app.Activity
import android.graphics.Rect
import vector.compat.notch.DisplayCutoutMode
import vector.compat.notch.api.Api
import vector.compat.notch.api.NotchInScreenListener
import vector.app.os.dp
import vector.app.util.Screen

/**
 * OPPO厂商
 * @author yuansui
 * @since 2020/5/11
 */
internal class ApiImplOPPO : Api {

    override fun fetchRect(activity: Activity, listener: NotchInScreenListener) {
        listener.onNotchInScreen(getNotchRect(activity))
    }

    override fun setDisplayCutout(activity: Activity, mode: DisplayCutoutMode) {
        // 方式未知, 待补充
    }

    private fun hasNotch(activity: Activity): Boolean {
        return activity.packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
    }

    override fun getNotchRect(activity: Activity): Rect? {
        if (!hasNotch(activity)) return null
        // oppo不提供接口获取刘海尺寸，目前oppo的刘海宽为108dp, 高为27dp
        // 必须使用[appContext]
        val width = 108.dp.toPx(activity.applicationContext)
        val height = 27.dp.toPx(activity.applicationContext)
        return Rect((Screen.width - width) / 2, 0, width, height)
    }

}