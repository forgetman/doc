package catroom.manager

import android.net.TrafficStats

/**
 * 流量监听, 不区分wifi和移动网络
 */
object TrafficManager {
    private var totalRxBytes: Long = 0
    private var totalTxBytes: Long = 0
    var diffRxBytes: Long = 0 // 收到的字节数
        private set
    var diffTxBytes: Long = 0 // 发送的字节数
        private set

    fun startMonitor() {
        // 获取开始时的流量大小
        totalRxBytes = TrafficStats.getTotalRxBytes()
        totalTxBytes = TrafficStats.getTotalTxBytes()
    }

    fun stopMonitor() {
        // 获取结束时的流量大小
        val newRxBytes = TrafficStats.getTotalRxBytes()
        val newTxBytes = TrafficStats.getTotalTxBytes()

        // 计算流量差值
        diffRxBytes = newRxBytes - totalRxBytes
        diffTxBytes = newTxBytes - totalTxBytes
    }
}