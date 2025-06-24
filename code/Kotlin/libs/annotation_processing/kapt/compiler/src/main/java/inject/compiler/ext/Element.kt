@file:OptIn(DelicateKotlinPoetApi::class)

package inject.compiler.ext

import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import java.util.Locale
import java.util.regex.Pattern
import javax.lang.model.element.Element


/**
 * 有时候类似kotlin.int会被识别为java的Integer导致错误, 原因未知, 这里做一下转换
 */
fun Element.asTypeName(): TypeName {
    val typeName = this.asType().asTypeName()
    return typeName.convertToKotlinType()
}

fun Element.getParamName(value: String? = null): String {
    var ret = if (value != null && !value.trim { it <= ' ' }.isEmpty()) value else this.simpleName.toString()
    if (ret.length >= 2 && ret.startsWith("m")) {
        if (Pattern.compile("[A-Z]").matcher(ret.substring(1, 2)).matches()) {
            // 去掉m开头和首字母的大写
            val sub = ret.substring(1, 2)
            ret = ret.substring(1)
            ret = ret.replaceFirst(sub.toRegex(), sub.lowercase(Locale.getDefault()))
        }
    }

    return ret
}