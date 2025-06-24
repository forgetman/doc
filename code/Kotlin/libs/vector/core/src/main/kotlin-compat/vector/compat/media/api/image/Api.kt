package vector.compat.media.api.image

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.IntRange
import vector.compat.media.MatchRule
import vector.compat.media.MediaData
import vector.compat.media.OnConflictStrategy

internal interface Api {
    /**
     * 保存到公共相册
     */
    fun saveToAlbum(
        context: Context,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        @IntRange(from = 0, to = 100) quality: Int,
        secondaryPath: String?,
        displayName: String?,
        onConflict: OnConflictStrategy
    ): Boolean

    /**
     * 保存到私人相册
     */
    fun saveToPrivateAlbum(
        context: Context,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        @IntRange(from = 0, to = 100) quality: Int,
        secondaryPath: String?,
        displayName: String?,
        onConflict: OnConflictStrategy
    ): Boolean

    /**
     * 在公共相册删除
     */
    fun deleteInAlbum(context: Context, displayName: String, secondaryPath: String?): Boolean

    /**
     * 在公共相册模糊删除
     * @param includeSubFolder 是否包含子路径, true则会向下查询, false为当前文件夹
     */
    fun deleteInAlbum(
        context: Context,
        secondaryPath: String?,
        rule: MatchRule,
        includeSubFolder: Boolean = false
    ): Int

    /**
     * 在私人相册删除
     */
    fun deleteInPrivateAlbum(context: Context, displayName: String, secondaryPath: String?): Boolean

    /**
     * 在私人相册模糊删除
     * @param includeSubFolder 是否包含子路径, true则会向下查询, false为当前文件夹
     */
    fun deleteInPrivateAlbum(
        context: Context,
        secondaryPath: String?,
        rule: MatchRule,
        includeSubFolder: Boolean = false
    ): Int

    /**
     * 获取相册的图片
     * @param includeSubFolder 是否包含子路径, true则会向下查询, false为当前文件夹
     */
    fun getDataInAlbum(
        context: Context,
        secondaryPath: String?,
        includeSubFolder: Boolean = false
    ): List<MediaData>

    /**
     * 获取私人相册的图片
     */
    fun getDataInPrivateAlbum(
        context: Context,
        secondaryPath: String?,
        includeSubFolder: Boolean = false
    ): List<MediaData>
}