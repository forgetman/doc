@file:Suppress("unused")

package vector.util

import android.os.Environment
import android.util.Log
import vector.appContext
import vector.ext.ensureDirExist
import java.io.File

/**
 * 概念: 内置存储, 外置存储
 *
 * @author yuansui
 * @since 2020/11/1
 */
object Dir {

    private const val LOG_TAG = "Dir"

    /**
     * 内部存储
     */
    object Internal {
        /**
         * /data
         */
        val data: File
            get() = Environment.getDataDirectory()

        /**
         * /data/cache
         */
        val downloadCache: File
            get() = Environment.getDownloadCacheDirectory()

        /**
         * /data/user/0/包名/cache
         */
        val cache: File
            get() = appContext.cacheDir

        /**
         * /data/user/0/包名/files
         */
        val files: File
            get() = appContext.filesDir
    }

    /**
     * 外部存储, 实际也属于内置存储, 跟随app卸载而删除
     */
    object External {
        /**
         * /storage/emulated/0/Android/data/包名/cache
         * 一般存放临时的缓存文件, 用户 清除缓存 时会被清掉
         */
        val cache: File
            get() = appContext.externalCacheDir ?: appContext.cacheDir

        /**
         * /storage/emulated/0/Android/data/包名/files
         * 一般存放较长时间保存的文件, 用户 清除数据 时会被删掉
         * @param type 任意文件夹名称, 建议使用系统预设的, 如[Environment.DIRECTORY_MOVIES]
         * @param dir 二级路径
         */
        fun getFileDir(type: String, dir: String? = null): File {
            val maybeFilesDir: File = appContext.getExternalFilesDir(type) ?: cache
            return if (dir.isNullOrEmpty()) maybeFilesDir else File(maybeFilesDir, dir)
        }
    }

    /**
     * 公共存储, 属于外置存储(如SD卡)
     * /storage/emulated/0/Download/包名/
     *
     * PS: android10之后因为沙盒机制会出现两种情况
     * 1. 默认无法访问
     * 2. manifest里设置了requestLegacyExternalStorage为true, 且targetSdk低于31的话, 还可以访问
     */
    object Public {
        // SD卡路径, 不跟随app卸载而删除, 其他app可以访问
        val cache: File
            get() {
                if (!isExternalStorageWritable) return External.cache

                @Suppress("DEPRECATION")
                val path = buildString {
                    append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                    append(File.separator)
                    append(appContext.packageName)
                    append(File.separator)
                }
                val dir = File(path)
                val result = dir.ensureDirExist()
                if (!result) {
                    Log.d(LOG_TAG, "公共文件存储路径不存在或创建失败")
                    return External.cache
                }
                return dir
            }

        /**
         * 外部存储是否可以读和写
         */
        val isExternalStorageWritable: Boolean
            get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

        /**
         * 外部存储是否可以只读
         */
        val isExternalStorageReadable: Boolean
            get() = Environment.getExternalStorageState() in
                    setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    private fun mkDirs(file: File, dir: String?): String {
        val fileDir = if (dir.isNullOrEmpty()) {
            file
        } else {
            File(file, dir)
        }
        val validPath = if (fileDir.ensureDirExist()) {
            fileDir.absolutePath
        } else {
            if (dir.isNullOrEmpty()) {
                External.cache.absolutePath
            } else {
                File(External.cache, dir).absolutePath
            }
        }
        Log.d(LOG_TAG, "mkDirs path = $validPath")
        return validPath
    }

    /**
     * 在cache目录下建立dir的path
     * @param dir 想要建立的dir的path
     * @return 能正常使用的path
     */
    fun mkCacheDir(dir: String): String {
        return mkDirs(External.cache, dir)
    }

    /**
     * 在files目录下建立dir的path
     * @param type 任意文件夹名称, 建议使用系统预设的, 如[Environment.DIRECTORY_MOVIES]
     * @param dir 想要建立的dir的path
     * @return 能正常使用的path
     */
    fun mkFilesDir(type: String, dir: String?): String {
        return mkDirs(External.getFileDir(type, dir), null)
    }
}