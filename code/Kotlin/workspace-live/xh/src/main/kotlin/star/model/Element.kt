package star.model

/**
 * @author yuansui
 * @since 2020/4/18
 */
class Element(name: String, value: Value)

object Elements {
    val data = mutableListOf<Element>()

    init {
        add("公会分成比例", IntValue(5))
        add("主播分成比例", IntValue(45))
    }

    private fun add(name: String, value: Value) =
        data.add(Element(name, value))
}

interface Value {
    fun getSuffix(): String?
}

class IntValue(v: Int) : Value {

//    val data = LiveInt(v)

    override fun getSuffix(): String? {
        return "%"
    }

}