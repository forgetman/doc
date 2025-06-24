package dsb.design.ui.frag

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import dsb.App
import dsb.Bus
import dsb.EventId
import dsb.R
import dsb.databinding.FragHomeBinding
import dsb.design.ui.activity.WebViewActivityCreator
import dsb.design.ui.activity.city.CityActivity
import dsb.design.ui.dialog.HomeMsgDialog
import dsb.design.ui.itembinder.*
import dsb.design.viewModel.HomeViewModel
import dsb.ext.withLoadMore
import dsb.ext.withSwipe
import dsb.ext.withToast
import dsb.ext.withViewState
import dsb.model.City
import dsb.model.GpsCity
import dsb.util.NetUtil
import lib.base.design.frag.BaseDBFrag
import vector.annotation.LayoutBindingClass
import vector.app.databinding.frag.DBFragEx
import vector.bindingadapter.bind.Bind
import vector.bindingadapter.trigger.BindTrigger
import vector.ext.startActivity
import vector.app.os.dp
import vector.app.os.drawableRes

/**
 * @author yuansui
 * @since 2019/1/17
 */
@AndroidEntryPoint
@LayoutBindingClass<FragHomeBinding>
class HomeFrag : BaseDBFrag<HomeViewModel>() {

    val itemBinders = listOf(
        Form0ItemBinder(),
        Form19ItemBinder(),
        Form24ItemBinder(),
        Form25ItemBinder(),
        Form30ItemBinder(),
        Form33ItemBinder(),
        Form34ItemBinder(),
        Form35ItemBinder(),
        Form36ItemBinder(),
        Form38ItemBinder(),
        Form39ItemBinder(),
        Form40ItemBinder(),
        Form41ItemBinder(),
        Form50ItemBinder(),
        Form51ItemBinder(),
        Form52ItemBinder()
    )
    val trigger = ScrollableBindTrigger.toTop()

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragHomeBinding.inflate(inflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    override fun initializeSystemBar() {
        appBar.mid.addText("大社宝")

        val tv = appBar.left.addText {
            text = GpsCity.name
            textSize = 12.dp.toPx(this@HomeFrag)
            drawableLeft = R.drawable.nav_bar_ic_location
            drawablePadding = 2.dp.toPx(this@HomeFrag)
            onClick = {
                startActivity<CityActivity>()
            }
        }

        appBar.right.addIcon(R.drawable.nav_bar_ic_msg.drawableRes) {

        }

        viewModel.gpsName.observe(this) {
            tv.text = it
        }
    }

    override fun flowOfSetup() {
        viewModel.message.observe(this) {
            HomeMsgDialog(context, it.icon).apply {
                onAction = {
                    WebViewActivityCreator.create().url(it.url).start(context)
                }
            }.show()
        }

        viewModel.fetchMessage()
        fetchForms()

        setBus()
    }

    private fun setBus() {
        Bus.get().with(this).onValue<City>(EventId.LOCATION_CITY) {
            // 成功
            if (!App.useGpsCity) return@onValue

            viewModel.gpsName.value = it.name
            if (it.id != App.currCity?.id) {
                App.currCity = it
                fetchForms()
            }
        }

        Bus.get().with(this).onValue<String>(EventId.LOCATION) {
            // 失败
            if (!App.useGpsCity) return@onValue

            GpsCity.name = it
            viewModel.gpsName.value = it
        }

        Bus.get().with(this).onValue<String>(EventId.LAUNCH_WEB) {
            WebViewActivityCreator.create().url(it).start(context)
        }

        Bus.get().with(this).onValue<City>(EventId.CHANGE_CITY) {
            App.useGpsCity = false

            viewModel.gpsName.value = it.name
            if (it.id != App.currCity?.id) {
                App.currCity = it
                fetchForms()
            }

        }

        Bus.get().with(this).onMessage(EventId.SIGN_IN) {
            // 登录以后刷新一次
            fetchForms()
        }
    }

    val onSwipe = RefreshBind.OnSwipe {
        viewModel.fetchForms(App.currCity?.id).withSwipe(this, it).withToast()
        NetUtil.refreshUnreadNumber()
    }

    val onLoadMore = RefreshBind.OnLoadMore { view, state ->
        viewModel.fetchForms(App.currCity?.id, state).withLoadMore(this, view)
    }

    override fun onRetryClick() {
        fetchForms()
    }

    private fun fetchForms() {
        viewModel.fetchForms(App.currCity?.id).withViewState(this).withToast()
        trigger.trig(false)
    }
}