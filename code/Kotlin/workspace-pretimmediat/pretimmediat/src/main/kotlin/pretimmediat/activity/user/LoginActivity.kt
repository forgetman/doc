package pretimmediat.activity.user

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import coroutine.flow.launchIn
import dagger.hilt.android.AndroidEntryPoint
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import logger.L
import pretimmediat.R
import pretimmediat.activity.MainActivity
import pretimmediat.activity.MainActivityCreator
import pretimmediat.activity.inputpiece.InfoPieceActivityCreator
import pretimmediat.databinding.ActivityLoginBinding
import pretimmediat.ext.addBackIcon
import pretimmediat.ext.bindLoading
import pretimmediat.ext.toast
import pretimmediat.ext.withLoading
import pretimmediat.ext.withNetworkError
import pretimmediat.manager.AccountManager
import pretimmediat.network.ParamsValue
import pretimmediat.viewmodel.LoginViewModel
import vector.app.databinding.activity.DBActivityEx
import vector.bindingadapter.bind.Bind

/**
 * 登录注册页
 */
@Suppress("OPT_IN_USAGE")
@AndroidEntryPoint
@Creator
class LoginActivity : DBActivityEx<LoginViewModel>() {

    companion object {
        private const val LOG_TAG = "LoginActivity"

        const val FROM_HOME = 0
        const val FROM_ORDER = 1
        const val FROM_ME = 2
    }

    @Extra
    var from: Int = FROM_HOME

    private val loading = MutableStateFlow(false)

    val onCaptchaClick = Bind.OnClick {
        if (!viewModel.isPhoneNumberValid()) {
            toast(this, R.string.login_tip_please_input_phone)
            return@OnClick
        }

        viewModel.fetchCaptcha()
            .withLoading(this)
            .withNetworkError(this)
            .flatMapConcat {
                viewModel.countdownFlow()
            }.catch { e ->
                L.e(LOG_TAG, "获取验证码失败", e)
            }.launchIn(this)
    }

    val onNextClick = Bind.OnClick {
        if (!viewModel.isPhoneNumberValid()) {
            toast(this, R.string.login_tip_please_input_phone)
            return@OnClick
        }

        if (!viewModel.isCaptchaValid()) {
            toast(this, R.string.login_tip_please_input_captcha)
            return@OnClick
        }

        startLogin()
    }

    val onSmsReadClick = Bind.OnClick {
        viewModel.stopSmsCountdown()
    }


    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivityLoginBinding.inflate(inflater).apply {
            owner = this@LoginActivity
            viewModel = this@LoginActivity.viewModel
        }
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(R.string.back) {
            finish()
        }
    }

    override fun initializeContentView() {
        loading.bindLoading(this)
    }

    private fun startLogin() {
        viewModel.login()
            .onStart { loading.value = true }
            .withNetworkError(this)
            .catch { e ->
                L.e(LOG_TAG, "登录失败", e)
                loading.value = false
            }.onEach {
                // 不能接入flatMap的流程里
                viewModel.intentTo()
                    .catch { e ->
                        L.e(LOG_TAG, "跳转失败", e)
                        loading.value = false
                        finish()
                    }.onEach { products ->
                        loading.value = false

                        when (from) {
                            FROM_HOME -> {
                                var flag = 0
                                for (p in products) {
                                    if (p.viewStatus != 0) {
                                        flag = 1
                                        break
                                    }
                                }

                                if (flag == 0) {
                                    // 跳转到进件页面, 传主产品的
                                    InfoPieceActivityCreator.create()
                                        .userId(AccountManager.account)
                                        .appSsid(ParamsValue.CLIENT_ID)
                                        .start(this)
                                    finish()
                                } else {
                                    // 跳转到首页第一个tab0页
                                    MainActivityCreator.create()
                                        .requiredTabIndex(MainActivity.TAB_HOME)
                                        .start(this)
                                }

                            }

                            FROM_ORDER -> {
                                MainActivityCreator.create()
                                    .requiredTabIndex(MainActivity.TAB_ORDER)
                                    .start(this)
                            }

                            FROM_ME -> {
                                MainActivityCreator.create()
                                    .requiredTabIndex(MainActivity.TAB_ME)
                                    .start(this)
                            }

                            else -> finish()
                        }
                    }.launchIn(this)
            }.launchIn(this)
    }
}