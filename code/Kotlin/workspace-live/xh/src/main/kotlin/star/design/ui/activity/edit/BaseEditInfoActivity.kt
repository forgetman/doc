package star.design.ui.activity.edit

import android.R.attr.onClick
import android.R.attr.text
import star.design.viewModel.edit.BaseEditInfoViewModel
import star.ext.addBackIcon
import vector.app.databinding.activity.DBActivityEx
import vector.config.Config.appBar

/**
 * @author yuansui
 * @since 2020/4/14
 */
abstract class BaseEditInfoActivity<VM : BaseEditInfoViewModel> : DBActivityEx<VM>() {

    override fun initializeSystemBar() {
        appBar.mid.addText("信息编辑")
        appBar.addBackIcon(this)
        appBar.right.addText {
            text = "保存"
            onClick = {
                onSaveClick()
            }
        }
    }

    abstract fun onSaveClick()
}