package pretimmediat.viewmodel

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import pretimmediat.R
import pretimmediat.adapter.LoanPlanAdapter
import pretimmediat.ext.amountTitle
import pretimmediat.ext.dateTitle
import pretimmediat.ext.titleText
import pretimmediat.model.ApplicationSettings
import pretimmediat.model.loan.InstallmentPlan
import pretimmediat.model.product.LoanPlan
import pretimmediat.model.product.SingleProduct
import pretimmediat.repo.RepayingOrOverdueRepo
import vector.ext.getStringForLanguage
import vector.ext.isNotNullOrEmpty
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class RepayingOrOverdueViewModel @Inject constructor(
    private val repo: RepayingOrOverdueRepo,
    app: Application
) : BaseViewModel(app) {
    val data = MutableStateFlow<List<LoanPlanAdapter.Data>>(emptyList())
    val prepaymentVisible = MutableStateFlow<Boolean?>(null)

    private lateinit var product: SingleProduct
    private lateinit var loanPlan: LoanPlan
    private lateinit var appSettings: ApplicationSettings

    fun initData(product: SingleProduct, plan: LoanPlan, settings: ApplicationSettings) {
        this.product = product
        this.loanPlan = plan
        this.appSettings = settings

        data.value = buildList {
            add(
                LoanPlanAdapter.ProductInfo(
                    product,
                    if (product.odStatus == SingleProduct.OD_STATUS_OVERDUE) {
                        applicationContext.getStringForLanguage(R.string.order_overdue_title2,)
                    } else applicationContext.getStringForLanguage(R.string.home_single_loan_plan_repaying_title)
                )
            )

            val size = loanPlan.plans.size
            addAll(loanPlan.plans.sortedBy { p ->
                // 排序一下, STATUS_UN_SETTLED 的放最后面
                p.settleStatus != InstallmentPlan.STATUS_UN_SETTLED
            }.mapIndexed { index, p ->
                when (p.settleStatus) {
                    InstallmentPlan.STATUS_SETTLED -> {
                        LoanPlanAdapter.PlanSettledInfo(
                            index + 1,
                            size,
                            p.titleText(applicationContext),
                            p.dateTitle(applicationContext),
                            p.repayDate,
                            p.amountTitle(applicationContext),
                            applicationContext.getStringForLanguage(R.string.xof_prefix) + p.loanAmountText,
                            p.lateFeeNullableText,
                            p.deductionFeeNullableText,
                        )
                    }

                    else -> {
                        LoanPlanAdapter.PlanInfo(
                            index + 1,
                            size,
                            p.titleText(applicationContext),
                            p.dateTitle(applicationContext),
                            p.repayDate,
                            p.amountTitle(applicationContext),
                            applicationContext.getStringForLanguage(R.string.xof_prefix) + p.loanAmountText,
                            p.lateFeeNullableText,
                            p.deductionFeeNullableText,
                        )
                    }
                }
            })

            if (appSettings.repayingComplaintToggle == "1") {
                add(LoanPlanAdapter.Complaint())
            }

            val extDuration = product.extendDuration
            if (product.extendFlag == "1" && extDuration.isNotNullOrEmpty()) {
                val duration = applicationContext.getStringForLanguage(
                    R.string.repaying_delay_by_days,
                    extDuration
                )
                add(LoanPlanAdapter.Buttons(duration))
            } else {
                add(LoanPlanAdapter.Buttons(null))
            }
        }

        prepaymentVisible.value = appSettings.repayingRepaymentToggle == "1"
    }

    // 调repayDetailInstallment 传0304，getPayChannelList0304 成功弹窗
    fun fetchDelay() = repo.fetchLoanPlan(userId, appSsid, product.orderId).flatMapConcat { plan ->
        repo.fetchPayChannels(userId, appSsid, "0304").map { Pair(plan, it) }
    }.flowOn(Dispatchers.IO)

    fun fetchRepay() = repo.fetchPayChannels(userId, appSsid, "03")

    fun fetchComplaintText() = repo.fetchComplaintText(userId, appSsid)

    fun fetchPrepaymentDocument() = repo.fetchPrepaymentDocument(userId, appSsid, product.orderId)
}