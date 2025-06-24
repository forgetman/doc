@file:Suppress("unused")

package vector.widget.photoview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.OverScroller
import android.widget.Scroller
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import logger.L
import sugar.ext.ifNotNull
import vector.app.util.toDrawable
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 网上Java代码改版, 核心逻辑没变
 */
class PhotoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    companion object {
        private const val MIN_ROTATE = 35
        private const val ANIM_DURING = 340
        private const val MAX_SCALE = 2.5f

        private fun getDrawableWidth(d: Drawable): Int {
            var width = d.intrinsicWidth
            if (width <= 0) {
                width = d.minimumWidth
            }
            if (width <= 0) {
                width = d.bounds.width()
            }
            return width
        }

        private fun getDrawableHeight(d: Drawable): Int {
            var height = d.intrinsicHeight
            if (height <= 0) {
                height = d.minimumHeight
            }
            if (height <= 0) {
                height = d.bounds.height()
            }
            return height
        }

        fun getImageViewInfo(imgView: ImageView): Info {
            val p = IntArray(2)
            getLocation(imgView, p)

            val drawable = imgView.drawable

            val matrix = imgView.imageMatrix

            val width = getDrawableWidth(drawable)
            val height = getDrawableHeight(drawable)

            val imgRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            matrix.mapRect(imgRect)

            val rect = RectF(
                p[0] + imgRect.left,
                p[1] + imgRect.top,
                p[0] + imgRect.right,
                p[1] + imgRect.bottom
            )
            val widgetRect = RectF(0f, 0f, imgView.width.toFloat(), imgView.height.toFloat())

            return Info(
                rect,
                imgRect,
                widgetRect,
                0f,
                imgView.scaleType
            )
        }

