package fund.design.ui.adapter

import android.widget.TextView
import androidx.databinding.ViewDataBinding
import fund.R
import lib.base.model.Form
import vector.design.ui.adapter.PagerAdapterEx

/**
 * @author yuansui
 * @since 2018/8/13
 */
class Form1Adapter : PagerAdapterEx<Form>() {

    companion object {
        const val ITEM_TYPE1 = 1
        const val ITEM_TYPE2 = 2
    }

    override fun getLayoutId(itemType: Int): Int {
        return when (itemType) {
            ITEM_TYPE1 -> R.layout.form1_item_type1
            else -> R.layout.form1_item_type2
        }
    }

    override fun onBindBinding(viewType: Int, item: Form, binding: ViewDataBinding) {
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == lastItemPosition) ITEM_TYPE1 else ITEM_TYPE2
    }

//    override fun refreshView(position: Int, holder: Form1ViewHolder, itemType: Int) {
//        val item = getItem(position) ?: return
//
//        when (itemType) {
//            ITEM_TYPE1 -> {
//                holder.tvTitle.text = item.title
//                holder.btn.text = item.btn
//                holder.tvNum.text = item.num.plus("人已查询成功")
//
//                holder.btn.onClick {
//                    toWebAndSendMsg(item.url)
//                }
//
//                if (position % 2 == 0) {
//                    holder.btn.setTextColor(Res.getColor(R.color.blue))
//                } else {
//                    holder.btn.setTextColor(Res.getColor(R.color.orange))
//                }
//            }
//            ITEM_TYPE2 -> {
//                holder.tvName.text = item.name
//                holder.tvDate.text = item.date.plus("更新")
//                onVisibleCheckedChange(holder.tvBalance, holder.cbVisible.isChecked, item.balance)
//
//                holder.cbVisible.setOnCheckedChangeListener { _, isChecked ->
//                    onVisibleCheckedChange(holder.tvBalance, isChecked, item.balance)
//                }
//
//                holder.layoutRoot.onClick {
//                    item.url.toWeb()
//                }
//            }
//        }
//
//        if (position % 2 == 0) {
//            holder.layoutRoot.setBackgroundResource(R.drawable.form1_bg_type1)
//        } else {
//            holder.layoutRoot.setBackgroundResource(R.drawable.form1_bg_type2)
//        }
//    }

    private fun onVisibleCheckedChange(textView: TextView, isChecked: Boolean, balance: String?) {
        if (isChecked) {
            textView.text = balance
        } else {
            textView.text = "****"
        }
    }

//    @CheckLogin
//    private fun toWebAndSendMsg(url: String?) {
//        if (url == null) return
//        WebViewActivityCreator.create(url).get(appContext)
//        Bus.get().send(EventId.REFRESH_HOME)
//    }
}