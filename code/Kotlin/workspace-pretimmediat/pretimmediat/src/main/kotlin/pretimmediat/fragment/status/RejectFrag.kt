package pretimmediat.fragment.status

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import pretimmediat.R
import pretimmediat.def.Constants
import pretimmediat.fragment.base.BaseFrag
import vector.app.ext.bind.bindView
import vector.app.ext.getStringForLanguage
import vector.app.ext.inflate
import vector.ext.getStringForLanguage
import vector.ext.inflate
import vector.app.util.toColor

@Creator
class RejectFrag : BaseFrag() {

    @Extra
    var networkTime: String = ""

    override val serviceFlag: Int
        get() = Constants.ServiceFlag.MAIN_SINGLE_REJECT

    private val tvTime by bindView<TextView>(R.id.tv_next_time)

    override fun createContentView(): View {
        return inflate(R.layout.frag_loan_status_reject)
    }

    override fun initializeContentView() {
        tvTime.text = SpannableStringBuilder().apply {
            val part1 = getStringForLanguage(R.string.home_single_reject_content4_prefix)
            append(part1)
            append(networkTime)
            val redSpan = ForegroundColorSpan(R.color.red.toColor(context))
            setSpan(
                redSpan,
                part1.length,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}