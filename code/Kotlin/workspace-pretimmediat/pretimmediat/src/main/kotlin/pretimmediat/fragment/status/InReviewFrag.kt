package pretimmediat.fragment.status

import android.view.View
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import coroutine.flow.launchIn
import inject.annotation.creator.Creator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import logger.L
import pretimmediat.R
import pretimmediat.def.Constants
import pretimmediat.fragment.base.BaseFrag
import pretimmediat.network.api.GlobalApi
import pretimmediat.network.createApi
import pretimmediat.stats.Stats
import vector.app.ext.inflate
import vector.ext.inflate

@Creator
class InReviewFrag : BaseFrag() {

    companion object {
        private const val LOG_TAG = "InReviewFrag"
    }

    override val serviceFlag: Int
        get() = Constants.ServiceFlag.MAIN_SINGLE_IN_REVIEW

    override fun createContentView(): View {
        return inflate(R.layout.frag_loan_status_in_review)
    }

    override fun initializeContentView() {
        createApi<GlobalApi>().checkGooglePlayToggle().flowOn(Dispatchers.IO)
            .catch { e ->
                L.e(LOG_TAG, "check toggle", e)
            }.onEach {
                if (it.fcFlag == "1") {
                    startGooglePlay()
                }
            }.launchIn(this)
    }

    @Suppress("OPT_IN_USAGE")
    private fun startGooglePlay() {
        val manager = ReviewManagerFactory.create(requireContext())
        flow {
            val info = manager.requestReview()
            emit(info)
        }.flatMapConcat { reviewInfo ->
            flow {
                reviewInfo.describeContents()
                manager.launchReview(requireActivity(), reviewInfo)
                emit(Unit)
            }
        }.flowOn(Dispatchers.Main).catch { e ->
            L.e(LOG_TAG, "startGooglePlay", e)
        }.launchIn(this)

        Stats.public.onEvent("POPUP_GOOGLE_FEEDBACK", userId, appSsid)
    }
}