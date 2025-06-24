package pretimmediat.manager

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logger.L
import pretimmediat.model.AccountInfo
import pretimmediat.property.Properties
import vector.datastore.preference.sync
import vector.ext.isNotNullOrEmpty

/**
 * 登录信息管理
 */
object AccountManager {
    private const val LOG_TAG = "AccountManager"

    private val coroutineScope = MainScope()

    val account: String
        get() = Properties.accountId.sync().getOrNull() ?: ""

    val token: String
        get() = Properties.accountToken.sync().getOrNull() ?: ""

    @JvmName("setAccountInfo")
    fun setInfo(info: AccountInfo, phoneNumber: String) {
        L.d(LOG_TAG, "setInfo, info: $info")
        coroutineScope.launch {
            Properties.accountId.put(info.account)
            Properties.accountToken.put(info.token)
            Properties.accountTest.put(info.testCustFlag == "1")
            Properties.accountPhoneNumber.put(phoneNumber)
        }
    }

    fun clear(callback: (Boolean) -> Unit) {
        callbackFlow {
            Properties.pieceGivenName.remove()
            Properties.pieceFaceUrl.remove()
            Properties.accountId.remove()
            Properties.accountToken.remove()
            Properties.accountPhoneNumber.remove()
            Properties.accountTest.remove()
            trySend(true)
            close()
        }.catch { e ->
            L.e(LOG_TAG, e)
            callback(false)
        }.onEach {
            callback(it)
        }.launchIn(coroutineScope)
    }

    /**
     * 是否已登录
     */
    fun isLoggedIn(): Boolean {
        return token.isNotNullOrEmpty()
    }
}