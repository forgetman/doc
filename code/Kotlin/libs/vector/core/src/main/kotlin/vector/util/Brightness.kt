package vector.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import coroutine.flow.launchForever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import logger.L
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import vector.appContext
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @author yuansui
 * @since 2025/4/29
 */
object Brightness {
    const val PERCENT_MIN = 0
    const val PERCENT_MAX = 100
    private const val MIN = 0
    private const val MAX = 255

    val maxBrightness: Int = getConfigMaxBrightness()

    @SuppressLint("DiscouragedApi")
    private fun getConfigMaxBrightness(): Int {
        try {
            val system = Resources.getSystem()
            val resId: Int = system.getIdentifier(
                "config_screenBrightnessSettingMaximum", "integer", "android"
            )
            if (resId != 0) {
                return system.getInteger(resId)
            }
        } catch (e: Exception) {
            // do nothing
        }
        return MAX
    }

    private fun calcPercent(brightness: Int): Int {
        return (brightness / maxBrightness.toFloat() * PERCENT_MAX).roundToInt()
    }

    object System {

        private const val DEFAULT_STEP = 10

        fun interface Listener {
            fun onBrightnessChanged(percent: Int)
        }

        private val listeners = mutableListOf<Listener>()
        private var contentObserver: ContentObserver? = null
        private val percent = MutableStateFlow(0)

        init {
            val brightness = getValue()
            percent.value = calcPercent(brightness)

            percent.onEach {
                listeners.forEach { listener ->
                    listener.onBrightnessChanged(it)
                }
            }.flowOn(Dispatchers.Main).launchForever()
        }

        fun canWrite(): Boolean {
            return if (isSdkAtLeast(SdkInt.M_23)) {
                Settings.System.canWrite(appContext)
            } else {
                ContextCompat.checkSelfPermission(
                    appContext, Manifest.permission.WRITE_SETTINGS
                ) == PackageManager.PERMISSION_GRANTED
            }
        }

        fun registerListener(listener: Listener) {
            if (listeners.contains(listener)) return
            listeners.add(listener)

            if (listeners.size == 1) {
                val observer = object : ContentObserver(Handler(Looper.myLooper() ?: Looper.getMainLooper())) {
                    override fun onChange(selfChange: Boolean) {
                        val brightness = getValue()
                        percent.value = calcPercent(brightness)
                    }
                }
                appContext.contentResolver.registerContentObserver(
                    Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true, observer
                )
                contentObserver = observer
            }
        }

        fun unregisterListener(listener: Listener) {
            listeners.remove(listener)
            if (listeners.isEmpty()) {
                contentObserver?.let {
                    appContext.contentResolver.unregisterContentObserver(it)
                    contentObserver = null
                }
            }
        }

        /**
         * 获取亮度百分比
         * @return 0 - 100
         */
        fun getPercent(): Int {
            return percent.value
        }

        /**
         * 设置亮度百分比
         * @param percent 0 - 100
         */
        fun setPercent(@IntRange(from = PERCENT_MIN.toLong(), to = PERCENT_MAX.toLong()) percent: Int): Boolean {
            var usePercent = percent
            if (percent < PERCENT_MIN) usePercent = PERCENT_MIN
            if (percent > PERCENT_MAX) usePercent = PERCENT_MAX
            if (usePercent == this.percent.value) return false

            val result = setValue((usePercent / PERCENT_MAX.toFloat() * maxBrightness).roundToInt())
            if (!result) return false
            this.percent.value = usePercent
            return true
        }

        fun up(percent: Int = DEFAULT_STEP): Boolean {
            return setPercent(getPercent() + percent)
        }

        fun down(percent: Int = DEFAULT_STEP): Boolean {
            return setPercent(getPercent() - percent)
        }

        internal fun getValue(): Int {
            return try {
                val value = Settings.System.getInt(
                    appContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS
                )
                convertGetValue(value)
            } catch (e: Settings.SettingNotFoundException) {
                L.e(e)
                0
            }
        }

