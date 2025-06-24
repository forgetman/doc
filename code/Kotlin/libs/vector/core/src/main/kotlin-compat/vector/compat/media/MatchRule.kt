@file:Suppress("unused")

package vector.compat.media

import vector.MimeType

/**
 * Media匹配的规则
 */
@Suppress("MemberVisibilityCanBePrivate")
sealed class MatchRule {

    internal abstract fun match(name: String): Boolean

    /**
     * 相等
     * @param text 对比的文本
     * @param includeType 对比范围是否包含指定[MimeType]类型的后缀
     */
    class Equals(
        private val text: String,
        private val includeType: MimeType = MimeType.Unspecified
    ) : MatchRule() {
        override fun match(name: String): Boolean {
            return name.plus(includeType.suffix) == text
        }
    }

    /**
     * 以...为开头
     * @param prefix 对比的前缀文本
     */
    class StartsWith(private val prefix: String) : MatchRule() {
        override fun match(name: String): Boolean {
            return name.startsWith(prefix)
        }
    }

    /**
     * 以...结尾
     * @param text 对比的文本
     * @param excludeType 对比范围是否排除指定[MimeType]的后缀
     */
    class EndsWith(
        private val text: String,
        private val excludeType: MimeType = MimeType.Unspecified
    ) :
        MatchRule() {
        override fun match(name: String): Boolean {
            return withoutTypeSuffix(name, excludeType) {
                it.endsWith(text)
            }
        }
    }

    /**
     * 包含
     * @param text 对比的文本
     * @param excludeType 对比范围是否排除指定[MimeType]的后缀
     */
    class Contains(
        private val text: String,
        private val excludeType: MimeType = MimeType.Unspecified
    ) :
        MatchRule() {
        override fun match(name: String): Boolean {
            return withoutTypeSuffix(name, excludeType) {
                it.contains(text)
            }
        }
    }

    /**
     * 以...为后缀
     * @param type 对比的后缀
     */
    class Suffix(private val type: MimeType) : MatchRule() {
        override fun match(name: String): Boolean {
            return name.endsWith(type.suffix)
        }
    }

    /**
     * 多重规则拼接
     */
    class Concat(private vararg val rules: MatchRule) : MatchRule() {
        override fun match(name: String): Boolean {
            var result = true
            kotlin.run run@{
                rules.forEach { r ->
                    result = result && r.match(name)
                    if (!result) return@run
                }
            }
            return result
        }
    }

    /**
     * 如果有指定的mimeType, 则转换成不带suffix的string
     */
    internal fun withoutTypeSuffix(
        name: String,
        type: MimeType,
        action: (text: String) -> Boolean
    ): Boolean {
        val hasSuffix = type != MimeType.Unspecified && name.endsWith(type.suffix)
        return if (hasSuffix) {
            val index = name.lastIndexOf(type.suffix)
            action(name.substring(0, index))
        } else {
            action(name)
        }
    }
}
