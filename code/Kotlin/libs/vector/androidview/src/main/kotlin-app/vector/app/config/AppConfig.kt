package vector.app.config

import vector.app.decor.CreateDecorErrorView
import vector.app.decor.CreateDecorLoadingView

/**
 * App整体配置
 * @author yuansui
 */
class AppConfig private constructor() {

    companion object {
        fun build(init: AppConfig.() -> Unit): AppConfig = AppConfig().apply(init)
    }

    // 是否使用沉浸式状态栏
    var enableFlatBar: Boolean = true

    var errorConstructor: CreateDecorErrorView? = null // 错误页面
    var loadingConstructor: CreateDecorLoadingView? = null // 加载中页面
}
