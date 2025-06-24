package pretimmediat.ext

import android.app.Activity
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import compat.network.NetworkCompat
import pretimmediat.R
import pretimmediat.activity.WebViewActivityCreator
import pretimmediat.manager.AccountManager
import pretimmediat.network.URL
import pretimmediat.property.Properties

fun Activity.startWebViewActivity(@StringRes titleId: Int, url: String) {
    if (NetworkCompat.isConnected(this)) {
        WebViewActivityCreator.create()
            .url(url)
            .titleId(titleId)
            .start(this)
    } else {
        showErrorDialog()
    }
}

fun Activity.startProtocolActivity() {
    startWebViewActivity(R.string.protocol_title, URL.PROTOCOL)
}

fun Fragment.startProtocolActivity() {
    activity?.startProtocolActivity()
}

fun Activity.startServiceActivity(flag: Int) {
    startWebViewActivity(R.string.me_custom_service, makeServiceUrl(flag))
}

private fun makeServiceUrl(flag: Int): String {
    return "https://www.pretimmediatpi.com/customer/index.html?frontSource=$flag&appSsid=288&userId=${AccountManager.account}&mobile=${Properties.accountPhoneNumber}"
}

fun Fragment.startServiceActivity(flag: Int) {
    activity?.startServiceActivity(flag)
}