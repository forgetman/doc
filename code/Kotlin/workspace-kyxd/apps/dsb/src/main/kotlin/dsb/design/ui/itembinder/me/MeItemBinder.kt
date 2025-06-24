package dsb.design.ui.itembinder.me

import android.graphics.Color
import dsb.databinding.LayoutMeFooterBinding
import dsb.databinding.LayoutMeHeaderBinding
import dsb.design.ui.itembinder.SimpleDBItemBinder
import eth.model.Nive
import image.ImageTransformation
import lib.base.Sp
import lib.base.model.Form
import live.Live
import vector.bindingadapter.GridLayoutSet
import vector.app.os.dp

interface FormMe {
    class Header {
        val visibility = Live<Boolean>() // 头部布局是否隐藏
        val avatar = Nive<String>()
        val transformation = ImageTransformation.Shape.Circle(2.dp.toPx(), Color.WHITE)
        val name = Live<String>()
        val desc = Live("让社保变得简单!")
        val sets = Live<List<GridLayoutSet>?>()
        val others = Live<List<Form>>()
        val signIn = Live(Sp.isSignIn())
    }

    class Footer

    interface ItemBinder {
        class Header(private val listener: Listener) :
            SimpleDBItemBinder<FormMe.Header, LayoutMeHeaderBinding>() {
            interface Listener {
                fun onAvatarClick()
                fun onNameClick()
            }

            override fun onBindBinding(
                item: FormMe.Header,
                binding: LayoutMeHeaderBinding,
                position: Int
            ) {
                binding.item = item
                binding.listener = listener
            }
        }

        class Footer(private val listener: Listener) :
            SimpleDBItemBinder<FormMe.Footer, LayoutMeFooterBinding>() {
            interface Listener {
                fun onShareClick()
                fun onGoodClick()
            }

            override fun onBindBinding(
                item: FormMe.Footer,
                binding: LayoutMeFooterBinding,
                position: Int
            ) {
                binding.listener = listener
            }
        }
    }
}