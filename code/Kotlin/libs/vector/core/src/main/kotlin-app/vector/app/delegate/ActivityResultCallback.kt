package vector.app.delegate

import android.content.Intent

fun interface ActivityResultCallback {
    fun onActivityResult(resultCode: Int, data: Intent?)
}