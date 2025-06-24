package fund.design.repo

import android.provider.MediaStore
import eth.NLiveList
import eth.bind
import fund.util.Caching
import io.reactivex.Observable
import lib.base.model.Album
import vector.appContext
import java.io.File

/**
 * @author yuansui
 * @since 2018/8/12
 */
class AlbumRepo {

    val photos = NLiveList<Album>()

    fun getPhotos() =
        Observable.fromCallable<MutableList<Album>> {
            val photos = mutableListOf<Album>()
            val path = Caching.cameraCacheDir
            val file = File(path)
            if (file.exists()) {
                addFile(photos, file)
            }

            // 系统图片
            val type = MediaStore.Images.Media.MIME_TYPE
            val select =
                "$type=\"image/jpeg\" or $type=\"image/jpg\" or $type=\"image/png\"" // jpeg , jpg , png
            val cursor = appContext.contentResolver
                .query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    select,
                    null,
                    MediaStore.Images.Media.DATE_MODIFIED + " desc"
                )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    photos.add(Album().apply {
                        this.path =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    })
                }
                cursor.close()
            }
            photos
        }.bind(photos)


    /**
     * 添加缓存目录下图片
     */
    private fun addFile(photos: MutableList<Album>, file: File) {
        if (file.isDirectory) {
            for (f in file.listFiles()) {
                addFile(photos, f)
            }
        } else {
            photos.add(Album().apply { path = file.absolutePath })
        }
    }

    private fun getImageFields(): Array<String> {
        return arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
    }
}