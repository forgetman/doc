package reader.pattern.repo

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import reader.model.Page
import reader.network.api.SearchApi
import reader.network.createApi
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2018/8/30
 */
@ViewModelScoped
class SearchRepo @Inject constructor() {

    fun fetchHots() = listOf(
        "唐家三少", "我吃西红柿", "苍天白鹤", "骷髅精灵", "鹅是老五", "方想",
        "辰东", "天蚕土豆", "鱼人二代", "耳根", "零下九十度", "梦入神机"
    )

    fun search(key: String?, page: Page) =
        createApi<SearchApi>().search(key, page.num).flowOn(Dispatchers.IO)
}