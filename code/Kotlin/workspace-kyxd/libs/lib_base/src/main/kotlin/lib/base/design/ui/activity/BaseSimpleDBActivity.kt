package lib.base.design.ui.activity

import androidx.annotation.CallSuper
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.util.Stats

/**
 * @author yuansui
 * @since 2021/5/11
 */
abstract class BaseSimpleDBActivity : SimpleDBActivityEx() {

    private val classTag = javaClass.canonicalName.orEmpty()

    @CallSuper
    override fun onResume() {
        super.onResume()

        Stats.onActivityResume(this, classTag)
    }

    @CallSuper
    override fun onPause() {
        super.onPause()

        Stats.onActivityPause(this, classTag)
    }
}