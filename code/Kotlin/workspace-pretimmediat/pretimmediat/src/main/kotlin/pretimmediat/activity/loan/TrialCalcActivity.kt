package pretimmediat.activity.loan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.core.view.size
import androidx.databinding.ViewDataBinding
import coroutine.flow.launchIn
import dagger.hilt.android.AndroidEntryPoint
import eth.model.EthException
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import logger.L
import pretimmediat.R
import pretimmediat.activity.MainActivity
import pretimmediat.activity.MainActivityCreator
import pretimmediat.databinding.ActivityTrialCalcBinding
import pretimmediat.def.Constants
import pretimmediat.dialog.AmountPopupWindow
import pretimmediat.dialog.LoanApplySuccessDialog
import pretimmediat.dialog.Style1Dialog
import pretimmediat.ext.addBackIcon
import pretimmediat.ext.addServiceIcon
import pretimmediat.ext.amountText
import pretimmediat.ext.dateText
import pretimmediat.ext.startServiceActivity
import pretimmediat.ext.startWebViewActivity
import pretimmediat.ext.toast
import pretimmediat.ext.withNetworkError
import pretimmediat.ext.withPieceLoading
import pretimmediat.ext.withViewState
import pretimmediat.manager.LocationManager
import pretimmediat.stats.Stats
import pretimmediat.viewmodel.TrialCalcViewModel
import vector.app.databinding.activity.DBActivityEx
import vector.app.ext.bind.bindView
import vector.app.ext.inflate
import vector.app.ext.view.setTextByFindId
import vector.app.os.dp
import vector.app.util.Screen
import vector.bindingadapter.GridLayoutSet
import vector.bindingadapter.bind.Bind
import vector.ext.getStringForLanguage

/**
 * 金额试算页
 */
@AndroidEntryPoint
@Creator(forResult = true)
class TrialCalcActivity : DBActivityEx<TrialCalcViewModel>() {

    companion object {
        private const val LOG_TAG = "TrialCalcActivity"
    }

    @Extra(true)
    var userId: String? = null

    @Extra(true)
    var appSsid: String? = null

    val gridLayoutSet = MutableStateFlow<List<GridLayoutSet>?>(null)

    private val gridLayouts = mutableListOf<View>()
    private val layoutContainerPlan by bindView<LinearLayout>(R.id.layout_container_plan)
    private val layoutContainerDynamic by bindView<LinearLayout>(R.id.layout_container_dynamic)

    val onLoanAmountClick = Bind.OnDebounceClick {
        if (viewModel.amountSelections.isEmpty()) {
            return@OnDebounceClick
        }
        val popupWindow = AmountPopupWindow(this, viewModel.amountSelections) { _, s ->
            viewModel.loanAmount.value = s
        }
        popupWindow.showAsDropDown(it)
    }

    val onApplyClick = Bind.OnDebounceClick {
        viewModel.apply()
            .withPieceLoading(this)
            .withNetworkError(this)
            .onEach {
                val dialog = LoanApplySuccessDialog(
                    this,
                    viewModel.loanAmount.value,
                    viewModel.perProduct.value?.disbursalAmount,
                    viewModel.perProduct.value?.resultMapPlanList ?: emptyList()
                )
                dialog.setOnDismissListener {
                    setResult(RESULT_OK)
                    finish()
                }
                dialog.show()
            }.catch { e ->
                L.e(LOG_TAG, "apply", e)
                if (e is EthException && e.code == "5001") {
                    // 弹窗
                    Style1Dialog.Builder(this@TrialCalcActivity)
                        .content(R.string.trial_calc_dialog_overdue_content)
                        .button(R.string.repaying) {
                            // 返回首页order tab
                            MainActivityCreator.create()
                                .requiredTabIndex(MainActivity.TAB_ORDER)
                                .start(this@TrialCalcActivity)
                        }
                        .build()
                        .show()
                }
            }.launchIn(this)
    }

