package vector.app.frag

import vector.app.viewmodel.ViewModelEx
import vector.app.viewmodel.ViewModelOwner
import vector.app.viewmodel.initViewModel
import vector.app.viewmodel.initViewTreeOwners
import vector.app.viewmodel.viewModels

/**
 * @author yuansui
 * @since 2018/2/6
 */
abstract class FragEx<VM : ViewModelEx> : SimpleFragEx(), ViewModelOwner<VM> {

    override val viewModel: VM by viewModels()

    init {
        initViewTreeOwners()
        initViewModel()
    }
}