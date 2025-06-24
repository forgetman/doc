package vector.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewConfiguration
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.webkit.WebResourceErrorCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import logger.L
import sugar.collection.safeMutableListOf
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.ext.isSdkLessThan
import sugar.ext.runOnMainThread
import vector.app.ext.view.gone
import vector.app.ext.view.show
import vector.app.os.dp
import vector.util.InjectUtil
import vector.util.Launcher
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import java.lang.reflect.Constructor
import java.util.concurrent.TimeUnit
import android.webkit.WebView as AndroidWebView


private typealias OnResultData = (resultCode: Int, data: Intent?) -> Unit

/**
 * @author yuansui
 * @since 2018/11/9
 */
@Suppress("unused")
class WebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    interface Listener {
        fun onLoadSettings(view: AndroidWebView, settings: WebSettings) {}
        fun onProgressChanged(view: AndroidWebView, progress: Int) {}
        fun onTitleChanged(view: AndroidWebView, text: String) {}

        fun onPageLoadStarted(view: AndroidWebView, url: String, favicon: Bitmap?) {}
        fun onPageLoadFinish(view: AndroidWebView, url: String) {}

        fun onClickChoosePhoto(view: AndroidWebView) {}

        fun onLoadingUrl(view: AndroidWebView, url: String): Boolean {
            return false
        }

        fun onLoadingScheme(view: AndroidWebView, url: String, intent: Intent): Boolean {
            return false
        }

