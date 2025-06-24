package lib.base.serv

import android.content.Intent
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import lib.base.DeviceApi
import lib.base.Sp
import lib.base.network.createApi
import sugar.ext.coroutines.launch
import vector.service.ServiceEx

/**
 * @author yuansui
 */
@Creator
class CommonService : ServiceEx() {

    @Extra
    lateinit var type: Type

    @Extra(true)
    var jpushId: String? = null

    enum class Type {
        LOGOUT,
        DEVICE_ID,
        UPLOAD_JPUSH_ID, // 上传jpush id
    }

    override fun onHandleIntent(intent: Intent) {
        when (type) {
            Type.LOGOUT -> {
            }
            Type.DEVICE_ID -> {
                createApi<DeviceApi>()
                    .id()
                    .launch(this) {
                        Sp.putDid(it)
                    }
            }
            Type.UPLOAD_JPUSH_ID -> {
                createApi<DeviceApi>()
                    .uploadJPushId(jpushId)
                    .launch(this)
            }
        }
    }
}
