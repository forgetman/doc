package star.model

/**
 * 流水返点
 * @author yuansui
 * @since 2020-04-10
 */
class Rebate {


    private fun getRatio(flow: Int): Int {
        return when {
//            flow > 100000 -> 0.12f // 流水大于10万返点12%
//            flow > 200000 -> 0.20f
            else -> 0
        }
    }

    fun value(flow: Int): Int = 0
}