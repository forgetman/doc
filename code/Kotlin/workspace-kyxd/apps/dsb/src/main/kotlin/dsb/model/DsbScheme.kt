package dsb.model

import android.app.Activity
import android.content.Context
import com.umeng.socialize.bean.SHARE_MEDIA
import dsb.R
import dsb.design.ui.activity.WebViewActivity
import lib.um.share.UMShare
import logger.L
import vector.ext.toast
import java.util.*

/**
 * H5页跳转原生页
 *
 * @author yuansui
 */
object DsbScheme {

    private const val SCHEME = WebViewActivity.SCHEME_DSB

    /**
     * 来自服务器的跳转原生页的请求
     */
    enum class UrlIntent(code: Int) {
        /**
         * 微信朋友
         */
        SHARE_WX_FRIEND(0);

        val code: Int = code + 20000

    }

    /**
     * 所有跳转请求中的参数
     */
    object H5Param {
        const val TITLE = "title"
        const val SUB_TITLE = "subtitle"
        const val LINK = "link"
    }

    /**
     * 根据url跳转界面的规则
     *
     * @param context
     * @param url
     */
    fun intent(context: Context, url: String) {
//        var code = 0
//        val ts = UrlIntent.values()
//        for (i in ts.indices) {
//            if (url.contains(ts[i].name)) {
//                code = ts[i].code
//                break
//            }
//        }

        if (url.startsWith(SCHEME)) {
            val intentStr = url.substring(SCHEME.length)

            var keys: HashMap<String, String>? = null
            val index = intentStr.indexOf("?")
            if (index != -1) {
                // 有参数
                keys = hashMapOf()
                intentStr.substring(index + 1)
                    .split("&")
                    .map {
                        it.split("=")
                    }.forEach {
                        keys[it[0]] = it[1]
                    }
            }

            when {
                intentStr.startsWith(UrlIntent.SHARE_WX_FRIEND.name.toLowerCase(Locale.getDefault())) -> {
                    val title = keys?.get(H5Param.TITLE)
                    val desc = keys?.get(H5Param.SUB_TITLE)
                    val link = keys?.get(H5Param.LINK)

                    UMShare.web {
                        host = context as? Activity
                        this.title = title
                        this.desc = desc
                        drawableRes = R.mipmap.ic_launcher
                        this.url = link
                        medias = arrayOf(SHARE_MEDIA.WEIXIN)
                        onShareResult = { toast("分享成功") }
                        onShareError = { _, throwable -> L.e(throwable) }
                    }.share()
                }
            }
        }

//        LaunchUtil.startActivityForResult(context, getIntent(context, url), code)
    }
}
