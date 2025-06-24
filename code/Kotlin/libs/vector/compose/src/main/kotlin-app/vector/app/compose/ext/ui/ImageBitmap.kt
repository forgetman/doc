package vector.app.compose.ext.ui

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

enum class ImageFormat {
    PNG,
    JPEG
}

fun ImageBitmap.toByteArray(
    format: ImageFormat = ImageFormat.PNG,
    quality: Int = 100
): ByteArray {
    return ByteArrayOutputStream().use { stream ->
        this.asAndroidBitmap().compress(
            when (format) {
                ImageFormat.PNG -> Bitmap.CompressFormat.PNG
                ImageFormat.JPEG -> Bitmap.CompressFormat.JPEG
            }, quality, stream
        )
        stream.toByteArray()
    }
}

@OptIn(ExperimentalEncodingApi::class)
fun ImageBitmap.encodeBase64(): String {
    return Base64.Default.encode(toByteArray())
}