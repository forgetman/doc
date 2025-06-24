@file:Suppress("unused")

package vector.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import logger.L
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import sugar.ext.isSdkLessThan
import vector.app.activity.ResultContractActivity

typealias DangerousPermResult = (EasyPermissions.Result) -> Unit

/**
 * 危险权限, 需要动态申请的
 * 6.0以上是按组申请, 8.0以上改为单一申请
 */
sealed class DangerousPerm(
    internal vararg val keys: String,
    internal val result: DangerousPermResult?
) {

    companion object {
        private const val LOG_TAG = "DangerousPerm"
    }

    class Camera(result: DangerousPermResult? = null) : DangerousPerm(
        Manifest.permission.CAMERA, result = result
    )

    class Calendar(result: DangerousPermResult? = null) : DangerousPerm(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR,
        result = result
    ) {
        class Read(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.READ_CALENDAR, result = result)

        class Write(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.WRITE_CALENDAR, result = result)
    }

    class Contacts(result: DangerousPermResult? = null) : DangerousPerm(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.GET_ACCOUNTS,
        result = result
    ) {
        class Read(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.READ_CONTACTS, result = result)

        class Write(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.WRITE_CONTACTS, result = result)

        class GetAccounts(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.GET_ACCOUNTS, result = result)
    }

    class Location(result: DangerousPermResult? = null) : DangerousPerm(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION, result = result
    ) {
        class Find(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.ACCESS_FINE_LOCATION, result = result)

        class Coarse(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.ACCESS_COARSE_LOCATION, result = result)
    }

    class Microphone(result: DangerousPermResult? = null) : DangerousPerm(
        Manifest.permission.RECORD_AUDIO, result = result
    )

    class Phone(result: DangerousPermResult? = null) : DangerousPerm(
        *(if (isSdkAtLeast(SdkInt.O_26)) {
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.USE_SIP,
//                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.ADD_VOICEMAIL,
            )
        } else arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.USE_SIP,
//                Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.ADD_VOICEMAIL,
        )),
        result = result
    ) {
        class ReadPhoneState(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.READ_PHONE_STATE, result = result)

        class ReadPhoneNumbers(result: DangerousPermResult? = null) : DangerousPerm(
            *(if (isSdkAtLeast(SdkInt.O_26)) arrayOf(Manifest.permission.READ_PHONE_NUMBERS) else emptyArray()),
            result = result
        )

        class ReadCallLog(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.READ_CALL_LOG, result = result)

        class WriteCallLog(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.WRITE_CALL_LOG, result = result)

        class CallPhone(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.CALL_PHONE, result = result)

        class UseSip(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.USE_SIP, result = result)

        class AddVoicemail(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.ADD_VOICEMAIL, result = result)
    }

    class Sensors(result: DangerousPermResult? = null) : DangerousPerm(
        Manifest.permission.BODY_SENSORS, result = result
    )

    class SMS(result: DangerousPermResult? = null) : DangerousPerm(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_WAP_PUSH,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.RECEIVE_MMS,
        result = result
    ) {
        class Send(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.SEND_SMS, result = result)

        class Read(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.READ_SMS, result = result)

        class ReceiveWapPush(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.RECEIVE_WAP_PUSH, result = result)

        class ReceiveSms(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.RECEIVE_SMS, result = result)

        class ReceiveMms(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.RECEIVE_MMS, result = result)
    }

    class Storage(result: DangerousPermResult? = null) : DangerousPerm(
        *(if (isSdkAtLeast(SdkInt.T_33)) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )),
        result = result
    ) {
        class Write(result: DangerousPermResult? = null) :
            DangerousPerm(Manifest.permission.WRITE_EXTERNAL_STORAGE, result = result)

        class Read(result: DangerousPermResult? = null) : DangerousPerm(
            *(if (isSdkLessThan(SdkInt.T_33)) arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE) else emptyArray()),
            result = result
        )

        class ReadMediaAudio(result: DangerousPermResult? = null) : DangerousPerm(
            *(if (isSdkAtLeast(SdkInt.T_33)) arrayOf(Manifest.permission.READ_MEDIA_AUDIO) else emptyArray()),
            result = result
        )

        class ReadMediaVideo(result: DangerousPermResult? = null) : DangerousPerm(
            *(if (isSdkAtLeast(SdkInt.T_33)) arrayOf(Manifest.permission.READ_MEDIA_VIDEO) else emptyArray()),
            result = result
        )

        class ReadMediaImages(result: DangerousPermResult? = null) : DangerousPerm(
            *(if (isSdkAtLeast(SdkInt.T_33)) arrayOf(Manifest.permission.READ_MEDIA_IMAGES) else emptyArray()),
            result = result
        )
    }

    class Bluetooth(result: DangerousPermResult? = null) : DangerousPerm(
        *(if (isSdkAtLeast(SdkInt.S_31)) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else emptyArray()),
        result = result
    ) {
        class Scan(result: DangerousPermResult? = null) : DangerousPerm(
            *(if (isSdkAtLeast(SdkInt.S_31)) arrayOf(Manifest.permission.BLUETOOTH_SCAN) else emptyArray()),
            result = result
        )

        class Advertise(result: DangerousPermResult? = null) : DangerousPerm(
            *(if (isSdkAtLeast(SdkInt.S_31)) arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE) else emptyArray()),
            result = result
        )

        class Connect(result: DangerousPermResult? = null) : DangerousPerm(
            *(if (isSdkAtLeast(SdkInt.S_31)) arrayOf(Manifest.permission.BLUETOOTH_CONNECT) else emptyArray()),
            result = result
        )
    }

    class Notification(result: DangerousPermResult? = null) : DangerousPerm(
        *(if (isSdkAtLeast(SdkInt.T_33)) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        } else emptyArray()),
        result = result
    )
}

