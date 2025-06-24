package pretimmediat.fragment

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import compat.network.NetworkCompat
import coroutine.flow.launchIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import logger.L
import pretimmediat.activity.user.LoginActivity
import pretimmediat.activity.user.LoginActivityCreator
import pretimmediat.bus.sendMessage
import pretimmediat.databinding.FragHomePrepareBinding
import pretimmediat.def.Constants
import pretimmediat.ext.showErrorDialog
import pretimmediat.ext.startProtocolActivity
import pretimmediat.ext.withNetworkError
import pretimmediat.fragment.base.databinding.BaseSimpleDBFrag
import pretimmediat.manager.AccountManager
import pretimmediat.model.ApplicationSettings
import pretimmediat.network.api.GlobalApi
import pretimmediat.network.api.ProductApi
import pretimmediat.network.createApi
import pretimmediat.property.Properties
import vector.bindingadapter.bind.Bind
import vector.ext.isNotNullOrEmpty

/**
 * 单产品首页
 */
class HomePrepareFrag : BaseSimpleDBFrag() {

    companion object {
        private const val LOG_TAG = "HomePrepareFrag"
    }

    override val serviceFlag: Int
        get() = Constants.ServiceFlag.MAIN_LOGGED_IN

    val applicationSettings = MutableStateFlow<ApplicationSettings?>(null)

    val maxAmount = combine(
        applicationSettings,
        Properties.accountTest.asFlow().filterNotNull()
    ) { settings, test ->
        if (settings == null) {
            return@combine null
        }
        if (test) settings.maxCAmountTestText else settings.maxCAmountText
    }.stateIn(lifecycleScope, SharingStarted.WhileSubscribed(), null)

    val maxDay = combine(
        applicationSettings,
        Properties.accountTest.asFlow().filterNotNull()
    ) { settings, test ->
        if (settings == null) {
            return@combine null
        }
        if (test) settings.maxDayTest else settings.maxDay
    }.stateIn(lifecycleScope, SharingStarted.WhileSubscribed(), null)

    val onApplyClick = Bind.OnDebounceClick {
        if (AccountManager.token.isNotNullOrEmpty()) {
            // 已经登录
            // 需要判断是否有网, 防止因为无网的原因展示此页导致的后续进件页的问题
            if (NetworkCompat.isConnected(requireContext())) {
                sendMessage(Constants.Bus.HOME_REFRESH_BY_APPLY)
            } else {
                showErrorDialog()
            }
        } else {
            // 未登录,跳转至登录页面
            LoginActivityCreator.create(LoginActivity.FROM_HOME).start(context)
        }
    }

    val onProtocolClick = Bind.OnDebounceClick {
        startProtocolActivity()
    }

    val refreshing = MutableStateFlow(false)
    val bannerUrl = MutableStateFlow<String?>(null)


    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return FragHomePrepareBinding.inflate(inflater).apply {
            owner = this@HomePrepareFrag
        }
    }

    override fun initializeContentView() {
        fetchAppSetting()
        fetchBanner()
    }

    private fun fetchAppSetting() {
        createApi<GlobalApi>().appSetting()
            .flowOn(Dispatchers.IO)
            .withNetworkError(context)
            .catch { e ->
                L.e(LOG_TAG, "fetchAppSetting", e)
            }.onEach {
                L.d(LOG_TAG, "fetchAppSetting = $it")
                applicationSettings.value = it
            }.launchIn(this)
    }

    private fun fetchBanner() {
        createApi<ProductApi>().singleBanner()
            .flowOn(Dispatchers.IO)
            .catch { e ->
                L.e(LOG_TAG, "fetchBanner", e)
            }.onEach {
                L.d(LOG_TAG, "fetchBanner = $it")
                bannerUrl.value = it.firstOrNull()?.url
            }.launchIn(this)
    }

    fun setRefreshing(refreshing: Boolean) {
        this.refreshing.value = refreshing
    }
}