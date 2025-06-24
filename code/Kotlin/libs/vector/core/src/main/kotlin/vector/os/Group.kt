package vector.os

abstract class Group<CHILD> {

    var children: MutableList<CHILD> = mutableListOf()
        set(value) {
            field.clear()
            field = value
        }

    val childrenCount: Int
        get() = children.count()

    fun isEmpty() = childrenCount == 0
    fun isNotEmpty() = childrenCount != 0

    fun addChild(child: CHILD) {
        children.add(child)
    }

    fun addChild(index: Int, child: CHILD) {
        children.add(index, child)
    }

    fun removeChild(child: CHILD): Boolean = children.remove(child)

    fun removeChild(position: Int): Boolean = children.removeAt(position) != null

    fun getChildAt(position: Int): CHILD = children[position]
}