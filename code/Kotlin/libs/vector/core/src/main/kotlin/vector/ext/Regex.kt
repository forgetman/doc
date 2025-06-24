@file:Suppress("unused")

package vector.ext

import java.util.regex.Pattern


object Regex {
    /**
     * 正则规则
     */
    object Rule {
        const val MAIL =
            "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"
        const val INTEGER = "^[0-9]*$"
        const val DECIMAL = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$"
        const val SYMBOL = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"

        /**
         * 中国区, 手机号码
         */
        const val MOBILE_CN = "^(1(2|3|4|5|6|7|8|9)[0-9])\\d{8}$"
        const val CHINESE = "^[\\u4E00-\\u9FA5]+$"

        /**
         *  数字+字母组合
         *  (?![0-9]+$) 预测该位置后面不全是数字
         *  (?![a-zA-Z]+$) 预测该位置后面不全是字母
         *  [0-9A-Za-z]{8,24} 由8-24位数字或字母组成
         */
        const val NUMBER_LETTER = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,24}$"

        /**
         *  数字或英文
         */
        const val NUMBER_ENGLISH = "^[a-zA-Z0-9]$"

        /**
         *  中文标点符号
         */
        const val PUNCTUATION_CHINESE = "^[?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？《》]$"

        /**
         *  英文标点符号
         */
        const val PUNCTUATION_ENGLISH = "^[`~!@#\$%^&*()+=|{}':;',//[//].<>/]$"
    }


    /**
     * @param regex 正则表达式字符串
     * @param text   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回**true**, 否则返回 **false**;
     */
    fun match(regex: String, text: CharSequence) =
        Pattern.compile(regex).matcher(text).matches()

    /**
     * 检查输入的数据中是否有指定字符
     *
     * @param text 要匹配的字符串
     * @param regex 特殊字符正则表达式
     * @return boolean 如果包含正则表达式regex中定义的特殊字符，返回true； 否则返回false
     */
    fun find(regex: String, text: CharSequence) =
        Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).find()
}

/**
 * 验证邮箱
 *
 * @return 如果是符合的字符串, 返回 **true **,否则为 **false **
 */
fun CharSequence.isEmail() = Regex.match(Regex.Rule.MAIL, this)

/**
 * 验证数字输入
 *
 * @return 如果是符合格式的字符串, 返回 **true **,否则为 **false **
 */
fun CharSequence.isInteger() = Regex.match(Regex.Rule.INTEGER, this)

fun CharSequence.isDecimal() = Regex.match(Regex.Rule.DECIMAL, this)

/**
 * 检验是否是电话号码
 */
fun CharSequence.isMobileCN() = Regex.match(Regex.Rule.MOBILE_CN, this)

/**
 * 检查输入的数据中是否有特殊字符
 *
 * @return boolean 如果包含正则表达式 ` regex ` 中定义的特殊字符，返回true； 否则返回false
 */
fun CharSequence.hasSpecialSymbol() = Regex.find(Regex.Rule.SYMBOL, this)

/**
 * 是否中文
 */
fun CharSequence.isChinese() = Regex.match(Regex.Rule.CHINESE, this)

/**
 * 是否中文标点符号
 */
fun CharSequence.isChinesePunctuation() = Regex.match(Regex.Rule.PUNCTUATION_CHINESE, this)

/**
 *  是否英文标点符号
 */
fun CharSequence.isEnglishPunctuation() = Regex.match(Regex.Rule.PUNCTUATION_ENGLISH, this)

/**
 *  是否数字+字母组合，多用于密码格式检验
 */
fun CharSequence.isNumAndLetter() = Regex.match(Regex.Rule.NUMBER_LETTER, this)

/**
 *  数字或者英文
 */
fun CharSequence.isNumOrEnglish() = Regex.match(Regex.Rule.NUMBER_ENGLISH, this)


