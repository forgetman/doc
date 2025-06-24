package pretimmediat.viewmodel.inputpiece

import android.app.Application
import androidx.lifecycle.viewModelScope
import coroutine.flow.launchIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import logger.L
import pretimmediat.ext.throwUploadError
import pretimmediat.model.AppConfig
import pretimmediat.model.PieceUploadError
import pretimmediat.model.inputpiece.BankPiece
import pretimmediat.repo.InputPieceRepo
import vector.ext.isNotNullOrEmpty
import javax.inject.Inject

@HiltViewModel
class BankPieceViewModel @Inject constructor(
    private val repo: InputPieceRepo,
    app: Application
) : AbstractPieceViewModel(app) {

    companion object {
        private const val LOG_TAG = "BankPieceViewModel"
    }

    private val piece = MutableStateFlow<BankPiece?>(null)

    var bankTypeSelections: List<AppConfig> = emptyList()
    var bankTypeCode = MutableStateFlow<String?>(null)
    val bankTypeText = MutableStateFlow<String?>(null)

    val bankAccount = MutableStateFlow<String?>(null)
    val bankAccountConfirm = MutableStateFlow<String?>(null)

    val nextEnabled = combine(bankTypeCode, bankAccount, bankAccountConfirm) { a, b, c ->
        a.isNotNullOrEmpty() && b.isNotNullOrEmpty() && c.isNotNullOrEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private var orderId: String? = null

    override fun onCreate() {
        piece.filterNotNull().onEach { data ->
            if (updateIfNeeded(data.baTypeDesc, bankTypeText)) {
                bankTypeCode.value = data.bankAccountTypeCode
            }

            if (orderId == null) {
                // 更新银行卡的时候不回填
                updateIfNeeded(data.baNumber, bankAccount)
                updateIfNeeded(data.baNumber, bankAccountConfirm)
            }
        }.launchIn(viewModelScope)

        fetchBankType().catch { e ->
            L.e(LOG_TAG, "fetchBankType", e)
        }.launchIn(viewModelScope)

        repo.fetchBank(userId, appSsid).catch { e ->
            L.e(LOG_TAG, "fetchBank", e)
        }.onEach {
            piece.value = it.firstOrNull()
        }.launchIn(viewModelScope)
    }

    fun fetchBankType() = repo.fetchBankType().onEach {
        bankTypeSelections = it
    }

    fun upload(smsCode: String?): Flow<String> {
        try {
            val bankType = bankTypeCode.value.throwUploadError(PieceUploadError.Type.BANK_TYPE)
            val account = bankAccount.value.throwUploadError(PieceUploadError.Type.BANK_ACCOUNT)
            val accountConfirm = bankAccountConfirm.value.throwUploadError(PieceUploadError.Type.BANK_ACCOUNT_CONFIRM)

            if (account != accountConfirm) {
                throw PieceUploadError(PieceUploadError.Type.BANK_ACCOUNT_CONFIRM)
            }

            val orderId = orderId
            return if (orderId.isNullOrEmpty()) {
                repo.uploadBank(userId, appSsid, bankType, account, smsCode)
            } else {
                repo.updateBank(userId, appSsid, bankType, account, orderId, smsCode)
            }
        } catch (e: PieceUploadError) {
            return flow {
                throw e
            }
        }
    }

    fun updateOrderInfo(orderId: String?) {
        this.orderId = orderId
    }
}