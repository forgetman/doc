package dsb.ext

import android.app.Activity
import dsb.R
import vector.app.appbar.AppBar
import vector.app.os.drawableRes

fun AppBar.addBackIcon(act: Activity, text: String? = null) {
    if (text.isNullOrEmpty()) {
        left.addIcon(R.drawable.nav_bar_ic_back.drawableRes) {
            act.finish()
        }
    } else {
        left.addText {
            this.text = text
            drawableLeft = R.drawable.nav_bar_ic_back
            onClick = {
                act.finish()
            }
        }
    }
}