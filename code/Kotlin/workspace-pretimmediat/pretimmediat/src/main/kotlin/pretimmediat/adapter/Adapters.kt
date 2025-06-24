package pretimmediat.adapter

import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import pretimmediat.BR
import pretimmediat.databinding.LayoutItemPermissionBinding
import pretimmediat.databinding.LayoutItemPopupBinding
import pretimmediat.model.Permission
import vector.widget.databinding.scrollable.adapter.DBItemBinder

abstract class BaseDBItemBinder<T, VDB : ViewDataBinding> : DBItemBinder<T, VDB>() {

    @CallSuper
    override fun onBindBinding(item: T, binding: VDB, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.owner, this)
        binding.executePendingBindings()
    }
}

class PermissionItemBinder : BaseDBItemBinder<Permission, LayoutItemPermissionBinding>()

class PopupDialogItemBinder : BaseDBItemBinder<String, LayoutItemPopupBinding>()