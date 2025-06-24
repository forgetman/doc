package dsb.view

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import java.util.concurrent.TimeUnit

/**
 * 验证码的view
 *
 * @author yuansui
 */
class CaptchaView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        private const val RESEND_TEXT = "重新获取"
        private const val SECOND = "秒"
        private const val MAX_COUNT = 120
    }

    private var resendText = RESEND_TEXT
    private var second = SECOND
    private var maxCount = MAX_COUNT

    private var countDownTimer: CountDownTimer? = null

    // 倒数时的操作
    var secondText: ((second: Long?) -> String) = {
        it.toString().plus(second)
    }

    fun start() {
        cancel()

        countDownTimer = object : CountDownTimer(
            TimeUnit.SECONDS.toMillis(maxCount.toLong()),
            TimeUnit.SECONDS.toMillis(1)
        ) {
            override fun onTick(millisUntilFinished: Long) {
                text = secondText.invoke(millisUntilFinished)
            }

            override fun onFinish() {
                isEnabled = true
                text = resendText
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        cancel()
    }

    private fun cancel() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    /**
     * 设置等待状态显示的文字
     *
     * @param text
     */
    fun setResendText(text: String) {
        resendText = text
    }

    fun setResendText(@StringRes resId: Int) {
        resendText = context.getString(resId)
    }

    fun setSymbol(@StringRes id: Int) {
        second = context.getString(id)
    }

    /**
     * 设置最大倒数次数
     *
     * @param count
     */
    fun setMaxCount(count: Int) {
        maxCount = count
    }

}
