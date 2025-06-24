package vector.widget.photoview

import android.view.MotionEvent
import kotlin.math.abs
import kotlin.math.atan

interface OnRotateListener {
    fun onRotate(degrees: Float, focusX: Float, focusY: Float)
}

class RotateGestureDetector(private val listener: OnRotateListener) {

    companion object {
        private const val MAX_DEGREES_STEP = 120
    }

    private var prevSlope: Float = 0f
    private var currSlope: Float = 0f

    private var x1: Float = 0f
    private var y1: Float = 0f
    private var x2: Float = 0f
    private var y2: Float = 0f

    fun onTouchEvent(event: MotionEvent) {

        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP -> if (event.pointerCount == 2) {
                prevSlope = calculateSlope(event)
            }
            MotionEvent.ACTION_MOVE -> if (event.pointerCount > 1) {
                currSlope = calculateSlope(event)

                val currDegrees = Math.toDegrees(atan(currSlope.toDouble()))
                val prevDegrees = Math.toDegrees(atan(prevSlope.toDouble()))

                val deltaSlope = currDegrees - prevDegrees

                if (abs(deltaSlope) <= MAX_DEGREES_STEP) {
                    listener.onRotate(deltaSlope.toFloat(), (x2 + x1) / 2, (y2 + y1) / 2)
                }
                prevSlope = currSlope
            }
            else -> Unit
        }
    }

    private fun calculateSlope(event: MotionEvent): Float {
        x1 = event.getX(0)
        y1 = event.getY(0)
        x2 = event.getX(1)
        y2 = event.getY(1)
        return (y2 - y1) / (x2 - x1)
    }

}