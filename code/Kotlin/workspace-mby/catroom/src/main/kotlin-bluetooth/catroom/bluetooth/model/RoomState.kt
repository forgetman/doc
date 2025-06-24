package catroom.bluetooth.model

/**
 * 猫屋状态
 *
 * @param lightState 照明状态
 * @param environment 外面环境: 白天/黑夜
 * @param food 猫粮
 * @param freezeDried 冻干
 * @param inducedState 感应状态
 * @param battery 电量
 */
data class RoomState(
    val lightState: Int,
    val environment: Int,
    val food: Int,
    val freezeDried: Int,
    val inducedState: Int,
    val battery: Int,
) {
    constructor() : this(0, 0, 0, 0, 0, 0)

    val lightStateDesc: String
        get() = when (lightState) {
            0 -> "关闭"
            1 -> "开启"
            else -> "未知"
        }

    val environmentDesc: String
        get() = when (environment) {
            0 -> "白天"
            1 -> "黑夜"
            else -> "未知"
        }

    val foodDesc: String
        get() = when (food) {
            0 -> "≥10%"
            1 -> "<10%"
            else -> "未知"
        }

    val freezeDriedDesc: String
        get() = when (freezeDried) {
            0 -> "≥10%"
            1 -> "<10%"
            else -> "未知"
        }

    val inducedStateDesc: String
        get() = when (inducedState) {
            0 -> "未感应"
            1 -> "感应"
            else -> "未知"
        }

    val batteryDesc: String
        get() = "$battery%1"
}