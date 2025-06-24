package dsb.design.ui.itembinder

import androidx.databinding.ViewDataBinding
import dsb.BR
import dsb.databinding.*
import dsb.model.*
import lib.base.model.Form
import vector.app.databinding.adapter.binder.DBItemBinder

abstract class BaseFormItemBinder<T : Form, VDB : ViewDataBinding> : DBItemBinder<T, VDB>() {
    override fun onBindBinding(item: T, binding: VDB, position: Int) {
        binding.setVariable(BR.item, item)
        binding.executePendingBindings()
    }
}

class Form0ItemBinder : BaseFormItemBinder<Form0, LayoutForm0Binding>()
class Form19ItemBinder : BaseFormItemBinder<Form19, LayoutForm19Binding>()
class Form24ItemBinder : BaseFormItemBinder<Form24, LayoutForm24Binding>()
class Form25ItemBinder : BaseFormItemBinder<Form25, LayoutForm25Binding>()
class Form30ItemBinder : BaseFormItemBinder<Form30, LayoutForm30Binding>()
class Form33ItemBinder : BaseFormItemBinder<Form33, LayoutForm33Binding>()
class Form34ItemBinder : BaseFormItemBinder<Form34, LayoutForm34Binding>()
class Form35ItemBinder : BaseFormItemBinder<Form35, LayoutForm35Binding>()
class Form36ItemBinder : BaseFormItemBinder<Form36, LayoutForm36Binding>()
class Form38ItemBinder : BaseFormItemBinder<Form38, LayoutForm38Binding>()
class Form39ItemBinder : BaseFormItemBinder<Form39, LayoutForm39Binding>()
class Form40ItemBinder : BaseFormItemBinder<Form40, LayoutForm40Binding>()
class Form41ItemBinder : BaseFormItemBinder<Form41, LayoutForm41Binding>()
class Form50ItemBinder : BaseFormItemBinder<Form50, LayoutForm50Binding>()
class Form51ItemBinder : BaseFormItemBinder<Form51, LayoutForm51Binding>()
class Form52ItemBinder : BaseFormItemBinder<Form52, LayoutForm52Binding>()