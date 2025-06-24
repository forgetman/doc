package reader.pattern.repo

import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import reader.model.Page
import reader.model.pack.unpack
import reader.network.api.Category
import reader.network.api.LeaderboardApi
import reader.network.api.LeaderboardType
import reader.network.createApi
import java.util.Locale
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019-06-18
 */
@FragmentScoped
class BookRepo @Inject constructor() {

    fun fetchLeaderboard(type: Category, listType: LeaderboardType, page: Page) =
        createApi<LeaderboardApi>().leaderboard(
            type.name.lowercase(),
            listType.name.lowercase(Locale.getDefault()),
            page.num
        ).unpack().flowOn(Dispatchers.IO)
}