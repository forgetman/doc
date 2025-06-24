package vector.app

import android.app.Activity
import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import vector.app.compose.ext.ui.AndroidWindowInsets
import vector.app.compose.ext.ui.androidWindowInsets
import vector.os.lifecycle.SimpleActivityLifecycleCallbacks

/**
 * @author yuansui
 * @since 2025/6/20
 */
class InitializationProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        context?.applicationContext
        val app = context?.applicationContext as? Application ?: return true
        app.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallbacks() {
            override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
                androidWindowInsets = AndroidWindowInsets(activity.window)
                app.unregisterActivityLifecycleCallbacks(this)
            }
        })
        return true
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        throw java.lang.IllegalStateException("Not allowed.")
    }

    override fun getType(uri: Uri): String? {
        throw java.lang.IllegalStateException("Not allowed.")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw java.lang.IllegalStateException("Not allowed.")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        throw IllegalStateException("Not allowed.")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        throw java.lang.IllegalStateException("Not allowed.")
    }
}