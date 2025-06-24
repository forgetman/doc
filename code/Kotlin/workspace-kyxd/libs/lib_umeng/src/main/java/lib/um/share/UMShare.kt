package lib.um.share

import android.app.Activity
import android.content.Context
import androidx.annotation.DrawableRes
import com.umeng.socialize.*
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb

/**
 * 友盟分享
 */
object UMShare {

    /**
     * 初始化
     */
    fun init(context: Context, appKey: String) {
        UMShareAPI.init(context, appKey)
        val config = UMShareConfig()
        config.isOpenShareEditActivity(false)
        config.isNeedAuthOnGetUserInfo(true)
        UMShareAPI.get(context).setShareConfig(config)
    }

    fun configWX(id: String, secret: String) {
        PlatformConfig.setWeixin(id, secret)
    }

    fun configQZone(id: String, key: String) {
        PlatformConfig.setQQZone(id, key)
    }

    fun configSina(key: String, secret: String, redirectUrl: String) {
        PlatformConfig.setSinaWeibo(key, secret, redirectUrl)
    }

    fun web(block: WebShare.WebShareAttr.() -> Unit): WebShare {
        val attr = WebShare.WebShareAttr()
        block(attr)
        return WebShare(attr)
    }

    abstract class BaseShare {
        abstract fun share()
    }

    class WebShare internal constructor(private val attr: WebShareAttr) : BaseShare() {

        class WebShareAttr {
            var host: Activity? = null
            var url: String? = null
            var title: String? = null
            var desc: String? = null
            var medias: Array<SHARE_MEDIA>? = null

            var onShareResult: ((media: SHARE_MEDIA) -> Unit)? = null
            var onShareError: ((media: SHARE_MEDIA, throwable: Throwable) -> Unit)? = null

            @DrawableRes
            var drawableRes: Int = 0
        }

        override fun share() {
            if (attr.host == null) return

            val umWeb = UMWeb(attr.url)

            umWeb.title = attr.title//标题
            umWeb.setThumb(UMImage(attr.host, attr.drawableRes))  //缩略图
            umWeb.description = attr.desc//描述

            ShareAction(attr.host)
                .withMedia(umWeb)
                .setDisplayList(*attr.medias!!)
                .setCallback(object : UMShareListener {

                    override fun onStart(share_media: SHARE_MEDIA) {}

                    override fun onResult(share_media: SHARE_MEDIA) {
                        attr.onShareResult?.invoke(share_media)
                    }

                    override fun onError(share_media: SHARE_MEDIA, throwable: Throwable) {
                        attr.onShareError?.invoke(share_media, throwable)
                    }

                    override fun onCancel(share_media: SHARE_MEDIA) {}
                })
                .open()
        }
    }
}
