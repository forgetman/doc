package lib.sy

import android.content.Context
import com.chuanglan.shanyan_sdk.OneKeyLoginManager
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig
import eson.Eson
import logger.L

typealias OnLoginListener = (token: String?) -> Unit

/**
 * @author yuansui
 * @since 2020/6/12
 */
object SY {

    private val manager: OneKeyLoginManager
        get() = OneKeyLoginManager.getInstance()

    fun config(action: Attr.() -> Unit) {
        val a = Attr()
        action(a)
        //闪验SDK配置debug开关 （必须放在初始化之前，开启后可打印闪验SDK更加详细日志信息）
        manager.setDebug(a.debuggable)

        // 初始化
        manager.init(a.context, a.appId) { code, result ->
            L.www("init code = $code")
            L.www("init result = $result")
        }

        manager.getPhoneInfo { code, result ->
            L.www("getPhoneInfo code = $code")
            L.www("getPhoneInfo result = $result")
        }

        val builder = ShanYanUIConfig.Builder()
            .setPrivacyText("同意", "和", "、", "、", "并授权大社宝获取本机号码")
            .setAppPrivacyOne("大社宝用户协议", "http://www.baidu.com")
            .setShanYanSloganHidden(true) // 隐藏"创蓝254技术支持"
        manager.setAuthThemeConfig(builder.build())
    }

    fun startLogin(listener: OnLoginListener) {
        manager.openLoginAuth(true, { code, result ->
            L.www("OpenLoginAuthListener code = $code")
            L.www("OpenLoginAuthListener result = $result")
        }, { code, result ->
            L.www("OneKeyLoginListener code = $code")
            L.www("OneKeyLoginListener result = $result")
            if (code == 1000) {
                val loginToken = Eson.default().fromJson(result, LoginToken::class.java)
                listener(loginToken?.token)
            } else {
                listener(null)
            }
        })
    }

    fun finishLogin() {
        manager.finishAuthActivity()
    }

    class LoginToken {
        var token: String? = null
    }

    class Attr internal constructor() {
        var appId: String? = null
        var context: Context? = null
        var debuggable: Boolean = true
    }
}