package fund.design.ui.frag

import android.graphics.Color
import androidx.databinding.ViewDataBinding
import fund.Bus
import fund.EventId
import fund.R
import fund.databinding.FragMeBinding
import fund.design.ui.activity.LoginActivity
import fund.design.ui.adapter.MeAdapter
import fund.design.viewModel.MeViewModel
import fund.ext.toWebWithoutLogin
import fund.model.MeLayoutStyle
import fund.model.MeType
import lib.base.Sp
import vector.annotation.LayoutId
import vector.bindingadapter.onBind.Bind
import vector.design.ui.frag.FragEx
import vector.ext.startActivity
import vector.fitter.DpFitter
import vector.image.CircleShaper

/**
 * @author yuansui
 * @since 2018/8/1
 */
@LayoutId(R.layout.frag_me)
class MeFrag : FragEx<MeViewModel>() {

    companion object {
        const val REQUEST_CAMERA = 0
        const val REQUEST_ALBUM = 1
    }

    val adapter by lazy { MeAdapter() }
    val shaper = CircleShaper(DpFitter.get().dp(2), Color.WHITE)

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = FragMeBinding.inflate(layoutInflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    override fun flowOfSetup() {
        Bus.get().with(this)
            .take(EventId.LOGIN)
            .subscribe {
                viewModel.onLogin()
            }

        Bus.get().with(this)
            .take(EventId.LOGOUT)
            .subscribe {
                viewModel.onLogout()
            }
    }

    val onItemClick = ScrollableBind.List.OnItemClick { view, position ->
        val item = viewModel.data.value?.get(position) ?: return@onItemClick

        if (item.style == MeLayoutStyle.DIVIDER) return@onItemClick

        when (item.type) {
            MeType.ABOUT -> viewModel.getAboutUrl().toWebWithoutLogin()
        }
    }

    val onNameClick = Bind.OnClick {
        startActivity(LoginActivity::class)
    }

    val onAvatarClick = Bind.OnClick {
        if (Sp.isLogin()) {

        } else {
            startActivity(LoginActivity::class)
        }
    }
}