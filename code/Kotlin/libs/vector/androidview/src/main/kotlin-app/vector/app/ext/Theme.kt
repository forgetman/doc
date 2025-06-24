package vector.app.ext

import android.R
import android.content.res.Resources.Theme
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import vector.MimeType
import vector.app.util.Res
import java.io.File

private const val RES_COLOR_PREFIX = "res/color/"
private const val RES_DRAWABLE_PREFIX = "res/drawable/"
private const val MISSING_DELIMITER_VALUE = ""

@Dimension
fun Theme.getDimension(@AttrRes attrId: Int): Float? {
    val typedValue = findTypedValue(attrId) ?: return null
    if (typedValue.type == TypedValue.TYPE_DIMENSION) {
        return typedValue.getDimension(resources.displayMetrics)
    }
    return null
}

@ColorInt
fun Theme.getColorInt(@AttrRes attrId: Int): Int? {
    val typedValue = findTypedValue(attrId) ?: return null
    if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT
        && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT
    ) {
        if (typedValue.data != 0) {
            return typedValue.data
        }
    }
    return null
}

@ColorRes
fun Theme.getColorStateListId(@AttrRes attrId: Int): Int? {
    val typedValue = findTypedValue(attrId) ?: return null
    if (typedValue.type != TypedValue.TYPE_STRING) return null

    val file = typedValue.string.toString()
    if (file.startsWith(RES_COLOR_PREFIX) && file.endsWith(MimeType.Text.Xml.suffix)) {
        /**
         * <item name="android:textColor">@selector/xxx</item>
         * res/color/xxx.xml
         */
        val idName = file.substringAfterLast(File.separator, MISSING_DELIMITER_VALUE)
            .substringBefore(MimeType.Text.Xml.suffix, MISSING_DELIMITER_VALUE)
        if (idName.isEmpty()) return null
        val colorStateListId = Res.getIdentifier(idName, Res.Type.COLOR)
        if (colorStateListId != 0) {
            return colorStateListId
        }
    }

    return null
}

@DrawableRes
fun Theme.getDrawableId(@AttrRes attrId: Int): Int? {
    val typedValue = findTypedValue(attrId) ?: return null
    if (typedValue.type != TypedValue.TYPE_STRING) return null

    val file = typedValue.string.toString()
    if (file.startsWith(RES_DRAWABLE_PREFIX) && file.endsWith(MimeType.Text.Xml.suffix)) {
        /**
         * <item name="android:background">@drawable/xxx</item>
         * res/drawable/xxx.xml
         */
        val idName = file.substringAfterLast(File.separator, MISSING_DELIMITER_VALUE)
            .substringBefore(MimeType.Text.Xml.suffix, MISSING_DELIMITER_VALUE)
        if (idName.isEmpty()) return null
        val drawableId = Res.getIdentifier(idName, Res.Type.DRAWABLE)
        if (drawableId != 0) {
            return drawableId
        }
    }

    return null
}

fun Theme.getTheme(): Theme? {
    val typedValue = findTypedValue(R.attr.theme) ?: return null
    if (typedValue.type != TypedValue.TYPE_REFERENCE) return null
    return resources.newTheme().apply {
        applyStyle(typedValue.resourceId, true)
    }
}

private fun Theme.findTypedValue(@AttrRes attrId: Int): TypedValue? {
    val outValue = TypedValue()
    val found = resolveAttribute(attrId, outValue, true)
    return if (found) outValue else null
}