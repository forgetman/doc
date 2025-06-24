package lib.udesk

import android.content.Context
import cn.udesk.UdeskSDKManager
import cn.udesk.config.UdeskConfig
import udesk.core.UdeskConst.UdeskUserInfo
import java.util.*

/**
 * @author yuansui
 * @since 2019/2/12
 */
object UDesk {

    private var token: String? = null
    private var name: String? = null

    fun init(context: Context, domain: String, appKey: String, appId: String) {
        UdeskSDKManager.getInstance().initApiKey(context, domain, appKey, appId)
    }

    /**
     * @param userId   客户的唯一标识，用来识别身份，请使用 只包含字母，数字的字符集
     * @param userName
     */
    fun login(userId: String?, userName: String?) {
        token = userId
        name = userName
    }

    fun chat(context: Context) {
        val info = HashMap<String, String?>()
        info[UdeskUserInfo.USER_SDK_TOKEN] = token
        info[UdeskUserInfo.NICK_NAME] = name

        val builder = UdeskConfig.Builder()
        builder.setDefaultUserInfo(info)
        UdeskSDKManager.getInstance().entryChat(context, builder.build(), token)
    }
}