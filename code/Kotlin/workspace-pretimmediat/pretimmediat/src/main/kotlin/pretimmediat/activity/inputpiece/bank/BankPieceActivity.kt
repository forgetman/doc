package pretimmediat.activity.inputpiece.bank

import android.app.Activity
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import inject.annotation.creator.Creator
import pretimmediat.activity.loan.TrialCalcActivityCreator
import pretimmediat.databinding.ActivityPieceBankBinding
import pretimmediat.def.Constants
import pretimmediat.network.api.InputPieceApi
import pretimmediat.service.CheckFirstServiceCreator

/**
 * 进件页 银行
 */
@AndroidEntryPoint
@Creator(forResult = true)
class BankPieceActivity : BaseBankPieceActivity() {

    override val serviceFlag: Int
        get() = Constants.ServiceFlag.PIECE_BANK

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivityPieceBankBinding.inflate(inflater).apply {
            owner = this@BankPieceActivity
            viewModel = this@BankPieceActivity.viewModel
        }
    }

    override fun onUploadSuccess(callback: () -> Unit) {
        TrialCalcActivityCreator.create()
            .userId(userId)
            .appSsid(appSsid)
            .startForResult(this@BankPieceActivity) { resultCode, _ ->
                if (resultCode == Activity.RESULT_OK) {
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    callback()
                }
            }

        CheckFirstServiceCreator.create()
            .userId(userId)
            .ssid(appSsid)
            .pageType(InputPieceApi.PAGE_TYPE_BANK)
            .start(this@BankPieceActivity)
    }
}