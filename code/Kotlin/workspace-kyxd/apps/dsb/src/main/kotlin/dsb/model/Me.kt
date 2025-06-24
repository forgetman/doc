package dsb.model

import lib.base.model.Form
import vector.bindingadapter.GridLayoutSet

/**
 * @author yuansui
 * @since 2019-05-28
 */
class Me {
    var title: String? = null
    var sets: MutableList<GridLayoutSet>? = null
    val others = mutableListOf<Form>()
}
