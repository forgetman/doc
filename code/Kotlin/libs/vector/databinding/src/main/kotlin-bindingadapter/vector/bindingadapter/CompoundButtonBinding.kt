@file:Suppress("unused")

package vector.bindingadapter

import android.widget.CompoundButton
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import vector.bindingadapter.bind.Bind


object CompoundButtonBinding {

    private const val CHECKED = BINDING_PREFIX + "compoundButton_checked"
    private const val ON_CHECKED_CHANGED = BINDING_PREFIX + "compoundButton_onCheckedChanged"

    @JvmStatic
    @BindingAdapter(ON_CHECKED_CHANGED, CHECKED + ATTR_CHANGED_SUFFIX, requireAll = false)
    fun onCheckChanged(
        view: CompoundButton,
        binding: Bind.CompoundButton.OnCheckedChanged?,
        attrChange: InverseBindingListener?
    ) {
        view.setOnCheckedChangeListener { buttonView, isChecked ->
            attrChange?.onChange()
            binding?.action?.invoke(buttonView, isChecked)
        }
    }

    @JvmStatic
    @BindingAdapter(CHECKED)
    fun setChecked(view: CompoundButton, checked: Boolean) {
        if (view.isChecked == checked) return
        view.isChecked = checked
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = CHECKED)
    fun getChecked(view: CompoundButton): Boolean {
        return view.isChecked
    }

}