object EasyPermissions {

    private const val LOG_TAG = "EasyPermissions"

    enum class Result {
        GRANT, // 允许
        DENIED, // 拒绝
        FORBIDDEN, // 禁止:拒绝而且勾了"不再询问"
    }

    /**
     * 检查是否有权限
     */
    fun check(context: Context, permission: String): Result {
        return if (isSdkLessThan(SdkInt.M_23)) {
            Result.GRANT
        } else {
            when (PermissionChecker.checkSelfPermission(context, permission)) {
                PermissionChecker.PERMISSION_GRANTED -> Result.GRANT
                PermissionChecker.PERMISSION_DENIED -> {
                    if (context is ContextWrapper) {
                        val host = findRealHost(context)
                        if (host != null) {
                            val rationale = ActivityCompat.shouldShowRequestPermissionRationale(host, permission)
                            if (rationale) Result.DENIED else Result.FORBIDDEN
                        } else Result.DENIED
                    } else Result.DENIED
                }

                else -> Result.GRANT
            }
        }
    }

    fun check(context: Context, vararg perms: DangerousPerm): Boolean {
        return if (isSdkLessThan(SdkInt.M_23)) {
            true
        } else {
            perms.all { perm ->
                perm.keys.all { key ->
                    PermissionChecker.checkSelfPermission(context, key) == PermissionChecker.PERMISSION_GRANTED
                }
            }
        }
    }

    /**
     * 申请权限, 先检查后申请
     * @param action 所有权限是否通过
     */
    fun request(host: Any?, vararg perms: DangerousPerm, action: (result: Boolean) -> Unit) {
        requestInternal(host, *perms) { resultMap ->
            action(resultMap.values.all { it == Result.GRANT })
        }
    }

    /**
     * 检查权限, 单独获取各个权限的结果
     */
    fun request(host: Any, vararg perms: DangerousPerm) {
        requestInternal(host, *perms) { resultMap ->
            resultMap.forEach { (perm, result) -> perm.result?.invoke(result ?: Result.DENIED) }
        }
    }

    private fun findRealHost(context: ContextWrapper): AppCompatActivity? {
        var base: Context? = context
        while (base != null && base !is AppCompatActivity) {
            base = (base as? ContextWrapper)?.baseContext
        }
        return base
    }

    private fun requestInternal(
        host: Any?,
        vararg perms: DangerousPerm,
        callback: (Map<DangerousPerm, Result?>) -> Unit
    ) {
        val activity: Activity = when (host) {
            is AppCompatActivity -> host
            is Fragment -> host.requireActivity()
            is ContextWrapper -> findRealHost(host)
                ?: throw IllegalArgumentException("host can only be activity or fragment")

            else -> throw IllegalArgumentException("host can only be activity or fragment")
        }

        val unGrantedPerms = perms.filterNot { check(activity, it) }
        if (unGrantedPerms.isEmpty()) {
            callback(emptyMap())
            return
        }

        val resultCallback = buildActivityResultCallback(activity, *perms, callback = callback)

        val intent = unGrantedPerms.flatMap { it.keys.toList() }.toTypedArray()

        @Suppress("UNCHECKED_CAST")
        ResultContractActivity.start(
            activity,
            ResultContractActivity.ContractWrapper(
                intent,
                ActivityResultContracts.RequestMultiplePermissions() as ActivityResultContract<Any, Any>
            ) { result ->
                result as Map<String, Boolean>
                resultCallback.onActivityResult(result)
            })
    }

    private fun buildActivityResultCallback(
        activity: Activity,
        vararg perms: DangerousPerm,
        callback: (Map<DangerousPerm, Result?>) -> Unit
    ): ActivityResultCallback<Map<String, Boolean>> {
        return ActivityResultCallback { map ->
            val resultMap = mutableMapOf<DangerousPerm, Result?>()

            map.forEach { (key, value) ->
                val permGroup = perms.find { perm -> perm.keys.contains(key) } ?: return@forEach

                val currentResult = if (value) {
                    L.d(LOG_TAG, "request multi, $key result = GRANT")
                    Result.GRANT
                } else {
                    val rationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, key)
                    if (rationale) {
                        L.d(LOG_TAG, "request multi, $key result = DENIED")
                        Result.DENIED
                    } else {
                        L.d(LOG_TAG, "request multi, $key result = FORBIDDEN")
                        Result.FORBIDDEN
                    }
                }

                val previousResult = resultMap[permGroup]
                if (previousResult == null || previousResult != Result.GRANT) {
                    resultMap[permGroup] = currentResult
                }
            }

            callback(resultMap)
        }
    }
}
