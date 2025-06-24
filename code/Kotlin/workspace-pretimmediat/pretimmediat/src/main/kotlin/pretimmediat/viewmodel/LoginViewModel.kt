package pretimmediat.viewmodel

import android.app.Application
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.lifecycle.viewModelScope
import com.appsflyer.AFInAppEventType
import com.facebook.appevents.AppEventsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import coroutine.flow.state.toFalse
import coroutine.flow.state.toTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import pretimmediat.R
import pretimmediat.ext.countdownFlow
import pretimmediat.manager.AccountManager
import pretimmediat.model.AccountInfo
import pretimmediat.model.Captcha
import pretimmediat.network.api.ProductApi
import pretimmediat.network.createApi
import pretimmediat.property.Properties
import pretimmediat.repo.UserRepo
import pretimmediat.stats.Stats
import sugar.ext.throwIfNull
import vector.app.viewmodel.ViewModelEx
import vector.ext.getStringForLanguage
import vector.ext.isNotNullOrEmpty
import vector.app.util.toColor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repo: UserRepo, app: Application) :
    ViewModelEx(app) {

    companion object {
        private const val COUNT_DOWN_INTERVAL = 1L
        private const val CAPTCHA_COUNT_DOWN_MAX_COUNT = 60
        private const val SMS_COUNT_DOWN_MAX_COUNT = 5
    }

    private val _fetchEnable = MutableStateFlow(false)
    val fetchEnable = _fetchEnable.asStateFlow()

    private var isCountdownRunning = false
    val countDownText = MutableStateFlow<CharSequence?>(null)

    val phoneNumber = MutableStateFlow<String?>(null)
    val captcha = MutableStateFlow<String?>(null)

    val nextSelected = combine(phoneNumber, captcha) { number, captcha ->
        val numberValid = number.isNotNullOrEmpty()
        val captchaValid = captcha.isNotNullOrEmpty()
        numberValid && captchaValid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    /**
     * sms读取相关
     */
    val smsContent = MutableStateFlow<String?>(null)
    val smsCountdownText = MutableStateFlow<CharSequence?>(null)
    val smsVisible = MutableStateFlow(false)
    private var smsCountdownJob: Job? = null


    override fun onCreate() {
        countDownText.value = applicationContext.getStringForLanguage(R.string.login_obtain_captcha)

        phoneNumber.onEach {
            if (isCountdownRunning) return@onEach // 在倒计时的时候不干涉
            if (it.isNullOrEmpty()) {
                _fetchEnable.value = false
            } else {
                _fetchEnable.value = true
            }
        }.launchIn(viewModelScope)
    }

    private fun startSmsCountdown() {
        smsCountdownJob = countdownFlow(
            SMS_COUNT_DOWN_MAX_COUNT,
            COUNT_DOWN_INTERVAL,
            TimeUnit.SECONDS
        ).onEach { count ->
            smsCountdownText.value =
                applicationContext.getStringForLanguage(R.string.login_mark_as_read, count)
        }.onCompletion {
            smsVisible.toFalse()
        }.launchIn(viewModelScope)
    }

    fun stopSmsCountdown() {
        smsCountdownJob?.cancel()
        smsCountdownJob = null
        smsVisible.toFalse()
    }

    fun countdownFlow(): Flow<Int> = countdownFlow(
        CAPTCHA_COUNT_DOWN_MAX_COUNT,
        COUNT_DOWN_INTERVAL,
        TimeUnit.SECONDS
    ).onEach { count ->
        // 文字变色
        countDownText.value = SpannableStringBuilder().apply {
            val countdown1 =
                applicationContext.getStringForLanguage(R.string.login_obtain_captcha_countdown)
            val countText = if (count < 10) "0$count" else count.toString()
            val countdown2 = applicationContext.getStringForLanguage(
                R.string.login_obtain_captcha_countdown_part2,
                countText
            )
            append(countdown1, countdown2)
            val blackSpan = ForegroundColorSpan(Color.BLACK)
            val graySpan =
                ForegroundColorSpan(R.color.text_tertiary.toColor(applicationContext))
            setSpan(graySpan, 0, countdown1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(
                blackSpan,
                countdown1.length,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // 加粗
            setSpan(
                StyleSpan(Typeface.BOLD),
                countdown1.length,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }.onStart {
        isCountdownRunning = true
        _fetchEnable.value = false
    }.onCompletion {
        countDownText.value =
            applicationContext.getStringForLanguage(R.string.login_obtain_captcha_again)
        isCountdownRunning = false
        if (phoneNumber.value.isNotNullOrEmpty()) {
            // 判断一下这个期间是否有修改手机号
            _fetchEnable.value = true
        }
    }.flowOn(Dispatchers.Main)

    fun fetchCaptcha(): Flow<Captcha> {
        val phone = phoneNumber.value.throwIfNull("phone number is null")
        return repo.fetchCaptcha(phone).onEach { captcha ->
            if (captcha.smsCode.isNotNullOrEmpty()) {
                smsVisible.toTrue()
                smsContent.value = captcha.smsTemplate
                startSmsCountdown()

                this.captcha.value = captcha.smsCode
            } else {
                // 清空已输入的验证码
                this.captcha.value = null
            }
        }
    }

    fun isPhoneNumberValid(): Boolean = phoneNumber.value.isNotNullOrEmpty()

    fun isCaptchaValid(): Boolean {
        val value = captcha.value
        return isCaptchaValid(value)
    }

    fun isCaptchaValid(value: String?): Boolean {
        return value.isNotNullOrEmpty() && value.length >= 4
    }

    fun login(): Flow<AccountInfo> {
        val phone = phoneNumber.value.throwIfNull("phone number is null")
        val code = captcha.value.throwIfNull("captcha is null")
        return repo.login(phone, code).onEach {
            AccountManager.setInfo(it, phone)

            if (it.newCustFlag == "1") {
                Stats.faceBook.onEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION)
                Stats.firebase.onEvent(FirebaseAnalytics.Event.SIGN_UP)
                Stats.flyer.onEvent(AFInAppEventType.COMPLETE_REGISTRATION)
            }
            FirebaseCrashlytics.getInstance()
                .setUserId("${it.account},$phone,${Properties.gaid}")
        }
    }

    fun intentTo() = createApi<ProductApi>().mulAppInstallment().flowOn(Dispatchers.IO)
}