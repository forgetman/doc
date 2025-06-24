package catroom.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.CancellationSignal
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import catroom.datastore.Properties
import com.google.android.gms.common.util.concurrent.HandlerExecutor
import coroutine.flow.launchForever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import logger.L
import sugar.ext.systemService
import vector.singleton.Singleton
import vector.util.DangerousPerm
import vector.util.EasyPermissions
import kotlin.coroutines.resume
import android.location.LocationManager as SystemLocationManager

class LocationManager private constructor(private val context: Context) {

    companion object : Singleton<Context, LocationManager> by Singleton({ context ->
        LocationManager(context.applicationContext)
    }) {
        private const val LOG_TAG = "LocationManager"
    }

    private val systemService = context.systemService<SystemLocationManager>()
    private var locationJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission") // EasyPermissions.check
    fun update() {
        val permissionEnabled = EasyPermissions.check(context, DangerousPerm.Location())
        if (!permissionEnabled) {
            L.d(LOG_TAG, "permission not enabled")
            return
        }
        val enabled = LocationManagerCompat.isLocationEnabled(systemService)
        if (!enabled) {
            L.d(LOG_TAG, "location not enabled")
            return
        }

        locationJob?.cancel()
        locationJob = combine(updateByNetworkFlow(), updateByGpsFlow()) { network, gps ->
            gps ?: network
        }.filterNotNull().onEach { location ->
            Properties.roomLongitude.put(location.longitude)
            Properties.roomLatitude.put(location.latitude)
        }.catch { e ->
            L.e(LOG_TAG, "updateLocation, error: $e")
        }.launchForever()
    }

    @SuppressLint("MissingPermission")
    private fun updateByNetworkFlow() = callbackFlow<Location?> {
        val result = withTimeout(10000) {
            suspendCancellableCoroutine<Location> { cont ->
                LocationManagerCompat.getCurrentLocation(
                    systemService,
                    SystemLocationManager.NETWORK_PROVIDER,
                    CancellationSignal(),
                    HandlerExecutor(Looper.getMainLooper())
                ) { value ->
                    L.d(LOG_TAG, "updateByNetworkFlow, result: $value")
                    cont.resume(value)
                }
            }
        }
        trySend(result)
        close()
    }

    @SuppressLint("MissingPermission")
    private fun updateByGpsFlow() = callbackFlow<Location?> {
        val result = withTimeout(10000) {
            suspendCancellableCoroutine { cont ->
                LocationManagerCompat.getCurrentLocation(
                    systemService,
                    SystemLocationManager.GPS_PROVIDER,
                    CancellationSignal(),
                    HandlerExecutor(Looper.getMainLooper())
                ) { value ->
                    L.d(LOG_TAG, "updateByGpsFlow, result: $value")
                    cont.resume(value)
                }
            }
        }
        trySend(result)
        close()
    }
}