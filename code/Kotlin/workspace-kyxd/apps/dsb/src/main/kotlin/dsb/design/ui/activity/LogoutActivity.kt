package dsb.design.ui.activity

import android.graphics.Color
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dsb.Bus
import dsb.EventId
import dsb.databinding.ActivityLogoutBinding
import dsb.design.ui.dialog.LogoutConfirmDialog
import dsb.ext.addBackIcon
import lib.base.design.ui.activity.BaseSimpleDBActivity
import lib.base.model.User
import vector.bindingadapter.bind.Bind

/**
 * @author yuansui
 * @since 2019-05-27
 */
class LogoutActivity : BaseSimpleDBActivity() {

    val mobile: String? = User.get().mobile

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityLogoutBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText("账号注销")
    }

    override fun flowOfSetup() {
        setBackgroundColor(Color.WHITE)
    }

    val onLogoutClick = Bind.OnClick {
        val dialog = LogoutConfirmDialog(this, mobile)
        dialog.onAction = {
            Bus.get().send(EventId.LOGOUT)
            finish()
        }
        dialog.show()
    }
}