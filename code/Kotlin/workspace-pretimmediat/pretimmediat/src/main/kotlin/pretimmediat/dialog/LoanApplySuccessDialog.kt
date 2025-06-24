package pretimmediat.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import coroutine.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import pretimmediat.R
import pretimmediat.ext.amountText
import pretimmediat.ext.countdownFlow
import pretimmediat.ext.dateText
import pretimmediat.ext.formatMoney
import pretimmediat.model.loan.InstallmentPlan
import vector.app.dialog.DialogEx
import vector.app.ext.bind.bindView
import vector.app.ext.inflate
import vector.ext.getStringForLanguage
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT
import java.util.concurrent.TimeUnit

/**
 * 贷款申请成功弹窗
 */
class LoanApplySuccessDialog(
    context: Context?,
    private val loanAmount: String?,
    private val amountReceive: String?,
    private val plans: List<InstallmentPlan>
) : DialogEx(context) {

    companion object {
        private const val COUNT_DOWN_COUNT = 10
    }

    private val layoutContainerPlan by bindView<LinearLayout>(R.id.layout_container_plan)
    private val ivClose by bindView<View>(R.id.iv_close)
    private val tvLoanAmount by bindView<TextView>(R.id.tv_loan_amount)
    private val tvAmountReceive by bindView<TextView>(R.id.tv_amount_receive)
    private val tvCountdown by bindView<TextView>(R.id.tv_countdown)

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)

    override fun createContentView(inflater: LayoutInflater): View {
        return inflater.inflate(R.layout.dialog_loan_apply_success)
    }

    @SuppressLint("SetTextI18n")
    override fun initializeContentView() {
        ivClose.setOnClickListener {
            dismiss()
        }

        tvLoanAmount.text = context.getStringForLanguage(R.string.xof_prefix) + loanAmount.formatMoney()
        tvAmountReceive.text = context.getStringForLanguage(R.string.xof_prefix) + amountReceive.formatMoney()

        val size = plans.size
        plans.forEachIndexed { index, plan ->
            val view =
                context.inflate(R.layout.layout_item_trial_calc_plan, layoutContainerPlan).apply {
                    findViewById<TextView>(R.id.tv_step)?.let { tv ->
                        val step = index + 1
                        tv.text = "$step/$size"
                    }
                    findViewById<TextView>(R.id.tv_date_title)?.let { tv ->
                        tv.text = plan.dateText(context)
                    }
                    findViewById<TextView>(R.id.tv_date)?.let { tv ->
                        tv.text = plan.repayDate
                    }
                    findViewById<TextView>(R.id.tv_amount_title)?.let { tv ->
                        tv.text = plan.amountText(context)
                    }
                    findViewById<TextView>(R.id.tv_amount)?.let { tv ->
                        tv.text = context.getStringForLanguage(R.string.xof_prefix) + plan.repayAmountText
                    }
                }
            layoutContainerPlan.addView(view)
        }

        countdownFlow(COUNT_DOWN_COUNT, 1, TimeUnit.SECONDS).onStart {
            tvCountdown.text = "${COUNT_DOWN_COUNT}s"
        }.onEach { second ->
            tvCountdown.text = "${second}s"
        }.onCompletion {
            dismiss()
        }.launchIn(this)
    }
}