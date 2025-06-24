package pretimmediat.adapter

import pretimmediat.databinding.LayoutItemHomeLoanPlanBinding
import pretimmediat.databinding.LayoutItemHomeLoanPlanButtonsBinding
import pretimmediat.databinding.LayoutItemHomeLoanPlanComplaintBinding
import pretimmediat.databinding.LayoutItemHomeLoanPlanProductBinding
import pretimmediat.databinding.LayoutItemHomeLoanPlanSettledBinding
import pretimmediat.model.product.SingleProduct

interface LoanPlanAdapter {
    interface Data

    class ProductInfo(val product: SingleProduct, val title: CharSequence) : Data

    abstract class AbstractPlanInfo(
        private val step: Int,
        private val size: Int,
        val title: CharSequence?,
        val dateTitle: CharSequence?,
        val dateText: CharSequence?,
        val amountTitle: CharSequence?,
        val amountText: CharSequence?,
        val lateFee: CharSequence?,
        val deductionFee: CharSequence?,
    ) : Data {
        fun stepText() = "$step/$size"
    }

    class PlanInfo(
        step: Int,
        size: Int,
        title: CharSequence?,
        dateTitle: CharSequence?,
        dateText: CharSequence?,
        amountTitle: CharSequence?,
        amountText: CharSequence?,
        lateFee: CharSequence?,
        deductionFee: CharSequence?,
    ) : AbstractPlanInfo(step, size, title, dateTitle, dateText, amountTitle, amountText, lateFee, deductionFee)

    class PlanSettledInfo(
        step: Int,
        size: Int,
        title: CharSequence?,
        dateTitle: CharSequence?,
        dateText: CharSequence?,
        amountTitle: CharSequence?,
        amountText: CharSequence?,
        lateFee: CharSequence?,
        deductionFee: CharSequence?,
    ) : AbstractPlanInfo(step, size, title, dateTitle, dateText, amountTitle, amountText, lateFee, deductionFee)

    class Complaint : Data
    class Buttons(val duration: String?) : Data

    interface Binder {
        class ProductInfo(val listener: Listener) :
            BaseDBItemBinder<LoanPlanAdapter.ProductInfo, LayoutItemHomeLoanPlanProductBinding>() {

            fun interface Listener {
                fun onCopyClick()
            }
        }

        class PlanInfo : BaseDBItemBinder<LoanPlanAdapter.PlanInfo, LayoutItemHomeLoanPlanBinding>()

        class PlanSettledInfo :
            BaseDBItemBinder<LoanPlanAdapter.PlanSettledInfo, LayoutItemHomeLoanPlanSettledBinding>()

        class Complaint(val listener: Listener) :
            BaseDBItemBinder<LoanPlanAdapter.Complaint, LayoutItemHomeLoanPlanComplaintBinding>() {
            fun interface Listener {
                fun onClick()
            }
        }

        class Buttons(val listener: Listener) :
            BaseDBItemBinder<LoanPlanAdapter.Buttons, LayoutItemHomeLoanPlanButtonsBinding>() {
            interface Listener {
                fun onDelayClick()
                fun onRepayClick()
            }
        }
    }
}