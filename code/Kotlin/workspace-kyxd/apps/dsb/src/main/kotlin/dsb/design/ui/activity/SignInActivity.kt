package dsb.design.ui.activity

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import dagger.hilt.android.AndroidEntryPoint
import dsb.Bus
import dsb.EventId
import dsb.R
import dsb.databinding.ActivitySignInBinding
import dsb.design.viewModel.SignInViewModel
import dsb.ext.addBackIcon
import dsb.ext.withLoading
import dsb.ext.withToast
import lib.base.design.repo.CaptchaType
import lib.base.design.ui.activity.BaseDBActivity
import logger.L
import org.json.JSONException
import org.json.JSONObject
import vector.bindingadapter.bind.Bind
import vector.ext.setNavigationBarColor
import vector.ext.startActivity
import vector.ext.toast
import vector.os.colorRes

/**
 * @author yuansui
 * @since 2019/1/23 0023
 */
@AndroidEntryPoint
class SignInActivity : BaseDBActivity<SignInViewModel>() {

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivitySignInBinding.inflate(inflater).apply {
            owner = this@SignInActivity
            viewModel = this@SignInActivity.viewModel
            input = this@SignInActivity.viewModel.input
        }
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText("登录")
    }

    override fun flowOfSetup() {
        setNavigationBarColor(R.color.app_bg.colorRes)

        with(viewModel) {
            input.onCaptchaClick.observe(this@SignInActivity) {
                if (checkPhone()) {
                    fetchCaptcha(CaptchaType.SNS)
                        .observe {
                            input.startCountdown.value = true
                        }
                        .withLoading(this@SignInActivity)
                        .withToast()
                }
            }

            user.observe(this@SignInActivity) {
                Bus.get().send(EventId.SIGN_IN)
                finish()
            }
        }
    }

    val onVoiceCaptchaClick = Bind.OnClick {
        if (viewModel.checkPhone()) {
            viewModel.fetchCaptcha(CaptchaType.CALL)
                .withLoading(this)
                .withToast()
        }
    }

    val onLoginClick = Bind.OnClick {
        if (viewModel.checkLoginEnabled()) {
            viewModel.login().withLoading(this)
        }
    }

    val onWxLogin = Bind.OnClick { _ ->
        UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, object : UMAuthListener {

            override fun onStart(share_media: SHARE_MEDIA) {}

            override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {
                val jsonObject = JSONObject()
                for ((key, value) in map) {
                    val k: String
                    val v: String
                    try {
                        k = key
                        v = value
                        jsonObject.put(k, v)
                    } catch (e: JSONException) {
                        L.e(e)
                    }
                }

                viewModel.loginWechat(jsonObject.toString())
                    .observe { user ->
                        val uid = user?.wxUid
                        uid?.run {
                            BindPhoneActivityCreator.create(this).start(this@SignInActivity)
                            finish()
                        } ?: toMain()

                    }.withLoading(this@SignInActivity).withToast()
            }

            override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
                toast(throwable.message)
            }

            override fun onCancel(share_media: SHARE_MEDIA, i: Int) {}
        })
    }

    private fun toMain() {
        startActivity<MainActivity>()
        finish()
        toast("登录成功")
    }
}