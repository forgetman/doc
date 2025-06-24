package pretimmediat.activity.loan

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coroutine.flow.launchIn
import dagger.hilt.android.AndroidEntryPoint
import inject.annotation.creator.Creator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import pretimmediat.R
import pretimmediat.activity.base.databinding.BaseSimpleDBActivity
import pretimmediat.databinding.ActivityOrderDetailBinding
import pretimmediat.def.Constants
import pretimmediat.delegate.ServiceFlagDelegate
import pretimmediat.ext.addBackIcon
import pretimmediat.ext.addServiceIcon
import pretimmediat.ext.installmentStatusToFragmentFlow
import pretimmediat.ext.startServiceActivity
import pretimmediat.ext.withSwipeState
import pretimmediat.ext.withTriggerState
import vector.app.ext.bind.bindView
import vector.widget.databinding.swiperefresh.RefreshBind
import vector.widget.databinding.swiperefresh.adapter.trigger.SwipeRefreshBindTrigger

@AndroidEntryPoint
@Creator
class OrderDetailActivity : BaseSimpleDBActivity() {
    companion object {
        private const val LOG_TAG = "OrderDetailActivity"
    }

    val onSwipe = RefreshBind.OnSwipe {
        fetchData().withSwipeState(it).launchIn(this)
    }
    val refreshTrigger = SwipeRefreshBindTrigger.refresh()

    private val refreshLayout by bindView<SwipeRefreshLayout>(R.id.home_swipe_refresh)
    private var currServiceFlagDelegate: ServiceFlagDelegate? = null

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivityOrderDetailBinding.inflate(inflater).apply {
            owner = this@OrderDetailActivity
        }
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(R.string.app_name) { finish() }
        appBar.addServiceIcon {
            val flag = currServiceFlagDelegate?.serviceFlag ?: Constants.ServiceFlag.MAIN_UN_LOGIN
            startServiceActivity(flag)
        }
    }

    override fun initializeContentView() {
        flow {
            emit(Unit)
        }.filterNot {
            refreshLayout.isRefreshing
        }.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).onEach {
            fetchData().withTriggerState(refreshTrigger).launchIn(this)
        }.flowOn(Dispatchers.Main).launchIn(this)
    }

    private fun fetchData() =
        installmentStatusToFragmentFlow(this, userId, appSsid) { null }.onEach { frag ->
            if (frag == null) {
                finish()
                return@onEach
            } else {
                if (frag is ServiceFlagDelegate) {
                    currServiceFlagDelegate = frag
                }

                supportFragmentManager.commit(true) {
                    replace(R.id.layout_container, frag)
                }
            }
        }
}