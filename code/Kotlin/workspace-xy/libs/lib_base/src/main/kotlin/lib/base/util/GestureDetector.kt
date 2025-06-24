package lib.base.util

import android.graphics.PointF
import android.view.MotionEvent

/**
 * 双指手势判断
 *
 * @author yuansui
 */
class GestureDetector {

    interface OnGestureListener {
        fun onMoveUp()

        fun onMoveDown()
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerCount = event.pointerCount
        if (pointerCount < 2 || pointerCount > 2) {
            // 最后不是双指落下
            return false
        }

        // 判断互相靠近, x和y坐标互相减的绝对值其中一个在变小代表在靠近
        val p0X = event.getX(0)
        val p0Y = event.getY(0)
        val p1X = event.getX(1)
        val p1Y = event.getY(1)

        val highPoint = PointF()
        val lowPoint = PointF()

        if (p0Y < p1Y) {
            // 0为在上面的落点
            highPoint.set(p0X, p0Y)
            lowPoint.set(p1X, p1Y)
        } else {
            // 1为在上面的落点
            highPoint.set(p1X, p1Y)
            lowPoint.set(p0X, p0Y)
        }

        //        TGestureType gestureType = TouchOptUtil.getVerticalGesture(highPoint, lowPoint, mPointDownHigh, mPointDownLow);
        //        if (gestureType == TGestureType.ENone) {
        //            return false;
        //        }
        return false
    }
}
