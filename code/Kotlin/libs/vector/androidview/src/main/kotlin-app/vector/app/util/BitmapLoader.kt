package vector.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import vector.MimeType
import vector.app.ext.resize
import vector.app.ext.toBitmap
import vector.ext.calcInSampleSize
import vector.ext.inputStream

@Suppress("MemberVisibilityCanBePrivate")
object BitmapLoader {

    fun fromId(context: Context, @DrawableRes id: Int) = Id(context, id)
    fun fromName(context: Context, name: String) = Name(context, name)
    fun fromPath(path: String) = Path(path)
    fun fromBytes(bytes: ByteArray) = Bytes(bytes)
    fun fromUri(uri: android.net.Uri) = Uri(uri)

    class Id internal constructor(private val context: Context, @DrawableRes private val id: Int) {
        fun asBitmap(): Bitmap? {
            return id.toBitmap(context)
        }

        fun asBitmap(width: Int, height: Int): Bitmap? {
            return id.toBitmap(context, options = id.toBitmapOptions(context)?.apply {
                inSampleSize = calcInSampleSize(width, height)
                inJustDecodeBounds = false
            })
        }

        fun asBitmap(scale: Float): Bitmap? {
            return id.toBitmap(context)?.run {
                val ret = resize(scale)
                recycle()
                return ret
            }
        }

        fun asBitmap(inSampleSize: Int): Bitmap? {
            return id.toBitmap(context, options = id.toBitmapOptions(context)?.apply {
                this.inSampleSize = inSampleSize
                inJustDecodeBounds = false
            })
        }

        fun fromVector(width: Int? = null, height: Int? = null): Bitmap? {
            return id.toVectorDrawable(context)?.toBitmap(width, height)
        }

        /**
         * 加载mipmap文件夹下的图片
         */
        fun fromMipmap(name: String): Bitmap? {
            return getMipmapId(name).toBitmap(context)
        }

        private fun getMipmapId(name: String): Int {
            return Res.getIdentifier(name, Res.Type.MIPMAP)
        }
    }

    class Name internal constructor(private val context: Context, private val name: String) {

        fun asBitmap(): Bitmap? {
            return getDrawableId(name).toBitmap(context)
        }

        fun asBitmap(width: Int, height: Int): Bitmap? {
            val id = getDrawableId(name)
            return Id(context, id).asBitmap(width, height)
        }

        fun asBitmap(inSampleSize: Int): Bitmap? {
            val id = getDrawableId(name)
            return Id(context, id).asBitmap(inSampleSize)
        }

        private fun getDrawableId(name: String): Int {
            return Res.getIdentifier(name, Res.Type.DRAWABLE)
        }
    }

    class Path internal constructor(private val path: String) {
        fun asBitmap(type: MimeType.Image? = null): Bitmap? {
            return Res.getBitmap(path.plus(type?.suffix))
        }

        fun asBitmap(width: Int, height: Int): Bitmap? {
            return path.toBitmap(options = path.toBitmapOptions().apply {
                inSampleSize = calcInSampleSize(width, height)
                inJustDecodeBounds = false
            })
        }

        fun asBitmap(sampleSize: Int): Bitmap? {
            return path.toBitmap(options = path.toBitmapOptions().apply {
                inSampleSize = sampleSize
                inJustDecodeBounds = false
            })
        }

        /**
         * 根据像素来获取图片
         * @param pixels
         */
        fun byPixels(pixels: Int): Bitmap? {
            val size = path.toBitmapOptions().calcInSampleSize(pixels)
            return asBitmap(size)
        }
    }

    class Bytes internal constructor(private val bytes: ByteArray) {

        @Throws(ArrayIndexOutOfBoundsException::class, IllegalArgumentException::class)
        fun asBitmap(): Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        @Throws(ArrayIndexOutOfBoundsException::class, IllegalArgumentException::class)
        fun asBitmap(maxPixels: Int): Bitmap {
            val options = BitmapFactory.Options()

            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            options.inSampleSize = options.calcInSampleSize(maxPixels)
            options.inJustDecodeBounds = false

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        }
    }

    class Uri internal constructor(private val uri: android.net.Uri) {

        fun asBitmap(width: Int, height: Int): Bitmap? {
            val options = BitmapFactory.Options()

            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(uri.inputStream(), null, options)
            options.inSampleSize = options.calcInSampleSize(width, height)
            options.inJustDecodeBounds = false

            return BitmapFactory.decodeStream(uri.inputStream(), null, options)
        }
    }
}