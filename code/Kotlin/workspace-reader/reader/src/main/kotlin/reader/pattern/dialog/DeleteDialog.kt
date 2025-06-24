package reader.pattern.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import reader.databinding.DialogDeleteBinding
import vector.app.databinding.dialog.DBDialogEx
import vector.bindingadapter.bind.Bind
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT

/**
 * 删除书本
 * @author yuansui
 * @since 2018/12/28
 */
class DeleteDialog(context: Context?) : DBDialogEx(context) {

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)

    var onDeleteConfirm: OnDialogAction? = null

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = DialogDeleteBinding.inflate(layoutInflater)
        binding.owner = this
        return binding
    }

    val onCancelClick = Bind.OnClick {
        dismiss()
    }

    val onConfirmClick = Bind.OnClick {
        onDeleteConfirm?.invoke()
        dismiss()
    }
}