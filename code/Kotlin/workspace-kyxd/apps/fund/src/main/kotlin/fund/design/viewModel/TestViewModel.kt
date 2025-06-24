package fund.design.viewModel

import fund.design.ui.activity.LoginActivity
import fund.design.ui.activity.MainActivity
import lib.base.design.ui.viewModel.BaseTestViewModel

/**
 * @author yuansui
 * @since 2018/8/3
 */
class TestViewModel : BaseTestViewModel() {

    override fun init() {
        super.init()

        add("首页", MainActivity::class)
        add("登录", LoginActivity::class)
//        add("绑定手机号", BindPhoneActivity::class)
    }
}