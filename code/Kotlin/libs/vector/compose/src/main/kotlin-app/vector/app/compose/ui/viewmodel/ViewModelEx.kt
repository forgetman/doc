package vector.app.compose.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelStoreOwner
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import sugar.ext.observeDestroy
import vector.app.compose.ui.state.ContentState
import vector.util.GenericUtil
import kotlin.reflect.KClass

sealed class NavigateTarget {
    abstract val route: String
    abstract val params: Bundle?

    data class Back(override val params: Bundle? = null) : NavigateTarget() {
        override val route: String = "back"
    }

    data class Forward(
        override val route: String,
        override val params: Bundle? = null
    ) : NavigateTarget()
}

abstract class ViewModelEx() : ViewModel() {
    private val _navigateTarget = MutableSharedFlow<NavigateTarget>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    internal val navigateEvent = _navigateTarget.asSharedFlow()

    private val _contentState = MutableStateFlow<ContentState?>(null)
    internal val contentState = _contentState.asStateFlow()

    fun updateContentState(state: ContentState) {
        _contentState.value = state
    }

    protected fun navigate(event: NavigateTarget) {
        _navigateTarget.tryEmit(event)
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <T, VM : ViewModelEx> T.getViewModelKClass(): KClass<VM> where T : ViewModelStoreOwner {
    return requireNotNull(GenericUtil.getClassType(this, ViewModel::class) as? KClass<VM>?)
}

internal fun <T, VM : ViewModelEx> T.viewModels(): Lazy<VM> where T : HasDefaultViewModelProviderFactory, T : ViewModelStoreOwner {
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

internal fun <T> T.initViewTreeOwners() where T : LifecycleOwner, T : ViewModelStoreOwner {
    observeDestroy {
        viewModelStore.clear()
    }
}