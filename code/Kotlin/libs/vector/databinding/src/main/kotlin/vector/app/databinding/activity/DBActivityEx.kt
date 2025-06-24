package vector.app.databinding.activity

import android.view.View
import androidx.annotation.CallSuper
import vector.app.activity.ActivityEx
import vector.app.databinding.BindingInflater
import vector.app.databinding.ext.createBindingView
import vector.app.viewmodel.ViewModelEx

/**
 * 封装MVVM模式的activity基类
 */
abstract class DBActivityEx<VM : ViewModelEx> : ActivityEx<VM>(), BindingInflater {

    @CallSuper
    override fun createContentView(): View {
        return createBindingView(this)
    }
}