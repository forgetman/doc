package pretimmediat.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import coroutine.flow.launchIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import logger.L
import pretimmediat.R
import pretimmediat.activity.WebViewActivityCreator
import pretimmediat.ext.amountTitle
import pretimmediat.ext.dateTitle
import pretimmediat.ext.formatMoney
import pretimmediat.ext.setRepaymentPrepareInfo
import pretimmediat.ext.titleText
import pretimmediat.ext.withLoading
import pretimmediat.ext.withNetworkError
import pretimmediat.model.loan.InstallmentPlan
import pretimmediat.model.product.LoanPlan
import pretimmediat.model.product.PayChannel
import pretimmediat.model.product.SingleProduct
import pretimmediat.network.api.ProductApi
import pretimmediat.network.createApi
import vector.app.dialog.DialogEx
import vector.app.ext.bind.bindView
import vector.app.ext.inflate
import vector.app.ext.view.setOnDebounceClickListener
import vector.app.ext.view.setTextByFindId
import vector.ext.getStringForLanguage
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT
import vector.util.intent.IntentAction

/**
 * 还款弹窗
 */
class RepaymentDialog(
    context: Context?,
    private val product: SingleProduct,
    private val loanPlan: LoanPlan,
    private val channels: List<PayChannel>
) : DialogEx(context) {

    companion object {
        private const val LOG_TAG = "RepaymentDialog"
    }

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)

    private val layoutContainer by bindView<LinearLayout>(R.id.layout_container)
    private val ivClose by bindView<View>(R.id.iv_close)
    private val checkBoxAll by bindView<ImageView>(R.id.checkbox_all)

    private val ivs = mutableMapOf<Int, ImageView>()
    private val tvRepayments = mutableListOf<TextView>()

    override fun createContentView(inflater: LayoutInflater): View {
        return inflater.inflate(R.layout.dialog_repayment)
    }

    override fun initializeContentView() {
        ivClose.setOnClickListener {
            dismiss()
        }

        val planSize = loanPlan.plans.size
        loanPlan.plans.filter { it.settleStatus == InstallmentPlan.STATUS_UN_SETTLED }
            .forEachIndexed { index, plan ->
                val view = context.inflate(R.layout.layout_item_repayment_plan, layoutContainer)
                view.findViewById<ImageView>(R.id.checkbox)?.let { iv ->
                    iv.tag = plan.loanAmount
                    if (index == 0) {
                        // 第一个不能取消
                        iv.isSelected = true
                        iv.isEnabled = false
                    } else {
                        if (plan.selectMark == "1") {
                            iv.isSelected = true
                        }

                        // 按照顺序, 选中一个index后, 其他的需要计算, 小于index的都要选中; 如果取消一个index, 大于index的都要取消
                        iv.setOnDebounceClickListener {
                            val isSelected = !iv.isSelected
                            iv.isSelected = isSelected
                            if (isSelected) {
                                ivs.forEach { (i, iv) ->
                                    if (i < index) {
                                        iv.isSelected = true
                                    }
                                }
                            } else {
                                ivs.forEach { (i, iv) ->
                                    if (i > index) {
                                        iv.isSelected = false
                                    }
                                }
                            }

                            checkCheckBoxAllState()
                            // 底部的总还款金额也要通知修改
                            modifyTotalAmount()
                        }
                    }

                    ivs.put(index, iv)
                }

                // 如果只有一期, 不能取消
                if (planSize <= 1) {
                    checkBoxAll.isSelected = true
                    checkBoxAll.isEnabled = false
                } else {
                    checkBoxAll.setOnDebounceClickListener {
                        val isSelected = !checkBoxAll.isSelected
                        checkBoxAll.isSelected = isSelected
                        ivs.forEach { (index, value) ->
                            if (index != 0) {
                                // 第一个不能处理
                                if (value.isSelected != isSelected) {
                                    value.isSelected = isSelected
                                }
                            }
                        }
                        modifyTotalAmount()
                    }
                }

                view.setTextByFindId(R.id.tv_step) { "${index + 1}/$planSize" }
                view.setTextByFindId(R.id.tv_title) { plan.titleText(context) }
                view.setTextByFindId(R.id.tv_date_title) { plan.dateTitle(context) }
                view.setTextByFindId(R.id.tv_date) { plan.repayDate }
                view.setTextByFindId(R.id.tv_amount_title) { plan.amountTitle(context) }
                view.setTextByFindId(R.id.tv_amount) { context.getStringForLanguage(R.string.xof_prefix) + plan.loanAmount.formatMoney() }

                layoutContainer.addView(view)
            }

        channels.forEach { channel ->
            val view = context.inflate(R.layout.layout_item_repayment_channel, layoutContainer)
            view.setTextByFindId(R.id.tv_title) { channel.channelName }
            val tvRepayment = view.findViewById<TextView>(R.id.tv_repayment)
            tvRepayment.setOnDebounceClickListener {
                val ids: List<String> = ivs.filter { it.value.isSelected }.keys.map { index ->
                    val id = loanPlan.plans[index].repayPlanId
                    L.d(LOG_TAG, "payInfoInstallment, index = $index, id = $id")
                    id
                }
                if (ids.isEmpty()) return@setOnDebounceClickListener

                L.d(LOG_TAG, "ids = $ids")

                createApi<ProductApi>().payInfoInstallment(
                    product.orderId,
                    channel.channelCode,
                    if (ids.size == 1) ids.first() else ids.joinToString(","),
                    "03"
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
                        setRepaymentPrepareInfo(product.orderId)
                        dismiss()
                    }.launchIn(this)
            }
            tvRepayments.add(tvRepayment)
            layoutContainer.addView(view)
        }

        modifyTotalAmount()

        if (planSize > 1) {
            checkCheckBoxAllState()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun modifyTotalAmount() {
        val totalAmount = ivs.values.filter { it.isSelected }.sumOf { it.tag.toString().toDouble() }
        tvRepayments.forEach {
            it.text = context.getStringForLanguage(R.string.repaying) +
                "\n" +
                context.getStringForLanguage(R.string.xof_prefix) +
                totalAmount.formatMoney()
        }
    }

    // 检查checkBoxAll的状态
    fun checkCheckBoxAllState() {
        // 如果全部的checkbox都选中了, checkBoxAll也要选中状态
        checkBoxAll.isSelected = ivs.all { it.value.isSelected }
    }
}