package pretimmediat.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import coroutine.flow.launchIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import pretimmediat.databinding.DialogStyle1Binding
import pretimmediat.ext.countdownFlow
import sugar.ext.self
import vector.app.databinding.dialog.DBDialogEx
import vector.bindingadapter.bind.Bind
import vector.ext.getStringForLanguage
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT
import java.util.concurrent.TimeUnit

class Style1Dialog private constructor(
    context: Context?,
    @DrawableRes val iconId: Int,
    val content: CharSequence,
    val button: String,
    private val dismissOnTouchOutside: Boolean?,
    private val dismissOnClick: Boolean?,
    private val dismissCountdownCount: Int?,
    private val action: (() -> Unit)?,
) : DBDialogEx(context) {

    class Builder(context: Context?) {
        private var iconId: Int = 0
        private var content: CharSequence = ""
        private var button: String = ""
        private var dismissOnTouchOutside: Boolean? = null
        private var dismissOnClick: Boolean? = null
        private var dismissCountdownCount: Int? = null
        private var action: (() -> Unit)? = null

        private val context: Context = requireNotNull(context) { "Context must not be null" }

        fun icon(@DrawableRes iconId: Int) = self { this.iconId = iconId }

        fun content(title: CharSequence) = self { this.content = title }
        fun content(@StringRes titleId: Int) =
            self { this.content = context.getStringForLanguage(titleId) }

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

        fun dismissCountdown(count: Int) = self {
            dismissCountdownCount = count
        }

        fun build() = Style1Dialog(
            context,
            iconId,
            content,
            button,
            dismissOnTouchOutside,
            dismissOnClick,
            dismissCountdownCount,
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

    val countdownText = MutableStateFlow<String?>(null)


    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return DialogStyle1Binding.inflate(inflater).apply {
            owner = this@Style1Dialog
        }
    }

    override fun initializeContentView() {
        dismissOnTouchOutside?.let {
            setCanceledOnTouchOutside(it)
        }

        dismissCountdownCount?.let {
            countdownFlow(it, 1, TimeUnit.SECONDS).onStart {
                countdownText.value = "${it}s"
            }.onEach { second ->
                countdownText.value = "${second}s"
            }.onCompletion {
                dismiss()
            }.launchIn(this)
        }
    }
}