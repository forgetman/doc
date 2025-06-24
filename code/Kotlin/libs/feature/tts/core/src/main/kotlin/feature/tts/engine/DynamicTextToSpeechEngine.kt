package feature.tts.engine

import android.content.Context
import androidx.annotation.CallSuper
import feature.tts.def.SpeechOpCallback
import feature.tts.model.SpeechItem
import logger.L

/**
 * engine代理, 根据配置内部切换, 属于上层业务engine
 */
abstract class DynamicTextToSpeechEngine(context: Context) : BaseTextToSpeechEngine(context) {

    private var engine: BaseTextToSpeechEngine? = null
        set(value) {
            synchronized(this) {
                field = value
            }
        }

    fun updateEngine(engine: BaseTextToSpeechEngine) {
        val old = this.engine
        if (old != null) {
            when {
                old.initialized -> {
                    old.holdItems.forEach { item ->
                        engine.preload(item)
                    }
                    old.release()
                }

                old.holdItems.isNotEmpty() -> {
                    L.d("$tag updateEngine, old engine not initialized, but holdItems is not empty")
                    old.holdItems.forEach { item ->
                        engine.preload(item)
                    }
                }
            }
        }
        this.engine = engine
    }

    @CallSuper
    override fun onInit(): Boolean {
        engine?.onInit() ?: run {
            L.d("$tag onInit, curr engine is null")
            return false
        }
        return true
    }

    @CallSuper
    override fun onDeinit() {
        engine?.onDeinit()
    }

    @CallSuper
    override fun onStart(item: SpeechItem, callback: SpeechOpCallback) {
        engine?.onStart(item, callback) ?: run {
            callback.onResult(false)
        }
    }

    @CallSuper
    override fun onCancel(item: SpeechItem, callback: SpeechOpCallback) {
        engine?.onCancel(item, callback) ?: run {
            callback.onResult(false)
        }
    }

    @CallSuper
    override fun onDestroy() {
        engine?.release()
        engine = null
    }
}