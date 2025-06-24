package reader.pattern.frag.leaderboard

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import live.Live
import reader.R
import reader.databinding.FragLeaderboardBinding
import reader.network.api.Category
import reader.network.api.LeaderboardType
import vector.app.databinding.annotation.LayoutBindingClass
import vector.app.databinding.frag.SimpleDBFragEx
import vector.app.frag.LazyLoadMode

/**
 * @author yuansui
 * @since 2021/4/8
 * 排行榜
 */
@Creator
@AndroidEntryPoint
@LayoutBindingClass<FragLeaderboardBinding>
class LeaderboardFrag : SimpleDBFragEx() {

    @Extra
    lateinit var category: Category

    private val subFrag by lazy {
        LeaderboardSubFragCreator.create(category, LeaderboardType.WEEK).get()
    }

    val currIndex = Live(0)

    override val lazyLoadMode: LazyLoadMode
        get() = LazyLoadMode.IDLE

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragLeaderboardBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeContentView() {
        childFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.frag_sub, subFrag)
            .commitNowAllowingStateLoss()

        currIndex.observe(this) {
            when (it) {
                0 -> subFrag.changeType(LeaderboardType.WEEK)
                1 -> subFrag.changeType(LeaderboardType.MONTH)
                2 -> subFrag.changeType(LeaderboardType.TOTAL)
            }
        }
    }
}