package fund.service

import android.content.Intent
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import vector.service.ServiceEx

/**
 * @author yuansui
 */
@Creator
class CommonService : ServiceEx() {

    @Extra
    lateinit var type: Type

    enum class Type {
        LOGOUT,
        DEVICE_ID,
    }

    override fun onHandleIntent(intent: Intent) {
        when (type) {
            Type.LOGOUT -> {
            }
            Type.DEVICE_ID -> {
//                createApi(DeviceApi::class).id("").toLive()
//                        .observe { Sp.putDid(it) }
//                        .doAction()
            }
        }
    }
}
