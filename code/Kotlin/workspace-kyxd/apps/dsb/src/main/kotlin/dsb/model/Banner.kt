package dsb.model

/**
 * @author yuansui
 * @since 2019/2/1
 */
open class Banner {

    companion object {
        const val TYPE30 = 30
        const val TYPE31 = 31
        const val TYPE40 = 40
    }

    var icon: String? = null
    var title: String? = null
    var subTitle: String? = null
    var content: String? = null
    var date: String? = null
    var url: String? = null
    var needLogin: Boolean = false

    var type: Int? = null
}