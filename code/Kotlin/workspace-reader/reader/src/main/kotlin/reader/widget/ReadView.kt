package reader.widget

import android.content.Context
import android.graphics.Canvas
import android.os.BatteryManager
import android.util.AttributeSet
import android.view.View
import reader.util.PageDrawer
import sugar.ext.systemService

/**
 * @author yuansui
 * @since 2019/11/8
 */
class ReadView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var lines: List<String>? = null
    var pageNumber: String? = null

    override fun onDraw(canvas: Canvas) {
        PageDrawer.drawBackground(canvas)

        canvas.save()
        canvas.translate(paddingStart.toFloat(), paddingTop.toFloat())

        PageDrawer.drawText(canvas, lines)

        val manager = context.systemService<BatteryManager>()
        val level = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        PageDrawer.drawBattery(canvas, level)

        PageDrawer.drawPageNumber(canvas, pageNumber)

        canvas.restore()
    }
}