package pretimmediat.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import pretimmediat.databinding.DialogStyle2Binding
import sugar.ext.self
import vector.app.databinding.dialog.DBDialogEx
import vector.bindingadapter.bind.Bind
import vector.ext.getStringForLanguage
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT

class Style2Dialog private constructor(
    context: Context,
    @DrawableRes val iconId: Int,
    val content: CharSequence,
    val buttonLeft: String,
    val buttonRight: String,
    private val leftAction: (() -> Unit)?,
    private val rightAction: (() -> Unit)?
) : DBDialogEx(context) {

    class Builder(context: Context?) {
        private var iconId: Int = 0
        private var content: CharSequence = ""
        private var buttonLeft: String = ""
        private var buttonRight: String = ""
        private var leftAction: (() -> Unit)? = null
        private var rightAction: (() -> Unit)? = null

        private val context: Context = requireNotNull(context) { "Context must not be null" }

        fun icon(@DrawableRes iconId: Int) = self { this.iconId = iconId }

        fun content(@StringRes titleId: Int) =
            self { this.content = context.getStringForLanguage(titleId) }

        fun content(title: CharSequence?) = self { this.content = title ?: "" }

        fun buttonLeft(@StringRes buttonLeftId: Int, action: (() -> Unit)? = null) = self {
            this.buttonLeft = context.getStringForLanguage(buttonLeftId)
            this.leftAction = action
        }

        fun buttonLeft(buttonLeft: String, action: (() -> Unit)? = null) = self {
            this.buttonLeft = buttonLeft
            this.leftAction = action
        }

        fun buttonRight(@StringRes buttonRightId: Int, action: (() -> Unit)? = null) = self {
            this.buttonRight = context.getStringForLanguage(buttonRightId)
            this.rightAction = action
        }

        fun buttonRight(buttonRight: String, action: (() -> Unit)? = null) = self {
            this.buttonRight = buttonRight
            this.rightAction = action
        }

        fun build(): Style2Dialog {
            return Style2Dialog(
                context,
                iconId,
                content,
                buttonLeft,
                buttonRight,
                leftAction,
                rightAction
            )
        }
    }

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)

    override val gravity: Int
        get() = Gravity.CENTER

    val onLeftClick = Bind.OnClick {
        leftAction?.invoke()
        dismiss()
    }

    val onRightClick = Bind.OnClick {
        rightAction?.invoke()
        dismiss()
    }

    val onCloseClick = Bind.OnClick {
        dismiss()
    }


    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return DialogStyle2Binding.inflate(inflater).apply {
            owner = this@Style2Dialog
        }
    }
}