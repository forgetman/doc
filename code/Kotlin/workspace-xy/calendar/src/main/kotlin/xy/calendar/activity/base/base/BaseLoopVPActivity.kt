package xy.calendar.activity.base.base

import android.view.ScaleGestureDetector
import vector.app.activity.ActivityEx
import vector.app.viewmodel.ViewModelEx

/**
 * @author yuansui
 * @since 2018/5/17
 */
abstract class BaseLoopVPActivity<VM : ViewModelEx> : ActivityEx<VM>() {

    private var prePosition: Int = 0
    private var scaleDetector: ScaleGestureDetector? = null

    override fun initializeData() {
        scaleDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                return onDetectorScale(detector.scaleFactor)
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {

            }
        })
    }

    override fun initializeContentView() {
        //        super.setViews()
//
//        setCurrPosition(Integer.MAX_VALUE / 2, false)
//        prePosition = getCurrPosition()
//
//        setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
//
//            override fun onPageSelected(position: Int) {
//                this@BaseLoopVPActivity.onPageSelected(position, prePosition)
//                prePosition = position
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {}
//        })
//
//        viewPager.setOnTouchListener { v, event ->
//            scaleDetector?.onTouchEvent(event)
//            false
//        }
    }

//    override fun createPagerAdapter(): FragPagerAdapterEx<*> {
//        return object : FragPagerAdapterImpl(supportFragmentManager) {
//            override val isLoop: Boolean
//                get() = true
//        }
//    }

    protected abstract fun onDetectorScale(scaleFactor: Float): Boolean
    protected abstract fun onPageSelected(position: Int, prePosition: Int)
}