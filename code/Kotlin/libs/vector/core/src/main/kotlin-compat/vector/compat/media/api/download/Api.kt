package vector.compat.media.api.download

import android.content.Context
import vector.MimeType
import vector.compat.media.MatchRule
import vector.compat.media.MediaData
import vector.compat.media.OnConflictStrategy

/**
 * @author yuansui
 * @since 2021/3/4
 * 根目录的Downloads文件夹
 */
internal interface Api {
    /**
     * 保存
     * @return 保存是否成功
     */
    fun save(
        context: Context,
        displayName: String?,
        secondaryPath: String?,
        content: Any,
        mimeType: MimeType,
        onConflict: OnConflictStrategy
    ): Boolean

    /**
     * 删除指定文件
     * @return 是否删除成功
     */
    fun delete(
        context: Context,
        displayName: String,
        secondaryPath: String?,
        mimeType: MimeType
    ): Boolean

    /**
     * 按规则匹配删除文件
     * @return 成功删除的个数
     */
    fun delete(
        context: Context,
        secondaryPath: String?,
        rule: MatchRule,
        includeSubFolder: Boolean = false
    ): Int

    /**
     * 按规则匹配获取文件
     * @param includeSubFolder 是否包含子路径, true则会向下查询, false为当前文件夹
     */
    fun getData(
        context: Context,
        secondaryPath: String?,
        rule: MatchRule,
        includeSubFolder: Boolean = false
    ): List<MediaData>

    fun getData(
        context: Context,
        displayName: String,
        secondaryPath: String?,
        mimeType: MimeType
    ): MediaData?
}