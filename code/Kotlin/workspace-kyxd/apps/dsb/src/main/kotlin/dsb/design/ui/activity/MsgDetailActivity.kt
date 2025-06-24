package dsb.design.ui.activity

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dsb.databinding.ActivityMsgDetailBinding
import dsb.design.repo.MessageRepo
import dsb.design.ui.itembinder.MsgDetailItemBinder
import dsb.ext.addBackIcon
import dsb.ext.withLoadMore
import dsb.ext.withSwipe
import dsb.ext.withViewState
import dsb.model.DetailMessage
import eth.ext.bind
import eth.model.Nive
import lib.base.design.ui.activity.BaseSimpleDBActivity
import lib.base.model.Page
import live.ext.get
import vector.bindingadapter.bind.Bind
import vector.swiperefresh.widget.LoadMore
import vector.util.VERTICAL
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/2/13
 */
@AndroidEntryPoint
class MsgDetailActivity : BaseSimpleDBActivity() {

    val itemBinder = MsgDetailItemBinder()

    // FIXME: 需要更换自己的divider样式
    val decoration by lazy { DividerItemDecoration(this, VERTICAL) }

    @Inject
    lateinit var repo: MessageRepo

    val data = Nive<List<DetailMessage>>()
    private var page = Page()

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityMsgDetailBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText("消息详情")
    }

    override fun flowOfSetup() {
        fetchData().withViewState(this)
    }

    val onSwipe = RefreshBind.OnSwipe {
        fetchData().withSwipe(this, it)
    }

    val onLoadMore = RefreshBind.OnLoadMore { view, state ->
        fetchData(state).withLoadMore(this, view)
    }

    val onItemClick = ScrollableBind.List.OnItemClick { position ->
        val item = data[position] ?: return@OnItemClick
        if (!item.url.isNullOrEmpty()) {
            WebViewActivityCreator.create().url(item.url).start(this)
        }
    }

    private fun fetchData(state: LoadMore.State? = null) =
        repo.fetchDetailMessage(page.change(state)).bind(data).launch(this)
}