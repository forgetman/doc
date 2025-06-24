package vector.app.databinding.popup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ViewDataBinding
import vector.app.popup.PopupWindowEx

abstract class DBPopupWindowEx(context: Context?) : PopupWindowEx(context) {

    final override fun createContentView(layoutInflater: LayoutInflater): View {
        val binding = createBinding(layoutInflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    abstract fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding
}