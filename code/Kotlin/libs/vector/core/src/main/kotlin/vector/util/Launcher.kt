package vector.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import sugar.ext.ifNotNull
import vector.app.activity.ResultContractActivity
import vector.app.delegate.ActivityResultCallback
import vector.appContext
import vector.service.ServiceEx
import kotlin.reflect.KClass

/**
 * 官方解释:
 *
 * Additional options for how the Activity should be started.
 * May be null if there are no options. See
 * [androidx.core.app.ActivityOptionsCompat] for how to build the Bundle
 * supplied here; there are no supported definitions for
 * building it manually.
 *
 * 还需要对App主题设置(5.0以上) "android:windowContentTransitions" 为 true
 */
fun interface AdditionalOptions {
    fun getOptions(): ActivityOptionsCompat
}

/**
 * 管理activity和service的启动方式
 * @author yuansui
 */
object Launcher {

    @JvmStatic
    fun startActivity(
        clz: Class<*>,
        extras: Bundle? = null,
        options: AdditionalOptions? = null
    ) {
        val intent = Intent(appContext, clz)
        startActivity(intent, extras, options)
    }

    fun startActivity(
        clz: KClass<*>,
        extras: Bundle? = null,
        options: AdditionalOptions? = null
    ) {
        startActivity(clz.java, extras, options)
    }

    fun startActivity(
        intent: Intent,
        extras: Bundle? = null,
        options: AdditionalOptions? = null
    ) {
        putExtras(intent, extras)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent, options?.getOptions()?.toBundle())
    }

    @JvmStatic
    fun startActivity(
        host: Any?,
        intent: Intent,
        extras: Bundle? = null,
        options: AdditionalOptions? = null
    ) {
        putExtras(intent, extras)

        when (host) {
            is Activity -> host.startActivity(intent, options?.getOptions()?.toBundle())
            is Fragment -> host.startActivity(intent, options?.getOptions()?.toBundle())
            is Context -> {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                host.startActivity(intent, options?.getOptions()?.toBundle())
            }

            else -> {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                appContext.startActivity(intent, options?.getOptions()?.toBundle())
            }
        }
    }

    fun startActivity(
        host: Any?,
        clz: KClass<*>,
        extras: Bundle? = null,
        options: AdditionalOptions? = null
    ) {
        val intent = Intent(host as? Context ?: appContext, clz.java)
        startActivity(host, intent, extras, options)
    }

    @JvmStatic
    fun registerForActivityResult(
        host: Any?,
        intent: Intent,
        extras: Bundle? = null,
        callback: ActivityResultCallback
    ) {
        putExtras(intent, extras)

        @Suppress("UNCHECKED_CAST")
        ResultContractActivity.start(
            host,
            ResultContractActivity.ContractWrapper(
                intent,
                ActivityResultContracts.StartActivityForResult() as ActivityResultContract<Any, Any>
            ) { result ->
                result as ActivityResult
                callback.onActivityResult(result.resultCode, result.data)
            })
    }

    @JvmStatic
    fun startService(host: Any?, intent: Intent, extras: Bundle? = null) {
        putExtras(intent, extras)

        val context = when (host) {
            is Activity, is Context -> host
            is Fragment -> host.context ?: appContext
            else -> appContext
        }

        try {
            context.startService(intent)
        } catch (_: IllegalStateException) {
            // 添加额外的标识, 标识是foreground service
            intent.putExtra(ServiceEx.FOREGROUND_FLAG, true)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    @JvmStatic
    fun startForegroundService(host: Any?, intent: Intent, extras: Bundle? = null) {
        putExtras(intent, extras)

        // 添加额外的标识, 标识是foreground service
        intent.putExtra(ServiceEx.FOREGROUND_FLAG, true)

        val context = when (host) {
            is Activity, is Context -> host
            is Fragment -> host.context ?: appContext
            else -> appContext
        }
        ContextCompat.startForegroundService(context, intent)
    }

    private fun putExtras(intent: Intent, extras: Bundle?) {
        extras.ifNotNull {
            intent.putExtras(it)
        }
    }
}
