package vector.widget.viewpager2.adapter

import vector.widget.scrollable.adapter.ItemAdapter

/**
 * @author yuansui
 * @since 2021/5/6
 */
class ItemPagerAdapter2 : ItemAdapter() {

    var itemCycle: Boolean = false

    override fun getItem(position: Int): Any? {
        var realPos = position
        if (itemCycle) {
            realPos %= super.getItemCount()
        }
        return super.getItem(realPos)
    }

    override fun getItemCount(): Int {
        val count = super.getItemCount()
        return if (itemCycle) {
            if (count == 1) {
                count
            } else Integer.MAX_VALUE
        } else {
            count
        }
    }
}