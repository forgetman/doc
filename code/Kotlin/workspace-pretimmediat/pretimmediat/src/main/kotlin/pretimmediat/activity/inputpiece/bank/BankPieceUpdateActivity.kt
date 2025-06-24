package pretimmediat.activity.inputpiece.bank

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import inject.annotation.creator.Creator
import pretimmediat.databinding.ActivityPieceBankUpdateBinding
import pretimmediat.def.Constants

/**
 * 进件页 银行 更新
 */
@AndroidEntryPoint
@Creator(forResult = true)
class BankPieceUpdateActivity : BaseBankPieceActivity() {

    override val serviceFlag: Int
        get() = Constants.ServiceFlag.PIECE_BANK_UPDATE


    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivityPieceBankUpdateBinding.inflate(inflater).apply {
            owner = this@BankPieceUpdateActivity
            viewModel = this@BankPieceUpdateActivity.viewModel
        }
    }

    override fun initializeData() {
        super.initializeData()
        viewModel.updateOrderInfo(orderId)
    }

    override fun onUploadSuccess(callback: () -> Unit) {
        finish()
    }
}