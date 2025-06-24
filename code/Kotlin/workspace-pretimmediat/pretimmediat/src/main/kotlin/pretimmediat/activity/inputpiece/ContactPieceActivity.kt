package pretimmediat.activity.inputpiece

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.InputFilter
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import coroutine.flow.launchIn
import dagger.hilt.android.AndroidEntryPoint
import inject.annotation.creator.Creator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import logger.L
import pretimmediat.R
import pretimmediat.databinding.ActivityPieceContactBinding
import pretimmediat.def.Constants
import pretimmediat.dialog.FormInfoSelectorDialog
import pretimmediat.dialog.PermissionSecondDialog
import pretimmediat.dialog.Style2Dialog
import pretimmediat.ext.addBackIcon
import pretimmediat.ext.requireAllPermissions
import pretimmediat.ext.startProtocolActivity
import pretimmediat.ext.withNetworkError
import pretimmediat.ext.withPieceLoading
import pretimmediat.manager.LocationManager
import pretimmediat.model.AppConfig
import pretimmediat.property.Properties
import pretimmediat.stats.Stats
import pretimmediat.viewmodel.inputpiece.ContactPieceViewModel
import vector.bindingadapter.bind.Bind
import vector.datastore.preference.sync


/**
 * 紧急联系人页
 */
@AndroidEntryPoint
@Creator(forResult = true)
class ContactPieceActivity : AbstractPieceActivity<ContactPieceViewModel>() {

    companion object {
        private const val LOG_TAG = "ContactPieceActivity"
    }

    val onFirstRelationshipClick = Bind.OnDebounceClick {
        showRelationshipDialog(
            viewModel.fetchRelationship(),
            viewModel.relationshipSelections
        ) { code, value ->
            viewModel.firstRelationshipCode = code
            viewModel.firstRelationshipDesc.value = value
        }
    }

    val onSecRelationshipClick = Bind.OnDebounceClick {
        showRelationshipDialog(
            viewModel.fetchSecRelationship(),
            viewModel.secRelationshipSelections
        ) { code, value ->
            viewModel.secRelationshipCode = code
            viewModel.secRelationshipDesc.value = value
        }
    }

    val onFirstPhoneNumberClick = Bind.OnDebounceClick {
        viewModel.selectFromContacts(this) { number, name ->
            viewModel.firstPhoneNumber.value = number
            viewModel.firstName.value = name
        }
    }

    val onSecPhoneNumberClick = Bind.OnDebounceClick {
        viewModel.selectFromContacts(this) { number, name ->
            viewModel.secPhoneNumber.value = number
            viewModel.secName.value = name
        }
    }

    val onFirstPhoneNumberClearClick = Bind.OnClick {
        viewModel.firstPhoneNumber.value = null
    }

    val onFirstNameClearClick = Bind.OnClick {
        viewModel.firstName.value = null
    }

    val onSecPhoneNumberClearClick = Bind.OnClick {
        viewModel.secPhoneNumber.value = null
    }

    val onSecNameClearClick = Bind.OnClick {
        viewModel.secName.value = null
    }

