package pretimmediat.viewmodel

import android.app.Application
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.MovementMethod
import android.text.style.ForegroundColorSpan
import androidx.core.text.method.LinkMovementMethodCompat
import androidx.lifecycle.viewModelScope
import com.appsflyer.AFInAppEventType
import com.facebook.appevents.AppEventsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import logger.L
import pretimmediat.R
import pretimmediat.ext.countdownFlow
import pretimmediat.ext.formatMoney
import pretimmediat.ext.setClickableSpan
import pretimmediat.model.loan.Installment
import pretimmediat.model.loan.LoanApplyResult
import pretimmediat.model.loan.LoanProducts
import pretimmediat.model.loan.PerProduct
import pretimmediat.model.loan.toInstallment
import pretimmediat.repo.LoanRepo
import pretimmediat.stats.Stats
import sugar.ext.throwIfNull
import sugar.kotlin.time.secondsInMinute
import vector.ext.getStringForLanguage
import vector.ext.isNotNullOrEmpty
import vector.app.util.toColor
import java.math.BigDecimal
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class TrialCalcViewModel @Inject constructor(
    private val repo: LoanRepo,
    app: Application
) : BaseViewModel(app) {

    companion object {
        private const val LOG_TAG = "TrialCalcViewModel"
        private const val COUNT_DOWN_INTERVAL = 1L
        private const val CAPTCHA_COUNT_DOWN_MAX_COUNT = 10 * 60
    }

    val product = MutableStateFlow<LoanProducts?>(null)
    val perProduct = MutableStateFlow<PerProduct?>(null)
    val contactUrl = MutableStateFlow<String?>(null)

    var amountSelections: List<String> = emptyList()

    val loanAmount = MutableStateFlow<String?>(null)

    val installments = MutableStateFlow<List<Installment>>(emptyList())
    val selectedInstallment = MutableStateFlow<Installment?>(null)
    val selectedInstallmentIndex = MutableStateFlow<Int>(0)

    val lowestRepay = MutableStateFlow<String?>(null)

    val protocolToggleChecked = MutableStateFlow(true)

    val countDownText = MutableStateFlow<CharSequence?>(null)

    val protocol = MutableStateFlow<CharSequence?>(null)
    val movementMethod: MovementMethod = LinkMovementMethodCompat.getInstance()
    val launchProtocol = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override fun onCreate() {
        combine(
            product.filterNotNull().map { it.proDetailList }.filterNotNull(),
            selectedInstallmentIndex
        ) { details, index ->
            val amountItem = details.getOrNull(index) ?: return@combine
            amountSelections = calcLoanAmounts(
                amountItem.maxCAmount,
                amountItem.minCAmount,
                amountItem.stepAmount
            )
            L.d(LOG_TAG, "onCreate, amountSelections = $amountSelections")

            // 每次切换都拿第一个(max)
            loanAmount.value = amountSelections.firstOrNull()
        }.launchIn(viewModelScope)

        product.filterNotNull().map { it.proDetailList }.filterNotNull()
            .onEach { details ->
                val installments = buildList {
                    val realInstallment = details.map { detail ->
                        detail.toInstallment(true)
                    }.sortedBy { it.period }
                    addAll(realInstallment)

                    val lastDetail = details.last()
                    val fakeInstallment1 = lastDetail.let { detail ->
                        val period = detail.periodDuration + 1
                        val day = period * detail.duration
                        Installment("-1", period, day, false)
                    }
                    add(fakeInstallment1)
                    val fakeInstallment2 = lastDetail.let { detail ->
                        val period = detail.periodDuration + 2
                        val day = period * detail.duration
                        Installment("-2", period, day, false)
                    }
                    add(fakeInstallment2)
                }
                selectedInstallment.value = installments.first()
                this.installments.value = installments

                countdownTimer().catch { e ->
                    L.e(LOG_TAG, "countdownTimer", e)
                }.launchIn(viewModelScope)
            }.launchIn(viewModelScope)

        protocol.value = SpannableStringBuilder().apply {
            val part1 =
                applicationContext.getStringForLanguage(R.string.trial_calc_loan_protocol_part1)
            val part2 =
                applicationContext.getStringForLanguage(R.string.trial_calc_loan_protocol_part2)
            append(part1, part2)
            setClickableSpan(
                part1.length,
                length,
                R.color.blue.toColor(applicationContext)
            ) {
                launchProtocol.tryEmit(Unit)
            }
        }
    }

    fun fetchInfo(products: LoanProducts, amount: String, installment: Installment) =
        repo.fetchPerProduct(
            userId,
            appSsid,
            products.productId,
            installment.id,
            amount
        ).zip(
            repo.fetchContractInfo(
                userId,
                appSsid,
                installment.id,
                amount
            )
        ) { perProduct, contractInfos ->
            L.d(LOG_TAG, "fetchPerProduct = $perProduct")
            L.d(LOG_TAG, "contacts = $contractInfos")
            this.perProduct.value = perProduct
            lowestRepay.value = getLowestRepay(perProduct, false)

            contactUrl.value = contractInfos.firstOrNull()?.url
        }

    fun fetchProduct() = repo.fetchProduct(userId, appSsid).onEach {
        L.d(LOG_TAG, "fetchProduct = $it")
        product.value = it
    }

    fun apply(): Flow<LoanApplyResult> {
        if (contactUrl.value.isNotNullOrEmpty() && !protocolToggleChecked.value) {
            return flow {
                throw IllegalStateException(applicationContext.getStringForLanguage(R.string.trial_calc_protocol_tips))
            }
        }

        return repo.apply(
            userId,
            appSsid,
            product.value?.productId.throwIfNull("product id is null"),
            selectedInstallment.value?.id.throwIfNull("installment id is null"),
            loanAmount.value.throwIfNull("loan amount is null")
        ).onEach { result ->
            if (result.isFirstApply()) {
                // 主产品首贷申请成功
                Stats.faceBook.onEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT)
                Stats.firebase.onEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT)
                Stats.flyer.onEvent(AFInAppEventType.INITIATED_CHECKOUT)
            }
        }
    }

    /**
     * 计算贷款金额可用选项
     */
    private fun calcLoanAmounts(maxCAmount: Int, minCAmount: Int, stepAmount: Int): List<String> {
        val amountList = mutableListOf<BigDecimal>()

        val max = BigDecimal(maxCAmount)
        var min = BigDecimal(minCAmount)
        val inc = BigDecimal(stepAmount)
        if (max.compareTo(min) != 0) {
            while (min < max) {
                amountList.add(min)
                min = min.add(inc)
            }
        }

        amountList.add(max)
        amountList.reverse() // 展示的时候amountList 倒序一下。用户优先选择最大金额
        return amountList.map { it.toString() }
    }

    /**
     * 获取最低还款文案
     * “复贷可解锁更多分期数，低至xxx/期还款”。xxx/期=真最低还$xxx/期-20%真最低还$xxx/期。比如真最低每期还款100，假xxx/期=100-100*20%=低至80/期还款
     * xxx=minRepayAmount-minRepayAmount * 0.2 (需要四舍五入)
     */
    fun getLowestRepay(p: PerProduct?, fake: Boolean): String {
        val minRepayAmount = p?.resultMapPlanList?.minOf { it.repayAmount } ?: 0f
        val decimal = minRepayAmount.times(if (fake) 0.8f else 1f).roundToInt().formatMoney()
        L.d(LOG_TAG, "initializeContentView, minRepayAmount = $decimal")
        return applicationContext.getStringForLanguage(
            R.string.trial_calc_number_of_instalment_lowest,
            decimal
        )
    }

    private fun countdownTimer(): Flow<Int> = countdownFlow(
        CAPTCHA_COUNT_DOWN_MAX_COUNT,
        COUNT_DOWN_INTERVAL,
        TimeUnit.SECONDS
    ).onEach { second ->
        // 文字变色
        countDownText.value = SpannableStringBuilder().apply {
            val part1 =
                applicationContext.getStringForLanguage(R.string.trial_calc_loan_passing_rate_part1)
            val duration = second.seconds
            val part2 = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                duration.inWholeMinutes, duration.secondsInMinute
            )
            val part3 =
                applicationContext.getStringForLanguage(R.string.trial_calc_loan_passing_rate_part3)
            append(part1, part2, part3)
            val blueSpan = ForegroundColorSpan(R.color.blue.toColor(applicationContext))
            setSpan(
                blueSpan,
                part1.length,
                part1.length + part2.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }.flowOn(Dispatchers.Main)
}