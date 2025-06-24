package pretimmediat.activity.inputpiece.bank

import androidx.annotation.CallSuper
import coroutine.flow.launchIn
import eth.model.EthException
import inject.annotation.creator.Extra
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import logger.L
import pretimmediat.R
import pretimmediat.activity.inputpiece.AbstractPieceActivity
import pretimmediat.dialog.BankModifyConfirmDialog
import pretimmediat.dialog.FormInfoSelectorDialog
import pretimmediat.ext.addBackIcon
import pretimmediat.ext.startProtocolActivity
import pretimmediat.ext.withNetworkError
import pretimmediat.ext.withPieceLoading
import pretimmediat.manager.LocationManager
import pretimmediat.model.AppConfig
import pretimmediat.stats.Stats
import pretimmediat.viewmodel.inputpiece.BankPieceViewModel
import vector.bindingadapter.bind.Bind

/**
 * 进件页 银行基类
 */
abstract class BaseBankPieceActivity : AbstractPieceActivity<BankPieceViewModel>() {

    companion object {
        private const val LOG_TAG = "BaseBankPieceActivity"
    }

    @Extra(true)
    var orderId: String? = null

    val onBankTypeClick = Bind.OnDebounceClick {
        fun showDialog(selections: List<AppConfig>) {
            FormInfoSelectorDialog(
                this,
                R.string.piece_bank_type,
                selections.map { it.value }
            ) { _, index, _ ->
                val item = selections[index]
                viewModel.bankTypeCode.value = item.code
                viewModel.bankTypeText.value = item.value
            }.show()
        }

        if (viewModel.bankTypeSelections.isEmpty()) {
            viewModel.fetchBankType()
                .withPieceLoading(this)
                .withNetworkError(this)
                .onEach { selections ->
                    showDialog(selections)
                }.catch { e ->
                    L.e(LOG_TAG, ", ", e)
                }.launchIn(this)
        } else {
            showDialog(viewModel.bankTypeSelections)
        }
    }

    val onBankAccountClearClick = Bind.OnClick {
        viewModel.bankAccount.value = null
    }

    val onBankAccountConfirmClearClick = Bind.OnClick {
        viewModel.bankAccountConfirm.value = null
    }

    val nextStep by lazy {
        NextStep(this, viewModel.nextEnabled, "420,000", object : NextStep.Listener {
            override fun onNextClick(callback: () -> Unit) {
                viewModel.upload(null)
                    .withPieceLoading(this@BaseBankPieceActivity)
                    .withNetworkError(this@BaseBankPieceActivity)
                    .onEach {
                        onUploadSuccess {
                            callback()
                        }
                    }.catch { e ->
                        L.e(LOG_TAG, "uploadBank", e)
                        if (e is EthException && e.code == "-1015") {
                            // 打开银行弹窗
                            BankModifyConfirmDialog(
                                this@BaseBankPieceActivity,
                                userId,
                                appSsid,
                                orderId,
                                viewModel.bankTypeCode.value,
                                viewModel.bankAccount.value,
                            ) {
                                onUploadSuccess {
                                    callback()
                                }
                            }.show()
                        } else {
                            callback()
                        }
                    }.launchIn(this@BaseBankPieceActivity)
            }

            override fun onProtocolClick() {
                startProtocolActivity()
            }
        })
    }


    @CallSuper
    override fun initializeSystemBar() {
        super.initializeSystemBar()
        appBar.addBackIcon(R.string.piece_bank_title) {
            finish()
        }
    }

    @CallSuper
    override fun initializeContentView() {
        super.initializeContentView()

        LocationManager.getInstance(this).update(this) {
            Stats.public.onEvent("ACCESS_LOCATION_BANK", userId, appSsid)
        }
    }

    abstract fun onUploadSuccess(callback: () -> Unit)
}