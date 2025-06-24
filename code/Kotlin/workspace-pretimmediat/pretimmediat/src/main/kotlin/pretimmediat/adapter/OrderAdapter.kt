package pretimmediat.adapter

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import image.ImageTransformation
import kotlinx.coroutines.flow.MutableStateFlow
import pretimmediat.R
import pretimmediat.databinding.LayoutItemOrderDescriptionBinding
import pretimmediat.databinding.LayoutItemOrderPaymentBinding
import pretimmediat.databinding.LayoutItemOrderPlanBinding
import pretimmediat.databinding.LayoutItemOrderRejectBinding
import pretimmediat.model.Order
import vector.widget.scrollable.adapter.ItemViewHolder
import vector.widget.scrollable.adapter.binder.EmptyItemBinder
import vector.ext.getStringForLanguage
import vector.app.util.inflate
import vector.app.util.toColor

interface OrderAdapter {

    interface Data {
        val order: Order
    }

    abstract class OrderPlan(override val order: Order) : Data
    class InReview(order: Order) : OrderPlan(order)
    class Processing(order: Order) : OrderPlan(order)

    abstract class OrderDescription(override val order: Order) : Data
    class Complete(order: Order) : OrderDescription(order)
    class PayFailed(order: Order) : OrderDescription(order)
    class Reject(order: Order) : OrderDescription(order)

    abstract class OrderPayment(override val order: Order) : Data
    class Overdue(order: Order) : OrderPayment(order)
    class Repaying(order: Order) : OrderPayment(order)

    interface Binder {
        abstract class BaseDescription<T : OrderDescription, VDB : ViewDataBinding>(
            @StringRes val titleId: Int,
            @ColorRes val titleColor: Int,
            @StringRes val contentId: Int,
            @StringRes val buttonId: Int,
            val listener: Listener
        ) : BaseDBItemBinder<T, VDB>() {
            fun interface Listener {
                fun onClick(order: Order)
            }

            val transformation = ImageTransformation.Shape.Circle()
        }

        abstract class BasePlan<T : OrderPlan>(
            @StringRes val titleId: Int,
            @ColorRes val titleColor: Int,
            @StringRes val buttonId: Int,
            val listener: Listener
        ) : BaseDBItemBinder<T, LayoutItemOrderPlanBinding>() {
            fun interface Listener {
                fun onClick(order: Order)
            }

            val transformation = ImageTransformation.Shape.Circle()
        }

        abstract class BasePayment<T : OrderPayment>(
            @ColorRes val titleColor: Int,
            @StringRes val buttonId: Int,
            val listener: Listener
        ) : BaseDBItemBinder<T, LayoutItemOrderPaymentBinding>() {
            interface Listener {
                fun onDelayClick(order: Order)
                fun onClick(order: Order)
            }

            val title = MutableStateFlow<String?>(null)
            val transformation = ImageTransformation.Shape.Circle()
            val info = MutableStateFlow<CharSequence?>(null)
            val duration = MutableStateFlow<String?>(null)
            val visible = MutableStateFlow(false)

            override fun onBindBinding(
                item: T,
                binding: LayoutItemOrderPaymentBinding,
                position: Int
            ) {
                val context = binding.root.context
                // 欠款总额分%1$s期，未偿还%2$s期
                info.value = SpannableStringBuilder().apply {
                    val part1 = context.getStringForLanguage(R.string.order_payment_info_part1)
                    val part2 = context.getStringForLanguage(R.string.order_payment_info_part2)
                    val part3 = context.getStringForLanguage(R.string.order_payment_info_part3)
                    append(part1)
                    append(item.order.totalPeriod)
                    append(part2)
                    append(item.order.notSettleCount)
                    append(part3)

                    val highlightColor = R.color.red.toColor(context)
                    var start = part1.length
                    setSpan(
                        ForegroundColorSpan(highlightColor),
                        start,
                        start + item.order.totalPeriod.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    start += item.order.totalPeriod.length + part2.length

                    setSpan(
                        ForegroundColorSpan(highlightColor),
                        start,
                        start + item.order.notSettleCount.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                duration.value = context.getStringForLanguage(
                    R.string.repaying_delay_by_days,
                    item.order.extendDuration
                )
                visible.value = item.order.extendFlag == "1"
                super.onBindBinding(item, binding, position)
            }
        }

        class Overdue(listener: Listener) : BasePayment<OrderAdapter.Overdue>(
            R.color.orange,
            R.string.repaying,
            listener
        ) {
            override fun onBindBinding(
                item: OrderAdapter.Overdue,
                binding: LayoutItemOrderPaymentBinding,
                position: Int
            ) {
                title.value = binding.root.context.getStringForLanguage(
                    R.string.order_overdue_title,
                    item.order.overdueDays
                )
                super.onBindBinding(item, binding, position)
            }
        }

        class Repaying(listener: Listener) : BasePayment<OrderAdapter.Repaying>(
            R.color.blue_middle_deep,
            R.string.repaying,
            listener
        ) {
            override fun onBindBinding(
                item: OrderAdapter.Repaying,
                binding: LayoutItemOrderPaymentBinding,
                position: Int
            ) {
                title.value =
                    binding.root.context.getStringForLanguage(R.string.order_replaying_title)
                super.onBindBinding(item, binding, position)
            }
        }

        class InReview(listener: Listener) : BasePlan<OrderAdapter.InReview>(
            R.string.order_in_review_title,
            R.color.blue_middle_deep,
            R.string.order_in_review_button,
            listener
        )

        class Processing(listener: Listener) : BasePlan<OrderAdapter.Processing>(
            R.string.order_processing_title,
            R.color.blue_middle_deep,
            R.string.order_processing_button,
            listener
        )

        class Complete(listener: Listener) :
            BaseDescription<OrderAdapter.Complete, LayoutItemOrderDescriptionBinding>(
                R.string.order_complete_title,
                R.color.green,
                R.string.order_complete_content,
                R.string.order_complete_button,
                listener
            )

        class PayFailed(listener: Listener) :
            BaseDescription<OrderAdapter.PayFailed, LayoutItemOrderDescriptionBinding>(
                R.string.order_pay_failed_title,
                R.color.red_light,
                R.string.order_pay_failed_content,
                R.string.order_pay_failed_button,
                listener
            )

        class Reject(listener: Listener) :
            BaseDescription<OrderAdapter.Reject, LayoutItemOrderRejectBinding>(
                R.string.order_reject_title,
                R.color.red_light,
                R.string.order_reject_content,
                R.string.order_reject_button,
                listener
            )

        class Empty : EmptyItemBinder<ViewHolder>() {
            override fun createViewHolder(parent: ViewGroup): ViewHolder {
                val view = R.layout.layout_item_order_empty.inflate(parent)
                return ViewHolder(view)
            }

            override fun onBindViewHolder(holder: ViewHolder) {
                // do nothing
            }
        }

        class ViewHolder(itemView: View) : ItemViewHolder(itemView)
    }
}