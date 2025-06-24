package pretimmediat.ext

import android.view.View
import androidx.annotation.StringRes
import pretimmediat.R
import vector.app.appbar.AppBar
import vector.app.os.drawableRes

fun AppBar.addBackIcon(@StringRes textId: Int, action: (View) -> Unit) {
    left.addText {
        text = context.getString(textId)
        drawableResLeft = R.drawable.appbar_ic_back
        onClick = action
    }
}

fun AppBar.addBackIcon(text: String, action: (View) -> Unit) {
    left.addText {
        this.text = text
        drawableResLeft = R.drawable.appbar_ic_back
        onClick = action
    }
}

fun AppBar.addBackIcon(action: (View) -> Unit) {
    left.addIcon(R.drawable.appbar_ic_back.drawableRes) {
        action(it)
    }
}

fun AppBar.addServiceIcon(action: (View) -> Unit) {
    right.addIcon(R.drawable.appbar_ic_service.drawableRes, action)
}