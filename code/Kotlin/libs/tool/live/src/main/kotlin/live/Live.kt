package live

import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import coroutine.scope.observeCancel
import kotlinx.coroutines.CoroutineScope
import sugar.ext.ensureRunOnMainThread
import sugar.ext.isMainThread

@SuppressLint("CheckResult")
open class Live<T>(default: T? = null, private val skipSameValue: Boolean = false) :
    MutableLiveData<T>() {

    init {
        if (default != null) {
            value = default
        }
    }

    open fun observe(owner: LifecycleOwner?, block: (T) -> Unit) {
        if (owner == null) {
            observeForever {
                block(it)
            }
        } else {
            observe(owner, Observer {
                block.invoke(it)
            })
        }
    }

    fun observe(observer: Observer<in T>) {
        observeForever(observer)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        ensureRunOnMainThread {
            super.observe(owner, observer)
        }
    }

    override fun observeForever(observer: Observer<in T>) {
        ensureRunOnMainThread {
            super.observeForever(observer)
        }
    }

    /**
     * PS:
     * 声明为T?类型是语法问题导致
     * 如果使用T类型, 会无法调用 value = xxx, 只能setValue(xxx)
     *
     * Error: "Val cannot be reassigned"
     * 实际上编译时并不接受T?的类型, 只会接受T类型
     */
    override fun setValue(value: T?) {
        if (skipSameValue && this.value == value) {
            return
        }

        if (isMainThread()) {
            super.setValue(value)
        } else {
            postValue(value)
        }
    }

    /**
     * 加入线程切换的操作
     */
    override fun removeObserver(observer: Observer<in T>) {
        ensureRunOnMainThread {
            super.removeObserver(observer)
        }
    }
}

/**
 * 引起自身数据刷新的回调(应用于改变了其中某些值但数据源又没有改变的情况)
 */
fun <T> MutableLiveData<T>.refresh() {
    value = value
}

fun <T> live(block: () -> T): Live<T> = Live(block())

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner? = null, observer: Observer<T>) {
    if (owner == null) {
        observeForever(object : Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    } else {
        observe(owner, object : Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

}

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner? = null, action: (T) -> Unit) {
    observeOnce(owner, Observer<T> { t -> action(t) })
}

fun <T> LiveData<T>.observe(scope: CoroutineScope, action: (T) -> Unit) {
    observeForever(action)
    scope.observeCancel {
        removeObserver(action)
    }
}