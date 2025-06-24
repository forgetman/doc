package pretimmediat.ext

import android.text.SpannableStringBuilder
import androidx.annotation.ColorInt
import pretimmediat.text.style.SoftReferenceClickableSpan

fun SpannableStringBuilder.setClickableSpan(
    start: Int,
    end: Int,
    @ColorInt textColor: Int, clickAction: (() -> Unit)
) {
    setSpan(
        SoftReferenceClickableSpan(textColor) {
            clickAction()
        },
        start,
        end,
        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}