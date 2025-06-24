package vector.app.fitter

import android.app.Activity
import sugar.ext.getAnnotation
import vector.os.lifecycle.SimpleActivityLifecycleCallbacks

/**
 * @author yuansui
 * @since 2019/4/11
 */
class ActivityLifecycleCallbackImpl : SimpleActivityLifecycleCallbacks() {

    override fun onActivityResumed(activity: Activity) {
        activity.getAnnotation(FitStrategy::class) {
            Fitter.fit(activity, it.value)
        } ?: Fitter.fit(activity)
    }
}
