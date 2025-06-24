package vector.util

import android.content.Context

interface StatsOption {
    fun onActivityResume(context: Context, tag: String)
    fun onActivityPause(context: Context, tag: String)
    fun onFragmentVisible(context: Context, tag: String)
    fun onFragmentInvisible(context: Context, tag: String)

    fun onEvent(context: Context?, eventId: String, map: HashMap<String, String>?)
    fun onEventStart(context: Context?, eventId: String, map: HashMap<String, String>?)
    fun onEventEnd(context: Context?, eventId: String, map: HashMap<String, String>?)
}

/**
 * 数据埋点统计
 */
@Suppress("unused")
object Stats {

    private var debugMode: Boolean = false
    private var statsOption: StatsOption? = null

    /**
     * @param option
     * @param debugMode 调试模式, 不计入真实统计
     */
    fun init(option: StatsOption, debugMode: Boolean) {
        this.debugMode = debugMode
        this.statsOption = option
    }

    fun onActivityResume(context: Context, tag: String) {
        if (debugMode) return
        statsOption?.onActivityResume(context, tag)
    }

    fun onActivityPause(context: Context, tag: String) {
        if (debugMode) return
        statsOption?.onActivityPause(context, tag)
    }

    fun onFragmentVisible(context: Context?, tag: String) {
        if (debugMode || context == null) return
        statsOption?.onFragmentVisible(context, tag)
    }

    fun onFragmentInvisible(context: Context?, tag: String) {
        if (debugMode || context == null) return
        statsOption?.onFragmentInvisible(context, tag)
    }

    fun onEvent(context: Context? = null, eventId: String, map: HashMap<String, String>? = null) {
        if (debugMode) return
        statsOption?.onEvent(context, eventId, map)
    }

    fun onEventStart(context: Context? = null, eventId: String, map: HashMap<String, String>? = null) {
        if (debugMode) return
        statsOption?.onEventStart(context, eventId, map)
    }

    fun onEventEnd(context: Context? = null, eventId: String, map: HashMap<String, String>? = null) {
        if (debugMode) return
        statsOption?.onEventEnd(context, eventId, map)
    }
}
