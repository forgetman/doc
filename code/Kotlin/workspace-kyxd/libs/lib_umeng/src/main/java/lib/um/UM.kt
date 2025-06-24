package lib.um

import android.content.Context

import com.umeng.commonsdk.UMConfigure

import lib.um.share.UMShare

/**
 * @author yuansui
 * @since 2017/11/22
 */
object UM {
    /**
     * 初始化common库
     * 参数1:上下文，不能为空
     * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
     * 参数3:Push推送业务的secret
     */
    fun init(context: Context, appKey: String, channel: String) {
        UMConfigure.init(context, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE, null)
        UMShare.init(context, appKey)
    }
}
