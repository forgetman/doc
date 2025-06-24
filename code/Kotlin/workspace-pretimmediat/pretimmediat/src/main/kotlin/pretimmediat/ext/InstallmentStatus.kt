@file:Suppress("OPT_IN_USAGE")

package pretimmediat.ext

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import logger.L
import pretimmediat.R
import pretimmediat.dialog.Style1Dialog
import pretimmediat.fragment.status.InReviewFragCreator
import pretimmediat.fragment.status.OverdueFragCreator
import pretimmediat.fragment.status.PayFailedFragCreator
import pretimmediat.fragment.status.ProcessingFrag
import pretimmediat.fragment.status.RejectFragCreator
import pretimmediat.fragment.status.RepayingFragCreator
import pretimmediat.manager.AccountManager
import pretimmediat.model.product.SingleProduct
import pretimmediat.network.ParamsValue
import pretimmediat.network.api.GlobalApi
import pretimmediat.network.api.ProductApi
import pretimmediat.network.createApi
import sugar.ext.NoArgBlock
import vector.ext.getStringForLanguage
import kotlin.coroutines.resume

private const val LOG_TAG = "InstallmentStatus"

fun interface InstallmentStatusCallback {
    fun onStatusChanged(status: Int): Fragment?
}

fun installmentStatusToFragmentFlow(
    context: Context?,
    userId: String?,
    appSsid: String?,
    callback: InstallmentStatusCallback
) = createApi<ProductApi>().singleInstallment(userId, appSsid)
    .flowOn(Dispatchers.IO)
    .flatMapConcat { product ->
        // 判断是否要弹出展期还款弹窗
        maybeShowInstallmentRepaymentSuccess(context, product.repaymentDate, product.orderId)
        // 判断是否要弹出还款成功
        maybeShowRepaymentSuccess(context, product.orderId, product.odStatus, product.applyAmt)

        when (product.odStatus) {
            SingleProduct.OD_STATUS_OVERDUE, SingleProduct.OD_STATUS_REPAYING -> {
                // 还款和逾期
                createApi<ProductApi>().checkLoanPlan(
                    AccountManager.account,
                    ParamsValue.CLIENT_ID,
                    product.orderId,
                    "03"
                ).flatMapConcat { plan ->
                    L.d(LOG_TAG, "singleInstallmentToFragments, plan = $plan")
                    createApi<GlobalApi>().appSetting().map { Pair(plan, it) }
                }
                    .flowOn(Dispatchers.IO)
                    .flatMapConcat { pair ->
                        var f = callback.onStatusChanged(product.odStatus)
                        if (f == null) {
                            if (product.odStatus == SingleProduct.OD_STATUS_OVERDUE) {
                                f = OverdueFragCreator.create()
                                    .product(product)
                                    .loanPlan(pair.first)
                                    .appSettings(pair.second)
                                    .userId(AccountManager.account)
                                    .appSsid(ParamsValue.CLIENT_ID)
                                    .get()
                            } else {
                                f = RepayingFragCreator.create()
                                    .product(product)
                                    .loanPlan(pair.first)
                                    .appSettings(pair.second)
                                    .userId(AccountManager.account)
                                    .appSsid(ParamsValue.CLIENT_ID)
                                    .get()
                            }
                        }
                        fragmentFlow(f)
                    }.flowOn(Dispatchers.Main)
            }

            SingleProduct.OD_STATUS_IN_REVIEW -> {
                when (product.loanStatus) {
                    SingleProduct.STATUS_FAILED -> {
                        // 失败
                        fragmentFlow(
                            callback.onStatusChanged(product.odStatus)
                                ?: PayFailedFragCreator.create(product.orderId).get()
                        )
                    }

                    SingleProduct.STATUS_PROCESSING -> {
                        // 放款中
                        fragmentFlow(
                            callback.onStatusChanged(product.odStatus) ?: ProcessingFrag()
                        )
                    }

                    else -> {
                        // 审核中
                        fragmentFlow(
                            callback.onStatusChanged(product.odStatus)
                                ?: InReviewFragCreator.create()
                                    .userId(userId)
                                    .appSsid(appSsid)
                                    .get()
                        )
                    }
                }
            }

            SingleProduct.OD_STATUS_REJECT -> {
                // 拒绝
                fragmentFlow(
                    callback.onStatusChanged(product.odStatus)
                        ?: RejectFragCreator.create(product.reapplyDate).get()
                )
            }

            else -> {
                // 没有贷过款或者贷款都已经完成
                fragmentFlow(callback.onStatusChanged(product.odStatus))
            }
        }
    }.flowOn(Dispatchers.Main)
    .withNetworkError(context)
    .optimizeLoading()
    .catch { e ->
        L.e(LOG_TAG, "singleInstallmentToFragments", e)
    }

fun fragmentFlow(frag: Fragment?) = flow { emit(frag) }.flowOn(Dispatchers.Main)

private var keepOrderId: String? = null
fun setInstallmentRepaymentPrepareInfo(orderId: String?) {
    keepOrderId = orderId
}

suspend fun maybeShowInstallmentRepaymentSuccess(
    context: Context?,
    repaymentDate: String,
    newOrderId: String?
) {
    suspendCancellableCoroutine { cont ->
        if (newOrderId != null && keepOrderId != null && newOrderId != keepOrderId) {
            // 判断只要orderId不一样就认为还款成功
            // 还款成功
            showInstallmentRepaymentSuccessDialog(context, repaymentDate) {
                cont.resume(Unit)
            }
        } else {
            cont.resume(Unit)
        }
    }
    keepOrderId = null
}

fun showInstallmentRepaymentSuccessDialog(
    context: Context?,
    repaymentDate: String,
    dismissCallback: NoArgBlock? = null
) {
    val content = SpannableStringBuilder()
        .append(context?.getStringForLanguage(R.string.installment_payment_content, repaymentDate))
        .apply {
            setSpan(
                StyleSpan(Typeface.BOLD),
                length - repaymentDate.length,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    Style1Dialog.Builder(context)
        .icon(R.drawable.dialog_ic_success)
        .content(content)
        .button(R.string.back)
        .build()
        .apply {
            setOnDismissListener {
                dismissCallback?.invoke()
            }
            show()
        }
}