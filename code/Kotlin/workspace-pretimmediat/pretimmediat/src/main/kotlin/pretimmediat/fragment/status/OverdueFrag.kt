package pretimmediat.fragment.status

import dagger.hilt.android.AndroidEntryPoint
import inject.annotation.creator.Creator
import pretimmediat.def.Constants

@AndroidEntryPoint
@Creator
class OverdueFrag : BaseRepayingOrOverdueFrag() {

    override val serviceFlag: Int
        get() = Constants.ServiceFlag.MAIN_SINGLE_OVERDUE
}