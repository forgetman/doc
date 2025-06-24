package compat.ext

import android.accounts.AccountManager
import android.app.usage.NetworkStatsManager
import android.bluetooth.BluetoothManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import android.view.inputmethod.InputMethodManager
import sugar.ext.systemService

internal fun Context.wifi() = systemService<WifiManager>()

internal fun Context.bluetooth() = systemService<BluetoothManager>()

internal fun Context.telephony() = systemService<TelephonyManager>()

internal fun Context.connectivity() = systemService<ConnectivityManager>()

internal fun Context.networkStats() = systemService<NetworkStatsManager>()

internal fun Context.account() = systemService<AccountManager>()

internal fun Context.inputMethod() = systemService<InputMethodManager>()