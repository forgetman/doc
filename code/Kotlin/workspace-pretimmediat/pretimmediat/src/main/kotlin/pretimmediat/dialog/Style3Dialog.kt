package pretimmediat.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import pretimmediat.databinding.DialogStyle3Binding
import sugar.ext.self
import vector.app.databinding.dialog.DBDialogEx
import vector.bindingadapter.bind.Bind
import vector.ext.getStringForLanguage
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT

class Style3Dialog private constructor(
    context: Context?,
    val title: String,
    val content: CharSequence,
    val button: String,
    private val dismissOnTouchOutside: Boolean?,
    private val dismissOnClick: Boolean?,
    private val action: (() -> Unit)?,
) : DBDialogEx(context) {

    class Builder(context: Context?) {
        private var title: String = ""
        private var content: CharSequence = ""
        private var button: String = ""
        private var dismissOnTouchOutside: Boolean? = null
        private var dismissOnClick: Boolean? = null
        private var action: (() -> Unit)? = null

        private val context: Context = requireNotNull(context) { "Context must not be null" }

        fun title(title: String) = self { this.title = title }
        fun title(@StringRes titleId: Int) =
            self { this.title = context.getStringForLanguage(titleId) }

        fun content(content: CharSequence) = self { this.content = content }
        fun content(@StringRes contentId: Int) =
            self { this.content = context.getStringForLanguage(contentId) }

        fun dismissOnTouchOutside(dismiss: Boolean) = self { this.dismissOnTouchOutside = dismiss }

        fun dismissOnClick(dismiss: Boolean) = self { this.dismissOnClick = dismiss }

        fun button(button: String, action: (() -> Unit)? = null) = self {
            this.button = button
            this.action = action
        }

        fun button(@StringRes buttonId: Int, action: (() -> Unit)? = null) = self {
            this.button = context.getString(buttonId)
            this.action = action
        }

        fun build() = Style3Dialog(
            context,
            title,
            content,
            button,
            dismissOnTouchOutside,
            dismissOnClick,
            action
        )
    }

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)

    override val gravity: Int
        get() = Gravity.CENTER

    val onClick = Bind.OnClick {
        action?.invoke()
        dismissOnClick?.let { if (it) dismiss() } ?: dismiss()
    }

    val onCloseClick = Bind.OnClick {
        dismissOnTouchOutside?.let {
            if (it) dismiss()
        } ?: dismiss()
    }


    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return DialogStyle3Binding.inflate(inflater).apply {
            owner = this@Style3Dialog
        }
    }

    override fun initializeContentView() {
        dismissOnTouchOutside?.let {
            setCanceledOnTouchOutside(it)
        }
    }
}