@file:Suppress("unused")

package vector.app.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.AnyRes
import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import vector.app.ext.cloneLayoutInflater
import vector.appContext

@ColorInt
fun @receiver:ColorRes Int.toColor(context: Context?): Int {
    return Res.getColor(context ?: return Color.TRANSPARENT, this)
}

fun @receiver:ColorRes Int.toColorStateList(context: Context?): ColorStateList? {
    return Res.getColorStateList(context ?: return null, this)
}

fun @receiver:DrawableRes Int.toDrawable(context: Context?): Drawable? {
    return Res.getDrawable(context ?: return null, this)
}

fun @receiver:DrawableRes Int.toVectorDrawable(context: Context?): VectorDrawableCompat? {
    return Res.getVectorDrawable(context ?: return null, this)
}

fun @receiver:DrawableRes Int.toBitmap(
    context: Context?,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
    options: BitmapFactory.Options? = null
): Bitmap? {
    return Res.getBitmap(context ?: return null, this, config, options)
}

fun @receiver:DrawableRes Int.toBitmapOptions(context: Context?): BitmapFactory.Options? {
    return Res.getBitmapOptions(context ?: return null, this)
}

fun String.toBitmapOptions(): BitmapFactory.Options {
    return Res.getBitmapOptions(this)
}

fun String.toBitmap(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
    options: BitmapFactory.Options? = null
): Bitmap? {
    return Res.getBitmap(this, config, options)
}

fun @receiver:IntegerRes Int.toInteger() = Res.getInteger(this)

fun @receiver:LayoutRes Int.inflate(context: Context): View {
    return context.cloneLayoutInflater().inflate(this, null)
}

/**
 * @param attachToRoot 是否将此视图添加到根视图中, 默认false
 */
fun @receiver:LayoutRes Int.inflate(root: ViewGroup, attachToRoot: Boolean = false): View {
    return root.context.cloneLayoutInflater().inflate(this, root, attachToRoot)
}

/**
 * 资源加载器
 * ps: 部分资源不需要区分context, 比如 资源id, Animation等
 * @author yuansui
 * @since 2018/2/7
 */
@Suppress("MemberVisibilityCanBePrivate")
object Res {

    enum class Type(val text: String) {
        ID("id"),
        STRING("string"),
        DRAWABLE("drawable"),
        MIPMAP("mipmap"),
        LAYOUT("layout"),
        DIMEN("dimen"),
        COLOR("color"),
    }

    @AnyRes
    const val ID_NULL = 0

    object Android {
        @SuppressLint("DiscouragedApi") // Discouraged的大概意思表示使用name效率比使用id低
        fun getIdentifier(name: String, type: Type) =
            Resources.getSystem().getIdentifier(name, type.text, "android")

        fun getDimensionPixelSize(@DimenRes id: Int) =
            if (id != 0) Resources.getSystem().getDimensionPixelSize(id) else 0
    }

    @SuppressLint("DiscouragedApi") // Discouraged的大概意思表示使用name效率比使用id低
    fun getIdentifier(name: String, type: Type) =
        appContext.resources.getIdentifier(name, type.text, appContext.packageName)

    fun getString(@StringRes id: Int, context: Context = appContext): String =
        context.getString(id)

    fun getStringArray(@ArrayRes id: Int, context: Context = appContext): Array<String> =
        context.resources.getStringArray(id)

    fun getBitmap(
        context: Context,
        @DrawableRes id: Int,
        config: Bitmap.Config = Bitmap.Config.ARGB_8888,
        options: BitmapFactory.Options? = null
    ): Bitmap? {
        val opt = options ?: BitmapFactory.Options()
        opt.inPreferredConfig = config
        return BitmapFactory.decodeResource(context.resources, id, opt)
    }

    fun getBitmap(
        path: String,
        config: Bitmap.Config = Bitmap.Config.ARGB_8888,
        options: BitmapFactory.Options? = null
    ): Bitmap? {
        val opt = options ?: BitmapFactory.Options()
        opt.inPreferredConfig = config
        return BitmapFactory.decodeFile(path, opt)
    }

    fun getBitmap(
        bytes: ByteArray,
        config: Bitmap.Config = Bitmap.Config.ARGB_8888,
        options: BitmapFactory.Options? = null
    ): Bitmap? {
        val opt = options ?: BitmapFactory.Options()
        opt.inPreferredConfig = config
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opt)
    }

    fun getDrawable(context: Context, @DrawableRes id: Int): Drawable? {
        if (id == 0) return null
        return AppCompatResources.getDrawable(context, id)
    }

    /**
     * 不载入内存, 只读取图片的信息
     */
    fun getBitmapOptions(context: Context, @DrawableRes id: Int): BitmapFactory.Options {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources, id, options)
        return options
    }

    /**
     * 不载入内存, 只读取图片的信息
     */
    fun getBitmapOptions(filePath: String): BitmapFactory.Options {
        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, opt)
        return opt
    }

    @ColorInt
    @JvmStatic
    fun getColor(context: Context, @ColorRes id: Int): Int =
        ContextCompat.getColor(context, id)

    @JvmStatic
    fun getColorStateList(context: Context, @ColorRes id: Int): ColorStateList? =
        AppCompatResources.getColorStateList(context, id)

    fun getDimen(context: Context?, @DimenRes id: Int) =
        ensureContext(context).resources.getDimension(id)

    fun getDimensionPixelSize(context: Context?, @DimenRes id: Int) =
        if (id != 0) ensureContext(context).resources.getDimensionPixelSize(id) else 0

    private fun ensureContext(context: Context?) = context ?: appContext

    fun getVectorDrawable(context: Context, @DrawableRes id: Int) =
        VectorDrawableCompat.create(context.resources, id, null)

    @Throws(Resources.NotFoundException::class)
    fun getAnim(@AnimRes id: Int): Animation = AnimationUtils.loadAnimation(appContext, id)

    fun getInteger(@IntegerRes id: Int): Int = appContext.resources.getInteger(id)

    fun getBoolean(@BoolRes id: Int): Boolean = appContext.resources.getBoolean(id)
}