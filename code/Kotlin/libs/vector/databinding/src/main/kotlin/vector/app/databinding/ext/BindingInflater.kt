package vector.app.databinding.ext

import android.view.View
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import vector.app.databinding.BindingInflater

internal fun <T : BindingInflater> ComponentActivity.createBindingView(inflater: T): View {
    val binding = inflater.createBinding(this.layoutInflater)
    binding.lifecycleOwner = this
    return binding.root
}

internal fun <T : BindingInflater> Fragment.createBindingView(inflater: T): View {
    val binding = inflater.createBinding(this.layoutInflater)
    binding.lifecycleOwner = this
    return binding.root
}