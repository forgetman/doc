package pretimmediat.ext

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coroutine.flow.launchIn
import eth.model.ErrorDefaultCode
import eth.model.EthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.suspendCancellableCoroutine
import pretimmediat.activity.MainActivity
import pretimmediat.activity.MainActivityCreator
import pretimmediat.dialog.LoadingPieceDialog
import pretimmediat.manager.AccountManager
import pretimmediat.network.StatusCode
import vector.app.dialog.LoadingDialog
import java.net.ConnectException
import java.net.UnknownHostException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


fun <T> Flow<T>.withLoading(context: Context?): Flow<T> {
    if (context == null) return this

    val dialog = LoadingDialog(context)
    return callbackFlow {
        dialog.setOnDismissListener {
            close()
        }
        this@withLoading.collect {
            trySend(it)
            close()
        }
    }.onStart {
        dialog.show()
    }.onEach {
        dialog.dismiss()
    }.catch { e ->
        dialog.dismiss()
        throw e
    }.flowOn(Dispatchers.Main)
}

/**
 * 进件页专用loading
 */
fun <T> Flow<T>.withPieceLoading(context: Context?, cancelable: Boolean = true): Flow<T> {
    if (context == null) return this

    val dialog = LoadingPieceDialog(context)
    dialog.setCancelable(cancelable)
    return callbackFlow {
        dialog.setOnDismissListener {
            close()
        }
        this@withPieceLoading.collect {
            trySend(it)
            close()
        }
    }.onStart {
        dialog.show()
    }.onEach {
        dialog.dismiss()
    }.catch { e ->
        dialog.dismiss()
        throw e
    }.onCompletion {
        dialog.dismiss()
    }.flowOn(Dispatchers.Main)
}

fun <T> Flow<Boolean>.bindLoading(owner: T?) where T : Context, T : LifecycleOwner {
    if (owner == null) return
    if (owner.lifecycle.currentState === Lifecycle.State.DESTROYED) return

    var dialog: LoadingDialog? = null
    val observer = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                owner.lifecycle.removeObserver(this)
                dialog?.dismiss()
            }
        }
    }

    this.onEach {
        if (it) {
            if (dialog == null) dialog = LoadingDialog(owner)
            dialog?.show()
            owner.lifecycle.addObserver(observer)
        } else {
            dialog?.dismiss()
            dialog = null
            owner.lifecycle.removeObserver(observer)
        }
    }.flowOn(Dispatchers.Main.immediate).launchIn(owner)
}

/**
 * 进件页专用
 */
fun <T> Flow<Boolean>.bindPieceLoading(owner: T?) where T : Context, T : LifecycleOwner {
    if (owner == null) return
    if (owner.lifecycle.currentState === Lifecycle.State.DESTROYED) return

    var dialog: LoadingPieceDialog? = null
    val observer = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                owner.lifecycle.removeObserver(this)
                dialog?.dismiss()
            }
        }
    }

    this.onEach {
        if (it) {
            if (dialog == null) dialog = LoadingPieceDialog(owner)
            dialog?.show()
            owner.lifecycle.addObserver(observer)
        } else {
            dialog?.dismiss()
            dialog = null
            owner.lifecycle.removeObserver(observer)
        }
    }.flowOn(Dispatchers.Main.immediate).launchIn(owner)
}

fun <T> Flow<T>.withNetworkError(context: Context?): Flow<T> {
    // 超时和其他错误都toast, 断网要弹窗
    return catch { e ->
        when (e) {
            is ConnectException, is UnknownHostException -> context.showErrorDialog()
            is EthException -> {
                when (e.code) {
                    ErrorDefaultCode.NETWORK, ErrorDefaultCode.CONNECT -> {
                        context.showErrorDialog()
                    }

                    StatusCode.ERROR_TOKEN -> {
                        // token失效, toast提示, 同时需要返回登录页
                        toast(context, e.message)
                        suspendCancellableCoroutine { cont ->
                            AccountManager.clear {
                                if (it) cont.resume(Unit) else cont.resumeWithException(e)
                            }
                        }
                        MainActivityCreator.create()
                            .requiredTabIndex(MainActivity.TAB_HOME)
                            .start(context)
                    }

                    "404" -> {
                        toast(context, "服务器404错误")
                    }

                    else -> toast(context, e.message)
                }
            }

            else -> toast(context, e.message)
        }
        throw e
    }.flowOn(Dispatchers.Main)
}