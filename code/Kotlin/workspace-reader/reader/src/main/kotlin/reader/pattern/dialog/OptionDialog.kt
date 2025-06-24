package reader.pattern.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import reader.databinding.DialogOptionBinding
import vector.app.databinding.dialog.DBDialogEx
import vector.bindingadapter.bind.Bind
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT

/**
 * @author yuansui
 * @since 2019/1/16
 */
class OptionDialog(context: Context?) : DBDialogEx(context) {

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)

    var onDeleteSelected: OnDialogAction? = null
    var onUpdateSelected: OnDialogAction? = null
    var onInfoSelected: OnDialogAction? = null

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = DialogOptionBinding.inflate(layoutInflater)
        binding.owner = this
        return binding
    }

    override fun initializeContentView() {
        setCanceledOnTouchOutside(true)
    }

    val onClickDelete = Bind.OnClick {
        onDeleteSelected?.invoke()
        dismiss()
    }

    val onClickInfo = Bind.OnClick {
        onInfoSelected?.invoke()
        dismiss()
    }

    val onClickUpdate = Bind.OnClick {
        onUpdateSelected?.invoke()
        dismiss()
    }
}