    private var infoRequireJob: Job? = null

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivityTrialCalcBinding.inflate(inflater).apply {
            owner = this@TrialCalcActivity
            viewModel = this@TrialCalcActivity.viewModel
        }
    }

    override fun initializeData() {
        viewModel.init(userId, appSsid)
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(R.string.trial_calc_title) {
            finish()
        }

        appBar.addServiceIcon {
            startServiceActivity(Constants.ServiceFlag.TRIAL_CALC)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initializeContentView() {
        viewModel.fetchProduct()
            .withViewState(this)
            .withNetworkError(this)
            .catch { e ->
                L.e(LOG_TAG, "fetchProduct", e)
            }.launchIn(this)

        viewModel.installments.onEach { installments ->
            val width = (Screen.width - 19.dp.toPx(this) * 2 - 9.dp.toPx(this)) / 2
            gridLayoutSet.value = buildList {
                installments.forEachIndexed { index, installment ->
                    val set = GridLayoutSet().apply {
                        val view = when (installment.available) {
                            true -> inflate(R.layout.layout_item_trial_calc_installment)
                            false -> inflate(R.layout.layout_item_trial_calc_installment_lock)
                        }
                        when (installment.available) {
                            true -> view as TextView
                            false -> view.findViewById<TextView>(R.id.tv_plan)
                        }?.let { tv ->
                            val text = getString(
                                R.string.trial_calc_number_of_instalment_content,
                                installment.period,
                                installment.day
                            )
                            tv.text = text
                        }
                        view.isSelected = index == 0

                        bottomMargin = 5.dp.toPx(this@TrialCalcActivity)
                        layout = view
                        layoutType = GridLayoutSet.LayoutType.exact(width)

                        onClick = {
                            if (installment.available) {
                                gridLayouts.forEach { it.isSelected = false }
                                view.isSelected = true

                                viewModel.selectedInstallment.value = installment
                                viewModel.selectedInstallmentIndex.value = index
                            } else {
                                toast(
                                    this@TrialCalcActivity,
                                    viewModel.getLowestRepay(viewModel.perProduct.value, true)
                                )
                            }
                        }

                        gridLayouts.add(view)
                    }

                    if (index % 2 != 0) {
                        // 每两个item之间加间隔
                        add(GridLayoutSet().apply {
                            layout = Space(this@TrialCalcActivity)
                            layoutType =
                                GridLayoutSet.LayoutType.exact(9.dp.toPx(this@TrialCalcActivity))
                        })
                        add(set)
                    } else {
                        add(set)
                    }
                }
            }
        }.launchIn(this)

        combine(
            viewModel.product.filterNotNull(),
            viewModel.loanAmount.filterNotNull(),
            viewModel.selectedInstallment.filterNotNull()
        ) { product, amount, installment ->
            L.d(
                LOG_TAG,
                "combine, product = $product, amount = $amount, installment = $installment"
            )
            infoRequireJob?.cancel()
            viewModel.fetchInfo(product, amount, installment)
        }.onEach { flow ->
            infoRequireJob = flow.withNetworkError(this)
                .withPieceLoading(this)
                .catch { e ->
                    L.e(LOG_TAG, "fetchInfo", e)
                }.launchIn(this)
        }.launchIn(this)


        viewModel.perProduct.filterNotNull().map { it.ext }.filterNotNull().onEach {
            layoutContainerDynamic.removeAllViews()
            it.forEach { dynamic ->
                val view = inflate(R.layout.layout_item_trial_calc_dynamic, layoutContainerDynamic)
                    .apply {
                        setTextByFindId(R.id.tv_amount_dynamic) { dynamic.name }
                        setTextByFindId(R.id.tv_amount_dynamic_value) { getStringForLanguage(R.string.xof_prefix) + dynamic.amount }
                    }
                layoutContainerDynamic.addView(view)
            }
        }.launchIn(this)

        viewModel.perProduct.filterNotNull().map { it.resultMapPlanList }.filterNotNull()
            .onEach { plans ->
                layoutContainerPlan.removeViews(1, layoutContainerPlan.size - 1)
                val size = plans.size
                plans.forEachIndexed { index, plan ->
                    val view = inflate(R.layout.layout_item_trial_calc_plan, layoutContainerPlan)
                        .apply {
                            setTextByFindId(R.id.tv_step) {
                                val step = index + 1
                                "$step/$size"
                            }
                            setTextByFindId(R.id.tv_date_title) { plan.dateText(this@TrialCalcActivity) }
                            setTextByFindId(R.id.tv_date) { plan.repayDate }
                            setTextByFindId(R.id.tv_amount_title) { plan.amountText(this@TrialCalcActivity) }
                            setTextByFindId(R.id.tv_amount) { getStringForLanguage(R.string.xof_prefix) + plan.repayAmountText }
                        }
                    layoutContainerPlan.addView(view)
                }
            }.launchIn(this)

        viewModel.launchProtocol.onEach {
            startWebViewActivity(
                R.string.trial_calc_loan_protocol_part2,
                viewModel.contactUrl.value ?: return@onEach
            )
        }.launchIn(this)

        LocationManager.getInstance(this).update(this) {
            Stats.public.onEvent("ACCESS_LOCATION_LOAN", userId, appSsid)
        }
    }

    override fun onRetryClick() {
        viewModel.fetchProduct()
            .withViewState(this)
            .withNetworkError(this)
            .catch { e ->
                L.e(LOG_TAG, "fetchProduct", e)
            }.launchIn(this)
    }
}