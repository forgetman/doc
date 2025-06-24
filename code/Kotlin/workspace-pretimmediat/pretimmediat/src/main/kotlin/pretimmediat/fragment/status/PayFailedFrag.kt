package pretimmediat.fragment.status

import android.view.View
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import pretimmediat.R
import pretimmediat.activity.inputpiece.bank.BankPieceUpdateActivityCreator
import pretimmediat.def.Constants
import pretimmediat.fragment.base.BaseFrag
import pretimmediat.manager.AccountManager
import pretimmediat.network.ParamsValue
import vector.app.ext.bind.bindView
import vector.app.ext.inflate
import vector.app.ext.view.setOnDebounceClickListener
import vector.ext.inflate

@Creator
class PayFailedFrag : BaseFrag() {

    @Extra
    var orderId: String = ""

    override val serviceFlag: Int
        get() = Constants.ServiceFlag.MAIN_SINGLE_PAY_FAILED

    private val tvUpdate by bindView<View>(R.id.tv_update_account)

    override fun createContentView(): View {
        return inflate(R.layout.frag_loan_status_pay_failed)
    }

    override fun initializeContentView() {
        tvUpdate.setOnDebounceClickListener {
            BankPieceUpdateActivityCreator.create()
                .orderId(orderId)
                .userId(AccountManager.account)
                .appSsid(ParamsValue.CLIENT_ID)
                .start(requireContext())
        }
    }
}