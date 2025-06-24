package dsb.design.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dsb.databinding.DialogHomeMsgBinding
import vector.app.databinding.dialog.DBDialogEx
import vector.bindingadapter.bind.Bind

/**
 * @author yuansui
 * @since 2019/2/21
 */
class HomeMsgDialog(context: Context?, val icon: String?) : DBDialogEx(context) {

    var onAction: OnDialogAction? = null

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = DialogHomeMsgBinding.inflate(layoutInflater)
        binding.owner = this
        return binding
    }

    override fun flowOfSetup() {
        dismissOnTouchOutside(true)
    }

    val onClick = Bind.OnClick {
        onAction?.invoke()
        dismiss()
    }

    val onCloseClick = Bind.OnClick {
        dismiss()
    }
}