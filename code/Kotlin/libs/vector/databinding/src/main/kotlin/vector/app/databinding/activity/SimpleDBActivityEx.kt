package vector.app.databinding.activity

import android.view.View
import androidx.annotation.CallSuper
import vector.app.activity.SimpleActivityEx
import vector.app.databinding.BindingInflater
import vector.app.databinding.ext.createBindingView


abstract class SimpleDBActivityEx : SimpleActivityEx(), BindingInflater {

    @CallSuper
    override fun createContentView(): View {
        return createBindingView(this)
    }
}