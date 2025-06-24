package pretimmediat.fragment

import android.graphics.Color
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coroutine.flow.launchIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import logger.L
import pretimmediat.R
import pretimmediat.activity.inputpiece.InfoPieceActivityCreator
import pretimmediat.activity.inputpiece.bank.BankPieceUpdateActivityCreator
import pretimmediat.activity.loan.OrderDetailActivityCreator
import pretimmediat.adapter.OrderAdapter
import pretimmediat.databinding.FragOrderBinding
import pretimmediat.def.Constants
import pretimmediat.dialog.InstallmentRepaymentDialog
import pretimmediat.ext.addServiceIcon
import pretimmediat.ext.checkUpgrade
import pretimmediat.ext.showInstallmentRepaymentSuccessDialog
import pretimmediat.ext.startServiceActivity
import pretimmediat.ext.withLoading
import pretimmediat.ext.withNetworkError
import pretimmediat.ext.withSwipeState
import pretimmediat.ext.withTriggerState
import pretimmediat.model.Order
import pretimmediat.viewmodel.OrderViewModel
import vector.app.databinding.frag.DBFragEx
import vector.app.ext.bind.bindView
import vector.app.os.dp
import vector.widget.scrollable.decoration.Decoration
import vector.widget.swiperefresh.delegate.SwipeRefreshDelegate

/**
 * 订单页
 */
@AndroidEntryPoint
class OrderFrag : DBFragEx<OrderViewModel>() {

    companion object {
        private const val LOG_TAG = "OrderFrag"
    }

    val decoration = Decoration.linear {
        color = Color.TRANSPARENT
        size = 10.dp.toPx(context)
    }
    val emptyBinder = OrderAdapter.Binder.Empty()
    val itemBinders = listOf(
        OrderAdapter.Binder.InReview { order ->
            startDetailActivity(order)
        },
        OrderAdapter.Binder.Processing { order ->
            startDetailActivity(order)
        },
        OrderAdapter.Binder.Overdue(object : OrderAdapter.Binder.BasePayment.Listener {
            override fun onDelayClick(order: Order) {
                showDelayDialog(order)
            }

            override fun onClick(order: Order) {
                startDetailActivity(order)
            }
        }),
        OrderAdapter.Binder.Repaying(object : OrderAdapter.Binder.BasePayment.Listener {
            override fun onDelayClick(order: Order) {
                showDelayDialog(order)
            }

            override fun onClick(order: Order) {
                startDetailActivity(order)
            }
        }),
        OrderAdapter.Binder.Complete { order ->
            InfoPieceActivityCreator.create()
                .userId(order.curUserId)
                .appSsid(order.appSsid)
                .start(requireContext())
        },
        OrderAdapter.Binder.PayFailed { order ->
            BankPieceUpdateActivityCreator.create()
                .orderId(order.orderId)
                .userId(order.curUserId)
                .appSsid(order.appSsid)
                .start(requireContext())
        },
        OrderAdapter.Binder.Reject { order ->
            startDetailActivity(order)
        }
    )

    private val refreshLayout by bindView<SwipeRefreshLayout>(R.id.order_swipe_refresh)
    val refreshDelegate by lazy { SwipeRefreshDelegate(refreshLayout) }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return FragOrderBinding.inflate(inflater).apply {
            owner = this@OrderFrag
            viewModel = this@OrderFrag.viewModel
        }
    }

    override fun initializeSystemBar() {
        appBar.left.addText(R.string.order_title)
        appBar.addServiceIcon {
            startServiceActivity(Constants.ServiceFlag.ORDER)
        }
    }

    override fun initializeContentView() {
        refreshDelegate.setOnSwipeRefresh {
            viewModel.fetchOrders().withNetworkError(context).withSwipeState(it).catch { e ->
                L.e(LOG_TAG, "fetchOrders", e)
            }.launchIn(this)
        }

        refreshDelegate.autoRefresh(lifecycle) { trigger ->
            viewModel.fetchOrders().withNetworkError(context).withTriggerState(trigger)
                .catch { e ->
                    L.e(LOG_TAG, "fetchOrders", e)
                }.launchIn(this)
        }

        viewModel.showInstallmentRepaymentSuccessDialog.onEach {
            showInstallmentRepaymentSuccessDialog(context, it)
        }.launchIn(this)

        checkUpgrade()
    }

    private fun startDetailActivity(order: Order) {
        OrderDetailActivityCreator.create()
            .userId(order.curUserId)
            .appSsid(order.appSsid)
            .start(context)
    }

    private fun showDelayDialog(order: Order) {
        viewModel.fetchDelay(order.curUserId, order.appSsid, order.orderId)
            .withNetworkError(requireContext())
            .withLoading(requireContext())
            .catch { e ->
                L.e(LOG_TAG, "onDelayClick", e)
            }.onEach { (plan, channels) ->
                L.d(LOG_TAG, "onDelayClick, success, plan = $plan, channels = $channels")
                viewModel.saveOrder(order)
                InstallmentRepaymentDialog(requireContext(), order.orderId, plan, channels).show()
            }.launchIn(this)
    }
}