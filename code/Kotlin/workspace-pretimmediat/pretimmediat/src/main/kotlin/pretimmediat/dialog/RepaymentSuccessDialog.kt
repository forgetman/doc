package pretimmediat.dialog

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import coroutine.flow.launchIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import pretimmediat.R
import pretimmediat.databinding.DialogRepaymentSuccessBinding
import pretimmediat.ext.countdownFlow
import vector.app.databinding.dialog.DBDialogEx
import vector.bindingadapter.bind.Bind
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT
import vector.app.util.toColor
import java.util.concurrent.TimeUnit

/**
 * 还款成功弹窗
 */
class RepaymentSuccessDialog(
    context: Context?,
    private val applyAmount: String?
) : DBDialogEx(context) {

    companion object {
        private const val COUNT_DOWN_COUNT = 10
    }

    val onClick = Bind.OnClick {
        // 重新贷款
        dismiss()
    }

    val onCloseClick = Bind.OnClick {
        dismiss()
    }

    val countdownText = MutableStateFlow<String?>(null)

    override val gravity: Int
        get() = Gravity.CENTER

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)

    val content2 = SpannableStringBuilder().apply {
        val part1 = "Votre montant est élevé à "
        val part2 = (applyAmount?.toInt()?.times(2))?.toString() ?: "0"
        val part3 = ". Bienvenue pour emprunter à nouveau !"
        append(part1)
        append(part2)
        append(part3)
        setSpan(
            ForegroundColorSpan(R.color.red.toColor(context)),
            part1.length,
            part1.length + part2.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }


    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return DialogRepaymentSuccessBinding.inflate(inflater).apply {
            owner = this@RepaymentSuccessDialog
        }
    }

    override fun initializeContentView() {
        countdownFlow(COUNT_DOWN_COUNT, 1, TimeUnit.SECONDS).onStart {
            countdownText.value = "${COUNT_DOWN_COUNT}s"
        }.onEach { second ->
            countdownText.value = "${second}s"
        }.onCompletion {
            dismiss()
        }.launchIn(this)
    }
}