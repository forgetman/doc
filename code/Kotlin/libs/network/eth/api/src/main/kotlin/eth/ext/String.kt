package eth.ext

import androidx.core.net.toUri
import kotlin.math.absoluteValue

/**
 * @author wuxi
 * @since 2019/3/15
 */
internal inline val String?.downloadName: String
    get() {
        if (this.isNullOrEmpty() || this.toUri().path.isNullOrEmpty()) {
            return System.currentTimeMillis().toString()
        }

        val suffix = if (contains(".")) {
            substring(lastIndexOf("."))
        } else {
            ""
        }
        return "${hashCode().absoluteValue}$suffix"
    }
