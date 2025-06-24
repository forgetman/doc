package vector.compat.notch

import android.app.Activity
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import vector.compat.notch.api.Api
import vector.compat.notch.api.Api28Impl
import vector.compat.notch.api.ApiImpl
import vector.compat.notch.api.NotchInScreenListener

/**
 * 刘海屏
 * @author yuansui
 * @since 2020/5/11
 */
object NotchInScreenCompat {

    private val api: Api = when {
        isSdkAtLeast(SdkInt.P_28) -> Api28Impl()
        else -> ApiImpl()
    }

    fun applyListener(activity: Activity, listener: NotchInScreenListener) {
        api.fetchRect(activity, listener)
    }

    fun setDisplayCutout(activity: Activity, mode: DisplayCutoutMode) {
        api.setDisplayCutout(activity, mode)
    }
}