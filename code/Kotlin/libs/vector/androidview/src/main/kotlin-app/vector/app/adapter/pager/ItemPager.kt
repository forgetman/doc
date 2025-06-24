package vector.app.adapter.pager

/**
 * @author yuansui
 * @since 2021/6/24
 */
data class ItemPager<T : Any>(val data: List<T>, val requiredCurrentItem: Int)