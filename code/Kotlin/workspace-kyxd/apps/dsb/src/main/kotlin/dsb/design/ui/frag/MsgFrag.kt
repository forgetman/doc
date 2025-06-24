package dsb.design.ui.frag

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import dsb.App
import dsb.databinding.FragMsgBinding
import dsb.design.ui.activity.MsgDetailActivity
import dsb.design.ui.itembinder.MsgItemBinder
import dsb.design.viewModel.MsgViewModel
import dsb.ext.withSwipe
import dsb.ext.withToast
import dsb.ext.withViewState
import dsb.model.InfoMessage
import dsb.util.NetUtil
import lib.base.design.frag.BaseDBFrag
import lib.udesk.UDesk
import live.ext.get
import vector.bindingadapter.bind.Bind
import vector.bindingadapter.trigger.BindTrigger
import vector.ext.startActivity

/**
 * @author yuansui
 * @since 2019/1/17
 */
@AndroidEntryPoint
class MsgFrag : BaseDBFrag<MsgViewModel>() {

    private var alreadyInit = false

    val itemBinder = MsgItemBinder()
    val trigger = SwipeRefreshBindTrigger.refresh()

    override val lazyLoadMode: LazyLoadMode
        get() = LazyLoadMode.RESUME

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragMsgBinding.inflate(inflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    override fun initializeSystemBar() {
        appBar.mid.addText("消息")
    }

    override fun flowOfSetup() {
        getData()

        App.unreadCount.observe(this) {
            viewModel.updateUnreadNumber(it)
        }

        viewModel.data.observe(this) {
            alreadyInit = true
        }
    }

    val onSwipe = RefreshBind.OnSwipe {
        viewModel.getMessage().withSwipe(this, it).withToast()
        NetUtil.refreshUnreadNumber()
    }

    val onItemClick = ScrollableBind.List.OnItemClick { position ->
        val item = viewModel.data[position] ?: return@OnItemClick
        when (item.type) {
            InfoMessage.Type.CUSTOMER -> UDesk.chat(requireContext())
            InfoMessage.Type.SYSTEM -> {
                startActivity<MsgDetailActivity>()
                // 直接本地重置红点状态
                App.unreadCount.value = 0
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (alreadyInit) {
            trigger.trig()
        }
    }

    override fun onRetryClick() {
        getData()
    }

    private fun getData() {
        viewModel.getMessage().withViewState(this).withToast()
    }

    override fun onDestroy() {
        super.onDestroy()

        App.unreadCount.removeObservers(this)
    }
}