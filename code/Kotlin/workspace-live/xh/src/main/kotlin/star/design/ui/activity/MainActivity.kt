package star.design.ui.activity

import android.text.InputType
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import star.databinding.ActivityMainBinding
import star.design.ui.activity.edit.CommonEditActivityCreator
import star.design.ui.activity.edit.EditNewPayerActivity
import star.design.ui.activity.edit.EditNewPayerActivityCreator
import star.design.viewModel.MainViewModel
import star.model.Input
import vector.app.databinding.activity.DBActivityEx
import vector.bindingadapter.bind.Bind

/**
 * @author yuansui
 * @since 2020-04-10
 */
class MainActivity : DBActivityEx<MainViewModel>() {

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityMainBinding.inflate(inflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    override fun initializeSystemBar() {
        appBar.mid.addText("首页")
    }

    override fun enableHideKeyboardWhenFocusChanged(): Boolean {
        return true
    }

    val onDayFlowChanged = Bind.Text.TextChanged {
        after {
            val value = it.toString().toIntOrNull() ?: return@after
            viewModel.onDayFlowChanged(value)
        }
    }

    val onMonthFlowChanged = Bind.Text.TextChanged {
        after {
            val value = it.toString().toIntOrNull() ?: return@after
            viewModel.onMonthFlowChanged(value)
        }
    }

    val onUnionDividendChanged = Bind.Text.TextChanged {
        after {
            val value = it.toString().toIntOrNull() ?: return@after
            viewModel.onUnionDividendChanged(value)
        }
    }

    val onAnchorDividendChanged = Bind.Text.TextChanged {
        after {
            val value = it.toString().toIntOrNull() ?: return@after
            viewModel.onAnchorDividendChanged(value)
        }
    }

    val onRebateClick = Bind.OnClick {
        EditNewPayerActivityCreator.create()
            .zoneCount(viewModel.newPayerData.zoneCount)
            .platformCount(viewModel.newPayerData.platformCount)
            .startForResult(this) { code, data ->
                viewModel.newPayerData.zoneCount =
                    data?.getIntExtra(EditNewPayerActivity.RESULT_NAME_ZONE_COUNT, 0)
                        ?: 0
                viewModel.newPayerData.platformCount =
                    data?.getIntExtra(EditNewPayerActivity.RESULT_NAME_PLATFORM_COUNT, 0)
                        ?: 0
            }
    }

    val hallClick = Bind.OnClick {
        CommonEditActivityCreator.create()
            .inputType(InputType.TYPE_CLASS_NUMBER)
            .incomeHint(Input.hallSize.value?.toString())
            .start(this)
    }

    val onCalcClick = Bind.OnClick {
        viewModel.calc()
    }
}