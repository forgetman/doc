package vector.widget.viewpager.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import sugar.ext.cast
import sugar.ext.throwIfNull
import vector.app.adapter.pager.FragPager
import vector.widget.viewpager.ViewPager
import java.lang.ref.WeakReference

/**
 * 功能:
 * 1. tab标题
 *
 * 2. 动态删除添加数据:
 *
 * 利用[modify]变量判断需要删除的时候, 改变[getItemPosition]为[PagerAdapter.POSITION_NONE]
 *
 * 3. 循环展示
 *
 *
 * 正常来说如果先调用detach([destroyItem])之后才调用attach([instantiateItem]),
 * 完全可以使用super方法来完成, 但是实际上左右滑行为是不一样的, 在往右滑的时候(切换到前一个fragment), 是
 * 先调用attach之后才调用detach, 导致fragment最后其实是分离不可见的
 * 所以使用自己的[FragmentTransaction]来保证依附是在分离之后被调用, 详情见[finishUpdate]
 */
@Suppress("DEPRECATION")
open class FragPagerAdapter private constructor(private val manager: FragmentManager) :
    FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT),
    BaseViewPagerAdapter {

    constructor(activity: FragmentActivity) : this(activity.supportFragmentManager)
    constructor(fragment: Fragment) : this(fragment.childFragmentManager)
    constructor(context: Context) : this(
        context as? FragmentActivity
            ?: throw IllegalArgumentException("context is not a activity")
    )

    private var modify = false

    private var currTransaction: FragmentTransaction? = null

    private var currItemRef: WeakReference<Fragment>? = null

    var fragPager: FragPager? = null
    private val dataCount: Int
        get() = fragPager?.size ?: 0

    val isEmpty: Boolean
        get() = count == 0

    /**
     * 和[androidx.viewpager.widget.ViewPager.setOffscreenPageLimit]冲突, loop为true的时候不能设置
     */
    open val enableLoop: Boolean
        get() = false

    override fun setData(p: FragPager, viewPager: ViewPager) {
        fragPager = p

        viewPager.notifyDataSetChanged()

        p.requiredCurrentItem?.let {
            viewPager.setCurrentItem(it, false)
        }
    }

    @Throws(NullPointerException::class)
    override fun getItem(position: Int): Fragment {
        val pos = if (enableLoop) {
            position % dataCount
        } else {
            position
        }

        return fragPager?.createInstance(pos).throwIfNull("Can not new a Fragment.")
    }

    override fun getCount(): Int {
        val count = dataCount
        return if (enableLoop) {
            if (count < 2) {
                count
            } else Integer.MAX_VALUE
        } else {
            count
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        return if (modify) {
            POSITION_NONE
        } else {
            POSITION_UNCHANGED
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragPager?.titles?.get(position)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (currTransaction == null) currTransaction = manager.beginTransaction()

        val name = makeFragmentName(container.id, getItemId(position))
        var frag = manager.findFragmentByTag(name)
        if (frag != null) {
            currTransaction?.attach(frag)
        } else {
            frag = getItem(position)
            currTransaction?.add(container.id, frag, name)
        }

        if (frag !== currItemRef?.get()) {
            frag.setMenuVisibility(false)
            currTransaction?.setMaxLifecycle(frag, Lifecycle.State.STARTED)
        }

        return frag
    }

    override fun getItemId(position: Int): Long {
        var id = position
        if (enableLoop) {
            id %= dataCount
        }

        return id.toLong()
    }

    override fun finishUpdate(container: ViewGroup) {
        if (currTransaction != null) {
            try {
                currTransaction?.commitNowAllowingStateLoss()
            } catch (e: IllegalStateException) {
                currTransaction?.commitAllowingStateLoss()
            }
            currTransaction = null
        }
    }

    private fun makeFragmentName(viewId: Int, id: Long): String {
        return "android:switcher:$viewId:$id"
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, any: Any) {
        any.cast<Fragment> { fragment ->
            val currPrimaryItem = currItemRef?.get()
            if (currPrimaryItem != fragment) {

                if (currTransaction == null) currTransaction = manager.beginTransaction()
                currPrimaryItem?.let {
                    it.setMenuVisibility(false)
                    currTransaction?.setMaxLifecycle(it, Lifecycle.State.STARTED)
                }

                fragment.setMenuVisibility(true)
                currTransaction?.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)

                currItemRef?.clear()
                currItemRef = WeakReference(fragment)
            }
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        if (any !is Fragment) return

        if (currTransaction == null) currTransaction = manager.beginTransaction()
        currTransaction?.detach(any)
        if (modify) {
            currTransaction?.remove(any)
        }
        if (currItemRef?.get() == any) {
            currItemRef?.clear()
            currItemRef = null
        }
    }

    override fun notifyDataSetChanged() {
        modify = true
        super.notifyDataSetChanged()
        modify = false
    }
}