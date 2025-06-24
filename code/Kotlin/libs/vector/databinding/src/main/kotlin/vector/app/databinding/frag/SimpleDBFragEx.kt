package vector.app.databinding.frag

import android.view.View
import androidx.annotation.CallSuper
import vector.app.databinding.BindingInflater
import vector.app.databinding.ext.createBindingView
import vector.app.frag.SimpleFragEx

/**
 * @author yuansui
 * @since 2018/2/6
 */
abstract class SimpleDBFragEx : SimpleFragEx(), BindingInflater {

    @CallSuper
    override fun createContentView(): View {
        return createBindingView(this)
    }
}