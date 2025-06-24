package pretimmediat.activity.inputpiece

import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import compat.ext.getParcelableExtra
import compat.intent.IntentCompat
import coroutine.flow.launchIn
import coroutine.flow.state.inverse
import dagger.hilt.android.AndroidEntryPoint
import image.ImageTransformation
import inject.annotation.creator.Creator
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import logger.L
import pretimmediat.R
import pretimmediat.activity.CameraActivityCreator
import pretimmediat.activity.inputpiece.bank.BankPieceActivityCreator
import pretimmediat.databinding.ActivityPieceIdCardBinding
import pretimmediat.def.Constants
import pretimmediat.dialog.PopupDialog
import pretimmediat.ext.addBackIcon
import pretimmediat.ext.startProtocolActivity
import pretimmediat.ext.withNetworkError
import pretimmediat.ext.withPieceLoading
import pretimmediat.manager.LocationManager
import pretimmediat.network.api.InputPieceApi
import pretimmediat.service.CheckFirstServiceCreator
import pretimmediat.stats.Stats
import pretimmediat.util.PhotoUtil
import pretimmediat.viewmodel.inputpiece.IdCardPieceViewModel
import vector.app.os.dimenRes
import vector.bindingadapter.bind.Bind
import vector.ext.getStringForLanguage

/**
 * 身份信息
 */
@AndroidEntryPoint
@Creator(forResult = true)
class IdCardPieceActivity : AbstractPieceActivity<IdCardPieceViewModel>() {

    companion object {
        private const val LOG_TAG = "IdCardPieceActivity"
        private const val ID_MAX_LENGTH = 20
    }

    val uploadTransformation = ImageTransformation.Shape.RoundCorner(R.dimen.radius.dimenRes.toPx())

    val onIdCardFrontClick = Bind.OnDebounceClick {
        Stats.risk.onEvent("SHOOT_PUZZLED_COMPOSITION", userId, appSsid)
        showPopup { uri ->
            viewModel.idCardSource.value = uri
        }
    }

    val onFaceClick = Bind.OnDebounceClick {
        Stats.risk.onEvent("SNAKE_MERCIFUL_SCHOLARSHIP", userId, appSsid)
        // 人脸只支持拍照
        CameraActivityCreator.create(CameraCharacteristics.LENS_FACING_FRONT)
            .startForResult(this) { resultCode: Int, data: Intent? ->
                if (resultCode == RESULT_OK && data != null) {
                    val uri = IntentCompat.getParcelableExtra<Uri>(data, Constants.Extra.PHOTO_URI)
                    viewModel.faceSource.value = uri
                } else {
                    // 取消或者异常
                    Stats.risk.onEvent("CLICK_FREEZING_TOUR", userId, appSsid)
                }
            }
    }

    val onSampleClick = Bind.OnDebounceClick {
        viewModel.sampleExpand.inverse()
    }

    val onIdNumberClearClick = Bind.OnClick {
        viewModel.idNumber.value = null
    }

    val nextStep by lazy {
        NextStep(this, viewModel.nextEnabled, "250,000", object : NextStep.Listener {
            override fun onNextClick(callback: () -> Unit) {
                viewModel.uploadIdCard()
                    .withPieceLoading(this@IdCardPieceActivity)
                    .withNetworkError(this@IdCardPieceActivity).onEach {
                        BankPieceActivityCreator.create()
                            .userId(userId)
                            .appSsid(appSsid)
                            .startForResult(this@IdCardPieceActivity) { resultCode, _ ->
                                if (resultCode == RESULT_OK) {
                                    setResult(RESULT_OK)
                                    finish()
                                } else {
                                    callback()
                                }
                            }

                        CheckFirstServiceCreator.create()
                            .userId(userId)
                            .ssid(appSsid)
                            .pageType(InputPieceApi.PAGE_TYPE_ID_CARD)
                            .start(this@IdCardPieceActivity)
                    }.catch { e ->
                        L.e(LOG_TAG, "uploadIdCard", e)
                        callback()
                    }.launchIn(this@IdCardPieceActivity)
            }

            override fun onProtocolClick() {
                startProtocolActivity()
            }
        })
    }

    override val serviceFlag: Int
        get() = Constants.ServiceFlag.PIECE_ID

    class IdInputFilterWrapper(private val base: InputFilter) : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            if (dend >= ID_MAX_LENGTH) {
                // 忽略输入
                return ""
            }

            return base.filter(source, start, end, dest, dstart, dend)
        }
    }

    val idInputFilter = IdInputFilterWrapper(commonInputFilter)

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivityPieceIdCardBinding.inflate(inflater).apply {
            owner = this@IdCardPieceActivity
            viewModel = this@IdCardPieceActivity.viewModel
        }
    }

    override fun initializeSystemBar() {
        super.initializeSystemBar()
        appBar.addBackIcon(R.string.piece_idcard_title) {
            finish()
        }
    }

    private fun showPopup(callback: (Uri?) -> Unit) {
        PopupDialog(
            this, listOf(
                getStringForLanguage(R.string.piece_idcard_use_camera),
                getStringForLanguage(R.string.piece_idcard_use_album)
            )
        ) { index, _ ->
            when (index) {
                0 -> {
                    CameraActivityCreator.create(CameraCharacteristics.LENS_FACING_BACK)
                        .startForResult(this) { resultCode: Int, data: Intent? ->
                            if (resultCode == RESULT_OK && data != null) {
                                val uri = IntentCompat.getParcelableExtra<Uri>(
                                    data,
                                    Constants.Extra.PHOTO_URI
                                )
                                callback(uri)
                            } else {
                                // 取消或者异常
                                Stats.risk.onEvent("STOP_FAIR_LOSS", userId, appSsid)
                            }
                        }
                }

                1 -> {
                    PhotoUtil.fromAlbum(this) { uri ->
                        callback(uri)
                    }
                }
            }
        }.show()
    }

    override fun initializeContentView() {
        super.initializeContentView()

        LocationManager.getInstance(this).update(this) {
            Stats.public.onEvent("ACCESS_LOCATION_ID", userId, appSsid)
        }
    }
}