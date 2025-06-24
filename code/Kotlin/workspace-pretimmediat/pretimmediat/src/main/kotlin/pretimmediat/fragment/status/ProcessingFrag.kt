package pretimmediat.fragment.status

import android.view.View
import pretimmediat.R
import pretimmediat.def.Constants
import pretimmediat.fragment.base.BaseFrag
import vector.app.ext.inflate

/**
 * 状态: 放款中
 */
class ProcessingFrag : BaseFrag() {

    override val serviceFlag: Int
        get() = Constants.ServiceFlag.MAIN_SINGLE_PROCESSING

    override fun createContentView(): View {
        return inflate(R.layout.frag_loan_status_processing)
    }
}