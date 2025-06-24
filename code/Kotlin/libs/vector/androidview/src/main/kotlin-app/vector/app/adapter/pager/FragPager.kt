@file:Suppress("unused")

package vector.app.adapter.pager

import androidx.fragment.app.Fragment

class FragPager private constructor() : AdapterPager<Fragment>() {

    companion object {
        fun newBuilder(): Builder = Builder()
    }

    class Builder internal constructor() : AdapterPager.Builder<Fragment, FragPager>() {
        override fun createPager(): FragPager {
            return FragPager()
        }
    }
}

fun FragPager.Companion.build(
    titles: List<String>? = null,
    requiredCurrentItem: Int? = null,
    creators: List<AdapterPager.PagerCreator<Fragment>>
) = newBuilder()
    .titles(titles)
    .creators(creators)
    .requiredCurrentItem(requiredCurrentItem)
    .build()

/**
 * 构造多个同样的Fragment
 */
fun FragPager.Companion.build(
    size: Int,
    titles: List<String>? = null,
    requiredCurrentItem: Int? = null,
    creator: AdapterPager.PagerCreator<Fragment>
) = newBuilder()
    .size(size)
    .titles(titles)
    .creators(creator)
    .requiredCurrentItem(requiredCurrentItem)
    .build()
