package vector.app.activity

import vector.app.viewmodel.ViewModelEx
import vector.app.viewmodel.ViewModelOwner
import vector.app.viewmodel.initViewModel
import vector.app.viewmodel.initViewTreeOwners
import vector.app.viewmodel.viewModels

/**
 * 封装MVVM模式的activity基类
 */
abstract class ActivityEx<VM : ViewModelEx> : SimpleActivityEx(), ViewModelOwner<VM> {

    override val viewModel: VM by viewModels()

    init {
        initViewTreeOwners()
        initViewModel()
    }
}