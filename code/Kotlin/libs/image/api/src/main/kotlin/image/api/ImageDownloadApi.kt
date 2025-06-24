package image.api

import android.content.Context
import android.graphics.Bitmap
import java.io.File

/**
 * @author yuansui
 * @since 2020/11/19
 */
interface ImageDownloadApi {

    fun toBitmap(
        context: Context,
        url: String,
        width: Int? = null,
        height: Int? = null,
        result: (Bitmap?) -> Unit
    )

    /**
     * @param result 返回临时的文件, 会在程序退出后删除
     */
    fun toFile(
        context: Context,
        url: String,
        width: Int? = null,
        height: Int? = null,
        result: (File?) -> Unit
    )

}