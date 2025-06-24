@file:Suppress("unused")

package vector.bindingadapter

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import vector.EMPTY
import vector.bindingadapter.bind.Bind
import vector.widget.WebView
import android.webkit.WebView as AndroidWebView

/**
 * @author yuansui
 * @since 2018/11/9
 */
object WebViewBinding {

    private const val URL = BINDING_PREFIX + "webView_url"
    private const val ON_PROGRESS_CHANGED = BINDING_PREFIX + "webView_onProgressChanged"
    private const val ON_TITLE_CHANGED = BINDING_PREFIX + "webView_onTitleChanged"

    private const val ON_LOADING_URL = BINDING_PREFIX + "webView_onLoadingUrl"
    private const val ON_LOADING_SCHEME = BINDING_PREFIX + "webView_onLoadScheme"

    private const val PROGRESS_DRAWABLE = BINDING_PREFIX + "webView_progressDrawable"
    private const val ZOOM = BINDING_PREFIX + "webView_zoom"


    @JvmStatic
    @BindingAdapter(ON_LOADING_URL, URL + ATTR_CHANGED_SUFFIX, requireAll = false)
    fun setOnLoadingUrl(
        view: WebView,
        binding: Bind.Web.OnLoadingUrl?,
        attrChange: InverseBindingListener?
    ) {
        view.addListener(object : WebView.Listener {
            override fun onLoadingUrl(view: AndroidWebView, url: String): Boolean {
                attrChange?.onChange()
                return binding?.action?.invoke(view, url) ?: false
            }
        })
    }

    @JvmStatic
    @BindingAdapter(ON_LOADING_SCHEME)
    fun setOnLoadScheme(view: WebView, binding: Bind.Web.OnLoadingScheme?) {
        view.addListener(object : WebView.Listener {
            override fun onLoadingScheme(view: AndroidWebView, url: String, intent: Intent): Boolean {
                return binding?.action?.invoke(view, url, intent) ?: false
            }
        })
    }

    @JvmStatic
    @BindingAdapter(URL)
    fun setUrl(view: WebView, url: String?) {
        view.loadUrl(url ?: return)
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = URL)
    fun getUrl(view: WebView): String {
        return view.url ?: EMPTY
    }

    @JvmStatic
    @BindingAdapter(ON_PROGRESS_CHANGED)
    fun setOnProgressChanged(view: WebView, binding: Bind.Web.OnProgressChanged) {
        view.addListener(object : WebView.Listener {
            override fun onProgressChanged(view: android.webkit.WebView, progress: Int) {
                binding.action(progress)
            }
        })
    }

    @JvmStatic
    @BindingAdapter(ON_TITLE_CHANGED)
    fun setOnTitleChanged(view: WebView, binding: Bind.Web.OnTitleChanged) {
        view.addListener(object : WebView.Listener {
            override fun onTitleChanged(view: android.webkit.WebView, text: String) {
                binding.action(text)
            }
        })
    }

    @JvmStatic
    @BindingAdapter(PROGRESS_DRAWABLE)
    fun setProgressDrawable(view: WebView, drawable: Drawable) {
        view.setProgressDrawable(drawable)
    }

    @JvmStatic
    @BindingAdapter(ZOOM)
    fun setZoom(view: WebView, zoom: Boolean) {
        view.setZoom(zoom)
    }
}