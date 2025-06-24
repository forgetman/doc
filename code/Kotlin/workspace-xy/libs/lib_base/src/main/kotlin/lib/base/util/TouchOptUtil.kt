package lib.base.util

import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import android.widget.AbsoluteLayout
import lib.base.Constants
import lib.base.model.TDragDirection
import lib.base.model.TGestureType
import lib.base.model.TMoveDirection

object TouchOptUtil {

    private const val PRESS_OFFSET = 3

    /**
     * 计算画布移动的方向
     *
     * @param move
     * @param isReturn
     * @return
     */
    fun computeMoveDirection(move: Float, isReturn: Boolean): TMoveDirection {
        return if (move < 0) {
            if (isReturn) {
                TMoveDirection.EMoveRight
            } else TMoveDirection.EMoveLeft
        } else if (move > 0) {
            if (isReturn) {
                TMoveDirection.EMoveLeft
            } else TMoveDirection.EMoveRight
        } else {
            TMoveDirection.EMoveNone
        }
    }

    /**
     * 计算手指的操作方向, 上下还是左右
     *
     * @param startEv
     */
    fun computeDragDirection(startEv: MotionEvent, endPoint: PointF): TDragDirection {

        var direction = TDragDirection.EDragNone

        var xMove = getMovement(startEv.x, endPoint.x)
        xMove = Math.abs(xMove)
        var yMove = getMovement(startEv.y, endPoint.y)
        yMove = Math.abs(yMove)

        // 只有yMove大于xMove的KDragYScale倍, 认为是纵向移动
        if (yMove > xMove * Constants.KDragYScale) {
            direction = TDragDirection.EDragVertical
        } else {
            direction = TDragDirection.EDragHorizontal
        }
        return direction
    }

    /**
     * Get the move distance between the start and end
     *
     * @param start
     * @param end
     * @return The result of move distance
     */
    fun getMovement(start: Float, end: Float): Float {
        return end - start
    }

    /**
     * 是否在做捏合操作
     *
     * @param highPoint 新的点high
     * @param lowPoint  先的点low
     * @param downHigh  按下的原点high
     * @param downLow   按下的原点low
     * @return
     */
    fun isPinchGesture(
        highPoint: PointF,
        lowPoint: PointF,
        downHigh: PointF,
        downLow: PointF
    ): Boolean {
        if (highPoint.x > lowPoint.x) {
            // 右手操作: 如果highPoint在右边
            if (downHigh.x - highPoint.x > Constants.KMinPinchDis // high的X在减小

                || highPoint.y - downHigh.y > Constants.KMinPinchDis // high的Y在增加

                || lowPoint.x - downLow.x > Constants.KMinPinchDis // low的X在增加

                || downLow.y - lowPoint.y > Constants.KMinPinchDis // low的Y在减小
            ) {
                return true
            }

        } else {
            // 左手操作: 如果highPoint在左边
            if (highPoint.x - downHigh.x > Constants.KMinPinchDis // high的X在增加

                || highPoint.y - downHigh.y > Constants.KMinPinchDis // high的Y在增加

                || downLow.x - lowPoint.x > Constants.KMinPinchDis // low的X在减小

                || downLow.y - lowPoint.y > Constants.KMinPinchDis // low的Y在减小
            ) {
                return true
            }
        }

        return false
    }

    /**
     * @param newHigh 新的点high
     * @param newLow  新的点low
     * @param orcHigh 按下的原点high
     * @param orcLow  按下的原点low
     * @return
     */
    fun getVerticalGesture(
        newHigh: PointF,
        newLow: PointF,
        orcHigh: PointF,
        orcLow: PointF
    ): TGestureType {
        var gestureType = TGestureType.ENone
        if (newHigh.y - orcHigh.y > Constants.KMinSwitchCityDis // high在往上
            && newLow.y - orcLow.y > Constants.KMinSwitchCityDis // low在往上
        ) {
            gestureType = TGestureType.EMultipleDown
        } else if (orcHigh.y - newHigh.y > Constants.KMinSwitchCityDis // high往下
            && orcLow.y - newLow.y > Constants.KMinSwitchCityDis // low往下
        ) {
            gestureType = TGestureType.EMultipleUp
        }

        return gestureType
    }

    fun getVerticalGesture(newHigh: PointF, orcHigh: PointF): TGestureType {
        var gestureType = TGestureType.ENone
        if (newHigh.y - orcHigh.y > Constants.KMinSwitchCityDis) {
            // high在往上
            gestureType = TGestureType.EMultipleDown
        } else if (orcHigh.y - newHigh.y > Constants.KMinSwitchCityDis) {
            // high往下
            gestureType = TGestureType.EMultipleUp
        }

        return gestureType
    }

    /**
     * 专为设置界面点击效果
     */
    fun absLayoutPressOffset(pressed: Boolean, v: View) {
        val params = v.layoutParams as AbsoluteLayout.LayoutParams
        if (pressed) {
            params.x += PRESS_OFFSET
            params.y += PRESS_OFFSET

        } else {
            params.x -= PRESS_OFFSET
            params.y -= PRESS_OFFSET
        }
        v.layoutParams = params
    }
}
