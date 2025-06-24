package pretimmediat.util

import vector.util.intent.IntentAction
import vector.util.intent.action.AlbumAction

object PhotoUtil {

    /**
     * 从图库里选择照片
     */
    fun fromAlbum(host: Any, cb: AlbumAction.Callback): Boolean {
        return IntentAction.album()
            .host(host)
            .callback(cb)
            .launch()
    }
}
