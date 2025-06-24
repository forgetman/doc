package fund.design.ui.adapter

import androidx.databinding.ViewDataBinding
import fund.BR
import vector.design.ui.adapter.AdapterEx

abstract class ItemAdapter<T> : AdapterEx<T>() {
    override fun onBindBinding(position: Int, item: T, binding: ViewDataBinding) {
        binding.setVariable(BR.item, item)
    }
}