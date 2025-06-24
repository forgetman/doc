package lib.base.model

import android.graphics.Paint.Align

enum class ElemType {
    BMP,
    TEXT,
    SIZE
}

abstract class Elem {

    abstract val type: ElemType

    /**
     * public
     */
    var align = Align.LEFT
    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f

    /**
     * bmp
     */
    var offsetX = 0f
    var offsetY = 0f
    var degree = 0

    /**
     * text
     */
    var size = 0f
//    shadowColor,
//    useShadow,
//    text_shadow_color,
//    shadowOffsetX,
//    shadowOffsetY
}

class SizeElem : Elem() {
    override val type: ElemType = ElemType.SIZE
}

class TextElem : Elem() {
    override val type: ElemType = ElemType.TEXT
}

class BmpElem : Elem() {
    override val type: ElemType = ElemType.BMP
}