    val nextStep by lazy {
        NextStep(this, viewModel.nextEnabled, "210,000", object : NextStep.Listener {
            override fun onNextClick(callback: () -> Unit) {
                viewModel.uploadContact()
                    .withPieceLoading(this@ContactPieceActivity)
                    .withNetworkError(this@ContactPieceActivity).onEach {
                        fun toIdCard() {
                            // 采集大json数据
                            viewModel.uploadBigJson()
                                .withPieceLoading(this@ContactPieceActivity, false)
                                .withNetworkError(this@ContactPieceActivity).onEach {
                                    IdCardPieceActivityCreator.create()
                                        .userId(userId)
                                        .appSsid(appSsid)
                                        .startForResult(this@ContactPieceActivity) { resultCode, _ ->
                                            if (resultCode == RESULT_OK) {
                                                setResult(RESULT_OK)
                                                finish()
                                            } else {
                                                callback()
                                            }
                                        }
                                }.catch { e ->
                                    L.e(LOG_TAG, "uploadContact", e)
                                    callback()
                                }.launchIn(this@ContactPieceActivity)
                        }

                        fun showSetupDialog() {
                            Style2Dialog.Builder(this@ContactPieceActivity)
                                .icon(R.drawable.dialog_ic_loud_speaker)
                                .content(R.string.piece_permission_content)
                                .buttonLeft(R.string.withdraw) {
                                    L.d(LOG_TAG, "onNextClick, withdraw permission jump")
                                }
                                .buttonRight(R.string.open) {
                                    L.d(LOG_TAG, "onNextClick, open setup")
                                    val intent = Intent()
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    intent.setData(
                                        Uri.fromParts(
                                            "package",
                                            packageName,
                                            null
                                        )
                                    )
                                    startActivity(intent)
                                }
                                .build().show()

                            callback()
                        }

                        Properties.showPermissionSecond.asFirstFlow().filterNotNull().onEach { shown ->
                            if (shown) {
                                // 未弹出过 权限弹窗
                                Properties.showPermissionSecond.put(false)
                                PermissionSecondDialog(this@ContactPieceActivity) { allow, applyResult ->
                                    L.d(
                                        LOG_TAG,
                                        "onNextClick, allow = $allow, result = $applyResult"
                                    )
                                    if (allow) {
                                        // 点了同意申请
                                        if (applyResult) {
                                            // 申请通过, 跳下一步
                                            toIdCard()
                                        } else {
                                            // 申请失败, 弹出设置弹窗
                                            showSetupDialog()
                                        }
                                    } else {
                                        // 拒绝申请, 留在当页
                                        // do nothing
                                        callback()
                                    }
                                }.show()
                            } else {
                                // 弹出过, 直接申请权限
                                requireAllPermissions { result ->
                                    if (result) {
                                        // 申请通过, 跳下一步
                                        toIdCard()
                                    } else {
                                        // 申请失败, 弹出设置弹窗
                                        showSetupDialog()
                                    }
                                }
                            }
                        }
                    }.catch { e ->
                        L.e(LOG_TAG, "uploadEmergencyContact", e)
                        callback()
                    }.launchIn(this@ContactPieceActivity)
            }

            override fun onProtocolClick() {
                startProtocolActivity()
            }
        })
    }


    override val serviceFlag: Int
        get() = Constants.ServiceFlag.PIECE_CONTACT

    val phoneNumberInputFilter = InputFilter { source, start, end, dest, dstart, _ ->
        for (i in start until end) {
            val char = source[i]
            when {
                // 检测空格, 开头不能输入空格
                dstart == 0 && char == ' ' && i == 0 -> {
                    return@InputFilter ""
                }

                // 后续只允许输入数字和+号
                !char.isDigit() && char != '+' -> {
                    return@InputFilter ""
                }
            }
        }
        // 如果没有检测到回车字符，则返回 null，表示不进行任何过滤
        null
    }


    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivityPieceContactBinding.inflate(inflater).apply {
            owner = this@ContactPieceActivity
            viewModel = this@ContactPieceActivity.viewModel
        }
    }

    override fun initializeSystemBar() {
        super.initializeSystemBar()
        appBar.addBackIcon(R.string.piece_emergency_contact_title) {
            finish()
        }
    }

    private fun cannotAccessContacts() {
        L.d(LOG_TAG, "get contact error")
    }

    private fun showRelationshipDialog(
        dataFetchFlow: Flow<List<AppConfig>>,
        selections: List<AppConfig>,
        callback: (code: String, value: String) -> Unit
    ) {
        fun showDialog(s: List<AppConfig>) {
            FormInfoSelectorDialog(
                this,
                R.string.piece_emergency_contact_relationship,
                s.map { it.value }
            ) { _, index, _ ->
                val item = s[index]
                callback(item.code, item.value)
            }.show()
        }

        if (selections.isEmpty()) {
            dataFetchFlow
                .withPieceLoading(this)
                .withNetworkError(this)
                .onEach { s ->
                    showDialog(s)
                }
                .catch { e ->
                    L.e(LOG_TAG, ", ", e)
                }.launchIn(this)
        } else {
            showDialog(selections)
        }
    }

    override fun initializeContentView() {
        super.initializeContentView()

        LocationManager.getInstance(this).update(this) {
            Stats.public.onEvent("ACCESS_LOCATION_CONTACT", userId, appSsid)
        }
    }
}