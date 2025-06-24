package test.service

import android.content.Intent
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import logger.L
import vector.service.ServiceEx

/**
 * @author yuansui
 * @since 2020/10/12
 */
@Creator
class TestServCommon : ServiceEx() {

    @Extra
    var servTest: Int? = null

    override fun onHandleIntent(intent: Intent) {
        L.www("common service start test = $servTest")
    }
}