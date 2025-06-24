package pretimmediat.adapter

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import image.ImageTransformation
import pretimmediat.R
import pretimmediat.databinding.LayoutItemMultiProductBinding
import pretimmediat.databinding.LayoutItemMultiProductCanApplyLuxuryBinding
import pretimmediat.databinding.LayoutItemMultiProductNoQuotaBinding
import pretimmediat.model.MultiProduct
import vector.app.util.Res

/**
 * 首页多产品适配器
 */
interface MultiProductAdapter {
    interface Data {
        val product: MultiProduct
    }

    class InReview(override val product: MultiProduct) : Data
    class Processing(override val product: MultiProduct) : Data
    class PayFailed(override val product: MultiProduct) : Data
    class Reject(override val product: MultiProduct) : Data
    class Overdue(override val product: MultiProduct) : Data
    class Repaying(override val product: MultiProduct) : Data
    class NoQuota(override val product: MultiProduct) : Data

    class CanApplyLuxury(override val product: MultiProduct, val index: Int) : Data
    class CanApply(override val product: MultiProduct, val index: Int) : Data

    interface Binder {
        fun interface Listener {
            fun onClick(product: MultiProduct)
        }

        abstract class BaseBinder<T : Data>(
            @StringRes val titleId: Int,
            @ColorRes val titleColor: Int,
            @StringRes val buttonId: Int,
            val listener: Listener
        ) : BaseDBItemBinder<T, LayoutItemMultiProductBinding>() {
            val transformation = ImageTransformation.Shape.Circle()
        }

        class Overdue(listener: Listener) : BaseBinder<MultiProductAdapter.Overdue>(
            R.string.home_multi_overdue_title,
            R.color.orange,
            R.string.repaying,
            listener
        )

        class Repaying(listener: Listener) : BaseBinder<MultiProductAdapter.Repaying>(
            R.string.home_multi_repaying_title,
            R.color.blue_middle_deep,
            R.string.home_multi_repaying_button,
            listener
        )

        class InReview(listener: Listener) : BaseBinder<MultiProductAdapter.InReview>(
            R.string.home_multi_in_review_title,
            R.color.blue_middle_deep,
            R.string.home_multi_in_review_button,
            listener
        )

        class Processing(listener: Listener) : BaseBinder<MultiProductAdapter.Processing>(
            R.string.home_multi_processing_title,
            R.color.blue_middle_deep,
            R.string.home_multi_processing_button,
            listener
        )

        class PayFailed(listener: Listener) : BaseBinder<MultiProductAdapter.PayFailed>(
            R.string.home_multi_pay_failed_title,
            R.color.red_light,
            R.string.home_multi_pay_failed_button,
            listener
        )

        class Reject(listener: Listener) : BaseBinder<MultiProductAdapter.Reject>(
            R.string.home_multi_reject_title,
            R.color.red_light,
            R.string.home_multi_reject_button,
            listener
        )

        class CanApply(listener: Listener) : BaseBinder<MultiProductAdapter.CanApply>(
            R.string.home_multi_can_apply_title,
            R.color.blue_middle_deep,
            R.string.home_multi_can_apply_button,
            listener
        )

        class CanApplyLuxury(
            val listener: Listener
        ) : BaseDBItemBinder<MultiProductAdapter.CanApplyLuxury, LayoutItemMultiProductCanApplyLuxuryBinding>() {

            companion object {
                private const val PREFIX = "home_ic_multi_level_"
            }

            val transformation = ImageTransformation.Shape.Circle()

            override fun onBindBinding(
                item: MultiProductAdapter.CanApplyLuxury,
                binding: LayoutItemMultiProductCanApplyLuxuryBinding,
                position: Int
            ) {
                super.onBindBinding(item, binding, position)
                // FIXME: 不能使用stateFlow方式, 只有直接使用ivLevel的对象才能设置不同的drawable, 原因未知
                val id = Res.getIdentifier(PREFIX + (item.index + 1), Res.Type.DRAWABLE)
                binding.ivLevel.setImageResource(id)
            }
        }

        class NoQuota(
            val listener: Listener
        ) : BaseDBItemBinder<MultiProductAdapter.NoQuota, LayoutItemMultiProductNoQuotaBinding>() {
            val transformation = ImageTransformation.Shape.Circle()
        }
    }
}