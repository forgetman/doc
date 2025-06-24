package dsb.design.ui.adapter

import dsb.databinding.LayoutNewTipItemBinding
import vector.app.databinding.adapter.binder.DBItemBinder

class NewTipItemBinder(private val listener: Listener) :
    DBItemBinder<Int, LayoutNewTipItemBinding>() {
    interface Listener {
        fun onClick()
    }

    override fun onBindBinding(item: Int, binding: LayoutNewTipItemBinding, position: Int) {
        binding.item = item
        binding.listener = listener
    }
}