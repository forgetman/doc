package pretimmediat.ext

import android.content.Context
import pretimmediat.manager.LocationManager
import pretimmediat.stats.Stats
import vector.util.DangerousPerm
import vector.util.EasyPermissions

/**
 * 检查所有需要的权限
 */
fun Context.requireAllPermissions(callback: (result: Boolean) -> Unit) {
    EasyPermissions.request(
        this,
        DangerousPerm.Phone.ReadPhoneState(),
        DangerousPerm.Phone.ReadCallLog(),
        DangerousPerm.SMS.Read(),
        DangerousPerm.Calendar(),
        DangerousPerm.Location { result ->
            if (result == EasyPermissions.Result.GRANT) {
                // 定位权限获取成功
                LocationManager.getInstance(this).update(this) {
                    Stats.public.onEvent("ACCESS_LOCATION_PERMISSION")
                }
            }
        },
        DangerousPerm.Camera(),
    ) {
        callback(it)
    }
}