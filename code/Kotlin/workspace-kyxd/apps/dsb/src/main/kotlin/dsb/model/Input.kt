package dsb.model

import android.view.View
import live.Live

/**
 * @author yuansui
 * @since 2019/1/29
 */
class Input {
    val phone = Live<String>()
    val captcha = Live<String>()
    val startCountdown = Live<Boolean>(false)
    val onCaptchaClick = Live<View>()
}