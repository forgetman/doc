package dsb.design.ui.activity

import android.graphics.Color
import android.view.LayoutInflater
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import dsb.R
import dsb.databinding.ActivityWebviewBinding
import dsb.model.DsbScheme
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import lib.base.design.ui.activity.BaseSimpleDBActivity
import logger.L
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.bindingadapter.bind.Bind
import vector.app.ext.bind.bindView
import vector.ext.cut
import vector.ext.setNavigationBarColor
import vector.os.colorInt
import vector.app.os.drawableRes
import vector.util.intent.IntentAction
import vector.widget.WebView
import java.io.UnsupportedEncodingException
import java.net.URLDecoder


/**
 * @author yuansui
 * @since 2019/1/19
 */
@Creator
class WebViewActivity : BaseSimpleDBActivity() {

    companion object {
        const val SCHEME_DSB = "dsbscheme://"
        const val SCHEME_ALIPAY = "alipay"
        const val SCHEME_WX = "weixin://"
        const val SCHEME_GO_BACK = "goback://"
        const val SCHEME_TEL = "tel:"

        const val PREV = "dashebao_prev_url="
    }

    @Extra(true)
    var url: String? = null

    @Extra(true)
    var title: String? = null

    private var prevUrl: String? = null

    private val webView by bindView<WebView>(R.id.web_view)
    private lateinit var tvTitle: TextView

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityWebviewBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.left.addIcon {
            drawable = R.drawable.nav_bar_ic_back.drawableRes
            onClick = {
                back()
            }
        }

        appBar.right.addIcon {
            drawable = R.drawable.nav_bar_ic_close.drawableRes
            onClick = {
                finish()
            }
        }

        tvTitle = appBar.midAlign.addText {
            maxLines = 1
        }
    }

    override fun flowOfSetup() {
        setNavigationBarColor(Color.WHITE.colorInt)
    }

    val onTitleChanged = Bind.Web.OnTitleChanged {
        val textSize = tvTitle.textSize
        val text = it.cut(textSize, 12 * textSize, "...")
        tvTitle.text = text
    }

    val onLoadingUrl = Bind.Web.OnLoadingUrl { web, url ->
        if (url in PREV) {
            val start = url.indexOf(PREV) + PREV.length
            prevUrl = url.substring(start)
            try {
                prevUrl = URLDecoder.decode(prevUrl, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                L.e(e)
            }
        }

        web.loadUrl(url)
        true
    }

    val onLoadingScheme = Bind.Web.OnLoadingScheme { web, url, _ ->
        when {
            url.startsWith(SCHEME_DSB) -> {
                DsbScheme.intent(this, url)
                // FIXME: 拦截之后再次点击就无效了. 暂时使用reload来解决, 找时间找一下原因
                web.reload()
            }
            url.startsWith(SCHEME_TEL) -> {
                IntentAction.phoneCall().tellNum(url.substring(SCHEME_TEL.length)).launch()
            }
            url.startsWith(SCHEME_GO_BACK) -> {
                // 返回首页
                finish()
            }
            url.startsWith(SCHEME_WX) -> {
                IntentAction.app().url(url).alert("未检测到微信客户端, 请安装后重试").launch()
            }
            url.startsWith(SCHEME_ALIPAY) -> {
                IntentAction.app().url(url).alert("未检测到支付宝客户端").launch()
            }
        }
        true
    }

    override fun onBackPressed() {
        back()
    }

    private fun back() {
        prevUrl?.let {
            webView.loadUrl(it)
            prevUrl = null
        } ?: if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
    }
}
