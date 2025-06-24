package lib.base.model.date

/**
 * @author yuansui
 */
class Year(val year: Int) {
    val animal = (year - 4) % 12 // 生肖
}
