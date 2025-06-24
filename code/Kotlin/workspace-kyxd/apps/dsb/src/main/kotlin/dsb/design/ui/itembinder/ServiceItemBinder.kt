package dsb.design.ui.itembinder

import android.view.View
import android.view.ViewGroup
import dsb.databinding.LayoutServiceEmptyFooterBinding
import dsb.databinding.LayoutServiceItemBinding
import dsb.design.ui.frag.EachServiceFrag
import dsb.model.Service
import vector.app.adapter.ItemViewHolder
import vector.app.databinding.adapter.binder.DBItemBinder
import vector.app.databinding.adapter.binder.EmptyDBItemBinder
import vector.ext.inflateSpace
import vector.app.os.dp

interface Services {
    class Header

    interface ItemBinder {
        class Header : vector.app.adapter.binder.ItemBinder<Services.Header, Header.ViewHolder>() {
            class ViewHolder(itemView: View) : ItemViewHolder(itemView)

            override fun createViewHolder(parent: ViewGroup): ViewHolder {
                return ViewHolder(parent.context.inflateSpace(8.dp))
            }

            override fun onBindViewHolder(
                holder: ViewHolder,
                item: Services.Header,
                position: Int
            ) {
            }
        }

        class Data(private val listener: Listener) :
            DBItemBinder<Service, LayoutServiceItemBinding>() {

            interface Listener {
                fun onDetailClick(item: Service)

                /**
                 * 点击服务进度
                 */
                fun onProgressClick(item: Service)
            }

            override fun onBindBinding(
                item: Service,
                binding: LayoutServiceItemBinding,
                position: Int
            ) {
                binding.item = item
                binding.listener = listener
            }
        }

        class Empty(private val owner: EachServiceFrag) :
            EmptyDBItemBinder<LayoutServiceEmptyFooterBinding>() {

            override fun onBindBinding(binding: LayoutServiceEmptyFooterBinding) {
                binding.owner = owner
            }
        }
    }
}

