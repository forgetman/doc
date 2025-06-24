package vector.util.intent.action

import android.content.Intent
import android.net.Uri
import logger.L
import vector.util.Launcher
import vector.util.intent.IntentAction
import java.io.File

/**
 * 调用第三方打开Word文件
 */
val IntentAction.Companion.word: WordAction
    get() = WordAction()

class WordAction internal constructor() : FileAction() {
    override val type: String
        get() = "application/msword"
}

/**
 * 调用第三方打开PPT文件
 */
val IntentAction.Companion.ppt: PptAction
    get() = PptAction()

class PptAction internal constructor() : FileAction() {
    override val type: String
        get() = "application/vnd.ms-powerpoint"
}

/**
 * 调用第三方打开excel文件
 */
val IntentAction.Companion.excel: ExcelAction
    get() = ExcelAction()

class ExcelAction internal constructor() : FileAction() {
    override val type: String
        get() = "application/vnd.ms-excel"
}

abstract class FileAction {
    abstract val type: String

    fun launch(path: String) {
        val uri = Uri.fromFile(File(path))
        val intent = Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_DEFAULT)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setDataAndType(uri, type)

        try {
            Launcher.startActivity(intent)
        } catch (e: Exception) {
            L.e("FileAction", e)
        }
    }
}
