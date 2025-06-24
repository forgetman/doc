package fund.design.ui.activity

/**
 * 绑定手机号
 * @author yuansui
 * @since 2018/8/3
 */
//@Creator
//class BindPhoneActivity : PhoneInputActivity<PhoneInputViewModel>() {
//
//    @Extra
//    lateinit var wxUid: String
//
//    override val layoutId: Int
//        get() = R.layout.activity_bind_phone
//
//    override val actionText: String
//        get() = "绑定手机号"
//
//    override fun flowOfNavBar() {
//        addNavBack()
//        navBar.mid.add("绑定手机号")
//    }
//
//    override fun onActionClick() {
//        if (!checkPhone() || !checkCaptcha()) {
//            return
//        }
//
//        val phone = getPhoneText()
//        val captchaState = getCaptchaText()
//
//        vm.bindPhone(phone, captchaState, wxUid)
//                .withLoading(this)
//                .observe(this) {
//                    startActivity(MainActivity::class)
//                    finish()
//                }.doAction()
//    }
//}
