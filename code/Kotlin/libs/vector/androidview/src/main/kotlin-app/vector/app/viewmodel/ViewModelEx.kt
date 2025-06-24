package vector.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelStoreOwner
import sugar.ext.observeDestroy
import vector.app.delegate.InitializeDelegate
import vector.util.GenericUtil
import kotlin.reflect.KClass

/**
 * ViewModel基类
 */
abstract class ViewModelEx(app: Application) : AndroidViewModel(app) {

    val applicationContext: Context
        get() = getApplication()

    open fun onCreate() {}

    final override fun onCleared() {
        onDestroy()
    }

    open fun onDestroy() {}
}

fun <T, VM : ViewModelEx> T.getViewModelClass(): Class<VM> where T : ViewModelStoreOwner {
    return getViewModelKClass<T, VM>().java
}

@Suppress("UNCHECKED_CAST")
fun <T, VM : ViewModelEx> T.getViewModelKClass(): KClass<VM> where T : ViewModelStoreOwner {
    return requireNotNull(GenericUtil.getClassType(this, ViewModel::class) as? KClass<VM>?)
}

fun <T, VM : ViewModelEx> T.viewModels(): Lazy<VM> where T : HasDefaultViewModelProviderFactory, T : ViewModelStoreOwner {
    val factoryPromise = {
        defaultViewModelProviderFactory
    }

    return ViewModelLazy(
        getViewModelKClass(),
        { viewModelStore },
        factoryPromise,
        { this.defaultViewModelCreationExtras }
    )
}

internal fun <T, VM : ViewModelEx> T.initViewModel() where T : InitializeDelegate, T : ViewModelOwner<VM> {

    addInitializeFlowListener(object : InitializeDelegate.Listener {
        override fun onSystemBarInitializeEnd() {
            viewModel.onCreate()
        }
    })
}

internal fun <T> T.initViewTreeOwners() where T : LifecycleOwner, T : ViewModelStoreOwner {
    observeDestroy {
        viewModelStore.clear()
    }
}