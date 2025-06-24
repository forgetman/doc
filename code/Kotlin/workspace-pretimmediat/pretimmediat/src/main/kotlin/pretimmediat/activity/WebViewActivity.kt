package pretimmediat.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import androidx.annotation.StringRes
import androidx.webkit.WebResourceErrorCompat
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import pretimmediat.R
import pretimmediat.ext.addBackIcon
import pretimmediat.ext.toast
import vector.app.activity.SimpleActivityEx
import vector.app.decor.ErrorViewEx
import vector.app.decor.ViewState
import vector.app.ext.bind.bindView
import vector.app.util.inflate
import vector.ext.copyToClipboard
import vector.util.intent.IntentAction
import vector.widget.WebView

@Creator
class WebViewActivity : SimpleActivityEx() {

    companion object {
        private const val SCHEME_TEL = "tel:"
        private const val SCHEME_MAIL = "mailto:"
        private const val SCHEME_WHATAPP = "whatsapp://"
    }

    @Extra(true)
    var url: String = ""

    @Extra(true)
    @StringRes
    var titleId: Int = 0

    private val webView by bindView<WebView>(R.id.webView)

    override fun createContentView(): View {
        return R.layout.activity_webview.inflate(this)
    }

    override fun initializeSystemBar() {
        if (titleId != 0) {
            appBar.addBackIcon(titleId) { finish() }
        } else {
            appBar.addBackIcon { finish() }
        }
    }

    override fun initializeContentView() {
        webView.setZoom(true)
        webView.addListener(object : WebView.Listener {

            override fun onLoadSettings(view: android.webkit.WebView, settings: WebSettings) {
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.setSupportMultipleWindows(true)
            }

            override fun onLoadingScheme(
                view: android.webkit.WebView,
                url: String,
                intent: Intent
            ): Boolean {
                when {
                    url.startsWith(SCHEME_WHATAPP) -> {
                        val result = IntentAction.app().url(url).launch()
                        if (!result) {
                            // 浏览器打开whatsapp官方
                            IntentAction.browser().url("https://api.whatsapp.com").launch()
                        }
                        return true
                    }

                    url.startsWith(SCHEME_TEL) -> {
                        // 复制并toast
                        val number = url.substring(SCHEME_TEL.length)
                        number.copyToClipboard()
                        toast(this@WebViewActivity, R.string.webview_copy_success)
                        IntentAction.phoneCall().tellNum(url.substring(SCHEME_TEL.length)).launch()
                        return true
                    }

                    url.startsWith(SCHEME_MAIL) -> {
                        IntentAction.mail().address(url.substring(SCHEME_MAIL.length)).launch()
                        return true
                    }
                }
                return super.onLoadingScheme(view, url, intent)
            }

            override fun onPageLoadStarted(
                view: android.webkit.WebView,
                url: String,
                favicon: Bitmap?
            ) {
                if (url.contains("/MPsuccess.html")) {
                    finish()
                }
            }

            override fun onClickChoosePhoto(view: android.webkit.WebView) {
                webView.uploadFile()
            }

            override fun onReceivedError(
                view: android.webkit.WebView,
                request: WebResourceRequest,
                error: WebResourceErrorCompat
            ) {
                viewState = ViewState.ERROR
                webView.loadUrl("about:blank");// 避免出现默认的错误界面
            }
        })

        webView.loadUrl(url)
    }

    override fun handleOnBackPressed() {
        webView.evaluateJavascript("javascript:onBackPressed()", object : ValueCallback<String> {
            override fun onReceiveValue(value: String?) {
                if (value == "0") {
                    // 不返回
                    return
                } else {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        supportFinishAfterTransition()
                    }
                }
            }
        })
    }

    override fun createErrorView(): ((Context) -> ErrorViewEx)? {
        return { WebErrorView(it) }
    }

    override fun onRetryClick() {
        viewState = ViewState.NORMAL
        webView.loadUrl(url)
    }

    class WebErrorView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : ErrorViewEx(context, attrs, defStyleAttr) {

        override val layoutId: Int
            get() = R.layout.layout_webview_error

        init {
            retryWith(R.id.tv_button)
        }
    }
}