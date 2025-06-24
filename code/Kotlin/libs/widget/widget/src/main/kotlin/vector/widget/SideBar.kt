package vector.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt
import vector.Constants
import vector.app.ext.drawTextInXAlign
import vector.app.os.dp

fun interface OnTouchLetterListener {
    fun onTouch(index: Int, s: String, isFocus: Boolean)
}

class SideBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class Align {
        TOP,
        CENTER,
        BOTTOM
    }

    companion object {
        private val COLOR_NORMAL = "#3A3F5C".toColorInt()
        private val COLOR_FOCUS = "#0091FF".toColorInt()
        private const val TEXT_SIZE_DP = 11
        private const val TEXT_GAP_DP = 5
        private val SELECTIONS = arrayOf(
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z"
        )
    }

    @ColorInt
    var color = COLOR_NORMAL // 画笔颜色

    @ColorInt
    var colorFocus = COLOR_FOCUS

    private var listener: OnTouchLetterListener? = null

    private val paint = Paint()
    private var choose = Constants.ERR_NOT_FOUND

    private var areaHeight: Int = 0 //  文字所在区域的高度
    private var totalHeight: Int = 0 // 整个画布的实际高度

    var textSize: Int = 0
    var textGap: Int = 0

    // TODO: 拓展文字对齐方式
    var align = Align.TOP

    private var lastSelect: Boolean = false

    // 准备好的A~Z的字母数组
    var selections: Array<String> = SELECTIONS
        set(value) {
            field = value
            requestLayout()
        }

    init {
        textSize = TEXT_SIZE_DP.dp.toPx(context)
        textGap = TEXT_GAP_DP.dp.toPx(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
            measureHeight()
        )
    }

    private fun measureHeight(): Int {
        areaHeight = textSize + textGap
        totalHeight = areaHeight * selections.size
        return totalHeight
    }

    override fun onDraw(canvas: Canvas) {
        val posX = width / 2f // x 坐标统一
        selections.forEachIndexed { index, s ->
            // 重新设置画笔
            paint.reset()

            // 设置字体格式
            paint.textSize = textSize.toFloat()
            paint.isAntiAlias = true

            // 如果这一项被选中，则换一种颜色画
            if (index == choose) {
                paint.color = colorFocus
                paint.isFakeBoldText = true
            } else {
                paint.color = color
            }

            // 要画的字母的x,y坐标
            val posY = (index * areaHeight).toFloat()

            // 画出字母
            canvas.drawTextInXAlign(s, posX, posY, paint, Paint.Align.CENTER)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val y = event.y

        // 算出点击的字母的索引
        val index = (y / areaHeight).toInt()

        // 保存上次点击的字母的索引到oldChoose
        val oldChoose = choose
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (oldChoose != index && index >= 0 && index < selections.size) {
                    choose = index
                    invalidate()

                    listener?.onTouch(index, selections[index], true)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (oldChoose != index && index >= 0 && index < selections.size) {
                    choose = index
                    invalidate()

                    listener?.onTouch(index, selections[index], true)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!lastSelect)
                    choose = -1
                invalidate()

                if (index <= 0) {
                    listener?.onTouch(0, selections[0], false)
                } else if (index >= 0 && index < selections.size) {
                    listener?.onTouch(index, selections[index], false)
                } else if (index >= selections.size) {
                    listener?.onTouch(selections.size - 1, selections[selections.size - 1], false)
                }
            }
        }

        return true
    }

    /**
     * 回调方法，注册监听器
     *
     * @param listener
     */
    fun setOnTouchLetterChangeListener(listener: OnTouchLetterListener) {
        this.listener = listener
    }
}