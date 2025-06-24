package lib.base.design.repo

import kotlinx.coroutines.flow.flow
import vector.compat.media.MediaCompat
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2018/8/12
 */
class AlbumRepo @Inject constructor() {

    fun fetchPhotos() =
        flow {
            val photos = MediaCompat.Image.getDataInAlbum("bmp/", true)
            emit(photos)
        }

    /**
     * 添加缓存目录下图片
     */
//    private fun addFile(photos: MutableList<MediaData>, file: File) {
//        if (file.isDirectory) {
//            file.listFiles()?.forEach {
//                addFile(photos, it)
//            }
//        } else {
//            photos.add(
//                MediaData(
//                    file.parent?.plus(File.separatorChar) ?: file.absolutePath,
//                    file.name
//                )
//            )
//        }
//    }
}