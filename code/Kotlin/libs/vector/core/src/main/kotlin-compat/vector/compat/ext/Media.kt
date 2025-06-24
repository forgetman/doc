package vector.compat.ext

import vector.compat.media.api.image.Api
import vector.util.PackageUtil

internal fun Api.getPrivateSecondaryPath(secondaryPath: String?): String? {
    // 私人相册如果外部不传路径的话, 使用appName
    return secondaryPath ?: PackageUtil.appName?.toString()
}