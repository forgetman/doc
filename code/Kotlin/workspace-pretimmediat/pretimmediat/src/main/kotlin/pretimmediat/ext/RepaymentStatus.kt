package pretimmediat.ext

import android.content.Context
import kotlinx.coroutines.suspendCancellableCoroutine
import logger.L
import pretimmediat.dialog.RepaymentSuccessDialog
import pretimmediat.model.product.SingleProduct
import sugar.ext.NoArgBlock
import kotlin.coroutines.resume

private const val LOG_TAG = "RepaymentStatus"

private var keepRepaymentOrderId: String? = null
fun setRepaymentPrepareInfo(orderId: String?) {
    keepRepaymentOrderId = orderId
    L.d(LOG_TAG, "setRepaymentPrepareInfo, orderId = $orderId")
}

/**
 * 同一个orderId, 只要状态为-1就弹
 */
suspend fun maybeShowRepaymentSuccess(
    context: Context?,
    sameOrderId: String?,
    newStatus: Int,
    applyAmount: String?,
) {
    L.d(
        LOG_TAG,
        "maybeShowRepaymentSuccess, keepRepaymentOrderId =$keepRepaymentOrderId, sameOrderId = $sameOrderId, newStatus = $newStatus"
    )
    if (keepRepaymentOrderId != null && keepRepaymentOrderId == sameOrderId) {
        suspendCancellableCoroutine { cont ->
            if (newStatus == SingleProduct.OD_STATUS_CAN_APPLY) {
                showRepaymentSuccessDialog(context, applyAmount) {
                    cont.resume(Unit)
                }
            } else {
                cont.resume(Unit)
            }
        }
    }
    setRepaymentPrepareInfo(null)
}

fun showRepaymentSuccessDialog(
    context: Context?,
    applyAmount: String?,
    dismissCallback: NoArgBlock
) {
    RepaymentSuccessDialog(context, applyAmount).apply {
        setOnDismissListener {
            dismissCallback()
        }
        show()
    }
}