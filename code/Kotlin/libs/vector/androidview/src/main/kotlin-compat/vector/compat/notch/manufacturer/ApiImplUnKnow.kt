package vector.compat.notch.manufacturer

import android.app.Activity
import android.graphics.Rect
import vector.compat.notch.DisplayCutoutMode
import vector.compat.notch.api.Api
import vector.compat.notch.api.NotchInScreenListener

/**
 * 未知厂商
 * @author yuansui
 * @since 2020/5/11
 */
internal class ApiImplUnKnow : Api {

    override fun fetchRect(activity: Activity, listener: NotchInScreenListener) {
        listener.onNotchInScreen(getNotchRect(activity))
    }

    override fun setDisplayCutout(activity: Activity, mode: DisplayCutoutMode) {
        // do nothing
    }

    override fun getNotchRect(activity: Activity): Rect? {
        return null
    }
}