package vector.validator

import android.view.View
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

/**
 * 表单验证器
 */
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
interface Validator : StateFlow<Boolean> {
    fun bindView(view: View)
}