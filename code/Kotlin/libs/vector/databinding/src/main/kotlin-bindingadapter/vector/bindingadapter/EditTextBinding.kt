@file:Suppress("unused")

package vector.bindingadapter

import android.text.InputFilter
import android.widget.EditText
import androidx.databinding.BindingAdapter
import vector.bindingadapter.trigger.EditClearTrigger
import vector.validator.Validator

/**
 * @author yuansui
 * @since 2018/3/6
 */
object EditTextBinding {

    private const val SELECTION = BINDING_PREFIX + "editText_selection"
    private const val INPUT_FILTER = BINDING_PREFIX + "editText_inputFilter"

    private const val TRIGGER_CLEAR = BINDING_PREFIX + "editText_trigger_clear"

    private const val VALIDATOR = BINDING_PREFIX + "editText_validator"


    @JvmStatic
    @BindingAdapter(SELECTION)
    fun setSelection(editText: EditText, index: Int) {
        editText.setSelection(index)
    }

    @JvmStatic
    @BindingAdapter(INPUT_FILTER)
    fun setFilters(editText: EditText, inputFilter: InputFilter) {
        editText.filters = arrayOf(inputFilter)
    }

    @JvmStatic
    @BindingAdapter(TRIGGER_CLEAR)
    fun setTrigger(editText: EditText, trigger: EditClearTrigger) {
        trigger.observe {
            editText.text.clear()
        }
    }

    @JvmStatic
    @BindingAdapter(VALIDATOR)
    fun setValidator(editText: EditText, validator: Validator) {
        validator.bindView(editText)
    }
}