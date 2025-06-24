package fund.design.ui.adapter

import androidx.databinding.ViewDataBinding
import fund.R
import fund.model.Msg
import vector.design.ui.adapter.AdapterEx

/**
 * @author yuansui
 * @since 2018/8/1
 */
class MsgAdapter : AdapterEx<Msg>() {
    override fun onBindBinding(item: Msg, binding: ViewDataBinding) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_item_msg
    }

//    override fun refreshView(position: Int, holder: MsgVH) {
//        val item = getItem(position) ?: return
//
//        holder.tvTitle.text = item.content
//        holder.tvDate.text = item.date
//    }

}

//class MsgVH(v: View) : ListViewHolderEx(v) {
//    val tvTitle by bindView<TextView>(R.id.msg_tv_title)
//    val tvDate by bindView<TextView>(R.id.msg_tv_date)
//}