package pretimmediat.fragment

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coroutine.flow.launchIn
import image.ImageTransformation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logger.L
import pretimmediat.R
import pretimmediat.activity.SetupActivity
import pretimmediat.databinding.FragMeBinding
import pretimmediat.def.Constants
import pretimmediat.ext.checkUpgrade
import pretimmediat.ext.openComplaintBrowser
import pretimmediat.ext.optimizeLoading
import pretimmediat.ext.startProtocolActivity
import pretimmediat.ext.startServiceActivity
import pretimmediat.ext.withNetworkError
import pretimmediat.ext.withSwipeState
import pretimmediat.ext.withTriggerState
import pretimmediat.manager.AccountManager
import pretimmediat.network.ParamsValue
import pretimmediat.property.Properties
import pretimmediat.repo.InputPieceRepo
import vector.app.databinding.frag.SimpleDBFragEx
import vector.app.ext.bind.bindView
import vector.app.ext.getStringForLanguage
import vector.app.ext.startActivity
import vector.bindingadapter.bind.Bind
import vector.widget.swiperefresh.delegate.SwipeRefreshDelegate

/**
 * 我的页面
 */
class MeFrag : SimpleDBFragEx() {

    companion object {
        private const val LOG_TAG = "MeFrag"
    }

    val onSetupClick = Bind.OnClick {
        startActivity<SetupActivity>()
    }

    val onServiceClick = Bind.OnClick {
        startServiceActivity(Constants.ServiceFlag.ME)
    }

    val onProtocolClick = Bind.OnClick {
        startProtocolActivity()
    }

    val onComplaintClick = Bind.OnClick {
        openComplaintBrowser()
    }

    val phoneNumber = Properties.accountPhoneNumber.asFlow()
        .stateIn(lifecycleScope, SharingStarted.WhileSubscribed(), null)

    val givenName by lazy {
        Properties.pieceGivenName.asFlow().stateIn(
            lifecycleScope,
            SharingStarted.WhileSubscribed(),
            getStringForLanguage(R.string.app_name)
        )
    }

    val source: StateFlow<Any> = Properties.pieceFaceUrl.asFlow().filterNotNull().map {
        it.ifEmpty {
            R.mipmap.ic_launcher
        }
    }.stateIn(
        lifecycleScope,
        SharingStarted.WhileSubscribed(),
        R.mipmap.ic_launcher
    )

    val transformation = ImageTransformation.Shape.Circle()

    private val refreshLayout by bindView<SwipeRefreshLayout>(R.id.swipe_refresh)
    val refreshDelegate by lazy { SwipeRefreshDelegate(refreshLayout) }

    private val repo = InputPieceRepo()

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return FragMeBinding.inflate(inflater).apply {
            owner = this@MeFrag
        }
    }

    override fun initializeSystemBar() {
        appBar.left.addText(R.string.app_name)
    }

    override fun initializeContentView() {
        refreshDelegate.setOnSwipeRefresh {
            fetchImages().withNetworkError(context).withSwipeState(it).catch { e ->
                L.e(LOG_TAG, "fetchImages", e)
            }.launchIn(this)
        }

        refreshDelegate.autoRefresh(lifecycle) { trigger ->
            fetchImages().withNetworkError(context).withTriggerState(trigger).catch { e ->
                L.e(LOG_TAG, "fetchImages", e)
            }.launchIn(this)
        }

        checkUpgrade()
    }

    private fun fetchImages() = repo.fetchIdImages(AccountManager.account, ParamsValue.CLIENT_ID)
        .optimizeLoading()
        .onEach { value ->
            val faceUrl = value.faceUrl
            L.d(LOG_TAG, "fetchImages, faceUrl = $faceUrl")
            if (value.faceVerified == "1" && faceUrl != null) {
                lifecycleScope.launch {
                    Properties.pieceFaceUrl.put(faceUrl)
                }
            }

            val givenName = value.givenName
            if (givenName != null) {
                lifecycleScope.launch {
                    Properties.pieceGivenName.put(givenName)
                }
            }
        }
}