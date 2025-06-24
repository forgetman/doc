package fund.design.ui.frag

/**
 * @author yuansui
 * @since 2018/8/1
 */
//class MsgFrag : FragEx<MsgViewModel>() {
//
//    override val enableRefresh: Boolean
//        get() = true
//
//    override val enableLoadMore: Boolean
//        get() = true
//
//    override fun flowOfNavBar() {
//        navBar.mid.add("消息")
//    }
//
//    override fun setSets() {
//        super.setSets()
//
//        setBackgroundColor(Color.WHITE)
//
//        vm.detail()
//                .withViewState(this)
//                .withLoadMore(this)
//                .load(this)
//
//        setOnSwipeRefreshListener {
//            if (it == SwipeState.START) {
//                vm.detail()
//                        .withSwipe(this)
//                        .load(this)
//            }
//        }
//
//        setOnLoadMoreListener {
//            vm.detailMore(it == LoadMore.State.ERROR)
//                    .withLoadMore(this)
//                    .load(this)
//        }
//
//        bindBus()
//    }
//
//    private fun bindBus() {
//        Bus.get().with(this)
//                .accept(EventId.LOGIN)
//                .subscribe {
//                    startSwipeRefresh()
//                }
//
//        Bus.get().with(this)
//                .accept(EventId.LOGOUT)
//                .subscribe {
//                    vm.onLogout()
//                }
//    }
//
//    override val useLazyLoad: Boolean
//        get() = true
//
//    override fun onVisible() {
//        super.onVisible()
//
//        if (viewState != ViewState.LOADING) startSwipeRefresh()
//    }
//}
