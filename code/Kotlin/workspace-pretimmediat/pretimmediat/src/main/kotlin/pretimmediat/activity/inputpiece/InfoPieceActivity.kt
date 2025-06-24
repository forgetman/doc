package pretimmediat.activity.inputpiece

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import coroutine.flow.launchIn
import dagger.hilt.android.AndroidEntryPoint
import inject.annotation.creator.Creator
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import logger.L
import pretimmediat.R
import pretimmediat.databinding.ActivityPieceInfoBinding
import pretimmediat.def.Constants
import pretimmediat.dialog.FormInfoSelectorDialog
import pretimmediat.ext.addBackIcon
import pretimmediat.ext.formatDate
import pretimmediat.ext.startProtocolActivity
import pretimmediat.ext.withNetworkError
import pretimmediat.ext.withPieceLoading
import pretimmediat.manager.LocationManager
import pretimmediat.model.AppConfig
import pretimmediat.model.Region
import pretimmediat.stats.Stats
import pretimmediat.viewmodel.inputpiece.InfoPieceViewModel
import pretimmediat.widget.picker.DatePickerView
import vector.bindingadapter.bind.Bind
import java.util.Calendar

/**
 *  个人信息页
 */
@AndroidEntryPoint
@Creator
class InfoPieceActivity : AbstractPieceActivity<InfoPieceViewModel>() {

    companion object {
        private const val LOG_TAG = "InfoPieceActivity"
    }

    val onBirthdayClick = Bind.OnDebounceClick {
        // 选择生日
        val beginTimestamp = "01-01-1950".formatDate()

        // 结束时间是当前时间的10年前
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -10)
        val endTimestamp = cal.timeInMillis

        // 通过时间戳初始化日期，毫秒级别
        DatePickerView(
            this, beginTimestamp, endTimestamp
        ) { timestamp ->
            L.d(LOG_TAG, "selected timestamp = $timestamp")
            viewModel.birthday.value = timestamp.formatDate()
        }.apply {
            // 不允许点击屏幕或物理返回键关闭
            setCancelable(false)
            // 不允许循环滚动
            setScrollLoop(false)
            // 不允许滚动动画
            setCanShowAnim(true)

            show(viewModel.birthday.value ?: "01-01-2000")
        }
    }

    val onSexClick = Bind.OnDebounceClick {
        fun showDialog(selections: List<AppConfig>) {
            FormInfoSelectorDialog(
                this,
                R.string.piece_info_sex,
                selections.map { it.value }
            ) { _, index, _ ->
                val item = selections[index]
                viewModel.sexCode = item.code
                viewModel.sexText.value = item.value
            }.show()
        }

        if (viewModel.sexSelections.isEmpty()) {
            viewModel.fetchSexSelection()
                .withPieceLoading(this)
                .withNetworkError(this)
                .onEach { selections ->
                    showDialog(selections)
                }.catch { e ->
                    L.e(LOG_TAG, ", ", e)
                }.launchIn(this)
        } else {
            showDialog(viewModel.sexSelections)
        }
    }

    val onLocationClick = Bind.OnDebounceClick {
        fun showSelectLocationDialog(provinces: List<Region>) {
            FormInfoSelectorDialog(
                this,
                R.string.piece_info_choice_location_province,
                provinces.map { it.regionName },
                -1,
                false
            ) { provinceDialog, index, _ ->
                viewModel.province.value = provinces[index]
                viewModel.fetchCities(index)
                    .withPieceLoading(this)
                    .withNetworkError(this)
                    .onEach { cities ->
                        L.d(LOG_TAG, "fetchCities = $cities")
                        FormInfoSelectorDialog(
                            this,
                            R.string.piece_info_choice_location_city,
                            cities.map { it.regionName }
                        ) { _, index, _ ->
                            viewModel.city.value = cities[index]
                            provinceDialog.dismiss()
                        }.show()
                    }
                    .catch { e ->
                        L.e(LOG_TAG, "fetchCities", e)
                    }.launchIn(this)
            }.show()
        }

        if (viewModel.provinceSelections.isEmpty()) {
            viewModel.fetchProvince().withPieceLoading(this).withNetworkError(this)
                .onEach {
                    showSelectLocationDialog(it)
                }.catch { e ->
                    L.e(LOG_TAG, "fetchRegions", e)
                }.launchIn(this)
        } else {
            showSelectLocationDialog(viewModel.provinceSelections)
        }
    }

    val onFamilyNameClearClick = Bind.OnClick {
        viewModel.familyName.value = null
    }

    val onGivenNameClearClick = Bind.OnClick {
        viewModel.givenName.value = null
    }

    val onEmailClearClick = Bind.OnClick {
        viewModel.email.value = null
    }

    val nextStep by lazy {
        NextStep(this, viewModel.nextEnabled, "170,000", object : NextStep.Listener {
            override fun onNextClick(callback: () -> Unit) {
                L.d(LOG_TAG, "userId = $userId, ssid = $appSsid")
                viewModel.uploadBasic()
                    .withPieceLoading(this@InfoPieceActivity)
                    .withNetworkError(this@InfoPieceActivity)
                    .onEach {
                        ContactPieceActivityCreator.create()
                            .userId(userId)
                            .appSsid(appSsid)
                            .startForResult(this@InfoPieceActivity) { resultCode, _ ->
                                if (resultCode == RESULT_OK) {
                                    finish()
                                } else {
                                    callback()
                                }
                            }
                    }
                    .catch { e ->
                        L.e(LOG_TAG, "uploadBasicInfo", e)
                        callback()
                    }.launchIn(this@InfoPieceActivity)
            }

            override fun onProtocolClick() {
                startProtocolActivity()
            }
        })
    }


    override val serviceFlag: Int
        get() = Constants.ServiceFlag.PIECE_INFO

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivityPieceInfoBinding.inflate(inflater).apply {
            owner = this@InfoPieceActivity
            viewModel = this@InfoPieceActivity.viewModel
        }
    }

    override fun initializeSystemBar() {
        super.initializeSystemBar()
        appBar.addBackIcon(R.string.piece_info_title) {
            finish()
        }
    }

    override fun initializeContentView() {
        super.initializeContentView()

        LocationManager.getInstance(this).update(this) {
            Stats.public.onEvent("ACCESS_LOCATION_BASIC", userId, appSsid)
        }
    }
}