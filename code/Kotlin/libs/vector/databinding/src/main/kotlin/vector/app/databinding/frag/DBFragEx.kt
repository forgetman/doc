package vector.app.databinding.frag

import android.view.View
import androidx.annotation.CallSuper
import vector.app.databinding.BindingInflater
import vector.app.databinding.ext.createBindingView
import vector.app.frag.FragEx
import vector.app.viewmodel.ViewModelEx

/**
 * @author yuansui
 * @since 2018/2/6
 */
abstract class DBFragEx<VM : ViewModelEx> : FragEx<VM>(), BindingInflater {

    @CallSuper
    override fun createContentView(): View {
        return createBindingView(this)
    }
}