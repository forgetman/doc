package dsb.design.ui.frag

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dsb.R
import dsb.databinding.FragEachInfoBinding
import dsb.design.ui.activity.WebViewActivityCreator
import dsb.design.ui.itembinder.InfoItemBinder
import dsb.design.viewModel.EachInfoViewModel
import dsb.ext.withLoadMore
import dsb.ext.withSwipe
import dsb.ext.withToast
import dsb.ext.withViewState
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import lib.base.design.frag.BaseDBFrag
import vector.app.databinding.frag.DBFragEx
import vector.bindingadapter.bind.Bind
import vector.os.dimenRes
import vector.recyclerview.decoration.Decoration

/**
 * 单页资讯
 * @author yuansui
 * @since 2019/1/24
 */
@Creator
class EachInfoFrag : BaseDBFrag<EachInfoViewModel>() {

    @Extra
    var categoryId: Int = 0

    val itemBinder = InfoItemBinder()
    override val lazyLoadMode: LazyLoadMode
        get() = LazyLoadMode.IDLE

    val decoration by lazy {
        Decoration.linear {
            color = R.color.divider
            size = R.dimen.divider_height.dimenRes.toPx(this@EachInfoFrag)
            drawTop = false
            drawBottom = false
        }
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragEachInfoBinding.inflate(inflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    override fun initializeData() {
        viewModel.id = categoryId
    }

    override fun flowOfSetup() {
        viewModel.fetchInfo().withViewState(this).withToast()
    }

    val onSwipe = RefreshBind.OnSwipe {
        viewModel.fetchInfo().withSwipe(this, it).withToast()
    }

    val onLoadMore = RefreshBind.OnLoadMore { view, state ->
        viewModel.fetchInfo(state).withLoadMore(this, view)
    }

    val onItemClick = ScrollableBind.List.OnItemClick { position ->
        val item = viewModel.infos.value?.getOrNull(position) ?: return@OnItemClick
        WebViewActivityCreator.create().url(item.url).title(item.title).start(context)
    }

    override fun onRetryClick() {
        viewModel.fetchInfo().withViewState(this).withToast()
    }
}