package pretimmediat.ext

import pretimmediat.property.Properties
import vector.datastore.preference.sync
import vector.util.intent.IntentAction

fun openComplaintBrowser() {
    // 跳转浏览器, url写死(规则拼接)
    IntentAction.browser()
        .url("https://www.pretimmediatpi.com/ecowasComplaint/#/?token=${Properties.accountToken.sync().getOrNull()}&orderType=1")
        .launch()
}