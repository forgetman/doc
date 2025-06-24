package star.design.ui.activity.edit

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import live.Live

import star.databinding.ActivityEditCommonBinding
import star.design.viewModel.edit.CommonEditViewModel
import vector.bindingadapter.bind.Bind

/**
 * @author yuansui
 * @since 2020/4/17
 */
@Creator
class CommonEditActivity : BaseEditInfoActivity<CommonEditViewModel>() {

    companion object {
        const val RESULT_NAME = "result_name"
    }

    @Extra(true)
    var incomeName: String? = null

    @Extra(true)
    var incomeHint: String? = null

    @Extra(true)
    var inputType: Int? = null

    val text = Live<String>()
    val hint = Live<String>()

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityEditCommonBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeData() {
        hint.value = incomeHint
    }

    override fun onSaveClick() {
        val result = text.value ?: hint.value
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(RESULT_NAME, result)
        })
        finish()
    }

    val textChanged = Bind.Text.TextChanged {
        after {
            text.value = it.toString()
        }
    }
}