        private fun getLocation(target: View, position: IntArray) {

            position[0] += target.left
            position[1] += target.top

            var viewParent = target.parent
            while (viewParent is View) {
                val view = viewParent as View

                if (view.id == android.R.id.content) {
                    return
                }

                position[0] -= view.scrollX
                position[1] -= view.scrollY

                position[0] += view.left
                position[1] += view.top

                viewParent = view.parent
            }

            position[0] = (position[0] + 0.5f).toInt()
            position[1] = (position[1] + 0.5f).toInt()
        }
    }

    // 能支持图片的最大尺寸，默认为 16384 x 16384
    private var maximumBitmapHeight = 16384

    private var minRotate: Int = 0
    /**
     * 获取动画持续时间
     */
    /**
     * 设置动画的持续时间
     */
    var animDuring: Int = 0
    /**
     * 获取最大可以缩放的倍数
     */
    /**
     * 设置最大可以缩放的倍数
     */
    var maxScale: Float = 0.toFloat()

    private var maxOverScroll = 0
    private var maxFlingOverScroll = 0
    private var maxOverResistance = 0
    private var maxAnimFromWaite = 500

    private val baseMatrix = Matrix()
    private val animMatrix = Matrix()
    private val synthesisMatrix = Matrix()
    private val tmpMatrix = Matrix()

    private var rotateDetector: RotateGestureDetector? = null
    private var detector: GestureDetector? = null
    private var scaleDetector: ScaleGestureDetector? = null
    private var clickListener: OnClickListener? = null

    private var innerScaleType: ScaleType = ScaleType.CENTER_INSIDE

    private var hasMultiTouch: Boolean = false
    private var hasDrawable: Boolean = false
    private var isKnowSize: Boolean = false
    private var hasOverTranslate: Boolean = false
    private var isEnable = false
    private var isRotateEnable = false
    private var isInit: Boolean = false
    private var innerAdjustViewBounds: Boolean = false

    // 当前是否处于放大状态
    private var isZoomUp: Boolean = false
    private var canRotate: Boolean = false

    private var imgLargeWidth: Boolean = false
    private var imgLargeHeight: Boolean = false

    private var rotateFlag: Float = 0f
    private var degrees: Float = 0f
    private var scale = 1.0f
    private var translateX: Int = 0
    private var translateY: Int = 0

    private var halfBaseRectWidth: Float = 0.toFloat()
    private var halfBaseRectHeight: Float = 0.toFloat()

    private val widgetRect = RectF()
    private val baseRect = RectF()
    private val imgRect = RectF()
    private val tmpRect = RectF()
    private val commonRect = RectF()

    private val screenCenter = PointF()
    private val scaleCenter = PointF()
    private val rotateCenter = PointF()

    private val translate = Transform()

    private var clip: RectF? = null
    private var fromInfo: Info? = null
    private var infoTime: Long = 0
    private var completeCallBack1: Runnable? = null

    private var longClick: OnLongClickListener? = null

    /**
     * 获取默认的动画持续时间
     */
    val defaultAnimDuring: Int
        get() = ANIM_DURING

    private val rotateListener = object : OnRotateListener {

        override fun onRotate(degrees: Float, focusX: Float, focusY: Float) {
            rotateFlag += degrees
            if (canRotate) {
                this@PhotoView.degrees += degrees
                animMatrix.postRotate(degrees, focusX, focusY)
            } else {
                if (abs(rotateFlag) >= minRotate) {
                    canRotate = true
                    rotateFlag = 0f
                }
            }
        }
    }

    private val scaleListener = object : ScaleGestureDetector.OnScaleGestureListener {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor


            if (scaleFactor.isNaN() || scaleFactor.isInfinite()) {
                return false
            }

            scale *= scaleFactor
            //            mScaleCenter.set(detector.getFocusX(), detector.getFocusY());
            animMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
            executeTranslate()
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
        }
    }

    private val clickRunnable = Runnable {
        clickListener?.onClick(this)
    }

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onLongPress(e: MotionEvent) {
            longClick?.onLongClick(this@PhotoView)
        }

        override fun onDown(e: MotionEvent): Boolean {
            hasOverTranslate = false
            hasMultiTouch = false
            canRotate = false
            removeCallbacks(clickRunnable)
            return false
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (hasMultiTouch) {
                return false
            }
            if (!imgLargeWidth && !imgLargeHeight) {
                return false
            }
            if (translate.isRunning) {
                return false
            }

            var vx = velocityX
            var vy = velocityY

            if (imgRect.left.roundToInt() >= widgetRect.left || imgRect.right.roundToInt() <= widgetRect.right) {
                vx = 0f
            }

            if (imgRect.top.roundToInt() >= widgetRect.top || imgRect.bottom.roundToInt() <= widgetRect.bottom) {
                vy = 0f
            }

            if (canRotate || degrees % 90 != 0f) {
                var toDegrees = ((degrees / 90).toInt() * 90).toFloat()
                val remainder = degrees % 90

                if (remainder > 45) {
                    toDegrees += 90f
                } else if (remainder < -45) {
                    toDegrees -= 90f
                }

                translate.withRotate(degrees.toInt(), toDegrees.toInt())

                degrees = toDegrees
            }

            doTranslateReset(imgRect)

            translate.withFling(vx, vy)

            translate.start()
            // onUp(e2);
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            var dx = distanceX
            var dy = distanceY
            if (translate.isRunning) {
                translate.stop()
            }

            if (canScrollHorizontallySelf(dx)) {
                if (dx < 0 && imgRect.left - dx > widgetRect.left) {
                    dx = imgRect.left
                }
                if (dx > 0 && imgRect.right - dx < widgetRect.right) {
                    dx = imgRect.right - widgetRect.right
                }

                animMatrix.postTranslate(-dx, 0f)
                translateX -= dx.toInt()
            } else if (imgLargeWidth || hasMultiTouch || hasOverTranslate) {
                checkRect()
                if (!hasMultiTouch) {
                    if (dx < 0 && imgRect.left - dx > commonRect.left) {
                        dx = resistanceScrollByX(imgRect.left - commonRect.left, dx)
                    }
                    if (dx > 0 && imgRect.right - dx < commonRect.right) {
                        dx = resistanceScrollByX(imgRect.right - commonRect.right, dx)
                    }
                }

                translateX -= dx.toInt()
                animMatrix.postTranslate(-dx, 0f)
                hasOverTranslate = true
            }

            if (canScrollVerticallySelf(dy)) {
                if (dy < 0 && imgRect.top - dy > widgetRect.top) {
                    dy = imgRect.top
                }
                if (dy > 0 && imgRect.bottom - dy < widgetRect.bottom) {
                    dy = imgRect.bottom - widgetRect.bottom
                }

                animMatrix.postTranslate(0f, -dy)
                translateY -= dy.toInt()
            } else if (imgLargeHeight || hasOverTranslate || hasMultiTouch) {
                checkRect()
                if (!hasMultiTouch) {
                    if (dy < 0 && imgRect.top - dy > commonRect.top) {
                        dy = resistanceScrollByY(imgRect.top - commonRect.top, dy)
                    }
                    if (dy > 0 && imgRect.bottom - dy < commonRect.bottom) {
                        dy = resistanceScrollByY(imgRect.bottom - commonRect.bottom, dy)
                    }
                }

                animMatrix.postTranslate(0f, -dy)
                translateY -= dy.toInt()
                hasOverTranslate = true
            }

            executeTranslate()
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            postDelayed(clickRunnable, 250)
            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {

            translate.stop()

            val imgcx = imgRect.left + imgRect.width() / 2
            val imgcy = imgRect.top + imgRect.height() / 2

            scaleCenter.set(imgcx, imgcy)
            rotateCenter.set(imgcx, imgcy)
            translateX = 0
            translateY = 0

            val from: Float
            val to: Float
            if (isZoomUp) {
                from = scale
                to = 1f
            } else {
                from = scale
                to = maxScale

                scaleCenter.set(e.x, e.y)
            }

            tmpMatrix.reset()
            tmpMatrix.postTranslate(-baseRect.left, -baseRect.top)
            tmpMatrix.postTranslate(rotateCenter.x, rotateCenter.y)
            tmpMatrix.postTranslate(-halfBaseRectWidth, -halfBaseRectHeight)
            tmpMatrix.postRotate(degrees, rotateCenter.x, rotateCenter.y)
            tmpMatrix.postScale(to, to, scaleCenter.x, scaleCenter.y)
            tmpMatrix.postTranslate(translateX.toFloat(), translateY.toFloat())
            tmpMatrix.mapRect(tmpRect, baseRect)
            doTranslateReset(tmpRect)

            isZoomUp = !isZoomUp
            translate.withScale(from, to)
            translate.start()

            return false
        }
    }

    val info: Info
        get() {
            val rect = RectF()
            val p = IntArray(2)
            getLocation(this, p)
            rect.set(
                p[0] + imgRect.left,
                p[1] + imgRect.top,
                p[0] + imgRect.right,
                p[1] + imgRect.bottom
            )
            return Info(
                rect,
                imgRect,
                widgetRect,
                degrees,
                innerScaleType
            )
        }

    init {
        super.setScaleType(ScaleType.MATRIX)

        rotateDetector = RotateGestureDetector(rotateListener)
        detector = GestureDetector(context, gestureListener)
        scaleDetector = ScaleGestureDetector(context, scaleListener)
        val density = resources.displayMetrics.density
        maxOverScroll = (density * 30).toInt()
        maxFlingOverScroll = (density * 30).toInt()
        maxOverResistance = (density * 140).toInt()

        minRotate = MIN_ROTATE
        animDuring = ANIM_DURING
        maxScale = MAX_SCALE
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        clickListener = l
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType == ScaleType.MATRIX) {
            return
        }

        if (this.innerScaleType != scaleType) {
            this.innerScaleType = scaleType

            if (isInit) {
                initBase()
            }
        }
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        longClick = l
    }

    /**
     * 设置动画的插入器
     */
    fun setInterpolator(interpolator: Interpolator) {
        translate.setInterpolator(interpolator)
    }

    /**
     * 启用缩放功能
     */
    fun enable() {
        isEnable = true
    }

    /**
     * 禁用缩放功能
     */
    fun disable() {
        isEnable = false
    }

    /**
     * 启用旋转功能
     */
    fun enableRotate() {
        isRotateEnable = true
    }

    /**
     * 禁用旋转功能
     */
    fun disableRotate() {
        isRotateEnable = false
    }

    fun setMaxAnimFromWaiteTime(wait: Int) {
        maxAnimFromWaite = wait
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        var drawable: Drawable? = null
        try {
            drawable = resId.toDrawable(context)
        } catch (e: Exception) {
            L.e(e)
        }

        setImageDrawable(drawable)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)

        if (drawable == null) {
            hasDrawable = false
            return
        }

        if (!hasSize(drawable)) {
            return
        }

        if (!hasDrawable) {
            hasDrawable = true
        }

        initBase()
    }

    private fun hasSize(d: Drawable): Boolean {
        return !((d.intrinsicHeight <= 0 || d.intrinsicWidth <= 0)
            && (d.minimumWidth <= 0 || d.minimumHeight <= 0)
            && (d.bounds.width() <= 0 || d.bounds.height() <= 0))
    }

    private fun initBase() {
        if (!hasDrawable) {
            return
        }
        if (!isKnowSize) {
            return
        }

        baseMatrix.reset()
        animMatrix.reset()

        isZoomUp = false

        val img = drawable

        val w = width
        val h = height
        val imgW = getDrawableWidth(img)
        val imgH = getDrawableHeight(img)

        baseRect.set(0f, 0f, imgW.toFloat(), imgH.toFloat())

        // 以图片中心点居中位移
        val tx = (w - imgW) / 2
        val ty = (h - imgH) / 2

        var sx = 1f
        var sy = 1f

        // 缩放，默认不超过屏幕大小
        if (imgW > w) {
            sx = w.toFloat() / imgW
        }

        if (imgH > h) {
            sy = h.toFloat() / imgH
        }

        val scale = if (sx < sy) sx else sy

        baseMatrix.reset()
        baseMatrix.postTranslate(tx.toFloat(), ty.toFloat())
        baseMatrix.postScale(scale, scale, screenCenter.x, screenCenter.y)
        baseMatrix.mapRect(baseRect)

        halfBaseRectWidth = baseRect.width() / 2
        halfBaseRectHeight = baseRect.height() / 2

        // 动态计算长图的最大缩放比例(图片太长的话，会导致等比压缩后宽度变得很小，
        // 所以，需要动态计算图片缩放后的宽度与屏幕宽度的比例，保证最大缩放比时，图片宽度能大于屏幕宽度)
        if (screenCenter.x > 0 && halfBaseRectWidth > 0 && screenCenter.x / halfBaseRectWidth > 1) {
            maxScale = screenCenter.x / halfBaseRectWidth + 1
        }

        scaleCenter.set(screenCenter)
        rotateCenter.set(scaleCenter)

        executeTranslate()

        when (innerScaleType) {
            ScaleType.CENTER -> initCenter()
            ScaleType.CENTER_CROP -> initCenterCrop()
            ScaleType.CENTER_INSIDE -> initCenterInside()
            ScaleType.FIT_CENTER -> initFitCenter()
            ScaleType.FIT_START -> initFitStart()
            ScaleType.FIT_END -> initFitEnd()
            ScaleType.FIT_XY -> initFitXY()
            else -> Unit
        }

        isInit = true

        fromInfo.ifNotNull {
            if (System.currentTimeMillis() - infoTime < maxAnimFromWaite) animFrom(it)
        }

        fromInfo = null
    }

    private fun initCenter() {
        if (!hasDrawable) {
            return
        }
        if (!isKnowSize) {
            return
        }

        val img = drawable

        val imgW = getDrawableWidth(img)
        val imgH = getDrawableHeight(img)

        if (imgW > widgetRect.width() || imgH > widgetRect.height()) {
            val scaleX = imgW / imgRect.width()
            val scaleY = imgH / imgRect.height()

            scale = if (scaleX > scaleY) scaleX else scaleY

            animMatrix.postScale(scale, scale, screenCenter.x, screenCenter.y)

            executeTranslate()

            resetBase()
        }
    }

    private fun initCenterCrop() {
        if (imgRect.width() < widgetRect.width() || imgRect.height() < widgetRect.height()) {
            val scaleX = widgetRect.width() / imgRect.width()
            val scaleY = widgetRect.height() / imgRect.height()

            scale = if (scaleX > scaleY) scaleX else scaleY

            animMatrix.postScale(scale, scale, screenCenter.x, screenCenter.y)

            executeTranslate()
            resetBase()
        }
    }

    private fun initCenterInside() {
        if (imgRect.width() > widgetRect.width() || imgRect.height() > widgetRect.height()) {
            val scaleX = widgetRect.width() / imgRect.width()
            val scaleY = widgetRect.height() / imgRect.height()

            scale = if (scaleX < scaleY) scaleX else scaleY

            animMatrix.postScale(scale, scale, screenCenter.x, screenCenter.y)

            executeTranslate()
            resetBase()
        }
    }

    private fun initFitCenter() {
        if (imgRect.width() < widgetRect.width()) {
            scale = widgetRect.width() / imgRect.width()

            animMatrix.postScale(scale, scale, screenCenter.x, screenCenter.y)

            executeTranslate()
            resetBase()
        }
    }

    private fun initFitStart() {
        initFitCenter()

        val ty = -imgRect.top
        animMatrix.postTranslate(0f, ty)
        executeTranslate()
        resetBase()
        translateY += ty.toInt()
    }

    private fun initFitEnd() {
        initFitCenter()

        val ty = widgetRect.bottom - imgRect.bottom
        translateY += ty.toInt()
        animMatrix.postTranslate(0f, ty)
        executeTranslate()
        resetBase()
    }

    private fun initFitXY() {
        val scaleX = widgetRect.width() / imgRect.width()
        val scaleY = widgetRect.height() / imgRect.height()

        animMatrix.postScale(scaleX, scaleY, screenCenter.x, screenCenter.y)

        executeTranslate()
        resetBase()
    }

    private fun resetBase() {
        val img = drawable
        val imgW = getDrawableWidth(img)
        val imgH = getDrawableHeight(img)
        baseRect.set(0f, 0f, imgW.toFloat(), imgH.toFloat())
        baseMatrix.set(synthesisMatrix)
        baseMatrix.mapRect(baseRect)
        halfBaseRectWidth = baseRect.width() / 2
        halfBaseRectHeight = baseRect.height() / 2
        scale = 1f
        translateX = 0
        translateY = 0
        animMatrix.reset()
    }

    private fun executeTranslate() {
        synthesisMatrix.set(baseMatrix)
        synthesisMatrix.postConcat(animMatrix)
        imageMatrix = synthesisMatrix

        animMatrix.mapRect(imgRect, baseRect)

        imgLargeWidth = imgRect.width() > widgetRect.width()
        imgLargeHeight = imgRect.height() > widgetRect.height()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!hasDrawable) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        val d = drawable
        val drawableW = getDrawableWidth(d)
        val drawableH = getDrawableHeight(d)

        val pWidth = MeasureSpec.getSize(widthMeasureSpec)
        val pHeight = MeasureSpec.getSize(heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var width: Int
        var height: Int

        var p: ViewGroup.LayoutParams? = layoutParams

        if (p == null) {
            p = LayoutParamsFactory.viewGroup(WRAP_CONTENT, WRAP_CONTENT)
        }

        width = when {
            p.width != MATCH_PARENT -> when (widthMode) {
                MeasureSpec.EXACTLY -> pWidth
                MeasureSpec.AT_MOST -> if (drawableW > pWidth) pWidth else drawableW
                else -> drawableW
            }

            else -> if (widthMode == MeasureSpec.UNSPECIFIED) drawableW else pWidth
        }

        height = when {
            p.height != MATCH_PARENT -> when (heightMode) {
                MeasureSpec.EXACTLY -> pHeight
                MeasureSpec.AT_MOST -> if (drawableH > pHeight) pHeight else drawableH
                else -> drawableH
            }

            else -> if (widthMode == MeasureSpec.UNSPECIFIED) drawableH else pHeight
        }

        if (innerAdjustViewBounds && drawableW.toFloat() / drawableH != width.toFloat() / height) {

            val hScale = height.toFloat() / drawableH
            val wScale = width.toFloat() / drawableW

            val scale = if (hScale < wScale) hScale else wScale
            width = if (p.width == MATCH_PARENT) width else (drawableW * scale).toInt()
            height = if (p.height == MATCH_PARENT) height else (drawableH * scale).toInt()
        }

        setMeasuredDimension(width, height)
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        super.setAdjustViewBounds(adjustViewBounds)
        this.innerAdjustViewBounds = adjustViewBounds
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        widgetRect.set(0f, 0f, w.toFloat(), h.toFloat())
        screenCenter.set((w / 2).toFloat(), (h / 2).toFloat())

        if (!isKnowSize) {
            isKnowSize = true
            initBase()
        }
    }

    override fun draw(canvas: Canvas) {
        if (clip != null) {
            canvas.clipRect(clip!!)
            clip = null
        }
        maximumBitmapHeight = canvas.maximumBitmapHeight
        super.draw(canvas)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (isEnable) {
            val action = event.actionMasked
            if (event.pointerCount >= 2) {
                hasMultiTouch = true
            }

            detector!!.onTouchEvent(event)
            if (isRotateEnable) {
                rotateDetector!!.onTouchEvent(event)
            }
            scaleDetector!!.onTouchEvent(event)

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                onUp()
            }

            return true
        } else {
            return super.dispatchTouchEvent(event)
        }
    }

    private fun onUp() {
        if (translate.isRunning) {
            return
        }

        if (canRotate || degrees % 90 != 0f) {
            var toDegrees = ((degrees / 90).toInt() * 90).toFloat()
            val remainder = degrees % 90

            if (remainder > 45) {
                toDegrees += 90f
            } else if (remainder < -45) {
                toDegrees -= 90f
            }

            translate.withRotate(degrees.toInt(), toDegrees.toInt())

            degrees = toDegrees
        }

        var scale = scale

        if (this.scale < 1) {
            scale = 1f
            translate.withScale(this.scale, 1f)
        } else if (this.scale > maxScale) {
            scale = maxScale
            translate.withScale(this.scale, maxScale)
        }

        val cx = imgRect.left + imgRect.width() / 2
        val cy = imgRect.top + imgRect.height() / 2

        scaleCenter.set(cx, cy)
        rotateCenter.set(cx, cy)

        translateX = 0
        translateY = 0

        tmpMatrix.reset()
        tmpMatrix.postTranslate(-baseRect.left, -baseRect.top)
        tmpMatrix.postTranslate(cx - halfBaseRectWidth, cy - halfBaseRectHeight)
        tmpMatrix.postScale(scale, scale, cx, cy)
        tmpMatrix.postRotate(degrees, cx, cy)
        tmpMatrix.mapRect(tmpRect, baseRect)

        doTranslateReset(tmpRect)
        translate.start()
    }

    private fun doTranslateReset(imgRect: RectF) {
        var tx = 0
        var ty = 0

        if (imgRect.width() <= widgetRect.width()) {
            if (!isImageCenterWidth(imgRect)) {
                tx = -((widgetRect.width() - imgRect.width()) / 2 - imgRect.left).toInt()
            }
        } else {
            if (imgRect.left > widgetRect.left) {
                tx = (imgRect.left - widgetRect.left).toInt()
            } else if (imgRect.right < widgetRect.right) {
                tx = (imgRect.right - widgetRect.right).toInt()
            }
        }

        if (imgRect.height() <= widgetRect.height()) {
            if (!isImageCenterHeight(imgRect)) {
                ty = -((widgetRect.height() - imgRect.height()) / 2 - imgRect.top).toInt()
            }
        } else {
            if (imgRect.top > widgetRect.top) {
                ty = (imgRect.top - widgetRect.top).toInt()
            } else if (imgRect.bottom < widgetRect.bottom) {
                ty = (imgRect.bottom - widgetRect.bottom).toInt()
            }
        }

        if (tx != 0 || ty != 0) {
            if (!translate.flingScroller.isFinished) {
                translate.flingScroller.abortAnimation()
            }
            translate.withTranslate(-tx, -ty)
        }
    }

    private fun isImageCenterHeight(rect: RectF): Boolean {
        return abs(rect.top.roundToInt() - (widgetRect.height() - rect.height()) / 2) < 1
    }

    private fun isImageCenterWidth(rect: RectF): Boolean {
        return abs(rect.left.roundToInt() - (widgetRect.width() - rect.width()) / 2) < 1
    }

    private fun resistanceScrollByX(overScroll: Float, detalX: Float): Float {
        return detalX * (abs(abs(overScroll) - maxOverResistance) / maxOverResistance.toFloat())
    }

    private fun resistanceScrollByY(overScroll: Float, detalY: Float): Float {
        return detalY * (abs(abs(overScroll) - maxOverResistance) / maxOverResistance.toFloat())
    }

    /**
     * 匹配两个Rect的共同部分输出到out，若无共同部分则输出0，0，0，0
     */
    private fun mapRect(r1: RectF, r2: RectF, out: RectF) {

        val l: Float = if (r1.left > r2.left) r1.left else r2.left
        val r: Float = if (r1.right < r2.right) r1.right else r2.right
        val t: Float = if (r1.top > r2.top) r1.top else r2.top
        val b: Float = if (r1.bottom < r2.bottom) r1.bottom else r2.bottom

        if (l > r) {
            out.set(0f, 0f, 0f, 0f)
            return
        }

        if (t > b) {
            out.set(0f, 0f, 0f, 0f)
            return
        }

        out.set(l, t, r, b)
    }

    private fun checkRect() {
        if (!hasOverTranslate) {
            mapRect(widgetRect, imgRect, commonRect)
        }
    }

    fun canScrollHorizontallySelf(direction: Float): Boolean {
        if (imgRect.width() <= widgetRect.width()) {
            return false
        }
        if (direction < 0 && imgRect.left.roundToInt() - direction >= widgetRect.left) {
            return false
        }
        return !(direction > 0 && imgRect.right.roundToInt() - direction <= widgetRect.right)
    }

    fun canScrollVerticallySelf(direction: Float): Boolean {
        if (imgRect.height() <= widgetRect.height()) {
            return false
        }
        if (direction < 0 && imgRect.top.roundToInt() - direction >= widgetRect.top) {
            return false
        }
        return !(direction > 0 && imgRect.bottom.roundToInt() - direction <= widgetRect.bottom)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return if (hasMultiTouch) {
            true
        } else canScrollHorizontallySelf(direction.toFloat())
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return if (hasMultiTouch) {
            true
        } else canScrollVerticallySelf(direction.toFloat())
    }

    private inner class InterpolatorProxy : Interpolator {

        private var target: Interpolator? = null

        init {
            target = DecelerateInterpolator()
        }

        fun setTargetInterpolator(interpolator: Interpolator) {
            target = interpolator
        }

        override fun getInterpolation(input: Float): Float {
            return if (target != null) {
                target!!.getInterpolation(input)
            } else input
        }
    }

    private inner class Transform : Runnable {

        var isRunning: Boolean = false

        var translateScroller: OverScroller
        var flingScroller: OverScroller
        var scaleScroller: Scroller
        var clipScroller: Scroller
        var rotateScroller: Scroller

        lateinit var calculate: ClipCalculate

        var lastFlingX: Int = 0
        var mLastFlingY: Int = 0

        var mLastTranslateX: Int = 0
        var mLastTranslateY: Int = 0

        var clipRect = RectF()

        var interpolatorProxy = InterpolatorProxy()

        init {
            val ctx = context
            translateScroller = OverScroller(ctx, interpolatorProxy)
            scaleScroller = Scroller(ctx, interpolatorProxy)
            flingScroller = OverScroller(ctx, interpolatorProxy)
            clipScroller = Scroller(ctx, interpolatorProxy)
            rotateScroller = Scroller(ctx, interpolatorProxy)
        }

        fun setInterpolator(interpolator: Interpolator) {
            interpolatorProxy.setTargetInterpolator(interpolator)
        }

        fun withTranslate(deltaX: Int, deltaY: Int) {
            mLastTranslateX = 0
            mLastTranslateY = 0
            translateScroller.startScroll(0, 0, deltaX, deltaY, animDuring)
        }

        fun withScale(form: Float, to: Float) {
            scaleScroller.startScroll(
                (form * 10000).toInt(),
                0,
                ((to - form) * 10000).toInt(),
                0,
                animDuring
            )
        }

        fun withClip(
            fromX: Float,
            fromY: Float,
            deltaX: Float,
            deltaY: Float,
            d: Int,
            c: ClipCalculate
        ) {
            clipScroller.startScroll(
                (fromX * 10000).toInt(),
                (fromY * 10000).toInt(),
                (deltaX * 10000).toInt(),
                (deltaY * 10000).toInt(),
                d
            )
            this.calculate = c
        }

        fun withRotate(fromDegrees: Int, toDegrees: Int) {
            rotateScroller.startScroll(fromDegrees, 0, toDegrees - fromDegrees, 0, animDuring)
        }

        fun withRotate(fromDegrees: Int, toDegrees: Int, during: Int) {
            rotateScroller.startScroll(fromDegrees, 0, toDegrees - fromDegrees, 0, during)
        }

        fun withFling(velocityX: Float, velocityY: Float) {
            lastFlingX = if (velocityX < 0) Integer.MAX_VALUE else 0
            var distanceX =
                (if (velocityX > 0) abs(imgRect.left) else imgRect.right - widgetRect.right).toInt()
            distanceX = if (velocityX < 0) Integer.MAX_VALUE - distanceX else distanceX
            var minX = if (velocityX < 0) distanceX else 0
            var maxX = if (velocityX < 0) Integer.MAX_VALUE else distanceX
            val overX = if (velocityX < 0) Integer.MAX_VALUE - minX else distanceX

            mLastFlingY = if (velocityY < 0) Integer.MAX_VALUE else 0
            var distanceY =
                (if (velocityY > 0) abs(imgRect.top) else imgRect.bottom - widgetRect.bottom).toInt()
            distanceY = if (velocityY < 0) Integer.MAX_VALUE - distanceY else distanceY
            var minY = if (velocityY < 0) distanceY else 0
            var maxY = if (velocityY < 0) Integer.MAX_VALUE else distanceY
            val overY = if (velocityY < 0) Integer.MAX_VALUE - minY else distanceY

            if (velocityX == 0f) {
                maxX = 0
                minX = 0
            }

            if (velocityY == 0f) {
                maxY = 0
                minY = 0
            }

            flingScroller.fling(
                lastFlingX,
                mLastFlingY,
                velocityX.toInt(),
                velocityY.toInt(),
                minX,
                maxX,
                minY,
                maxY,
                if (abs(overX) < maxFlingOverScroll * 2) 0 else maxFlingOverScroll,
                if (abs(overY) < maxFlingOverScroll * 2) 0 else maxFlingOverScroll
            )
        }

        fun start() {
            isRunning = true
            postExecute()
        }

        fun stop() {
            removeCallbacks(this)
            translateScroller.abortAnimation()
            scaleScroller.abortAnimation()
            flingScroller.abortAnimation()
            rotateScroller.abortAnimation()
            isRunning = false
        }

        override fun run() {

            // if (!isRunning) return;

            var endAnima = true

            if (scaleScroller.computeScrollOffset()) {
                scale = scaleScroller.currX / 10000f
                endAnima = false
            }

            if (translateScroller.computeScrollOffset()) {
                val tx = translateScroller.currX - mLastTranslateX
                val ty = translateScroller.currY - mLastTranslateY
                translateX += tx
                translateY += ty
                mLastTranslateX = translateScroller.currX
                mLastTranslateY = translateScroller.currY
                endAnima = false
            }

            if (flingScroller.computeScrollOffset()) {
                val x = flingScroller.currX - lastFlingX
                val y = flingScroller.currY - mLastFlingY

                lastFlingX = flingScroller.currX
                mLastFlingY = flingScroller.currY

                translateX += x
                translateY += y
                endAnima = false
            }

            if (rotateScroller.computeScrollOffset()) {
                degrees = rotateScroller.currX.toFloat()
                endAnima = false
            }

            if (clipScroller.computeScrollOffset() || clip != null) {
                val sx = clipScroller.currX / 10000f
                val sy = clipScroller.currY / 10000f
                tmpMatrix.setScale(
                    sx,
                    sy,
                    (imgRect.left + imgRect.right) / 2,
                    calculate.calculateTop()
                )
                tmpMatrix.mapRect(clipRect, imgRect)

                if (sx == 1f) {
                    clipRect.left = widgetRect.left
                    clipRect.right = widgetRect.right
                }

                if (sy == 1f) {
                    clipRect.top = widgetRect.top
                    clipRect.bottom = widgetRect.bottom
                }

                clip = clipRect
            }

            if (!endAnima) {
                applyAnima()
                postExecute()
            } else {
                isRunning = false

                // 修复动画结束后边距有些空隙，
                var needFix = false

                if (imgLargeWidth) {
                    if (imgRect.left > 0) {
                        translateX -= imgRect.left.toInt()
                    } else if (imgRect.right < widgetRect.width()) {
                        translateX -= (widgetRect.width() - imgRect.right).toInt()
                    }
                    needFix = true
                }

                if (imgLargeHeight) {
                    if (imgRect.top > 0) {
                        translateY -= imgRect.top.toInt()
                    } else if (imgRect.bottom < widgetRect.height()) {
                        translateY -= (widgetRect.height() - imgRect.bottom).toInt()
                    }
                    needFix = true
                }

                if (needFix) {
                    applyAnima()
                }

                invalidate()

                if (completeCallBack1 != null) {
                    completeCallBack1!!.run()
                    completeCallBack1 = null
                }
            }
        }

        private fun applyAnima() {
            animMatrix.reset()
            animMatrix.postTranslate(-baseRect.left, -baseRect.top)
            animMatrix.postTranslate(rotateCenter.x, rotateCenter.y)
            animMatrix.postTranslate(-halfBaseRectWidth, -halfBaseRectHeight)
            animMatrix.postRotate(degrees, rotateCenter.x, rotateCenter.y)
            animMatrix.postScale(scale, scale, scaleCenter.x, scaleCenter.y)
            animMatrix.postTranslate(translateX.toFloat(), translateY.toFloat())
            executeTranslate()
        }

        private fun postExecute() {
            if (isRunning) {
                post(this)
            }
        }
    }

    private fun reset() {
        animMatrix.reset()
        executeTranslate()
        scale = 1f
        translateX = 0
        translateY = 0
    }

    interface ClipCalculate {
        fun calculateTop(): Float
    }

    inner class START : ClipCalculate {
        override fun calculateTop(): Float {
            return imgRect.top
        }
    }

    inner class END : ClipCalculate {
        override fun calculateTop(): Float {
            return imgRect.bottom
        }
    }

    inner class OTHER : ClipCalculate {
        override fun calculateTop(): Float {
            return (imgRect.top + imgRect.bottom) / 2
        }
    }

    /**
     * 在PhotoView内部还没有图片的时候同样可以调用该方法
     *
     *
     * 此时并不会播放动画，当给PhotoView设置图片后会自动播放动画。
     *
     *
     * 若等待时间过长也没有给控件设置图片，则会忽略该动画，若要再次播放动画则需要重新调用该方法
     * (等待的时间默认500毫秒，可以通过setMaxAnimFromWaiteTime(int)设置最大等待时间)
     */
    private fun animFrom(info: Info) {
        if (isInit) {
            reset()

            val mine = info

            val scaleX = info.imgRect.width() / mine.imgRect.width()
            val scaleY = info.imgRect.height() / mine.imgRect.height()
            val scale = if (scaleX < scaleY) scaleX else scaleY

            val ocx = info.rect.left + info.rect.width() / 2
            val ocy = info.rect.top + info.rect.height() / 2

            val mcx = mine.rect.left + mine.rect.width() / 2
            val mcy = mine.rect.top + mine.rect.height() / 2

            animMatrix.reset()
            // animMatrix.postTranslate(-baseRect.left, -baseRect.top);
            animMatrix.postTranslate(ocx - mcx, ocy - mcy)
            animMatrix.postScale(scale, scale, ocx, ocy)
            animMatrix.postRotate(info.degrees, ocx, ocy)
            executeTranslate()

            scaleCenter.set(ocx, ocy)
            rotateCenter.set(ocx, ocy)

            translate.withTranslate((-(ocx - mcx)).toInt(), (-(ocy - mcy)).toInt())
            translate.withScale(scale, 1f)
            translate.withRotate(info.degrees.toInt(), 0)

            if (info.widgetRect.width() < info.imgRect.width() || info.widgetRect.height() < info.imgRect.height()) {
                var clipX = info.widgetRect.width() / info.imgRect.width()
                var clipY = info.widgetRect.height() / info.imgRect.height()
                clipX = if (clipX > 1) 1f else clipX
                clipY = if (clipY > 1) 1f else clipY

                val c =
                    if (info.scaleType == ScaleType.FIT_START) START() else if (info.scaleType == ScaleType.FIT_END) END() else OTHER()

                translate.withClip(clipX, clipY, 1 - clipX, 1 - clipY, animDuring / 3, c)

                tmpMatrix.setScale(
                    clipX,
                    clipY,
                    (imgRect.left + imgRect.right) / 2,
                    c.calculateTop()
                )
                tmpMatrix.mapRect(translate.clipRect, imgRect)
                clip = translate.clipRect
            }

            translate.start()
        } else {
            fromInfo = info
            infoTime = System.currentTimeMillis()
        }
    }

    fun animaTo(info: Info, completeCallBack: Runnable) {
        if (isInit) {
            translate.stop()

            translateX = 0
            translateY = 0

            val tcx = info.rect.left + info.rect.width() / 2
            val tcy = info.rect.top + info.rect.height() / 2

            scaleCenter.set(imgRect.left + imgRect.width() / 2, imgRect.top + imgRect.height() / 2)
            rotateCenter.set(scaleCenter)

            // 将图片旋转回正常位置，用以计算
            animMatrix.postRotate(-degrees, scaleCenter.x, scaleCenter.y)
            animMatrix.mapRect(imgRect, baseRect)

            // 缩放
            val scaleX = info.imgRect.width() / baseRect.width()
            val scaleY = info.imgRect.height() / baseRect.height()
            val scale = if (scaleX > scaleY) scaleX else scaleY

            animMatrix.postRotate(degrees, scaleCenter.x, scaleCenter.y)
            animMatrix.mapRect(imgRect, baseRect)

            degrees %= 360

            translate.withTranslate((tcx - scaleCenter.x).toInt(), (tcy - scaleCenter.y).toInt())
            translate.withScale(this.scale, scale)
            translate.withRotate(degrees.toInt(), info.degrees.toInt(), animDuring * 2 / 3)

            if (info.widgetRect.width() < info.rect.width() || info.widgetRect.height() < info.rect.height()) {
                var clipX = info.widgetRect.width() / info.rect.width()
                var clipY = info.widgetRect.height() / info.rect.height()
                clipX = if (clipX > 1) 1f else clipX
                clipY = if (clipY > 1) 1f else clipY

                val cx = clipX
                val cy = clipY
                val c =
                    if (info.scaleType == ScaleType.FIT_START) START() else if (info.scaleType == ScaleType.FIT_END) END() else OTHER()

                postDelayed(
                    { translate.withClip(1f, 1f, -1 + cx, -1 + cy, animDuring / 2, c) },
                    (animDuring / 2).toLong()
                )
            }

            completeCallBack1 = completeCallBack
            translate.start()
        }
    }

    fun rotate(degrees: Float) {
        this.degrees += degrees
        val centerX = (widgetRect.left + widgetRect.width() / 2).toInt()
        val centerY = (widgetRect.top + widgetRect.height() / 2).toInt()

        animMatrix.postRotate(degrees, centerX.toFloat(), centerY.toFloat())
        executeTranslate()
    }
}