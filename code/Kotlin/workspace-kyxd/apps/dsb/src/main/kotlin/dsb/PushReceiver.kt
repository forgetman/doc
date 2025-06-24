package dsb

import android.content.Context
import dsb.design.ui.activity.MainActivity
import dsb.design.ui.activity.MainActivityCreator
import dsb.design.ui.activity.SplashActivityCreator
import lib.base.serv.CommonService
import lib.base.serv.CommonServiceCreator
import lib.jg.BaseJPushReceiver

/**
 * @author yuansui
 * @since 2019/1/25
 */
class PushReceiver : BaseJPushReceiver() {

    override fun onRegistrationId(context: Context, id: String?) {
        CommonServiceCreator.create(CommonService.Type.UPLOAD_JPUSH_ID).jpushId(id).start(context)
    }

    override fun onOpenNotification(context: Context, message: String?) {
        if (MainActivity.running) {
            MainActivityCreator.create().pushMessage(message).start(context)
        } else {
            SplashActivityCreator.create(message).start(context)
        }
    }
}