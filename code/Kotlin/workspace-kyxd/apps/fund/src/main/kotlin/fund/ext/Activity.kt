package fund.ext

import fund.R
import vector.design.ui.activity.BaseDBActivityEx

fun BaseDBActivityEx.addNavBack(text: String? = null) {
    if (text.isNullOrEmpty()) {
        navBar.left.addImage {
            id = R.drawable.nav_bar_ic_back
            onClick = {
                finish()
            }
        }
    } else {
        navBar.left.addText {
            this.text = text
            drawableLeft = R.drawable.nav_bar_ic_back
            onClick = {
                finish()
            }
        }
    }
}