package dsb.design.ui.activity

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dsb.Bus
import dsb.EventId
import dsb.databinding.ActivityBindPhoneBinding
import dsb.design.viewModel.BindPhoneViewModel
import dsb.ext.addBackIcon
import dsb.ext.withLoading
import dsb.ext.withToast
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import lib.base.design.repo.CaptchaType
import lib.base.design.ui.activity.BaseDBActivity
import vector.bindingadapter.bind.Bind

/**
 * 微信登录后的绑定手机号
 * @author yuansui
 * @since 2019/1/29
 */
@Creator
class BindPhoneActivity : BaseDBActivity<BindPhoneViewModel>() {

    @Extra
    var wxUid: String? = null

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityBindPhoneBinding.inflate(inflater)
        binding.owner = this
        binding.input = viewModel.input
        return binding
    }

    override fun initializeData() {
        viewModel.wxUid = wxUid
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText("绑定手机号")
    }

    override fun flowOfSetup() {
        with(viewModel) {
            input.onCaptchaClick.observe(this@BindPhoneActivity) {
                if (checkPhone()) {
                    fetchCaptcha(CaptchaType.SNS)
                        .observe {
                            input.startCountdown.value = true
                        }
                        .withLoading(this@BindPhoneActivity)
                        .withToast()
                }
            }

            user.observe(this@BindPhoneActivity) {
                Bus.get().send(EventId.SIGN_IN)
                finish()
            }
        }
    }

    val onBindClick = Bind.OnClick {
        if (viewModel.checkLoginEnabled()) {
            viewModel.bindPhone().withLoading(this).withToast()
        }
    }
}