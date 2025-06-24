package vector.app.config

import android.view.View


/**
 * @author yuansui
 */
class ViewPagerConfig private constructor() {

    companion object {
        fun build(init: ViewPagerConfig.() -> Unit): ViewPagerConfig = ViewPagerConfig().apply(init)
    }

    var overScrollMode = View.OVER_SCROLL_ALWAYS
}
