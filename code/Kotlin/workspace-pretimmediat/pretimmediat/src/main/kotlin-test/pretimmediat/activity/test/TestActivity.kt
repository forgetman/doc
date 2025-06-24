package pretimmediat.activity.test

import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.view.View
import android.widget.Button
import compat.ext.getParcelableExtra
import compat.intent.IntentCompat
import logger.L
import pretimmediat.R
import pretimmediat.activity.CameraActivityCreator
import pretimmediat.activity.MainActivity
import pretimmediat.activity.MainActivityCreator
import pretimmediat.activity.user.LoginActivity
import pretimmediat.def.Constants
import vector.app.activity.SimpleActivityEx
import vector.app.ext.bind.bindView
import vector.ext.startActivity
import vector.app.util.inflate

/**
 * 测试页
 */
class TestActivity : SimpleActivityEx() {

    private val btnHome by bindView<Button>(R.id.btn_home)
    private val btnOrder by bindView<Button>(R.id.btn_order)
    private val btnMe by bindView<Button>(R.id.btn_me)
    private val btnPiece by bindView<Button>(R.id.btn_piece)
    private val btnDialog by bindView<Button>(R.id.btn_dialog)
    private val btnLogin by bindView<Button>(R.id.btn_login)
    private val btnCameraFront by bindView<Button>(R.id.btn_camera_front)

    override fun createContentView(): View {
        return R.layout.activity_test.inflate(this)
    }

    override fun initializeContentView() {
        btnHome.setOnClickListener {
            MainActivityCreator.create().requiredTabIndex(MainActivity.TAB_HOME).start(this)
        }

        btnOrder.setOnClickListener {
            MainActivityCreator.create().requiredTabIndex(MainActivity.TAB_ORDER).start(this)
        }

        btnMe.setOnClickListener {
            MainActivityCreator.create().requiredTabIndex(MainActivity.TAB_ME).start(this)
        }

        btnPiece.setOnClickListener {
            startActivity<TestPieceActivity>()
        }

        btnDialog.setOnClickListener {
            startActivity<TestDialogActivity>()
        }

        btnLogin.setOnClickListener {
            startActivity<LoginActivity>()
        }

        btnCameraFront.setOnClickListener {
            CameraActivityCreator.create(CameraCharacteristics.LENS_FACING_FRONT)
                .startForResult(this) { resultCode: Int, data: Intent? ->
                    if (resultCode == RESULT_OK && data != null) {
                        val uri = IntentCompat.getParcelableExtra<Uri>(data, Constants.Extra.PHOTO_URI)
                        L.www("拍照成功, uri = $uri")
                    } else {
                        L.www("拍照失败")
                    }
                }
        }
    }
}