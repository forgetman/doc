package vector.bindingadapter.adapter

import androidx.databinding.BindingAdapter
import dsb.view.CaptchaView
import vector.bindingadapter.BINDING_PREFIX

object CaptchaViewBinding {

    private const val MAX_COUNT = BINDING_PREFIX + "captchaView_maxCount"
    private const val RESENT_TEXT = BINDING_PREFIX + "captchaView_resendText"
    private const val COUNTDOWN_TEXT = BINDING_PREFIX + "captchaView_countdownText"
    private const val COUNTDOWN_START = BINDING_PREFIX + "captchaView_countdownStart"

    @JvmStatic
    @BindingAdapter(MAX_COUNT)
    fun setMaxCount(captchaView: CaptchaView, count: Int) {
        captchaView.setMaxCount(count)
    }

    @JvmStatic
    @BindingAdapter(RESENT_TEXT)
    fun setResendText(captchaView: CaptchaView, text: String) {
        captchaView.setResendText(text)
    }

    @JvmStatic
    @BindingAdapter(COUNTDOWN_TEXT)
    fun setCountdownText(captchaView: CaptchaView, text: String) {
        captchaView.secondText = {
            String.format(text, it)
        }
    }

    @JvmStatic
    @BindingAdapter(COUNTDOWN_START)
    fun setCountdownStart(captchaView: CaptchaView, flag: Boolean) {
        if (flag) captchaView.start()
    }

}