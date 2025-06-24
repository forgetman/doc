package pretimmediat.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import coroutine.flow.launchIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import logger.L
import pretimmediat.R
import pretimmediat.activity.WebViewActivityCreator
import pretimmediat.databinding.DialogInstallmentRepaymentBinding
import pretimmediat.ext.formatMoney
import pretimmediat.ext.setInstallmentRepaymentPrepareInfo
import pretimmediat.ext.toast
import pretimmediat.ext.withLoading
import pretimmediat.ext.withNetworkError
import pretimmediat.model.product.LoanPlan
import pretimmediat.model.product.PayChannel
import pretimmediat.network.api.ProductApi
import pretimmediat.network.createApi
import vector.app.databinding.dialog.DBDialogEx
import vector.bindingadapter.bind.Bind
import vector.app.ext.bind.bindView
import vector.app.ext.inflate
import vector.app.ext.view.setOnDebounceClickListener
import vector.app.ext.view.setTextByFindId
import vector.ext.copyToClipboard
import vector.ext.getStringForLanguage
import vector.ext.inflate
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT
import vector.util.intent.IntentAction

/**
 * 展期还款弹窗
 */
class InstallmentRepaymentDialog(
    context: Context?,
    private val orderId: String,
    val plan: LoanPlan,
    private val channels: List<PayChannel>
) : DBDialogEx(context) {

    companion object {
        private const val LOG_TAG = "InstallmentRepaymentDialog"
    }

    val onCopyClick = Bind.OnClick {
        plan.orderNo.copyToClipboard()
        toast(context, R.string.home_single_loan_plan_copy_order_success)
    }

    val onCloseClick = Bind.OnClick {
        dismiss()
    }

    val content = MutableStateFlow<CharSequence?>(null)
    val item = plan.plans.first()

    private val layoutChannels by bindView<LinearLayout>(R.id.layout_channels)

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)


    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return DialogInstallmentRepaymentBinding.inflate(inflater).apply {
            owner = this@InstallmentRepaymentDialog
            item = this@InstallmentRepaymentDialog.item
        }
    }

    override fun initializeContentView() {
        content.value = context.getStringForLanguage(
            R.string.installment_payment_content,
            item.repayAmount.formatMoney(),
            item.extendDuration
        )

        channels.forEach { channel ->
            val view = context.inflate(R.layout.layout_item_installment_channel, layoutChannels)
            view.setTextByFindId(R.id.tv_title) { channel.channelName }
            val tvRepayment = view.findViewById<TextView>(R.id.tv_repayment)
            tvRepayment.setOnDebounceClickListener {
                createApi<ProductApi>().payInfoInstallment(
                    orderId,
                    channel.channelCode,
                    item.repayPlanId,
                    "0304"
                ).flowOn(Dispatchers.IO)
                    .withNetworkError(context)
                    .withLoading(context)
                    .catch { e ->
                        L.e(LOG_TAG, "payInfoInstallment", e)
                    }.onEach { info ->
                        if (info.shouldUseBrowser) {
                            IntentAction.browser().url(info.url).launch()
                        } else {
                            WebViewActivityCreator.create().url(info.url).start(context)
                        }
                        setInstallmentRepaymentPrepareInfo(orderId)
                        dismiss()
                    }.launchIn(this)
            }
            layoutChannels.addView(view)
        }
    }
}