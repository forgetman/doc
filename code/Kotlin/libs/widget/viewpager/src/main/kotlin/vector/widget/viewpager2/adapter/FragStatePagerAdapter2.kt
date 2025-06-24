package vector.widget.viewpager2.adapter

import android.annotation.SuppressLint
import android.util.SparseLongArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import sugar.ext.throwIfNull
import vector.app.adapter.pager.FragPager
import vector.widget.viewpager2.ViewPager2

/**
 * Only for [vector.widget.viewpager2.ViewPager2]
 * @author yuansui
 * @since 2019-07-18
 */
open class FragStatePagerAdapter2(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    constructor(activity: FragmentActivity) : this(
        activity.supportFragmentManager,
        activity.lifecycle
    )

    constructor(fragment: Fragment) : this(
        fragment.childFragmentManager,
        fragment.lifecycle
    )

    private var fragPager: FragPager? = null
    private val dataCount: Int
        get() = fragPager?.size ?: 0

    private var nextValue = 1L
    private val itemIds = SparseLongArray()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(new: FragPager, viewPager2: ViewPager2) {
        val old = fragPager
        fragPager = new

        itemIds.clear()

        when {
            old == null && new.size != 0 -> {
                notifyItemRangeInserted(0, new.size)
            }

            old != null -> {
                // 对比changed范围
                val oldSize = old.size
                val newSize = new.size
                if (oldSize == newSize) {
                    notifyItemRangeChanged(0, oldSize)
                } else {
                    notifyDataSetChanged()
                }
            }

            else -> notifyDataSetChanged()
        }

        new.requiredCurrentItem?.let {
            val oldPosition = viewPager2.currentItem
            if (oldPosition != it) viewPager2.setCurrentItem(it, false)
        }
    }

    override fun getItemCount(): Int {
        return dataCount
    }

    override fun getItemId(position: Int): Long {
        var id = itemIds[position, RecyclerView.NO_ID]
        if (id == -1L) {
            nextValue++
            itemIds.put(position, nextValue)
            id = nextValue
        }
        return id
    }

    override fun containsItem(itemId: Long): Boolean {
        val index = itemIds.indexOfValue(itemId)
        return index >= 0
    }

    override fun createFragment(position: Int): Fragment {
        return fragPager?.createInstance(position).throwIfNull("Can not create a Fragment.")
    }

    fun getPageTitle(position: Int): CharSequence? {
        return fragPager?.titles?.get(position)
    }

}