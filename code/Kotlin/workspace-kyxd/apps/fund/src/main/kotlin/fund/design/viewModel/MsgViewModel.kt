package fund.design.viewModel

/**
 * @author yuansui
 * @since 2018/8/1
 */
//class MsgViewModel : ListViewModelEx<Msg, MsgAdapter>() {
//
//    private val repo = MsgRepo()
//    private var currPage = 0
//
//    fun detail(): ELive<MutableList<Msg>> {
//        currPage = 0
//        return repo.detail(currPage).observe {
//            removeAll()
//            addAll(it)
//            invalidate()
//        }.error {
//            toast(it)
//        }
//    }
//
//    fun detailMore(retry: Boolean = false): ELive<MutableList<Msg>> {
//        currPage = if (retry) currPage else currPage + 1
//        return repo.detail(currPage).observe {
//            addAll(it)
//            invalidate()
//        }.error {
//            toast(it)
//        }
//    }
//
//    fun onLogout() {
//        removeAll()
//        invalidate()
//    }
//
//    override fun onItemClick(v: View?, position: Int) {
//        getItem(position)?.url.toWebWithoutLogin()
//    }
//}