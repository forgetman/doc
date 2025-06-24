package reader.pattern.popup

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import reader.databinding.PopupMenuThemeBinding
import sugar.ext.NoArgBlock
import vector.app.databinding.popup.DBPopupWindowEx
import vector.bindingadapter.bind.Bind

/**
 * @author yuansui
 * @since 2020/6/1
 */
class ThemePopup(context: Context?) : DBPopupWindowEx(context) {

    var onDayClick: NoArgBlock? = null
    var onNightClick: NoArgBlock? = null
    var onFollowClick: NoArgBlock? = null

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = PopupMenuThemeBinding.inflate(layoutInflater)
        binding.owner = this
        return binding
    }

    val onClickDay = Bind.OnClick {
        onDayClick?.invoke()
    }

    val onClickNight = Bind.OnClick {
        onNightClick?.invoke()
    }

    val onClickAuto = Bind.OnClick {
        onFollowClick?.invoke()
    }
}