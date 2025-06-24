package vector.util.intent.action

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import logger.L
import vector.util.Launcher
import vector.util.intent.IntentAction

val IntentAction.Companion.album: AlbumAction
    get() = AlbumAction()

class AlbumAction internal constructor() {

    companion object {
        private const val LOG_TAG = "AlbumAction"
    }

    fun interface Callback {
        fun onPhotoResult(uri: Uri?)
    }

    fun launch(host: Any?, callback: Callback) {
        // 从图库里选择照片 返回的数据在 intent.getData()里, 是Uri的形式
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            Launcher.registerForActivityResult(host, intent) { resultCode, data ->
                if (resultCode != RESULT_OK) {
                    callback.onPhotoResult(null)
                } else {
                    callback.onPhotoResult(data?.data)
                }
            }
        } catch (e: Exception) {
            L.e(LOG_TAG, e)
        }
    }
}