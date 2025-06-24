package lib.base.design.frag

import androidx.annotation.CallSuper
import vector.app.databinding.frag.SimpleDBFragEx
import vector.util.Stats

/**
 * @author yuansui
 * @since 2021/5/11
 */
abstract class BaseSimpleDBFrag : SimpleDBFragEx() {
    private val classTag = javaClass.canonicalName.orEmpty()

    @CallSuper
    override fun onResume() {
        super.onResume()

        Stats.onFragmentVisible(context, classTag)
    }

    @CallSuper
    override fun onPause() {
        super.onPause()

        Stats.onFragmentInvisible(context, classTag)
    }
}