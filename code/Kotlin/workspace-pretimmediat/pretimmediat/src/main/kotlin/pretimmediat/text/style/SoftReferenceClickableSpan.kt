package pretimmediat.text.style

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorInt
import java.lang.ref.SoftReference

class SoftReferenceClickableSpan(
    @ColorInt private val textColor: Int,
    listener: View.OnClickListener
) : ClickableSpan() {
    private val listenerRef: SoftReference<View.OnClickListener> = SoftReference(listener)

    override fun onClick(widget: View) {
        listenerRef.get()?.onClick(widget)
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = textColor
    }
}