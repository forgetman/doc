package eth.def

import android.os.Environment
import java.io.File

/**
 * @author yuansui
 * @since 2023/3/20
 */
object DownloadConst {

    const val TEMP_FILE_SUFFIX = ".temp"
    private const val TEMP_PARENT_DIR = "temp/"

    val tempDir: File
        get() {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            return File(dir, TEMP_PARENT_DIR)
        }
}