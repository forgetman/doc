package vector

import android.R
import vector.app.appbar.AppBarConfig
import vector.app.config.AppConfig
import vector.app.config.ImageConfig
import vector.app.config.ListConfig
import vector.app.os.colorRes
import vector.app.os.dp

/**
 * 经常要是用测试工程, 继承这个是用更方便, 不用重写一堆东西
 * @author yuansui
 * @since 2018/4/24
 */
abstract class AppTestEx : AppEx() {

    override fun configureAppBar(): AppBarConfig = AppBarConfig.build {
        setLayout {
            height = 44.dp
            background = R.color.holo_blue_light.colorRes
        }
        setIcon {
            shape = AppBarConfig.Icon.Shape.SQUARE
            size = 16.dp
            paddingStart = 12.dp
            paddingEnd = 12.dp
        }
        setText {
            paddingStart = 12.dp
            paddingEnd = 12.dp
            textSize = 16.dp
        }
    }

    override fun configureApp(): AppConfig = AppConfig.build {
        enableFlatBar = true
    }

    override fun configureImage(): ImageConfig = ImageConfig.build {
    }

    override fun configureList(): ListConfig = ListConfig.build {
    }
}