package star.model

import sugar.ext.until

/**
 * 积分
 * @author yuansui
 * @since 2020/4/13
 */
abstract class BaseIntegral {
    /**
     * 考核指标
     *
     * 流水金额
     * 新增付费人数
     * 流水增量/有效收礼人数
     */
}

/**
 * 新增付费人数
 */
class NewPayersIntegral : BaseIntegral() {
    fun value(flow: Float): Int {
        val result = flow / 10000f
        when {
            result > 200f -> {

            }
            result <= 200f -> {

            }
        }
        return 0
    }
}

/**
 * 流水增量
 */
class FlowIncrementIntegral : BaseIntegral() {

}

/**
 * 有效收礼人数
 */
class ReceiveGiftCountIntegral : BaseIntegral() {}


/**
 * 流水积分
 */
class FlowIntegral : BaseIntegral() {

    fun value(flow: Float): Int {
        return when (flow / 10000f) {
            in 0f until 1f -> 0
            in 1f until 50f -> 20
            in 50f until 100f -> 30
            in 100f until 200f -> 35
            in 200f until 300f -> 45
            in 300f until 500f -> 50
            else -> 60
        }
    }
}
