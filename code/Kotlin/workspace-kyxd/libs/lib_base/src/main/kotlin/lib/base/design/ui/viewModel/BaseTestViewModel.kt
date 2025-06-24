package lib.base.design.ui.viewModel

import android.app.Application
import android.app.Dialog
import android.content.Intent
import lib.base.model.Test
import vector.app.dialog.DialogEx
import vector.app.viewmodel.ViewModelEx
import vector.bindingadapter.bind.Bind
import vector.util.Launcher.startActivity
import kotlin.reflect.KClass

/**
 * @author yuansui
 * @since 2018/7/6
 */
abstract class BaseTestViewModel(app: Application) : ViewModelEx(app) {

    val list = mutableListOf<Test>()

    fun add(name: String, clz: KClass<*>) {
        val t = Test()
        t.name = name
        t.clazz = clz
        list.add(t)
    }

    fun add(name: String, intent: Intent) {
        val t = Test()
        t.name = name
        t.intent = intent
        list.add(t)
    }

    fun add(name: String, dialog: DialogEx) {
        val t = Test()
        t.name = name
        t.dialogEx = dialog
        list.add(t)
    }

    fun add(name: String, dialog: Dialog) {
        val t = Test()
        t.name = name
        t.dialog = dialog
        list.add(t)
    }

    val onClick = ScrollableBind.List.OnItemClick { _, position ->
        val t = list[position]
        when {
            t.clazz != null -> t.clazz?.let { startActivity(it) }
            t.intent != null -> t.intent?.let { startActivity(it) }
            t.dialogEx != null -> t.dialogEx?.show()
            t.dialog != null -> t.dialog?.show()
        }
    }
}