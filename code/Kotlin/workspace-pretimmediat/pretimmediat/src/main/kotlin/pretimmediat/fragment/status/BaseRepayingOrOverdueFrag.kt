package pretimmediat.fragment.status

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ViewDataBinding
import coroutine.flow.launchIn
import inject.annotation.creator.Extra
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import logger.L
import pretimmediat.R
import pretimmediat.adapter.LoanPlanAdapter
import pretimmediat.databinding.FragHomeLoanPlanBinding
import pretimmediat.dialog.InstallmentRepaymentDialog
import pretimmediat.dialog.RepaymentDialog
import pretimmediat.dialog.Style2Dialog
import pretimmediat.dialog.Style3Dialog
import pretimmediat.ext.buildMixText
import pretimmediat.ext.openComplaintBrowser
import pretimmediat.ext.toast
import pretimmediat.ext.withLoading
import pretimmediat.ext.withNetworkError
import pretimmediat.fragment.base.databinding.BaseDBFrag
import pretimmediat.model.ApplicationSettings
import pretimmediat.model.product.LoanPlan
import pretimmediat.model.product.SingleProduct
import pretimmediat.viewmodel.RepayingOrOverdueViewModel
import vector.bindingadapter.bind.Bind
import vector.app.ext.bind.bindView
import vector.ext.copyToClipboard
import vector.app.os.dp
import vector.app.util.Res
import vector.app.util.toColor
import vector.widget.scrollable.decoration.Decoration

/**
 * 单产品(多产品二级页) 等待还款/逾期
 */
abstract class BaseRepayingOrOverdueFrag : BaseDBFrag<RepayingOrOverdueViewModel>() {

    companion object {
        private const val LOG_TAG = "RepayingOrOverdueFrag"
    }

    @Extra(true)
    lateinit var product: SingleProduct

    @Extra(true)
    lateinit var loanPlan: LoanPlan

    @Extra(true)
    lateinit var appSettings: ApplicationSettings

    val decoration = Decoration.linear {
        size = 14.dp.toPx(context)
    }
    val itemBinders = listOf(
        LoanPlanAdapter.Binder.ProductInfo {
            product.orderNo.copyToClipboard()
            toast(context, R.string.home_single_loan_plan_copy_order_success)
        },
        LoanPlanAdapter.Binder.PlanInfo(),
        LoanPlanAdapter.Binder.PlanSettledInfo(),
        LoanPlanAdapter.Binder.Complaint {
            openComplaintBrowser()
        },
        LoanPlanAdapter.Binder.Buttons(object : LoanPlanAdapter.Binder.Buttons.Listener {
            override fun onDelayClick() {
                viewModel.fetchDelay()
                    .withNetworkError(requireContext())
                    .withLoading(requireContext())
                    .catch { e ->
                        L.e(LOG_TAG, "onDelayClick", e)
                    }.onEach { pair ->
                        L.d(LOG_TAG, "onDelayClick, success, pair = $pair")
                        InstallmentRepaymentDialog(
                            requireContext(),
                            product.orderId,
                            pair.first,
                            pair.second
                        ).show()
                    }.launchIn(this@BaseRepayingOrOverdueFrag)
            }

            override fun onRepayClick() {
                viewModel.fetchRepay()
                    .withNetworkError(requireContext())
                    .withLoading(requireContext())
                    .catch { e ->
                        L.e(LOG_TAG, "onRepayClick", e)
                    }.onEach { channels ->
                        L.d(LOG_TAG, "onRepayClick, channels = $channels")
                        RepaymentDialog(requireContext(), product, loanPlan, channels).show()
                    }.launchIn(this@BaseRepayingOrOverdueFrag)
            }
        })
    )

    private val tvPrepayment by bindView<View>(R.id.tv_prepayment)

    val onPrepaymentClick = Bind.OnDebounceClick {
        showPrepaymentDialog(false)
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return FragHomeLoanPlanBinding.inflate(inflater).apply {
            owner = this@BaseRepayingOrOverdueFrag
            viewModel = this@BaseRepayingOrOverdueFrag.viewModel
        }
    }

    override fun initializeData() {
        super.initializeData()

        viewModel.initData(product, loanPlan, appSettings)
    }

    override fun initializeContentView() {
        when {
            appSettings.isRepayingRepaymentToggle() -> {
                showPrepaymentDialog(true)
            }

            appSettings.isRepayingComplaintAutoPopupToggle() -> {
                // 如果不需要提前还款弹窗, 才弹出投诉弹窗
                popupComplaintDialog()
            }
        }

        viewModel.prepaymentVisible.filterNotNull().onEach { visible ->
            if (visible) {
                tvPrepayment.startAnimation(Res.getAnim(R.anim.breath_infinite))
            } else {
                tvPrepayment.clearAnimation()
            }
        }.launchIn(this)
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.prepaymentVisible.value == true) {
            tvPrepayment.startAnimation(Res.getAnim(R.anim.breath_infinite))
        }
    }

    override fun onPause() {
        super.onPause()

        tvPrepayment.clearAnimation()
    }

    /**
     * 弹出投诉弹窗
     */
    private fun popupComplaintDialog() {
        viewModel.fetchComplaintText()
            .withNetworkError(requireContext())
            .withLoading(requireContext())
            .catch { e ->
                L.e(LOG_TAG, "onComplaint click", e)
            }.onEach {
                L.d(LOG_TAG, "popupComplaintDialog, complaint = $it")
                Style2Dialog.Builder(requireContext())
                    .content(it.repayAlertDesc)
                    .buttonLeft(R.string.cancel)
                    .buttonRight(R.string.complaint) {
                        openComplaintBrowser()
                    }
                    .build()
                    .show()
            }.launchIn(this)
    }

    private fun showPrepaymentDialog(shouldShowComplaint: Boolean) {
        viewModel.fetchPrepaymentDocument()
            .withNetworkError(requireContext())
            .withLoading(requireContext())
            .catch { e ->
                L.e(LOG_TAG, "onPrepaymentClick", e)
            }.onEach { document ->
                val content = buildMixText(
                    document.documents.joinToString("\n\n"),
                    document.map,
                    R.color.red.toColor(requireContext())
                )

                Style3Dialog.Builder(requireContext())
                    .title(R.string.prepayment_title)
                    .content(content)
                    .button(R.string.cancel)
                    .build()
                    .apply {
                        setOnDismissListener {
                            if (shouldShowComplaint && appSettings.isRepayingComplaintAutoPopupToggle()) {
                                popupComplaintDialog()
                            }
                        }
                        show()
                    }
            }.launchIn(this)
    }
}