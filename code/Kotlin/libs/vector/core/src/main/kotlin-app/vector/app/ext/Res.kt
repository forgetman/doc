@file:Suppress("unused")

package vector.app.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.NinePatch
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import vector.appContext
import java.io.InputStream

fun getNinePatch(context: Context, @DrawableRes id: Int): NinePatch? {
    val opt = BitmapFactory.Options()
    opt.inPreferredConfig = Bitmap.Config.ARGB_8888
    val bitmap = BitmapFactory.decodeResource(context.resources, id, opt)
    return when (bitmap) {
        null -> null
        else -> NinePatch(bitmap, bitmap.ninePatchChunk, null)
    }
}

fun getRaw(@RawRes id: Int): InputStream = appContext.resources.openRawResource(id)