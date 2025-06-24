package vector.app.databinding

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding

interface BindingInflater {
    fun createBinding(inflater: LayoutInflater): ViewDataBinding
}