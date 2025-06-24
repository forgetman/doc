package reader.pattern.popup

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import reader.databinding.PopupMenuOptionsBinding
import sugar.ext.NoArgBlock
import vector.app.databinding.popup.DBPopupWindowEx
import vector.bindingadapter.bind.Bind

/**
 * @author yuansui
 * @since 2020/6/1
 */
class MenuPopup(context: Context?) : DBPopupWindowEx(context) {

    var onUpdateClick: NoArgBlock? = null
    var onSwitchToGridClick: NoArgBlock? = null
    var onSwitchToListClick: NoArgBlock? = null

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = PopupMenuOptionsBinding.inflate(layoutInflater)
        binding.owner = this
        return binding
    }

    val onClickUpdate = Bind.OnClick {
        onUpdateClick?.invoke()
    }

    val onClickSwitchToGrid = Bind.OnClick {
        onSwitchToGridClick?.invoke()
    }

    val onClickSwitchToList = Bind.OnClick {
        onSwitchToListClick?.invoke()
    }
}