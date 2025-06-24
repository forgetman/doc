package star.design.ui.activity.edit

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import star.databinding.ActivityEditNewPayerBinding
import star.design.viewModel.edit.EditNewPayerViewModel

/**
 * @author yuansui
 * @since 2020/4/14
 */
@Creator(forResult = true)
class EditNewPayerActivity : BaseEditInfoActivity<EditNewPayerViewModel>() {

    companion object {
        const val RESULT_NAME_ZONE_COUNT = "zone_count"
        const val RESULT_NAME_PLATFORM_COUNT = "platform_count"
    }

    @Extra(true)
    var zoneCount = 0

    @Extra(true)
    var platformCount = 0

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityEditNewPayerBinding.inflate(inflater)
        binding.viewModel = viewModel
        return binding
    }

    override fun initializeData() {
        viewModel.input.zoneCount.value = zoneCount
        viewModel.input.platformCount.value = platformCount
    }

    override fun onSaveClick() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(RESULT_NAME_ZONE_COUNT, viewModel.input.zoneCount.value)
            putExtra(RESULT_NAME_PLATFORM_COUNT, viewModel.input.platformCount.value)
        })
        finish()
    }
}