@file:Suppress("unused")

package vector.os

data class Size(var width: Int = 0, var height: Int = 0) {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Size) return false
        return this === other
                || (width == other.width && height == other.height)
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        return result
    }

    override fun toString(): String {
        return "width = $width, height = $height"
    }
}

data class SizeF(var width: Float = 0f, var height: Float = 0f) {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is SizeF) return false
        return this === other
                || (width == other.width && height == other.height)
    }

    override fun hashCode(): Int {
        var result = width.hashCode()
        result = 31 * result + height.hashCode()
        return result
    }

}