        private fun setValue(@IntRange(from = MIN.toLong()) value: Int): Boolean {
            if (isAuto()) return false //自动模式下设置亮度无效

            if (!canWrite()) return false

            val checkValue = min(value, maxBrightness)
            val cr: ContentResolver = appContext.contentResolver
            return Settings.System.putInt(
                cr, Settings.System.SCREEN_BRIGHTNESS, convertSetValue(checkValue)
            )
        }

        /**
         * 是否开启了自动亮度调节
         */
        fun isAuto(): Boolean {
            return try {
                getMode() == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            } catch (e: Settings.SettingNotFoundException) {
                L.e(e)
                false
            }
        }

        fun setAuto(): Boolean {
            if (!canWrite()) return false
            return try {
                if (!isAuto()) {
                    val cr: ContentResolver = appContext.contentResolver
                    Settings.System.putInt(
                        cr, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                    )
                } else {
                    true
                }
            } catch (e: Settings.SettingNotFoundException) {
                L.e(e)
                false
            }
        }

        fun isManual(): Boolean {
            return try {
                getMode() == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            } catch (e: Settings.SettingNotFoundException) {
                L.e(e)
                false
            }
        }

        fun setManual(): Boolean {
            if (!canWrite()) return false
            return try {
                if (!isManual()) {
                    val cr: ContentResolver = appContext.contentResolver
                    Settings.System.putInt(
                        cr, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                    )
                } else {
                    true
                }
            } catch (e: Settings.SettingNotFoundException) {
                L.e(e)
                false
            }
        }

        private fun getMode() = Settings.System.getInt(
            appContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE
        )

        private fun convertSetValue(value: Int): Int {
            val configMax = maxBrightness
            return if (configMax == MAX) {
                value
            } else {
                val rate = configMax / MAX.toFloat()
                val actualValue = value * rate
                actualValue.toInt()
            }
        }
    }

    object Window {

        fun setPercent(
            window: android.view.Window,
            @IntRange(from = PERCENT_MIN.toLong(), to = PERCENT_MAX.toLong()) percent: Int
        ) {
            var usePercent = percent
            if (percent < PERCENT_MIN) usePercent = PERCENT_MIN
            if (percent > PERCENT_MAX) usePercent = PERCENT_MAX

            setValue(
                window,
                (usePercent / PERCENT_MAX.toFloat() * maxBrightness).roundToInt()
            )
        }

        fun getPercent(window: android.view.Window): Int {
            val brightness = getValue(window)
            return calcPercent(brightness)
        }

        private fun getValue(window: android.view.Window): Int {
            val params = window.attributes
            val brightness = params.screenBrightness
            return if (brightness == BRIGHTNESS_OVERRIDE_NONE) {
                // 使用的默认选项, 跟随System的亮度
                System.getValue()
            } else {
                (params.screenBrightness * maxBrightness).toInt()
            }
        }

        private fun setValue(
            window: android.view.Window,
            @IntRange(from = MIN.toLong()) value: Int
        ) {
            val useValue = min(value, maxBrightness)
            val lp = window.attributes
            lp.screenBrightness = useValue / maxBrightness.toFloat()
            window.attributes = lp
        }

        fun reset(window: android.view.Window) {
            val lp = window.attributes
            lp.screenBrightness = BRIGHTNESS_OVERRIDE_NONE
            window.attributes = lp
        }
    }

    /**
     * 获取实际应该使用的值, 基于手机本身可以获取到的最大亮度值
     * @return 转换成谷歌体系的值
     */
    private fun convertGetValue(value: Int): Int {
        val configMax = maxBrightness
        return if (configMax == MAX) {
            // 使用的是谷歌体系
            value
        } else {
            // 使用的厂商体系, 比如小米
            // 要做数值转换
            val rate = MAX.toFloat() / configMax
            val actualValue = value * rate
            actualValue.roundToInt()
        }
    }
}