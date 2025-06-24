package lib.base.util

import android.graphics.Paint.Align
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import vector.config.Config
import vector.util.LayoutParamsFactory
import vector.app.util.Res

object ViewSetter {

    private val scale: Float
        get() = Config.fit().density

    fun textSize(tv: TextView, size: Float) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * scale)
    }

    fun absTv(tv: TextView, w: Float, x: Float, y: Float, align: Align) {
        var vx = x
        when (align) {
            Align.LEFT -> {
                // do nothing
            }

            Align.CENTER -> {
                vx -= w / 2
            }

            Align.RIGHT -> {
                vx -= w
            }
        }

        abs(tv, vx, y)
    }

    fun absTv(tv: TextView, x: Float, y: Float, align: Align) {
        absTv(tv, tv.text.toString(), x, y, align)
    }

    fun absTv(tv: TextView, text: String, x: Float, y: Float, align: Align) {
        var vx = x
        var w = tv.paint.measureText(text)
        w /= scale
        when (align) {
            Align.LEFT -> {
                // do nothing
            }

            Align.CENTER -> {
                vx -= w / 2
            }

            Align.RIGHT -> {
                vx -= w
            }
        }

        abs(tv, vx, y)
    }

    fun abs(v: View, x: Float, y: Float) {
        v.layoutParams = LayoutParamsFactory.abs(x = dealXY(x), y = dealXY(y))
    }

    fun linear(v: View, w: Float, h: Float) {
        v.layoutParams = LayoutParamsFactory.linear(dealWH(w), dealWH(h))
    }

    fun linear(v: View, url: String) {
        val id = BaseBmpMaker.splitResId(url)
        val opts = Res.getBitmapOptions(v.context, id.toInt())
        linear(v, opts.outWidth.toFloat(), opts.outHeight.toFloat())
    }

    fun abs(v: View, w: Float, h: Float, x: Float, y: Float) {
        v.layoutParams = LayoutParamsFactory.abs(dealWH(w), dealWH(h), dealXY(x), dealXY(y))
    }

    fun abs(v: View, url: String, x: Float, y: Float) {
        val id = BaseBmpMaker.splitResId(url)
        val opts = Res.getBitmapOptions(v.context, id.toInt())
        abs(v, opts.outWidth.toFloat(), opts.outHeight.toFloat(), x, y)
    }

    fun abs(v: View, url: String, x: Float, y: Float, align: Align) {
        var vx = x
        val id = BaseBmpMaker.splitResId(url)
        val opts = Res.getBitmapOptions(v.context, id.toInt())

        val w = opts.outWidth
        when (align) {
            Align.LEFT -> {
                // do nothing
            }

            Align.CENTER -> {
                vx -= (w / 2).toFloat()
            }

            Align.RIGHT -> {
                vx -= w.toFloat()
            }
        }
        abs(v, w.toFloat(), opts.outHeight.toFloat(), vx, y)
    }

    private fun dealWH(value: Float): Int {
        return if (value <= 0) {
            value.toInt()
        } else {
            (value * scale).toInt()
        }
    }

    private fun dealXY(value: Float): Int {
        return (value * scale).toInt()
    }
}
