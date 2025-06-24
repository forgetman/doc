package lib.base.model

import android.app.Dialog
import android.content.Intent
import vector.app.dialog.DialogEx
import kotlin.reflect.KClass

/**
 * @author yuansui
 * @since 2018/7/6
 */
class Test {
    var name: String? = null

    var clazz: KClass<*>? = null
    var intent: Intent? = null
    var dialogEx: DialogEx? = null
    var dialog: Dialog? = null
}