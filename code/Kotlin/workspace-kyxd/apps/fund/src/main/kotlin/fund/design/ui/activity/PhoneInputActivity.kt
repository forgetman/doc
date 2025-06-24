package fund.design.ui.activity

import fund.design.viewModel.user.PhoneInputViewModel
import vector.design.ui.activity.ActivityEx

/**
 * @author yuansui
 * @since 2018/8/5
 */
abstract class PhoneInputActivity<VM : PhoneInputViewModel> : ActivityEx<VM>() {

//    private val etPhone by bindView<EditText>(R.id.phone_input_et_phone)
//    private val etCaptcha by bindView<EditText>(R.id.phone_input_et_captcha)
//    private val tvAction by bindView<TextView>(R.id.phone_input_tv_action)
//    private val tvVoice by bindView<View>(R.id.phone_input_tv_voice)
//    private val captchaView by bindView<CaptchaView>(R.id.phone_input_tv_captcha)
//
//    private var phoneValid = false
//    private var captchaValid = false
//
//    abstract val actionText: String
//
//    @CallSuper
//    override fun setSets() {
//        etPhone.onTextChanged {
//            phoneValid = it.trim().length == 11 && it.isMobileCN()
//        }
//
//        etCaptcha.onTextChanged {
//            // 没有特殊字符
//            captchaValid = it.isNotEmpty() && !it.hasSpecialSymbol()
//        }
//
//        @Suppress("ConstantConditionIf")
//        if (BuildConfig.TEST) {
//            etPhone.setText("18511003538")
//            etCaptcha.setText("666666")
//        }
//
//        captchaView.maxCount = 60
//
//        tvAction.text = actionText
//
//        bindLives()
//        bindClicks()
//    }
//
//    private fun bindClicks() {
//        tvAction.onClick {
//            onActionClick()
//        }
//
//        tvVoice.onClick {
//            if (!checkPhone()) {
//                return@onClick
//            }
//
//            loadCaptcha(CaptchaType.CALL)
//        }
//
//        captchaView.onClick {
//            if (!checkPhone()) {
//                return@onClick
//            }
//
//            loadCaptcha(CaptchaType.SNS)
//        }
//    }
//
//    private fun bindLives() {
////        captchaView.bind(vm.captchaState, this)
//    }
//
//    fun checkPhone(): Boolean {
//        if (!phoneValid) {
//            toast("手机格式不正确!")
//        }
//        return phoneValid
//    }
//
//    fun checkCaptcha(): Boolean {
//        if (!captchaValid) {
//            toast("密码格式不正确")
//        }
//        return captchaValid
//    }
//
//    private fun loadCaptcha(type: CaptchaType) {
////        vm.getCaptchaState(getPhoneText(), type).withLoading(this)
//    }
//
//    fun getPhoneText() = etPhone.text.toString()
//
//    fun getCaptchaText() = etCaptcha.text.toString()
//
//    abstract fun onActionClick()
//
//    fun finishLogin() {
//        startActivity(MainActivity::class)
//        finish()
//        toast("登录成功")
//    }
}