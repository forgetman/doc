
package sugar.ext

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

private const val DEFAULT_INDENT = 4

/**
 * Encodes this object as a human readable JSON string for debugging, such as:
 * ```
 * {
 *     "query": "Pizza",
 *     "locations": [
 *         94043,
 *         90210
 *     ]
 * }
 * ```
 *
 * @param indentSpaces the number of spaces to indent for each level of nesting.
 */
fun String.formatJson(indentSpaces: Int = DEFAULT_INDENT): String {
    return when (val parsed = JSONTokener(this).nextValue()) {
        is JSONObject -> parsed.toString(indentSpaces)
        is JSONArray -> parsed.toString(indentSpaces)
        else -> this
    }
}