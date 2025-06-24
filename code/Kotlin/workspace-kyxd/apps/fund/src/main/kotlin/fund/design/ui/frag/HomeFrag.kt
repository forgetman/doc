package fund.design.ui.frag

import androidx.databinding.ViewDataBinding
import fund.Bus
import fund.EventId
import fund.R
import fund.databinding.FragHomeBinding
import fund.design.ui.adapter.FormAdapter
import fund.design.viewModel.HomeViewModel
import fund.ext.withListState
import vector.annotation.LayoutId
import vector.bindingadapter.onBind.Bind
import vector.design.ui.decor.ViewState
import vector.design.ui.frag.FragEx

/**
 * @author yuansui
 * @since 2018/7/19
 */
@LayoutId(R.layout.frag_home)
class HomeFrag : FragEx<HomeViewModel>() {

    val adapter = FormAdapter()

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = FragHomeBinding.inflate(layoutInflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    private var needRefresh = false

    val onSwipe = RefreshBind.OnSwipe {
        viewModel.query().withListState(this, it)
    }

    val onLoadMore = RefreshBind.OnLoadMore { view, state ->
        viewModel.query(state).withListState(this, view)
    }

    override fun flowOfNavBar() {
        navBar.mid.addText("首页")
    }

    override fun flowOfSetup() {

//        viewModel.query().withViewState(this)

//        vm.query()
//                .withViewState(this)
//                .withLoadMore(this)
//                .load(this)
//
//        setOnSwipeRefreshListener {
//            if (it == SwipeState.START) {
//                vm.query()
//                        .withSwipe(this)
//                        .observe { _ ->
//                            scrollTo(0)
//                        }.load(this)
//            }
//        }
//
//        setOnLoadMoreListener {
//            vm.queryMore(it == LoadMore.State.ERROR)
//                    .withLoadMore(this)
//                    .load(this)
//        }

        bindBus()
    }

    private fun bindBus() {
        Bus.get().with(this)
            .take(EventId.LOGIN, EventId.LOGOUT)
            .subscribe {
                if (viewState != ViewState.LOADING) {
//                        vm.resetData()
//                        startSwipeRefresh()
                }
            }

        Bus.get().with(this)
            .take(EventId.REFRESH_HOME)
            .subscribe {
                needRefresh = true
            }
    }

//    override fun onResume() {
//        super.onResume()
//
//        if (needRefresh) {
//            startSwipeRefresh()
//        }
//    }
}