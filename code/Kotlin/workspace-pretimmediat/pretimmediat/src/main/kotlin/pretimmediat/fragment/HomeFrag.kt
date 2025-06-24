package pretimmediat.fragment

import android.view.LayoutInflater
import android.widget.ScrollView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coroutine.flow.launchIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import logger.L
import pretimmediat.R
import pretimmediat.activity.inputpiece.InfoPieceActivityCreator
import pretimmediat.bus.withBus
import pretimmediat.databinding.FragHomeBinding
import pretimmediat.def.Constants
import pretimmediat.delegate.ServiceFlagDelegate
import pretimmediat.ext.addServiceIcon
import pretimmediat.ext.checkUpgrade
import pretimmediat.ext.fragmentFlow
import pretimmediat.ext.installmentStatusToFragmentFlow
import pretimmediat.ext.optimizeLoading
import pretimmediat.ext.startServiceActivity
import pretimmediat.ext.withNetworkError
import pretimmediat.ext.withSwipeState
import pretimmediat.ext.withTriggerState
import pretimmediat.manager.AccountManager
import pretimmediat.model.product.SingleProduct
import pretimmediat.network.ParamsValue
import pretimmediat.network.api.ProductApi
import pretimmediat.network.createApi
import pretimmediat.property.Properties
import vector.app.databinding.annotation.LayoutBindingClass
import vector.app.databinding.frag.SimpleDBFragEx
import vector.app.ext.bind.bindView
import vector.app.ext.view.findViewByType
import vector.widget.databinding.swiperefresh.RefreshBind
import vector.widget.databinding.swiperefresh.adapter.trigger.SwipeRefreshBindTrigger

/**
 * 首页
 */
@LayoutBindingClass<FragHomeBinding>
class HomeFrag : SimpleDBFragEx() {

    companion object {
        private const val LOG_TAG = "HomeFrag"
    }

    private val refreshLayout by bindView<SwipeRefreshLayout>(R.id.home_swipe_refresh)
    private val container by bindView<FragmentContainerView>(R.id.layout_container)

    val onSwipe = RefreshBind.OnSwipe {
        fetchMultiProductInfo().withSwipeState(it).launchIn(this)
    }
    val refreshTrigger = SwipeRefreshBindTrigger.refresh()

    private var homePrepareFrag: HomePrepareFrag? = null
    private var currServiceFlagDelegate: ServiceFlagDelegate? = null

    private var hasFirstData = false


    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return FragHomeBinding.inflate(inflater).apply {
            owner = this@HomeFrag
        }
    }

    override fun initializeSystemBar() {
        appBar.left.addText(R.string.home_single_title)
        appBar.addServiceIcon {
            val flag = if (AccountManager.isLoggedIn()) {
                // 判断单产品还是多产品
                currServiceFlagDelegate?.serviceFlag ?: Constants.ServiceFlag.MAIN_LOGGED_IN
            } else Constants.ServiceFlag.MAIN_UN_LOGIN
            startServiceActivity(flag)
        }
    }

    override fun initializeContentView() {
        flow {
            emit(Unit)
        }.filterNot {
            refreshLayout.isRefreshing
        }.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).onEach {
            fetchMultiProductInfo().withTriggerState(refreshTrigger).launchIn(this)
        }.flowOn(Dispatchers.Main).launchIn(this)

        withBus().onMessage(Constants.Bus.HOME_REFRESH_BY_APPLY) {
            fetchMultiProductInfo().withTriggerState(refreshTrigger).onEach { fragment ->
                L.d(LOG_TAG, "onMessage HOME_REFRESH_STATE")
                if (fragment != null && fragment is HomePrepareFrag) {
                    InfoPieceActivityCreator.create()
                        .userId(AccountManager.account)
                        .appSsid(ParamsValue.CLIENT_ID)
                        .start(requireContext())
                }
            }.launchIn(this)
        }

        Properties.accountToken.asFirstFlow().filterNotNull().filter { it.isEmpty() }.onEach { token ->
            hasFirstData = false
        }.launchIn(this)

        refreshLayout.setOnChildScrollUpCallback { _, child ->
            if (child == container) {
                val findScrollView = container.findViewByType<ScrollView>()
                if (findScrollView != null) {
                    findScrollView.canScrollVertically(-1)
                } else {
                    val findListView = container.findViewByType<RecyclerView>()
                    findListView?.canScrollVertically(-1) ?: false
                }
            } else {
                false
            }
        }

        checkUpgrade()
    }

    @Suppress("OPT_IN_USAGE")
    private fun fetchMultiProductInfo(): Flow<Fragment?> {
        return createApi<ProductApi>().mulAppInstallment()
            .flowOn(Dispatchers.IO)
            .flatMapConcat { data ->
                L.d(LOG_TAG, "onMultiProductResult = $data")
                hasFirstData = true
                when {
                    data.isEmpty() -> {
                        // 没有贷过款
                        fragmentFlow(HomePrepareFrag())
                    }

                    data.size <= 1 -> {
                        // 单产品
                        installmentStatusToFragmentFlow(
                            context,
                            AccountManager.account,
                            ParamsValue.CLIENT_ID
                        ) { status ->
                            if (status == SingleProduct.OD_STATUS_CAN_APPLY) {
                                HomePrepareFrag()
                            } else null
                        }
                    }

                    else -> {
                        // 多产品
                        fragmentFlow(HomeMultiFragCreator.create(data).get())
                    }
                }
            }
            .withNetworkError(context)
            .optimizeLoading()
            .onEach { frag ->
                homePrepareFrag = if (frag is HomePrepareFrag) frag else null
                if (frag == null) throw IllegalStateException("fetchMultiProductInfo, frag is null")

                if (frag is ServiceFlagDelegate) {
                    currServiceFlagDelegate = frag
                }

                childFragmentManager.commit(true) {
                    replace(R.id.layout_container, frag)
                }
            }.withPrepareState().catch { e ->
                L.e(LOG_TAG, "fetchMultiProductInfo", e)
                if (!hasFirstData) {
                    switchToPrepare()
                }
            }
    }

    private fun switchToPrepare() {
        childFragmentManager.commit(true) {
            val frag = HomePrepareFrag()
            replace(R.id.layout_container, frag)
            homePrepareFrag = frag
            currServiceFlagDelegate = frag
        }
    }

    private fun <T> Flow<T>.withPrepareState(): Flow<T> {
        return onStart {
            homePrepareFrag?.setRefreshing(true)
        }.onCompletion {
            homePrepareFrag?.setRefreshing(false)
        }.flowOn(Dispatchers.Main)
    }
}