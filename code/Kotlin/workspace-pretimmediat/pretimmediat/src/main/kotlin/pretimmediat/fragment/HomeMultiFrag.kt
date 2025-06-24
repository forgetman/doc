package pretimmediat.fragment

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import kotlinx.coroutines.flow.MutableStateFlow
import pretimmediat.R
import pretimmediat.activity.inputpiece.InfoPieceActivityCreator
import pretimmediat.activity.inputpiece.bank.BankPieceUpdateActivityCreator
import pretimmediat.activity.loan.OrderDetailActivityCreator
import pretimmediat.adapter.MultiProductAdapter
import pretimmediat.databinding.FragHomeMultiProductsBinding
import pretimmediat.def.Constants
import pretimmediat.dialog.Style1Dialog
import pretimmediat.fragment.base.databinding.BaseSimpleDBFrag
import pretimmediat.model.MultiProduct
import vector.app.os.dp
import vector.widget.scrollable.decoration.Decoration

/**
 * 多产品首页
 */
@Creator
class HomeMultiFrag : BaseSimpleDBFrag() {

    @Extra
    var products = emptyList<MultiProduct>()

    override val serviceFlag: Int
        get() = Constants.ServiceFlag.MAIN_MULTI_LOGGED_IN

    val decoration = Decoration.linear {
        size = 15.dp.toPx(context)
    }
    val itemBinders = listOf(
        MultiProductAdapter.Binder.CanApplyLuxury { product ->
            InfoPieceActivityCreator.create()
                .userId(product.curUserId)
                .appSsid(product.appSsid)
                .start(requireContext())
        },
        MultiProductAdapter.Binder.CanApply { product ->
            InfoPieceActivityCreator.create()
                .userId(product.curUserId)
                .appSsid(product.appSsid)
                .start(requireContext())
        },
        MultiProductAdapter.Binder.Overdue { product ->
            startDetailActivity(product)
        },
        MultiProductAdapter.Binder.Repaying { product ->
            startDetailActivity(product)
        },
        MultiProductAdapter.Binder.InReview { product ->
            startDetailActivity(product)
        },
        MultiProductAdapter.Binder.Processing { product ->
            startDetailActivity(product)
        },
        MultiProductAdapter.Binder.PayFailed { product ->
            BankPieceUpdateActivityCreator.create()
                .orderId(product.orderId)
                .userId(product.curUserId)
                .appSsid(product.appSsid)
                .start(context)
        },
        MultiProductAdapter.Binder.Reject { product ->
            startDetailActivity(product)
        },
        MultiProductAdapter.Binder.NoQuota {
            // 固定弹窗
            Style1Dialog.Builder(context)
                .content(R.string.home_multi_no_quota_dialog_content)
                .button("OK")
                .dismissCountdown(10)
                .build()
                .show()
        }
    )
    val data = MutableStateFlow<List<MultiProductAdapter.Data>>(emptyList())

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return FragHomeMultiProductsBinding.inflate(inflater).apply {
            owner = this@HomeMultiFrag
        }
    }

    override fun initializeData() {
        // 自行排序, 已下单的排在前面
        // 获取三个未下单的数据
        val applies = products.filter { it.viewStatus == MultiProduct.STATUS_CAN_APPLY }
            .mapIndexed { index, p ->
                if (index < 3) {
                    MultiProductAdapter.CanApplyLuxury(p, index)
                } else {
                    MultiProductAdapter.CanApply(p, index)
                }
            }
        val noQuotas = products.filter { it.viewStatus == MultiProduct.STATUS_NO_QUOTA }
            .map {
                MultiProductAdapter.NoQuota(it)
            }
        val others = products.filter {
            it.viewStatus != MultiProduct.STATUS_CAN_APPLY && it.viewStatus != MultiProduct.STATUS_NO_QUOTA
        }.map { p ->
            when (val status = p.viewStatus) {
                MultiProduct.STATUS_IN_REVIEW -> MultiProductAdapter.InReview(p)
                MultiProduct.STATUS_REJECT -> MultiProductAdapter.Reject(p)
                MultiProduct.STATUS_OVERDUE -> MultiProductAdapter.Overdue(p)
                MultiProduct.STATUS_REPAYING -> MultiProductAdapter.Repaying(p)
                MultiProduct.STATUS_LOAN_PROCESSING -> MultiProductAdapter.Processing(p)
                MultiProduct.STATUS_PAY_FAILED -> MultiProductAdapter.PayFailed(p)
                else -> throw IllegalStateException("Unknown viewStatus: $status")
            }
        }
        data.value = others + applies + noQuotas
    }

    private fun startDetailActivity(product: MultiProduct) {
        OrderDetailActivityCreator.create()
            .userId(product.curUserId)
            .appSsid(product.appSsid)
            .start(context)
    }
}