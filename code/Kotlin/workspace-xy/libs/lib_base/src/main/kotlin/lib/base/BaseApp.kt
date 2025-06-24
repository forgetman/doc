//package lib.base
//
//import android.R.attr.height
//import android.R.attr.mode
//import android.content.Context
//
///**
// * @author yuansui
// */
//abstract class BaseApp : AppEx() {
//
//    companion object {
//
//        lateinit var context: Context
//
//        var isDrawInBg = true
//            private set
//
//        fun setDrawInBgState(isCompleted: Boolean) {
//            isDrawInBg = isCompleted
//        }
//
//        var isMemoryLow = false
//
//        /**
//         * 是否需要重新绘制所有的buffer
//         */
//        var needRedrawAllBuffer = false
//
//        /**
//         * 用户选择了皮肤后, 返回是否需要重新加载皮肤
//         */
//        var needReloadSkin = false
//
//        /**
//         * 从年视图返回是点的返回还是月份
//         */
//        var yearViewMonth: Int? = null
//            private set
//        var yearViewYear: Int? = null
//            private set
//
//        fun setYearViewInfo(month: Int?, year: Int?) {
//            yearViewMonth = month
//            yearViewYear = year
//        }
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        context = applicationContext
//    }
//
//    override fun configureFit(): FitConfig = FitConfig.build {
//        width = 800f
//        height = 1280f
//        density = 2f
//        mode = Mode.FULL_SCREEN
//    }
//
//    override fun configureAppBar(): AppBarConfig {
//        return AppBarConfig.build {}
//    }
//}
