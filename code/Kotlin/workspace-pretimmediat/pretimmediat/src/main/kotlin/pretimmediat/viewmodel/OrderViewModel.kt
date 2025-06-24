package pretimmediat.viewmodel

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import logger.L
import pretimmediat.adapter.OrderAdapter
import pretimmediat.ext.optimizeLoading
import pretimmediat.model.Order
import pretimmediat.model.product.SingleProduct
import pretimmediat.repo.OrderRepo
import vector.app.viewmodel.ViewModelEx
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class OrderViewModel @Inject constructor(private val repo: OrderRepo, app: Application) : ViewModelEx(app) {

    companion object {
        private const val LOG_TAG = "OrderViewModel"
    }

    val data = MutableStateFlow<List<OrderAdapter.Data>>(emptyList())
    var keepOrder: Order? = null
        private set

    private val _showInstallmentRepaymentSuccessDialog = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val showInstallmentRepaymentSuccessDialog = _showInstallmentRepaymentSuccessDialog.asSharedFlow()

    fun fetchOrders() = repo.fetchOrders().zip(fetchInstallment(keepOrder)) { orders, product: SingleProduct? ->
        L.d(LOG_TAG, "fetchOrders: $orders, product = $product")
        // 检查展期弹窗逻辑
        val oldOrder = keepOrder
        if (product != null && oldOrder != null) {
            // 如果product不为空，说明之前点击过展期弹窗，需要对比展期还款结果是否成功
            if (oldOrder.orderId != product.orderId) {
                _showInstallmentRepaymentSuccessDialog.tryEmit(product.repaymentDate)
            }
        }
        keepOrder = null

        orders.map { o: Order ->
            when (val status = o.viewStatus) {
                Order.STATUS_IN_REVIEW -> OrderAdapter.InReview(o)
                Order.STATUS_OVERDUE -> OrderAdapter.Overdue(o)
                Order.STATUS_REPAYING -> OrderAdapter.Repaying(o)
                Order.STATUS_LOAN_PROCESSING -> OrderAdapter.Processing(o)
                Order.STATUS_REJECT -> OrderAdapter.Reject(o)
                Order.STATUS_COMPLETE -> OrderAdapter.Complete(o)
                Order.STATUS_PAY_FAILED -> OrderAdapter.PayFailed(o)
                else -> throw IllegalStateException("Unknown viewStatus: $status")
            }
        }
    }.flowOn(Dispatchers.IO).optimizeLoading().onEach { items ->
        data.value = items
    }

    // 调repayDetailInstallment 传0304，getPayChannelList0304 成功弹窗
    fun fetchDelay(userId: String?, ssid: String?, orderId: String) =
        repo.fetchLoanPlan(userId, ssid, orderId).flatMapConcat { plan ->
            repo.fetchPayChannels(userId, ssid, "0304").map { Pair(plan, it) }
        }.flowOn(Dispatchers.IO)

    private fun fetchInstallment(order: Order?) = if (order == null) {
        flow<SingleProduct?> {
            emit(null)
        }
    } else {
        repo.fetchInstallment(order.curUserId, order.appSsid)
    }

    fun saveOrder(order: Order) {
        keepOrder = order
    }
}