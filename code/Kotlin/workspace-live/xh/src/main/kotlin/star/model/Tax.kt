package star.model

/**
 * 流水扣税
 * @author yuansui
 * @since 2020-04-10
 */
open class Tax {
    var ratio: Float = 0f // 税点
}

/**
 * 公会扣税(企业所得税)
 */
class UnionTax : Tax()

/**
 * 主播自提税
 */
class AnchorDrawTax : Tax()