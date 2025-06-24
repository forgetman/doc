package compat.network.api.wifi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.ScanResult
import android.os.Build
import androidx.annotation.RequiresApi
import compat.ext.checkPermission
import compat.ext.wifi

/**
 * @author yuansui
 * @since 2022/8/1
 *
 * @deprecate 暂时不使用, 保留代码
 */
@SuppressLint("MissingPermission") // Manifest.permission.ACCESS_WIFI_STATE
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class Api33Impl : Api by Api31Impl() {

    override fun getScanResults(context: Context): List<ScanResult> {
        if (!context.checkPermission(Manifest.permission.ACCESS_WIFI_STATE)) return emptyList()
        if (!context.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return emptyList()
        return context.wifi().scanResults
    }
}