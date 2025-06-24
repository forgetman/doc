package fund.design.ui.activity

import android.widget.TextView
import androidx.databinding.ViewDataBinding
import fund.R
import fund.databinding.ActivityWebBinding
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import vector.design.ui.activity.ActivityEx
import vector.design.viewModel.ViewModelEx

/**
 * @author yuansui
 */
@Creator
class WebViewActivity : ActivityEx<WebViewModel>() {

    private object WebPrefix {
        const val KTel = "tel:"
        const val GO_BACK = "goback://"
        const val WX = "weixin://"
        const val PREV = "dashebao_prev_url="
        const val ALIPAY = "alipays://"
    }

    @Extra(true)
    var title: String? = null

    @Extra
    lateinit var url: String

    private var prev: String? = null

    private lateinit var tvTitle: TextView

    private var preUrl: String? = null

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityWebBinding.inflate(layoutInflater)
        binding.owner = this
        return binding
    }

    override fun flowOfNavBar() {
        tvTitle = navBar.mid.addText { text = title }
        navBar.left.apply {
            addImage(R.drawable.nav_bar_ic_back) {
                onBackPressed()
            }
            addImage(R.drawable.nav_bar_ic_close) {
                finish()
            }
        }
    }

//    override fun onLoadStart() {
//        loadUrl(url)
//    }
//
//    override fun onReceivedWebTitle(h5Title: String?) {
//        val size = tvTitle.textSize
//        title = h5Title?.cut(size, 12 * size, "…")
//        tvTitle.text = title
//    }
//
//    override fun onBackPressed() {
//        preUrl?.let {
//            loadUrl(it)
//            preUrl = null
//        } ?: if (canGoBack()) {
//            goBack()
//        } else {
//            super.onBackPressed()
//        }
//    }
//
//    override val option: WebOption
//        get() = WebOption.build {
//            onLoading = object : OnWebLoading {
//                override fun loading(view: WebView, url: String): Boolean {
//                    when {
//                        url.startsWith(WebPrefix.KTel) -> IntentAction.phoneCall().tellNum(url.substring(WebPrefix.KTel.length)).launch()
//                        url.startsWith(WebPrefix.GO_BACK) -> // 返回首页
//                            finish()
//                        url.startsWith(WebPrefix.WX) -> IntentAction.app().url(url).alert("未检测到微信客户端, 请安装后重试").launch()
//                        url.startsWith(WebPrefix.ALIPAY) -> IntentAction.app().url(url).launch()
//                        else -> {
//                            if (url.contains(WebPrefix.PREV)) {
//                                // 解析出来值
//                                val start = url.indexOf(WebPrefix.PREV) + WebPrefix.PREV.length
//                                prev = url.substring(start)
//                                try {
//                                    prev = URLDecoder.decode(prev, "UTF-8")
//                                } catch (e: UnsupportedEncodingException) {
//                                    L.e(e)
//                                }
//
//                            }
//
//                            view.loadUrl(url)
//                        }
//                    }
//                    return true
//                }
//
//            }
//        }
}

class WebViewModel : ViewModelEx()
