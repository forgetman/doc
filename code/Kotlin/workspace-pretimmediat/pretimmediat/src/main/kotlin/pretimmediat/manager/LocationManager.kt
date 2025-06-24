package pretimmediat.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.CancellationSignal
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.common.util.concurrent.HandlerExecutor
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import coroutine.flow.launchForever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.suspendCancellableCoroutine
import logger.L
import pretimmediat.property.Properties
import pretimmediat.stats.Stats
import sugar.ext.systemService
import vector.singleton.Singleton
import vector.util.DangerousPerm
import vector.util.EasyPermissions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.location.LocationManager as SystemLocationManager

@SuppressLint("MissingPermission")
class LocationManager private constructor(context: Context) {

    companion object : Singleton<Context, LocationManager> by Singleton({ context ->
        LocationManager(context.applicationContext)
    }) {
        private const val LOG_TAG = "LocationManager"
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
        private const val TIMEOUT_IN_MILLISECONDS: Long = 5000
    }

    private val systemService = context.systemService<SystemLocationManager>()
    private var locationJob: Job? = null

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    private val settingsClient = LocationServices.getSettingsClient(context)

    // 埋点开关
    private var debuggable: Boolean = true

    fun <T> update(host: T, callback: () -> Unit) where T : Context {
        EasyPermissions.request(host, DangerousPerm.Location()) { result ->
            if (result) {
                val enabled = LocationManagerCompat.isLocationEnabled(systemService)
                if (!enabled) {
                    L.d(LOG_TAG, "location not enabled")
                    return@request
                }

                locationJob?.cancel()
                locationJob = combine(
                    updateByGoogleSdk(),
                    updateByGpsFlow(),
                    updateByNetworkFlow()
                ) { sdk, gps, network ->
                    L.d(LOG_TAG, "update, sdk: $sdk, gps: $gps, network: $network")
                    when {
                        sdk != null -> sdk
                        gps != null -> gps
                        else -> network
                    }
                }.filterNotNull().catch { e ->
                    L.e(LOG_TAG, "updateLocation, error: $e")
                }.flowOn(Dispatchers.IO).onEach { location ->
                    L.d(LOG_TAG, "update, result = $location")
                    Properties.location.put(location.let { "${it.latitude},${it.longitude}" })
                    Properties.latitude.put(location.latitude.toString())
                    Properties.longitude.put(location.longitude.toString())
                }.launchForever()

                callback()
            }
        }
    }

    private fun updateByNetworkFlow() = callbackFlow {
        statsDebugEvent("ACCESS_LOCATION_3")
        send(null)
        val result = kotlinx.coroutines.withTimeout(TIMEOUT_IN_MILLISECONDS) {
            suspendCancellableCoroutine<Location> { cont ->
                LocationManagerCompat.getCurrentLocation(
                    systemService,
                    SystemLocationManager.NETWORK_PROVIDER,
                    CancellationSignal(),
                    HandlerExecutor(Looper.getMainLooper())
                ) { value ->
                    cont.resume(value)
                }
            }
        }
        L.d(LOG_TAG, "updateByNetworkFlow, result: $result")
        statsDebugEvent("SAVE_LOCATION_3")
        send(result)
        close()
    }

    private fun updateByGpsFlow() = callbackFlow {
        statsDebugEvent("ACCESS_LOCATION_2")
        send(null)
        val result = kotlinx.coroutines.withTimeout(TIMEOUT_IN_MILLISECONDS) {
            suspendCancellableCoroutine { cont ->
                LocationManagerCompat.getCurrentLocation(
                    systemService,
                    SystemLocationManager.GPS_PROVIDER,
                    CancellationSignal(),
                    HandlerExecutor(Looper.getMainLooper())
                ) { value ->
                    cont.resume(value)
                }
            }
        }
        L.d(LOG_TAG, "updateByGpsFlow, result: $result")
        statsDebugEvent("SAVE_LOCATION_2")
        send(result)
        close()
    }

    private fun updateByGoogleSdk() = callbackFlow {
        statsDebugEvent("ACCESS_LOCATION_1")
        send(null)
        val result = kotlinx.coroutines.withTimeout(TIMEOUT_IN_MILLISECONDS) {
            suspendCancellableCoroutine { cont ->
                val locationRequest = LocationRequest.Builder(UPDATE_INTERVAL_IN_MILLISECONDS)
                    .setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                    .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                    .build()
                settingsClient.checkLocationSettings(
                    LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest)
                        .build()
                ).addOnSuccessListener {
                    val callback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            val last = result.lastLocation
                            fusedClient.removeLocationUpdates(this)
                            cont.resume(last)
                        }
                    }
                    fusedClient.requestLocationUpdates(locationRequest, callback, Looper.myLooper())
                        .addOnFailureListener {
                            fusedClient.removeLocationUpdates(callback)
                            cont.resumeWithException(it)
                        }
                }.addOnFailureListener {
                    cont.resumeWithException(it)
                }
            }
        }
        L.d(LOG_TAG, "updateByGoogleSdk, result: $result")
        statsDebugEvent("SAVE_LOCATION_1")
        send(result)
        close()
    }

    private fun statsDebugEvent(eventName: String) {
        if (debuggable) {
            Stats.public.onEvent(eventName)
        }
    }
}