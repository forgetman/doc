@file:Suppress("unused")

package vector.compat.media

import android.content.Context
import android.graphics.Bitmap
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.ext.isSdkLessThan
import vector.MimeType
import vector.compat.media.api.download.Api as DownloadApi
import vector.compat.media.api.download.Api29Impl as DownloadApi29Impl
import vector.compat.media.api.download.ApiImpl as DownloadApiImpl
import vector.compat.media.api.image.Api as ImageApi
import vector.compat.media.api.image.Api29Impl as ImageApi29Impl
import vector.compat.media.api.image.ApiImpl as ImageApiImpl

/**
 * 如果文件存在, 处理的策略
 */
enum class OnConflictStrategy {
    DEFAULT, // 默认(在后面加数字序号)
    REPLACE, // 替换
    IGNORE // 不处理
}

/**
 * 简易Sql构造器, 只针对Media语句(查询的value全部都是 ? )
 */
internal object MediaSql {

    fun build(action: SqlBuilder.() -> Unit): String {
        val builder = SqlBuilder()
        action(builder)
        return builder.build()
    }

    internal class SqlBuilder {
        private val builder = StringBuilder()

        fun where(where: String) {
            builder.append("$where = ?")
        }

        fun and(and: String) {
            builder.append(" and $and = ?")
        }

        fun or(or: String) {
            builder.append(" or $or = ?")
        }

        fun andOr(vararg or: String) {
            builder.append(" and ")
            builder.append("(")
            or.forEachIndexed { index, s ->
                if (index == 0) builder.append("$s = ?") else builder.append(" or $s = ?")
            }
            builder.append(")")
        }

        fun build(): String = builder.toString()
    }
}

/**
 * @author yuansui
 * @since 2020/10/30
 */
object MediaCompat {

    object Download {
        private val api: DownloadApi = when {
            isSdkAtLeast(SdkInt.Q_29) -> DownloadApi29Impl()
            else -> DownloadApiImpl()
        }

        fun save(
            context: Context,
            displayName: String?,
            secondaryPath: String?,
            content: Any,
            mimeType: MimeType,
            onConflict: OnConflictStrategy
        ): Boolean {
            return api.save(context, displayName, secondaryPath, content, mimeType, onConflict)
        }

        fun delete(
            context: Context,
            displayName: String,
            secondaryPath: String?,
            mimeType: MimeType
        ): Boolean {
            return api.delete(context, displayName, secondaryPath, mimeType)
        }

        fun delete(
            context: Context,
            secondaryPath: String?,
            rule: MatchRule,
            includeSubFolder: Boolean
        ): Int {
            return api.delete(context, secondaryPath, rule, includeSubFolder)
        }

        fun getData(
            context: Context,
            secondaryPath: String?,
            rule: MatchRule,
            includeSubFolder: Boolean
        ): List<MediaData> {
            return api.getData(context, secondaryPath, rule, includeSubFolder)
        }

        fun getData(
            context: Context,
            displayName: String,
            secondaryPath: String?,
            mimeType: MimeType
        ): MediaData? {
            return api.getData(context, displayName, secondaryPath, mimeType)
        }
    }

    object Image {
        private val api: ImageApi = when {
            isSdkLessThan(SdkInt.Q_29) -> ImageApiImpl()
            else -> ImageApi29Impl()
        }

        fun saveToAlbum(
            context: Context,
            bitmap: Bitmap,
            format: Bitmap.CompressFormat,
            quality: Int,
            secondaryPath: String?,
            displayName: String?,
            onConflict: OnConflictStrategy
        ): Boolean {
            return api.saveToAlbum(context, bitmap, format, quality, secondaryPath, displayName, onConflict)
        }

        fun saveToPrivateAlbum(
            context: Context,
            bitmap: Bitmap,
            format: Bitmap.CompressFormat,
            quality: Int,
            secondaryPath: String?,
            displayName: String?,
            onConflict: OnConflictStrategy
        ): Boolean {
            return api.saveToPrivateAlbum(
                context,
                bitmap,
                format,
                quality,
                secondaryPath,
                displayName,
                onConflict
            )
        }

        fun deleteInAlbum(context: Context, displayName: String, secondaryPath: String?): Boolean {
            return api.deleteInAlbum(context, displayName, secondaryPath)
        }

        fun deleteInAlbum(
            context: Context,
            secondaryPath: String?,
            rule: MatchRule,
            includeSubFolder: Boolean
        ): Int {
            return api.deleteInAlbum(context, secondaryPath, rule, includeSubFolder)
        }

        fun deleteInPrivateAlbum(
            context: Context,
            displayName: String,
            secondaryPath: String?
        ): Boolean {
            return api.deleteInPrivateAlbum(context, displayName, secondaryPath)
        }

        fun deleteInPrivateAlbum(
            context: Context,
            secondaryPath: String?,
            rule: MatchRule,
            includeSubFolder: Boolean
        ): Int {
            return api.deleteInPrivateAlbum(context, secondaryPath, rule, includeSubFolder)
        }

        fun getDataInAlbum(
            context: Context,
            secondaryPath: String?,
            includeSubFolder: Boolean
        ): List<MediaData> {
            return api.getDataInAlbum(context, secondaryPath, includeSubFolder)
        }

        fun getDataInPrivateAlbum(
            context: Context,
            secondaryPath: String?,
            includeSubFolder: Boolean
        ): List<MediaData> {
            return api.getDataInPrivateAlbum(context, secondaryPath, includeSubFolder)
        }
    }
}