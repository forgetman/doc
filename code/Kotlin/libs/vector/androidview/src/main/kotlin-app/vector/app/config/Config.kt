package vector.app.config

import vector.app.appbar.AppBarConfig

/**
 * 统一管理所有的配置内容
 * @author yuansui
 * @since 2020/9/25
 */
object Config {

    private lateinit var appConfig: AppConfig
    private lateinit var listConfig: ListConfig
    private lateinit var imageConfig: ImageConfig
    private lateinit var fitConfig: FitConfig
    private lateinit var appBarConfig: AppBarConfig
    private lateinit var viewPagerConfig: ViewPagerConfig

    internal object Initializer {

        fun forApp(c: AppConfig) {
            appConfig = c
        }

        fun forList(c: ListConfig) {
            listConfig = c
        }

        fun forImage(c: ImageConfig) {
            imageConfig = c
        }

        fun forFit(c: FitConfig) {
            fitConfig = c
        }

        fun forAppBar(c: AppBarConfig) {
            appBarConfig = c
        }

        fun forViewPager(c: ViewPagerConfig) {
            viewPagerConfig = c
        }
    }

    fun app(): AppConfig = appConfig
    fun list(): ListConfig = listConfig
    fun image(): ImageConfig = imageConfig
    fun fit(): FitConfig = fitConfig
    fun appBar(): AppBarConfig = appBarConfig
    fun viewPager(): ViewPagerConfig = viewPagerConfig
}