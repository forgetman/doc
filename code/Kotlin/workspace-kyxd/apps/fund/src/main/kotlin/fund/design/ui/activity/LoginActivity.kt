package fund.design.ui.activity

import android.view.View
import androidx.databinding.ViewDataBinding
import fund.R
import fund.databinding.ActivityLoginBinding
import fund.design.viewModel.user.PhoneInputViewModel
import fund.ext.addNavBack
import vector.app.ext.bind.bindView

/**
 * @author yuansui
 * @since 2018/8/6
 */
class LoginActivity : PhoneInputActivity<PhoneInputViewModel>() {

//    override val actionText: String
//        get() = "登录"

    private val layoutWxLogin by bindView<View>(R.id.login_layout_wx)

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.owner = this
        binding.input
        return binding
    }

    override fun flowOfNavBar() {
        addNavBack()
        navBar.mid.addText("登录")
    }
//
//    override fun setSets() {
//        super.setSets()
//
//        layoutWxLogin.onClick {
//            doWxLogin()
//        }
//    }
//
//    private fun doWxLogin() {
//        UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, object : UMAuthListener {
//
//            override fun onStart(share_media: SHARE_MEDIA) {}
//
//            override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {
//                val jsonObject = JSONObject()
//                for ((key, value) in map) {
//                    val k: String
//                    val v: String
//                    try {
//                        k = key
//                        v = value
//                        jsonObject.put(k, v)
//                    } catch (e: JSONException) {
//                        L.e(e)
//                    }
//                }
//
//                vm.loginWechat(jsonObject.toString())
//                        .withLoading(this@LoginActivity)
//                        .observe {
//                            if (it.wxUid.isNotEmpty()) {
//                                BindPhoneActivityCreator.create(it.wxUid.orEmpty()).get(this@LoginActivity)
//                                finish()
//                            } else {
//                                finishLogin()
//                            }
//                        }.doAction()
//            }
//
//            override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
//                toast(throwable.message)
//            }
//
//            override fun onCancel(share_media: SHARE_MEDIA, i: Int) {}
//        })
//    }
//
//    override fun onActionClick() {
//        if (!checkPhone() || !checkCaptcha()) {
//            return
//        }
//
//        val phone = getPhoneText()
//        val captchaState = getCaptchaText()
////        vm.login(phone, captchaState)
////                .withLoading(this)
////                .observe(this) {
////                    finishLogin()
////                }.error {
////                    toast(it.message)
////                }.doAction()
//    }
}