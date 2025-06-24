package dsb.design.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import dsb.databinding.DialogLogoutConfirmBinding
import dsb.ext.withLoading
import dsb.ext.withToast
import dsb.network.api.MeApi
import eth.ext.asBinder
import lib.base.UserApi
import lib.base.design.repo.CaptchaType
import lib.base.network.createApi
import live.Live
import vector.app.databinding.dialog.DBDialogEx
import vector.bindingadapter.bind.Bind
import vector.ext.toast
import vector.util.LayoutParamsFactory
import vector.util.MATCH
import vector.util.WRAP

/**
 * @author yuansui
 * @since 2019/2/21
 */
class LogoutConfirmDialog(context: Context?, private val mobile: String?) : DBDialogEx(context) {

    var onAction: OnDialogAction? = null

    val startCountdown = Live(false)

    val text = "发送至手机号码".plus(mobile)

    val captcha = Live<String?>()
    val confirmEnable = Live<Boolean>()

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH, WRAP)

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = DialogLogoutConfirmBinding.inflate(layoutInflater)
        binding.owner = this
        return binding
    }

    override fun initializeData() {
        captcha.observe(this) {
            confirmEnable.value = !it.isNullOrEmpty()
        }
    }

    override fun flowOfSetup() {
        setGravity(Gravity.BOTTOM)
        setDimAmount(0.5f)

        cancelable(true)
        dismissOnTouchOutside(true)
    }

    val onCaptchaClick = Bind.OnClick {
        createApi<UserApi>()
            .captcha(mobile, CaptchaType.SNS.id)
            .asBinder()
            .withLoading(this, context)
            .withToast()
            .observe(this) {
                startCountdown.value = true
            }
            .launch(this)
    }

    val onConfirmClick = Bind.OnClick {
        // 确认注销
        createApi<MeApi>()
            .logout(captcha.value)
            .asBinder()
            .withLoading(this, context)
            .withToast()
            .observe(this) {
                toast("用户已经注销!")
                dismiss()
                onAction?.invoke()
            }
            .launch(this)
    }

    val onCloseClick = Bind.OnClick {
        dismiss()
    }
}