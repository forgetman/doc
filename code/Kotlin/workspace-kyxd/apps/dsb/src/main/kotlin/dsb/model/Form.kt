package dsb.model

import dsb.R
import dsb.design.ui.adapter.Banner30ItemBinder
import dsb.design.ui.adapter.Banner31ItemBinder
import dsb.design.ui.adapter.Banner40ItemBinder
import lib.base.model.Form
import vector.bindingadapter.GridLayoutSet
import vector.pageindicator.widget.IconPageIndicator

class Form0 : Form()

class Form19 : Form() {
    var sets: List<GridLayoutSet>? = null
}

class Form24 : Form()
class Form25 : Form()
class Form34 : Form()
class Form36 : Form()
class Form30 : Form()
class Form39 : Form()
class Form41 : Form()
class Form50 : Form()
class Form51 : Form()
class Form52 : Form()

class Form33 : Form() {
    var texts: Array<String>? = null
    val toggle: Boolean
        get() = !texts.isNullOrEmpty()
}

class Form35 : Form() {
    var sets: List<GridLayoutSet>? = null
}

class Form38 : BannerForm()

class Form40 : BannerForm()

open class BannerForm : Form() {
    val config: IconPageIndicator.Config = IconPageIndicator.Config.Builder.create()
        .resId(R.drawable.selector_banner_indicator)
        .space(2.5f)
        .build()

    var data: List<Banner>? = null

    val itemBinders = listOf(
        Banner30ItemBinder(),
        Banner31ItemBinder(),
        Banner40ItemBinder(),
    )
}