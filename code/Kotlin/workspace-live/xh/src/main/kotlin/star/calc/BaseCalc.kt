package star.calc

import live.Live

/**
 * @author yuansui
 * @since 2020-04-10
 */
class BaseCalc {
    var commissions = Live<Float>() // 提成
    var bonuses = Live<Float>() // 奖金
    var handlingFees = Live<Float>() // 手续费
}