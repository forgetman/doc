package pretimmediat.dialog

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import coroutine.flow.launchIn
import coroutine.flow.state.isFalse
import coroutine.flow.state.isTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logger.L
import pretimmediat.R
import pretimmediat.databinding.DialogBankModifyConfirmBinding
import pretimmediat.ext.countdownFlow
import pretimmediat.ext.toast
import pretimmediat.ext.withNetworkError
import pretimmediat.ext.withPieceLoading
import pretimmediat.network.api.InputPieceApi
import pretimmediat.network.createApi
import pretimmediat.property.Properties
import pretimmediat.repo.InputPieceRepo
import pretimmediat.viewmodel.inputpiece.BankPieceViewModel
import vector.app.databinding.dialog.DBDialogEx
import vector.bindingadapter.bind.Bind
import vector.ext.getStringForLanguage
import vector.ext.isNotNullOrEmpty
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT
import vector.app.util.toColor
import java.util.concurrent.TimeUnit

/**
 * 银行卡修改确认
 */
class BankModifyConfirmDialog(
    context: Context?,
    private val userId: String?,
    private val ssid: String?,
    private val orderId: String?,
    private val bankTypeCode: String?,
    private val bankAccount: String?,
    private val callback: () -> Unit,
) : DBDialogEx(context) {

    companion object {
        private const val LOG_TAG = "BankModifyConfirmDialog"
        private const val COUNT_DOWN_INTERVAL = 1L
        private const val CAPTCHA_COUNT_DOWN_MAX_COUNT = 60
    }

    override val params: ViewGroup.LayoutParams?
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)

    val captcha = MutableStateFlow<String?>(null)
    val fetchEnable = MutableStateFlow(true)
    val countDownText = MutableStateFlow<CharSequence?>(null)
    val commitEnabled = captcha.map {
        it.isNotNullOrEmpty() && it.length == 4
    }.stateIn(lifecycleScope, SharingStarted.WhileSubscribed(), false)

    @Suppress("OPT_IN_USAGE")
    val onCaptchaClick = Bind.OnClick {
        if (fetchEnable.isFalse()) {
            toast(this.context, R.string.piece_bank_modify_confirm_try_later)
            return@OnClick
        }
        fetchCaptcha()
            .withPieceLoading(context)
            .withNetworkError(context)
            .flatMapConcat {
                countdownFlow()
            }.catch { e ->
                L.e(LOG_TAG, "获取验证码失败", e)
            }.launchIn(this)
    }

    val onCloseClick = Bind.OnClick {
        dismiss()
    }

    val onCommitClick = Bind.OnDebounceClick {
        if (commitEnabled.isTrue()) {
            upload(code ?: "")
        } else {
            toast(context, R.string.piece_bank_modify_confirm_enter_captcha)
        }
    }

    val contentPart2 = MutableStateFlow<CharSequence?>(null)

    private var code: String? = null

    private val repo = InputPieceRepo()
    private val viewModel = BankPieceViewModel(repo, context?.applicationContext as Application)


    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return DialogBankModifyConfirmBinding.inflate(inflater).apply {
            owner = this@BankModifyConfirmDialog
        }
    }

    override fun initializeContentView() {
        countDownText.value = context.getStringForLanguage(R.string.login_obtain_captcha)

        lifecycleScope.launch {
            contentPart2.value = SpannableStringBuilder().apply {
                val part1 = context.getStringForLanguage(R.string.piece_bank_modify_confirm_content_part2)
                val part2 = context.getStringForLanguage(R.string.piece_bank_modify_confirm_content_part3)
                val partNumber = Properties.accountPhoneNumber.getOrNull() ?: return@launch
                val numberLength = partNumber.length

                // 隐藏部分手机号, 只展示后4位, 其余位数位展示*号
                val number = if (numberLength > 4) {
                    val hideLength = numberLength - 4
                    val hide = "*".repeat(hideLength)
                    val show = partNumber.substring(hideLength)
                    "$hide$show"
                } else {
                    partNumber
                }

                append(part1, number, part2)
                setSpan(
                    ForegroundColorSpan(R.color.red.toColor(context)),
                    part1.length,
                    part1.length + partNumber.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        captcha.filterNotNull().onEach { c ->
            if (c.length == 4) {
                upload(c)
            }
        }.launchIn(this)

        viewModel.onCreate()
        viewModel.init(userId, ssid)
        viewModel.updateOrderInfo(orderId)
        viewModel.bankTypeCode.value = bankTypeCode
        viewModel.bankAccount.value = bankAccount
        viewModel.bankAccountConfirm.value = bankAccount
    }

    private fun fetchCaptcha() = Properties.accountPhoneNumber.asFlow().filterNotNull().flatMapConcat { phoneNumber ->
        createApi<InputPieceApi>()
            .bankConfirmCaptcha(userId, ssid, phoneNumber)
            .flowOn(Dispatchers.IO)
            .onEach {
                code = it.smsCode
                L.d(LOG_TAG, "fetchCaptcha, code = $code")
            }
    }

    private fun countdownFlow(): Flow<Int> = countdownFlow(
        CAPTCHA_COUNT_DOWN_MAX_COUNT,
        COUNT_DOWN_INTERVAL,
        TimeUnit.SECONDS
    ).onEach { count ->
        // 文字变色
        countDownText.value = SpannableStringBuilder().apply {
            val countdown1 =
                context.getStringForLanguage(R.string.login_obtain_captcha_countdown)
            val countText = if (count < 10) "0$count" else count.toString()
            val countdown2 = context.getStringForLanguage(
                R.string.login_obtain_captcha_countdown_part2,
                countText
            )
            append(countdown1, countdown2)
            val blackSpan = ForegroundColorSpan(Color.BLACK)
            val graySpan = ForegroundColorSpan(R.color.text_tertiary.toColor(context))
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
        fetchEnable.value = false
    }.onCompletion {
        countDownText.value = context.getStringForLanguage(R.string.login_obtain_captcha_again)
        fetchEnable.value = true
    }.flowOn(Dispatchers.Main)

    private fun upload(code: String) = viewModel.upload(code)
        .withPieceLoading(this.context)
        .withNetworkError(this.context)
        .catch { e ->
            L.e(LOG_TAG, "uploadBank", e)
        }.onEach {
            callback()
            dismiss()
        }.launchIn(this)

    override fun onStop() {
        super.onStop()

        viewModel.onDestroy()
    }
}