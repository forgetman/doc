package sugar.ext

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner

var View.lifecycleOwner: LifecycleOwner?
    set(owner) {
        setViewTreeLifecycleOwner(owner)
    }
    get() {
        return findViewTreeLifecycleOwner() ?: context.lifecycleOwner
    }

val View.lifecycle: Lifecycle? get() = lifecycleOwner?.lifecycle