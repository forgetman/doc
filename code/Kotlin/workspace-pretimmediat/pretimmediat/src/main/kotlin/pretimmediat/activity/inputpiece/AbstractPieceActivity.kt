package pretimmediat.activity.inputpiece

import android.content.Context
import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.MovementMethod
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.text.method.LinkMovementMethodCompat
import coroutine.flow.state.toFalse
import coroutine.flow.state.toTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pretimmediat.R
import pretimmediat.activity.base.databinding.BaseDBActivity
import pretimmediat.ext.adaptImeResizeChanged
import pretimmediat.ext.addServiceIcon
import pretimmediat.ext.setClickableSpan
import pretimmediat.ext.startServiceActivity
import pretimmediat.viewmodel.inputpiece.AbstractPieceViewModel
import vector.bindingadapter.bind.Bind
import vector.app.ext.bind.bindView
import vector.app.util.toColor
import vector.ext.getStringForLanguage


/**
 * @author yuansui
 * @since 2024/6/15
 */
abstract class AbstractPieceActivity<VM : AbstractPieceViewModel> : BaseDBActivity<VM>() {

    private val layoutRoot by bindView<View>(R.id.layout_root)

    abstract val serviceFlag: Int

    // 检查用户是否尝试输入非法字符
    val commonInputFilter = InputFilter { source, start, end, dest, dstart, _ ->
        // 只过滤单字符, 批量输入的不处理
        val length = end - start
        if (length > 1) return@InputFilter null
        for (i in start until end) {
            val char = source[i]
            when {
                // 检测空格, 开头不能输入空格
                dstart == 0 && char == ' ' && i == 0 -> {
                    return@InputFilter ""
                }

                // 后续只允许输入字母、数字、空格
                !char.isLetterOrDigit() && char != ' ' -> {
                    return@InputFilter ""
                }
            }
        }
        // 如果没有检测到回车字符，则返回 null，表示不进行任何过滤
        null
    }

    class NextStep(
        context: Context,
        val selected: StateFlow<Boolean>,
        private val money: String,
        private val listener: Listener
    ) {
        interface Listener {
            fun onNextClick(callback: () -> Unit)
            fun onProtocolClick()
        }

        val onNextClick = Bind.OnDebounceClick {
            nextEnabled.toFalse()
            listener.onNextClick {
                nextEnabled.toTrue()
            }
        }
        val nextEnabled = MutableStateFlow(true)

        val movementMethod: MovementMethod = LinkMovementMethodCompat.getInstance()
        val protocol = MutableStateFlow<CharSequence?>(null)

        val moneyText = MutableStateFlow<CharSequence?>(null)

        init {
            val part1String = context.getString(R.string.protocol_part1)
            val linkString = context.getString(R.string.protocol_part2_link)
            protocol.value = SpannableStringBuilder()
                .append(part1String)
                .append(linkString).apply {
                    setClickableSpan(
                        part1String.length,
                        part1String.length + linkString.length,
                        R.color.blue.toColor(context)
                    ) {
                        listener.onProtocolClick()
                    }
                }

            val moneyPart1 = context.getStringForLanguage(R.string.piece_next_tip_part1)
            val moneyPart2 = context.getStringForLanguage(R.string.piece_next_tip_part2)
            moneyText.value = SpannableStringBuilder()
                .append(moneyPart1)
                .append(money)
                .append(moneyPart2)
                .apply {
                    setSpan(
                        ForegroundColorSpan(R.color.red.toColor(context)),
                        moneyPart1.length,
                        moneyPart1.length + money.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
        }
    }

    @CallSuper
    override fun initializeSystemBar() {
        appBar.addServiceIcon {
            startServiceActivity(serviceFlag)
        }
    }

    @CallSuper
    override fun initializeContentView() {
        layoutRoot.adaptImeResizeChanged()
    }

    final override fun enableHideKeyboardWhenFocusChanged(): Boolean {
        return true
    }
}