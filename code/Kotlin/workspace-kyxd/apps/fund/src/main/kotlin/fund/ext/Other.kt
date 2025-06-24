package fund.ext

import fund.Consts
import fund.design.ui.activity.WebViewActivityCreator
import vector.appContext

/**
 * @author yuansui
 * @since 2018/8/21
 */

fun String?.toWeb() {
    nativeToWeb(this ?: Consts.DEFAULT_URL)
}

fun String?.toWebWithoutLogin() {
    nativeToWebWithoutLogin(this ?: Consts.DEFAULT_URL)
}

private fun nativeToWeb(url: String) {
    // FIXME: 检查登录
    WebViewActivityCreator.create(url).start(appContext)
}

private fun nativeToWebWithoutLogin(url: String) {
    WebViewActivityCreator.create(url).start(appContext)
}