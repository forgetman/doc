package reader

import android.app.Activity
import vector.app.ext.adaptStatusBarTextColorByBackground
import vector.app.ext.setNavigationBarColor
import vector.app.os.colorRes
import vector.app.util.toColor
import vector.os.lifecycle.SimpleActivityLifecycleCallbacks

/**
 * @author yuansui
 * @since 2019/4/11
 */
class ActivityLifecycleCallbackImpl : SimpleActivityLifecycleCallbacks() {

    override fun onActivityResumed(activity: Activity) {
        activity.setNavigationBarColor(R.color.bottom_nav_bar_color.colorRes)
        activity.window.adaptStatusBarTextColorByBackground(
            R.color.bg_primary.toColor(activity)
        )
    }
}
