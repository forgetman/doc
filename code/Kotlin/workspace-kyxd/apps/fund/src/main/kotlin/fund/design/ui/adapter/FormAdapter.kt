package fund.design.ui.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import fund.R
import lib.base.model.Form
import vector.design.ui.adapter.data.MultiAdapterEx
import vector.app.ext.bind.bindView
import vector.image.NImageView

/**
 * @author yuansui
 * @since 2018/7/28 0028
 */
class FormAdapter : MultiAdapterEx<Form>() {

    companion object {
        const val TYPE_1 = 1
        const val TYPE_2 = 2
        const val TYPE_3 = 3
        const val TYPE_4 = 4
        const val TYPE_5 = 5
        const val TYPE_ERROR = -1
    }

    override fun onBindBinding(viewType: Int, item: Form, binding: ViewDataBinding) {

    }

//    override fun refreshView(position: Int, holder: FormVH, itemType: Int) {
//        val item = getItem(position) ?: return
//
//        when (itemType) {
//            Form.TYPE_1 -> {
//                val layout = holder.form1Layout
//                item.list?.let {
//                    layout.setData(it)
//                } ?: layout.clear()
//            }
//            Form.TYPE_2 -> {
//                holder.gridLayout.let {
//                    it.removeAllViews()
//
//                    it.columnCount = item.list?.size ?: 0
//                    item.list?.forEachIndexed { index, form ->
//                        val view = Form2Layout(context)
//                        view.tvTitle.text = form.title
//                        view.iv.url(form.icon).load()
//
//                        val rowSpec = GridLayout.spec(index / it.columnCount, 1f)
//                        val columnSpec = GridLayout.spec(index % it.columnCount, 1f)
//                        val params = GridLayout.LayoutParams(rowSpec, columnSpec)
//                        it.addView(view, params)
//
//                        view.onClick {
//                            form.url.toWeb()
//                        }
//                    }
//                }
//            }
//            Form.TYPE_3 -> {
//                holder.tvTitle.text = item.title
//                holder.listLayout.let {
//                    it.removeAllViews()
//
//                    item.list?.forEach { form ->
//                        val view = Form3Layout(context)
//                        view.iv.url(form.icon).load()
//                        view.tvTitle.text = form.title
//                        view.tvDesc.text = form.desc
//                        view.tvRange.text = form.range
//
//                        it.addView(view)
//
//                        view.onClick {
//                            form.url.toWebWithoutLogin()
//                        }
//                    }
//                }
//            }
//            Form.TYPE_4 -> {
//                holder.tvTitle.text = item.title
//                holder.tvMore.gone()
//            }
//            Form.TYPE_5 -> {
//                holder.tvTitle.text = item.title
//                holder.tvNum.text = "阅读数".plus(item.num)
//                holder.tvDate.text = item.date
//                holder.niv.url(item.img).load()
//            }
//        }
//    }

    override fun getLayoutId(viewType: Int): Int {
        return when (viewType) {
            TYPE_1 -> R.layout.form1
            TYPE_2 -> R.layout.form2
            TYPE_3 -> R.layout.form3
            TYPE_4 -> R.layout.form4
            TYPE_5 -> R.layout.form5
            else -> R.layout.form_error
        }
    }

    override fun getViewType(position: Int): Int {
        return getItem(position)?.viewType ?: TYPE_ERROR
    }

}

//class FormVH(v: View) : ListViewHolderEx(v) {
//    val form1Layout by bindView<Form1View>(R.id.form_layout_form1view)
//    val gridLayout by bindView<GridLayout>(R.id.form_layout_grid)
//    val listLayout by bindView<LinearLayout>(R.id.form_layout_list)
//    val tvTitle by bindView<TextView>(R.id.form_tv_title)
//    val tvNum by bindView<TextView>(R.id.form_tv_num)
//    val tvDate by bindView<TextView>(R.id.form_tv_date)
//    val tvMore by bindView<TextView>(R.id.form_tv_more)
//    val niv by bindView<NImageView>(R.id.form_niv)
//}

private class Form2Layout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val iv by bindView<NImageView>(R.id.form_niv)
    val tvTitle by bindView<TextView>(R.id.form_tv_title)

    init {
        inflate(context, R.layout.form2_item, this)
    }
}

private class Form3Layout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val iv by bindView<NImageView>(R.id.form_niv)
    val tvTitle by bindView<TextView>(R.id.form_tv_title)
    val tvDesc by bindView<TextView>(R.id.form_tv_desc)
    val tvRange by bindView<TextView>(R.id.form_tv_range)

    init {
        View.inflate(context, R.layout.form3_item, this)
    }
}