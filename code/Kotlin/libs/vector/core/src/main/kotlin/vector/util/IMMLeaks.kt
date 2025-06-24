package vector.util

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import sugar.ext.systemService
import vector.os.lifecycle.SimpleActivityLifecycleCallbacks
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * LeakCanary出品, hack方式修复InputMethodManager造成的leak
 */
object IMMLeaks {

    internal class ReferenceCleaner(
        private val inputMethodManager: InputMethodManager,
        private val hField: Field,
        private val servedViewField: Field,
        private val finishInputLockedMethod: Method
    ) :
        MessageQueue.IdleHandler, View.OnAttachStateChangeListener,
        ViewTreeObserver.OnGlobalFocusChangeListener {

        override fun onGlobalFocusChanged(oldFocus: View?, newFocus: View?) {
            if (newFocus == null) {
                return
            }
            oldFocus?.removeOnAttachStateChangeListener(this)
            Looper.myQueue().removeIdleHandler(this)
            newFocus.addOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View) {}

        override fun onViewDetachedFromWindow(v: View) {
            v.removeOnAttachStateChangeListener(this)
            Looper.myQueue().removeIdleHandler(this)
            Looper.myQueue().addIdleHandler(this)
        }

        override fun queueIdle(): Boolean {
            clearInputMethodManagerLeak()
            return false
        }

        @TargetApi(VERSION_CODES.KITKAT)
        private fun clearInputMethodManagerLeak() {
            try {
                var lock: Any? = hField.get(inputMethodManager)
                if (lock == null) {
                    lock = Any()
                }
                // This is highly dependent on the InputMethodManager implementation.
                synchronized(lock) {
                    val servedView = servedViewField.get(inputMethodManager) as? View
                    if (servedView != null) {

                        val servedViewAttached = servedView.windowVisibility != View.GONE

                        if (servedViewAttached) {
                            // The view held by the IMM was replaced without a global focus change. Let's make
                            // sure we get notified when that view detaches.

                            // Avoid double registration.
                            servedView.removeOnAttachStateChangeListener(this)
                            servedView.addOnAttachStateChangeListener(this)
                        } else {
                            // servedView is not attached. InputMethodManager is being stupid!
                            val activity = extractActivity(servedView.context)
                            if (activity == null || activity.window == null) {
                                // Unlikely case. Let's finish the input anyways.
                                finishInputLockedMethod.invoke(inputMethodManager)
                            } else {
                                val decorView = activity.window.peekDecorView()
                                val windowAttached = decorView.windowVisibility != View.GONE
                                if (!windowAttached) {
                                    finishInputLockedMethod.invoke(inputMethodManager)
                                } else {
                                    decorView.requestFocusFromTouch()
                                }
                            }
                        }
                    }
                }
            } catch (unexpected: IllegalAccessException) {
                Log.e("IMMLeaks", "Unexpected reflection exception", unexpected)
            } catch (unexpected: InvocationTargetException) {
                Log.e("IMMLeaks", "Unexpected reflection exception", unexpected)
            }

        }

        private fun extractActivity(context: Context): Activity? {
            var useContext = context
            while (true) {
                when (useContext) {
                    is Application -> return null
                    is Activity -> return useContext
                    is ContextWrapper -> {
                        val baseContext = useContext.baseContext
                        // Prevent Stack Overflow.
                        if (baseContext === useContext) {
                            return null
                        }
                        useContext = baseContext
                    }

                    else -> return null
                }
            }
        }
    }

    /**
     * Fix for https://code.google.com/p/android/issues/detail?id=171190 .
     *
     *
     * When a view that has focus gets detached, we wait for the main thread to be idle and then
     * check if the InputMethodManager is leaking a view. If yes, we tell it that the decor view got
     * focus, which is what happens if you press home and come back from recent apps. This replaces
     * the reference to the detached view with a reference to the decor view.
     *
     *
     */
    @TargetApi(VERSION_CODES.KITKAT)
    fun fixFocusedViewLeak(application: Application) {

        // Don't know about other versions yet.
        if (Build.VERSION.SDK_INT > 23) {
            return
        }

        val inputMethodManager = application.systemService<InputMethodManager>()

        val mServedViewField: Field
        val mHField: Field
        val finishInputLockedMethod: Method
        val focusInMethod: Method
        try {
            mServedViewField = InputMethodManager::class.java.getDeclaredField("mServedView")
            mServedViewField.isAccessible = true
            mHField = InputMethodManager::class.java.getDeclaredField("mServedView")
            mHField.isAccessible = true
            finishInputLockedMethod =
                InputMethodManager::class.java.getDeclaredMethod("finishInputLocked")
            finishInputLockedMethod.isAccessible = true
            focusInMethod =
                InputMethodManager::class.java.getDeclaredMethod("focusIn", View::class.java)
            focusInMethod.isAccessible = true
        } catch (unexpected: NoSuchMethodException) {
            Log.e("IMMLeaks", "Unexpected reflection exception", unexpected)
            return
        } catch (unexpected: NoSuchFieldException) {
            Log.e("IMMLeaks", "Unexpected reflection exception", unexpected)
            return
        }

        application.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallbacks() {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                val cleaner = ReferenceCleaner(
                    inputMethodManager, mHField, mServedViewField,
                    finishInputLockedMethod
                )
                val rootView = activity.window.decorView.rootView
                val viewTreeObserver = rootView.viewTreeObserver
                viewTreeObserver.addOnGlobalFocusChangeListener(cleaner)
            }
        })
    }
}