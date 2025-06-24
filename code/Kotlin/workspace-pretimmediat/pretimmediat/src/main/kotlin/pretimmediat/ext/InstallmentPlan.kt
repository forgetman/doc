package pretimmediat.ext

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import pretimmediat.R
import pretimmediat.model.loan.InstallmentPlan
import vector.app.util.toColor

fun buildMixText(
    text: String?,
    map: Map<String, String>?,
    @ColorInt spanColor: Int
): CharSequence {
    return SpannableStringBuilder().apply {
        var mix = text ?: return@apply
        val values = mutableListOf<String>()
        map?.forEach { (key, value) ->
            if (mix.indexOf(key) != -1) {
                mix = mix.replace(key, value)
                values.add(value)
            }
        }
        append(mix)
        values.forEach { value ->
            val valueIndex = mix.indexOf(value)
            setSpan(
                ForegroundColorSpan(spanColor),
                valueIndex,
                valueIndex + value.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}

fun InstallmentPlan.dateText(context: Context): CharSequence {
    return buildMixText(titleDate, titleDateRedMap, R.color.blue.toColor(context))
}

fun InstallmentPlan.amountText(context: Context): CharSequence {
    return buildMixText(titleAmount, titleAmountRedMap, R.color.blue.toColor(context))
}

fun InstallmentPlan.titleText(context: Context): CharSequence {
    return buildMixText(title, redTitleMap, R.color.red.toColor(context))
}

fun InstallmentPlan.dateTitle(context: Context): CharSequence {
    return buildMixText(title2, redTitle2Map, R.color.blue.toColor(context))
}

fun InstallmentPlan.amountTitle(context: Context): CharSequence {
    return buildMixText(title3, redTitle3Map, R.color.blue.toColor(context))
}