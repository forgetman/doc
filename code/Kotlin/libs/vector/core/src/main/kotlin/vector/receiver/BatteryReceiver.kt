package vector.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.LifecycleOwner

class Battery {

    /**
     * 充电状态
     */
    enum class Status {
        UNKNOWN, // 未知状态
        CHARGING, // 充电中
        DISCHARGING, // 放电中
        NOT_CHARGING, // 未充电
        FULL, // 充电完成
    }

    enum class Health {
        UNKNOWN, // 未知
        GOOD, // 良好
        OVERHEAT, // 过热
        COLD, // 过冷
        DEAD, //
        OVER_VOLTAGE, // 电压过高
        UNSPECIFIED_FAILURE, // 未知

    }

    var temperature: Int = 0 // 温度
    var voltage = 0 // 电压
    var level = 0 // 电量
    var status = Status.UNKNOWN
    var health = Health.UNKNOWN

    // 充电类型 0 未充电
    // BatteryManager.BATTERY_PLUGGED_AC(1 充电器充电)
    // BatteryManager.BATTERY_PLUGGED_USB(2 USB充电)
    // BatteryManager.BATTERY_PLUGGED_WIRELESS(4 无线充电)
    var plugged = 0

    /**
     * 根据系统[BatteryManager.BATTERY_PROPERTY_STATUS]的状态改变[status]
     */
    fun applySystemStatus(systemStatus: Int) {
        status = when (systemStatus) {
            BatteryManager.BATTERY_STATUS_CHARGING -> Status.CHARGING
            BatteryManager.BATTERY_STATUS_DISCHARGING -> Status.DISCHARGING
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> Status.NOT_CHARGING
            BatteryManager.BATTERY_STATUS_FULL -> Status.FULL
            else -> Status.UNKNOWN
        }
    }

    /**
     * 根据系统[BatteryManager.EXTRA_HEALTH]的状态改变[health]
     */
    fun applySystemHealth(systemHealth: Int) {
        health = when (systemHealth) {
            BatteryManager.BATTERY_HEALTH_GOOD -> Health.GOOD
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> Health.OVERHEAT
            BatteryManager.BATTERY_HEALTH_DEAD -> Health.DEAD
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> Health.OVER_VOLTAGE
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> Health.UNSPECIFIED_FAILURE
            BatteryManager.BATTERY_HEALTH_COLD -> Health.COLD
            else -> Health.UNKNOWN
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as Battery

        if (temperature != that.temperature) return false
        if (voltage != that.voltage) return false
        if (level != that.level) return false
        if (status != that.status) return false
        if (health != that.health) return false
        if (plugged != that.plugged) return false

        return true
    }

    override fun hashCode(): Int {
        var result = temperature
        result = 31 * result + voltage
        result = 31 * result + level
        result = 31 * result + status.hashCode()
        result = 31 * result + health.hashCode()
        result = 31 * result + plugged
        return result
    }

    override fun toString(): String {
        return "temperature = $temperature\nvoltage = $voltage\nlevel = $level\nstatus = $status\nhealth = $health"
    }
}

class BatteryReceiver internal constructor() : BaseReceiver<BatteryReceiver>() {

    fun interface Listener {
        fun onChanged(battery: Battery)
    }

    override val filter: IntentFilter
        get() = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

    private var listener: Listener? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (isDestroyed()) return

        val battery = Battery()

        battery.voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
        battery.temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        battery.level = if (scale == 100) level else (level / (scale * 100f)).toInt()

        val status =
            intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        battery.applySystemStatus(status)

        val health =
            intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        battery.applySystemHealth(health)

        battery.plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)

        listener?.onChanged(battery)
    }

    fun observe(context: Context?, owner: LifecycleOwner, listener: Listener) {
        this.listener = listener
        register(context, owner)
    }

    fun registerListener(context: Context?, listener: Listener): Boolean {
        this.listener = listener
        return register(context)
    }

    fun unregisterListener(context: Context?): Boolean {
        listener = null
        return unregister(context)
    }
}
