帧
===

- **帧数** - 帧生成数量的简称
- **帧率(FPS)** - 以帧称为单位的位图图像连续出现在显示器上的频率, 又称为FPS
- **最大帧率** - 手机的屏幕一般为60HZ，其他设备有可以达到90HZ
- **肉眼识别** - 一般肉眼能接受30HZ左右的就觉得不怎么卡了，但是时间长了可能会有点难受
- **计算** - 简单的来说，就是每秒实际调用重绘的次数
    ```kotlin
    private var calcCount = 0
    private var drawCount = 0
    private var lastFrameTime = System.currentTimeMillis()
    
    override fun onDraw(canvas: Canvas) {
        calcCount++
    
        val curr = System.currentTimeMillis()
        val interval = curr - lastFrameTime
        if (interval >= TimeUnit.SECONDS.toMillis(1)) {
            drawCount = calcCount
            calcCount = 0
            lastFrameTime = curr
            L.d("FPS = $drawCount")
        }
    }
    ```

Interpolator(插值器)
===

- **类型(系统提供)**
    1. Linear
    2. Accelerate
    3. Decelerate
    4. OverShoot
    5. Bounds
- **实际运用举例**
    ```kotlin
    override fun onDraw(canvas: Canvas) {
        val interval = System.currentTimeMillis() - startTime
        var timeFactor = interval / 2000
        if (timeFactor >= 1) {
            // 动画达到满帧
            timeFactor = 1
        }
        L.d("timeFactor = $timeFactor")
        
        val factor = interpolator?.getInterpolation(timeFactor) ?: return
        L.d("factor = $factor")
        // draw something
    }
    ```
    

