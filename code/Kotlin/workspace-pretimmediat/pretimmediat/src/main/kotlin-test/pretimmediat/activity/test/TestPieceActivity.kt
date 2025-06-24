package pretimmediat.activity.test

import android.view.View
import android.widget.Button
import pretimmediat.R
import pretimmediat.activity.inputpiece.ContactPieceActivityCreator
import pretimmediat.activity.inputpiece.IdCardPieceActivityCreator
import pretimmediat.activity.inputpiece.InfoPieceActivityCreator
import pretimmediat.activity.inputpiece.bank.BankPieceActivityCreator
import pretimmediat.activity.inputpiece.bank.BankPieceUpdateActivityCreator
import pretimmediat.activity.loan.TrialCalcActivityCreator
import pretimmediat.ext.addBackIcon
import pretimmediat.manager.AccountManager
import pretimmediat.network.ParamsValue
import vector.app.activity.SimpleActivityEx
import vector.app.ext.bind.bindView
import vector.app.util.inflate

class TestPieceActivity : SimpleActivityEx() {

    private val btnInfo by bindView<Button>(R.id.btn_info)
    private val btnContact by bindView<Button>(R.id.btn_contact)
    private val btnIdCard by bindView<Button>(R.id.btn_id_card)
    private val btnBank by bindView<Button>(R.id.btn_bank)
    private val btnBankUpdate by bindView<Button>(R.id.btn_bank_update)
    private val btnTrial by bindView<Button>(R.id.btn_trial)

    override fun createContentView(): View {
        return R.layout.activity_test_piece.inflate(this)
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon("测试进件页") {
            finish()
        }
    }

    override fun initializeContentView() {
        btnInfo.setOnClickListener {
            InfoPieceActivityCreator.create()
                .userId(AccountManager.account)
                .appSsid(ParamsValue.CLIENT_ID)
                .start(this)
        }

        btnContact.setOnClickListener {
            ContactPieceActivityCreator.create()
                .userId(AccountManager.account)
                .appSsid(ParamsValue.CLIENT_ID)
                .start(this)
        }

        btnIdCard.setOnClickListener {
            IdCardPieceActivityCreator.create()
                .userId(AccountManager.account)
                .appSsid(ParamsValue.CLIENT_ID)
                .start(this)
        }

        btnBank.setOnClickListener {
            BankPieceActivityCreator.create()
                .userId(AccountManager.account)
                .appSsid(ParamsValue.CLIENT_ID)
                .start(this)
        }

        btnBankUpdate.setOnClickListener {
            BankPieceUpdateActivityCreator.create()
                .orderId("")
                .userId(AccountManager.account)
                .appSsid(ParamsValue.CLIENT_ID)
                .start(this)
        }

        btnTrial.setOnClickListener {
            TrialCalcActivityCreator.create()
                .userId(AccountManager.account)
                .appSsid(ParamsValue.CLIENT_ID)
                .start(this)
        }
    }
}