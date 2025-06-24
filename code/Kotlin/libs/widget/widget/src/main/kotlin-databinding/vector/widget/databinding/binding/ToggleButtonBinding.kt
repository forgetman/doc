package vector.widget.databinding.binding

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import vector.bindingadapter.ATTR_CHANGED_SUFFIX
import vector.bindingadapter.BINDING_PREFIX
import vector.widget.OnToggleButtonCheckedChanged
import vector.widget.ToggleButton
import vector.widget.databinding.ToggleButtonBind

/**
 * @author yuansui
 * @since 2019/4/10
 */
object ToggleButtonBinding {

    private const val CHECKED = BINDING_PREFIX + "toggleButton_checked"
    private const val ON_CHECKED_CHANGED = BINDING_PREFIX + "toggleButton_onCheckedChanged"

    @JvmStatic
    @BindingAdapter(ON_CHECKED_CHANGED, CHECKED + ATTR_CHANGED_SUFFIX, requireAll = false)
    fun setOnChecked(
        view: ToggleButton,
        binding: ToggleButtonBind.OnCheckedChanged?,
        attrChange: InverseBindingListener?
    ) {
        view.listener = object : OnToggleButtonCheckedChanged {
            override fun onChanged(view: View, checked: Boolean) {
                attrChange?.onChange()
                binding?.action?.onChanged(view, checked)
            }
        }
    }

    @JvmStatic
    @BindingAdapter(CHECKED)
    fun setChecked(view: ToggleButton, isChecked: Boolean) {
        if (view.isChecked == isChecked) return
        view.toggle(isChecked)
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = CHECKED)
    fun getChecked(view: ToggleButton): Boolean {
        return view.isChecked
    }

}