        fun onReceivedError(view: AndroidWebView, request: WebResourceRequest, error: WebResourceErrorCompat) {}
    }

    private val listeners = safeMutableListOf<Listener>()

    private var uploadMessages: ValueCallback<Array<Uri>>? = null

    val url: String?
        get() = webView.url


    private val webView by lazy {
        hookWebView()

        val v = AndroidWebView(context, attrs, defStyleAttr)
        v.isFocusableInTouchMode = true
        v.layoutParams = LayoutParamsFactory.relative(MATCH_PARENT, MATCH_PARENT)
        v
    }
    private val progressBar by lazy {
        val bar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        bar.layoutParams = LayoutParamsFactory.relative(MATCH_PARENT, 2.dp.toPx(context))
        bar
    }

    private val assetLoader = WebViewAssetLoader.Builder()
        .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
        .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(context))
        .build()

    init {
        addView(webView)
        addView(progressBar)
        setSettings()
        setWebClient()
        setChromeClient()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setSettings() {
        with(webView.settings) {
            cacheMode = WebSettings.LOAD_DEFAULT
            javaScriptEnabled = true

            if (isSdkAtLeast(SdkInt.L_21)) {
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            loadWithOverviewMode = true

            allowContentAccess = true // 是否可访问Content Provider的资源，默认值 true
            allowFileAccess = true    // 是否可访问本地文件，默认值 true

            if (isSdkLessThan(SdkInt.Q_29)) {
                // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
                @Suppress("DEPRECATION")
                allowFileAccessFromFileURLs = true
                // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
                @Suppress("DEPRECATION")
                allowUniversalAccessFromFileURLs = true
            }

            builtInZoomControls = true
            domStorageEnabled = true

//            layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
            useWideViewPort = true

            listeners.forEachElement {
                it.onLoadSettings(webView, this)
            }
        }

        webView.scrollBarStyle = SCROLLBARS_INSIDE_OVERLAY
    }

    private fun setWebClient() {
        webView.webViewClient = object : WebViewClientCompat() {

            @Deprecated("Deprecated in Java")
            @Suppress("DEPRECATION")
            override fun shouldOverrideUrlLoading(
                view: AndroidWebView?,
                url: String?
            ): Boolean {
                if (view == null || url == null) return false

                var comsumed: Boolean? = null
                //支持scheme唤醒已安装的app
                if (URLUtil.isNetworkUrl(url)) {
                    var urlConsumed = false
                    listeners.forEachElement {
                        if (!urlConsumed) {
                            urlConsumed = it.onLoadingUrl(view, url)
                        }
                    }
                    comsumed = urlConsumed
                } else {
                    try {
                        var schemeConsumed = false
                        listeners.forEachElement {
                            if (!schemeConsumed) {
                                schemeConsumed = it.onLoadingScheme(
                                    view,
                                    url,
                                    Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                                )
                            }
                        }
                        comsumed = schemeConsumed
                    } catch (e: Exception) {
                        L.e(e)
                    }
                }

                return if (comsumed != true) {
                    super.shouldOverrideUrlLoading(view, url)
                } else {
                    true
                }
            }

            override fun onPageStarted(view: AndroidWebView, url: String, favicon: Bitmap?) {
                listeners.forEachElement {
                    it.onPageLoadStarted(view, url, favicon)
                }
            }

            override fun onPageFinished(view: AndroidWebView, url: String) {
                listeners.forEachElement {
                    it.onPageLoadFinish(view, url)
                }
            }

            @Suppress("UNUSED_PARAMETER")
            fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            //ssl证书有问题，导致有些手机加载不出H5页面，或者布局样式错乱
            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: AndroidWebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
            }

            override fun onReceivedError(
                view: AndroidWebView,
                request: WebResourceRequest,
                error: WebResourceErrorCompat
            ) {
                super.onReceivedError(view, request, error)
                listeners.forEachElement {
                    it.onReceivedError(view, request, error)
                }
            }
        }
    }

    private fun setChromeClient() {
        webView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: AndroidWebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                if (newProgress == 100) {
                    progressBar.gone()
                } else {
                    progressBar.show()
                    progressBar.progress = newProgress
                }

                listeners.forEachElement {
                    it.onProgressChanged(view, newProgress)
                }
            }

            override fun onReceivedTitle(view: AndroidWebView, title: String) {
                super.onReceivedTitle(view, title)
                listeners.forEachElement {
                    it.onTitleChanged(view, title)
                }
            }

            // 5.0以上使用
            override fun onShowFileChooser(
                webView: AndroidWebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                onShowFileChooser(filePathCallback)
                return true
            }

            override fun getDefaultVideoPoster(): Bitmap {
                return super.getDefaultVideoPoster() ?: createBitmap(50, 50)
            }
        }
    }

    /**
     * webview上传文件5.0以上
     *
     * @param uploadMsg
     */
    @JavascriptInterface
    fun onShowFileChooser(uploadMsg: ValueCallback<Array<Uri>>) {
        uploadMessages = uploadMsg
        listeners.forEachElement {
            it.onClickChoosePhoto(webView)
        }
    }

    @JavascriptInterface
    fun uploadFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        FileUploadActivity.onResultData = { _, data ->
            handleResultData(data)
            FileUploadActivity.onResultData = null
            FileUploadActivity.uploadIntentLocal = null
        }

        FileUploadActivity.uploadIntentLocal = Intent.createChooser(intent, "选择要使用的应用")
        val uploadIntent = Intent(context, FileUploadActivity::class.java)
        Launcher.startActivity(context, uploadIntent)
    }

    private fun handleResultData(data: Intent?) {
        if (data == null) {
            uploadMessages?.onReceiveValue(null)
            return
        }

        if (uploadMessages != null) {
            var results: Array<Uri>? = null
            val dataString = data.dataString
            if (!dataString.isNullOrEmpty()) {
                results = arrayOf(dataString.toUri())
            }
            uploadMessages?.onReceiveValue(results)
            uploadMessages = null
        }

    }

    fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    fun loadUrl(url: String, headers: Map<String, String>) {
        webView.loadUrl(url, headers)
    }

    fun reloadUrl() {
        webView.reload()
    }

    fun setProgressDrawable(drawable: Drawable) {
        // 这里必须使用ClipDrawable, 不然的话效果会变成整个drawable平铺, 看不出来进度了
        val d = ClipDrawable(drawable, Gravity.START, ClipDrawable.HORIZONTAL)
        progressBar.progressDrawable = d
    }

    /**
     * 设置是否可以缩放
     */
    fun setZoom(enable: Boolean) {
        with(webView.settings) {
            setSupportZoom(enable)
            builtInZoomControls = enable
            useWideViewPort = enable
//            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            loadWithOverviewMode = enable
        }
    }

    /**
     * 获取UserAgent
     */
    fun getUserAgentString(): String {
        return webView.settings.userAgentString
    }

    /**
     * 设置UserAgent
     */
    fun setUserAgentString(userAgentString: String) {
        webView.settings.userAgentString = userAgentString
    }

    fun goBack() {
        webView.goBack()
    }

    fun goForward() {
        webView.goForward()
    }

    fun canGoBack() = webView.canGoBack()

    fun canGoForward() = webView.canGoForward()

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        webView.gone()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        webView.show()
    }

    fun destroy() {
        listeners.clear()

        webView.gone()
        clearCookie()
        if (webView.settings.builtInZoomControls) {
            val timeout = ViewConfiguration.getZoomControlsTimeout()
            runOnMainThread(timeout, TimeUnit.MILLISECONDS) {
                webView.destroy()
            }
        } else {
            webView.destroy()
        }
    }

    fun syncCookie(url: String, cookieValue: HashMap<String, String>) {
        try {
            with(CookieManager.getInstance()) {
                setAcceptCookie(true)
                if (isSdkAtLeast(SdkInt.L_21)) setAcceptThirdPartyCookies(webView, true)
                cookieValue.forEach {
                    setCookie(url, it.key.plus("=").plus(it.value))
                }

                if (isSdkAtLeast(SdkInt.R_30)) {
                    removeAllCookies(null)
                    removeSessionCookies(null)
                } else {
                    @Suppress("DEPRECATION")
                    removeAllCookie()
                    @Suppress("DEPRECATION")
                    removeSessionCookie()
                    @Suppress("DEPRECATION")
                    CookieManager.setAcceptFileSchemeCookies(true)
                }

                if (isSdkAtLeast(SdkInt.L_21)) flush()
            }
        } catch (e: Exception) {
            L.e(e)
        }
    }

    private fun clearCookie() {
        if (isSdkAtLeast(SdkInt.R_30)) {
            CookieManager.getInstance().removeAllCookies(null)
        } else {
            @Suppress("DEPRECATION")
            CookieManager.getInstance().removeAllCookie()
        }
    }

    /**
     * 解决部分手机webView闪退的bug
     * android.view.InflateException:
     * Binary XML file line #11: Binary XML file line #11: Error inflating class vector.view.WebViewEx
     *
     * 参考: https://www.jianshu.com/p/7ea97df73dee
     */
    @SuppressLint("PrivateApi", "DiscouragedPrivateApi", "SoonBlockedPrivateApi")
    private fun hookWebView() {
        val sdkInt = Build.VERSION.SDK_INT
        try {
            val factoryClass = Class.forName("android.webkit.WebViewFactory")
            val field = factoryClass.getDeclaredField("sProviderInstance")
            field.isAccessible = true
            var sProviderInstance = field.get(null)
            if (sProviderInstance != null) {
                L.d("sProviderInstance isn't null")
                return
            }
            val getProviderClassMethod = when {
                sdkInt > 22 -> factoryClass.getDeclaredMethod("getProviderClass")
                sdkInt == 22 -> factoryClass.getDeclaredMethod("getFactoryClass")
                else -> {
                    L.d("Don't need to Hook WebView")
                    return
                }
            }
            getProviderClassMethod.isAccessible = true
            val providerClass = getProviderClassMethod.invoke(factoryClass) as Class<*>
            val delegateClass = Class.forName("android.webkit.WebViewDelegate")
            val providerConstructor: Constructor<out Any>? =
                providerClass.getConstructor(delegateClass)
            if (providerConstructor != null) {
                providerConstructor.isAccessible = true
                val declaredConstructor = delegateClass.getDeclaredConstructor()
                declaredConstructor.isAccessible = true
                sProviderInstance =
                    providerConstructor.newInstance(declaredConstructor.newInstance())
                field.set("sProviderInstance", sProviderInstance)
            }
            L.d("Hook done!")
        } catch (e: Throwable) {
            L.e(e)
        }
    }

    @SuppressLint("JavascriptInterface")
    fun addJavascriptInterface(obj: Any, interfaceName: String) {
        webView.addJavascriptInterface(obj, interfaceName)
    }

    fun evaluateJavascript(script: String, resultCallback: ValueCallback<String>?) {
        webView.evaluateJavascript(script, resultCallback)
    }

    fun onResume() {
        webView.onResume()
    }

    fun onPause() {
        webView.onPause()
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }
}

internal class FileUploadActivity : AppCompatActivity() {

    companion object {
        var onResultData: OnResultData? = null
        var uploadIntentLocal: Intent? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        InjectUtil.bind(this)

        window.setGravity(Gravity.START or Gravity.TOP)
        val attrs = window.attributes
        attrs.x = 0
        attrs.y = 0
        attrs.width = 1
        attrs.height = 1
        window.attributes = attrs

        uploadIntentLocal?.let {
            Launcher.registerForActivityResult(this, it) { resultCode, data ->
                onResultData?.invoke(resultCode, data)
                finish()
            }
        } ?: finish()
    }
}