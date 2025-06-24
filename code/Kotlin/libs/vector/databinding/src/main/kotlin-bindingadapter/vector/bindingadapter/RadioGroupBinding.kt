package vector.bindingadapter

import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import vector.app.ext.view.findAll
import vector.bindingadapter.bind.Bind

/**
 * 所有的下标绑定和检查, 都基于[RadioGroup]底下的一级[RadioButton]来计算的, 包含在[RadioGroup]里面的所有其他类型的view都不作数
 * @author yuansui
 * @since 2018/12/29
 */
object RadioGroupBinding {

    private const val ON_CHECKED_CHANGED = BINDING_PREFIX + "radioGroup_onCheckedChanged"
    private const val CHECKED = BINDING_PREFIX + "radioGroup_checked"

    @JvmStatic
    @BindingAdapter(
        ON_CHECKED_CHANGED, CHECKED + ATTR_CHANGED_SUFFIX,
        requireAll = false
    )
    fun setOnCheckedChanged(
        view: RadioGroup,
        binding: Bind.RadioGroup.OnCheckedChanged?,
        attrChange: InverseBindingListener?
    ) {
        view.setOnCheckedChangeListener { _, checkedId ->
            attrChange?.onChange()

            if (binding != null) {
                val list = view.getAllRadioButton()
                for (i in list.indices) {
                    if (list[i].id == checkedId) {
                        binding.action(i, checkedId)
                        break
                    }
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter(CHECKED)
    fun setChecked(view: RadioGroup, index: Int) {
        val list = view.getAllRadioButton()
        if (index >= list.size) return
        val child = list[index]
        if (view.checkedRadioButtonId != child.id) view.check(child.id)
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = CHECKED)
    fun getChecked(view: RadioGroup): Int {
        val id = view.checkedRadioButtonId

        var index = -1
        val list = view.getAllRadioButton()
        for (i in list.indices) {
            if (list[i].id == id) {
                index = i
                break
            }
        }
        return index
    }

    /**
     * 获取一级里的所有[RadioButton]
     */
    private fun RadioGroup.getAllRadioButton() = findAll<RadioButton>()
}