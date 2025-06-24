package pretimmediat.activity.test

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import coroutine.flow.launchIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import logger.L
import pretimmediat.R
import pretimmediat.dialog.BankModifyConfirmDialog
import pretimmediat.dialog.FormInfoSelectorDialog
import pretimmediat.dialog.InstallmentRepaymentDialog
import pretimmediat.dialog.LoanApplySuccessDialog
import pretimmediat.dialog.PermissionDialog
import pretimmediat.dialog.PermissionSecondDialog
import pretimmediat.dialog.PopupDialog
import pretimmediat.dialog.RepaymentDialog
import pretimmediat.dialog.Style1Dialog
import pretimmediat.dialog.Style2Dialog
import pretimmediat.ext.addBackIcon
import pretimmediat.ext.formatDate
import pretimmediat.ext.withPieceLoading
import pretimmediat.manager.AccountManager
import pretimmediat.model.product.LoanPlan
import pretimmediat.model.product.PayChannel
import pretimmediat.model.product.SingleProduct
import pretimmediat.network.ParamsValue
import pretimmediat.widget.picker.DatePickerView
import vector.app.activity.SimpleActivityEx
import vector.app.ext.bind.bindView
import vector.app.util.inflate

class TestDialogActivity : SimpleActivityEx() {

    private val btnPermission by bindView<View>(R.id.btn_permission)
    private val btnPermission2 by bindView<View>(R.id.btn_permission2)
    private val btnFormSelection by bindView<View>(R.id.btn_form_selection)
    private val btnDate by bindView<View>(R.id.btn_date)
    private val btnLoanSuccess by bindView<View>(R.id.btn_loan_success)
    private val btnStyle1 by bindView<View>(R.id.btn_style1)
    private val btnBankUpdateConfirm by bindView<View>(R.id.btn_bank_update_confirm)
    private val btnListSelection by bindView<View>(R.id.btn_list_selection)
    private val btnPieceLoading by bindView<View>(R.id.btn_piece_loading)
    private val btnRepayment by bindView<View>(R.id.btn_repayment)
    private val btnInstallmentRepayment by bindView<View>(R.id.btn_installment_repayment)

    override fun createContentView(): View {
        return R.layout.activity_test_dialog.inflate(this)
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon("测试弹窗") {
            finish()
        }
    }

    override fun initializeContentView() {
        btnPermission.setOnClickListener {
            val dialog =  PermissionDialog(this) { result ->
                L.www("permission result: $result")
            }
            dialog.setOnCancelListener {
                L.www("permission cancel")
            }
            dialog.show()
        }

        btnPermission2.setOnClickListener {
            PermissionSecondDialog(this) { a, b ->
                L.www("permission2 allow = $a, result = $b")
                if (!b) {
                    Style2Dialog.Builder(this)
                        .icon(R.drawable.dialog_ic_loud_speaker)
                        .content("Afin de vous fournir un service de qualité, veuillez accéder aux paramètres de votre téléphone portable pour activer les autorisations appropriées.")
                        .buttonLeft("Annuler") {
                            L.www("permission2 cancel")
                        }
                        .buttonRight("Ouvrir") {
                            L.www("permission2 open")
                            val intent = Intent()
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.setData(Uri.fromParts("package", packageName, null))
                            startActivity(intent)
                        }
                        .build().show()
                }
            }.show()
        }

        btnFormSelection.setOnClickListener {
            val dialog = FormInfoSelectorDialog(
                this, R.string.piece_info_family_name, listOf(
                    "2asdfasdfasdf2e4asdfasdfasdfasfdasdfsasdfasdfasdfsadfdsfasdf8sadfa89sdf98as9df89asdf89asdfasdfasda",
                    "2asdfasdfasdf2e4asdfasdfasdfasfdasdfsasdfasdfasdfsadfdsfasdf8sadfa89sdf98as9df89asdf89asdfasdfasdasdfsdfdfddgdfsdfsdfsdfsfdfsdfadfasdfasdfafgdfg",
                    "2asdfasdfasdf2e4asdfasdfasdfasfdasdfsasdfasdfasdfsadfdsfasdf8sadfa89sdf98as9df89asdf89asdfasdfasda",
                    "2asdfasdfasdf2e4asdfasdfasdfasfdasdfsasdfasdfasdfsadfdsfasdf8sadfa89sdf98as9df89asdf89asdfasdfasda2",
                    "2asdfasdfasdf2e4asdfasdfasdfasfdasdfsasdfasdfasdfsadfdsfasdf8sadfa89sdf98as9df89asdf89asdfasdfasda3",
                    "2asdfasdfasdf2e4asdfasdfasdfasfdasdfsasdfasdfasdfsadfdsfasdf8sadfa89sdf98as9df89asdf89asdfasdfasda4",
                )
            ) { _, _, _ ->
            }
            dialog.show()
        }

        btnDate.setOnClickListener {
            initDatePicker()
            mDatePicker?.show(System.currentTimeMillis())
        }

        btnLoanSuccess.setOnClickListener {
            val dialog = LoanApplySuccessDialog(this, "111111", "22333332.22", emptyList())
            dialog.show()
        }

        btnStyle1.setOnClickListener {
            Style1Dialog.Builder(this)
                .content("测试内容")
                .button("测试按钮") {

                }
                .dismissCountdown(10)
                .build()
                .show()
        }

        btnBankUpdateConfirm.setOnClickListener {
            BankModifyConfirmDialog(
                this,
                AccountManager.account,
                ParamsValue.CLIENT_ID,
                "123123123",
                "code1",
                "12345678"
            ) {
                L.www("bank confirm result: $it")
            }.show()
        }

        btnListSelection.setOnClickListener {
            PopupDialog(this, listOf("123123123", "234234234")) { index, data ->
                L.www("index: $index, data: $data")
            }.show()
        }

        btnPieceLoading.setOnClickListener {
            flow {
                delay(3000)
                emit(Unit)
            }.withPieceLoading(this, false).launchIn(this)
        }

        btnRepayment.setOnClickListener {
            RepaymentDialog(
                this,
                SingleProduct.TEST,
                LoanPlan.TEST,
                listOf(PayChannel.TEST, PayChannel.TEST)
            ).show()
        }

        btnInstallmentRepayment.setOnClickListener {
            InstallmentRepaymentDialog(
                this,
                "123",
                LoanPlan.TEST,
                listOf(PayChannel.TEST, PayChannel.TEST)
            ).show()
        }
    }

    private var mDatePicker: DatePickerView? = null
    private fun initDatePicker() {
        val beginTimestamp = "01-01-2000".formatDate()
        val endTimestamp = System.currentTimeMillis()

        // 通过时间戳初始化日期，毫秒级别
        mDatePicker = DatePickerView(
            this, beginTimestamp, endTimestamp
        ) { timestamp ->
            L.www("selected time: $timestamp")
        }
        // 不允许点击屏幕或物理返回键关闭
        mDatePicker?.setCancelable(false)
        // 不允许循环滚动
        mDatePicker?.setScrollLoop(false)
        // 不允许滚动动画
        mDatePicker?.setCanShowAnim(false)
    }
}