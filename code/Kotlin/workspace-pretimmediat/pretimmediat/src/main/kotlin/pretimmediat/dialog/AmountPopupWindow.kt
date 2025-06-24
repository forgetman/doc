package pretimmediat.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import pretimmediat.R
import pretimmediat.ext.formatMoney
import vector.app.popup.PopupWindowEx
import vector.app.ext.bind.bindView
import vector.ext.getStringForLanguage
import vector.ext.inflate
import vector.util.MATCH_PARENT
import vector.app.util.inflate

class AmountPopupWindow(
    context: Context?,
    private val contents: List<String>,
    private val listener: Listener
) : PopupWindowEx(context) {

    fun interface Listener {
        fun onClick(index: Int, text: String)
    }

    private val layoutContents by bindView<LinearLayout>(R.id.layout_contents)

    override val width: Int
        get() = MATCH_PARENT

    override fun createContentView(layoutInflater: LayoutInflater): View {
        return layoutInflater.inflate(R.layout.popup_window_amount)
    }

    override fun initializeContentView() {
        contents.mapIndexed { index, s ->
            createItem(index, s)
        }.forEach {
            layoutContents.addView(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createItem(index: Int, text: String): View {
        val tv = R.layout.layout_item_popup_amount.inflate(context) as TextView
        tv.text = context.getStringForLanguage(R.string.xof_prefix) + text.formatMoney()
        tv.setOnClickListener {
            listener.onClick(index, text)
            dismiss()
        }
        return tv
    }
}