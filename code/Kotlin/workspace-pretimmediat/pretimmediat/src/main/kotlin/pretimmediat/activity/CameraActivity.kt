package pretimmediat.activity

import android.hardware.camera2.CameraCharacteristics
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.FragmentContainerView
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import pretimmediat.R
import pretimmediat.ext.findCameraFormat
import pretimmediat.fragment.CameraFragment
import pretimmediat.fragment.CameraFragmentCreator
import vector.app.activity.SimpleActivityEx
import vector.app.ext.bind.bindView
import vector.app.ext.view.setOnDebounceClickListener
import vector.app.util.inflate

@Creator(forResult = true)
class CameraActivity : SimpleActivityEx() {

    @Extra
    var facing: Int = 0

    private val container by bindView<FragmentContainerView>(R.id.layout_container)
    private val captureButton by bindView<ImageButton>(R.id.capture_button)
    private val ivCancel by bindView<View>(R.id.iv_cancel)
    private val ivFlip by bindView<View>(R.id.iv_flip)

    private val frontFormat by lazy { findCameraFormat(this, CameraCharacteristics.LENS_FACING_FRONT) }
    private val backFormat by lazy { findCameraFormat(this, CameraCharacteristics.LENS_FACING_BACK) }

    private var currFrag: CameraFragment? = null

    override fun createContentView(): View {
        return R.layout.activity_camera.inflate(this)
    }

    override fun initializeContentView() {
        val frag = createFragment(facing)
        if (frag == null) {
            finish()
            return
        }
        currFrag = frag

        supportFragmentManager.beginTransaction().replace(container.id, frag).commit()

        captureButton.setOnDebounceClickListener {
            currFrag?.startTakePhoto()
        }

        ivCancel.setOnClickListener {
            finish()
        }

        ivFlip.setOnDebounceClickListener {
            switchCamera()
        }
    }

    /**
     * 反转摄像头
     */
    private fun switchCamera() {
        val newFacing = if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
            CameraCharacteristics.LENS_FACING_BACK
        } else {
            CameraCharacteristics.LENS_FACING_FRONT
        }
        val newFrag = createFragment(newFacing) ?: return
        supportFragmentManager.beginTransaction().replace(container.id, newFrag).commit()
        currFrag = newFrag
        facing = newFacing
    }

    private fun createFragment(facing: Int) = when {
        facing == CameraCharacteristics.LENS_FACING_FRONT && frontFormat != null -> {
            CameraFragmentCreator.create(frontFormat!!.cameraId, frontFormat!!.imageFormat).get()
        }

        facing == CameraCharacteristics.LENS_FACING_BACK && backFormat != null -> {
            CameraFragmentCreator.create(backFormat!!.cameraId, backFormat!!.imageFormat).get()
        }

        else -> null
    }
}
