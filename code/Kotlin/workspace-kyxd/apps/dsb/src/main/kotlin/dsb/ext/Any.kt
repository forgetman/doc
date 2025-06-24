package dsb.ext

import lib.base.Sp
import lib.sy.SY
import logger.L

fun checkSignIn(): Boolean {
    return if (!Sp.isSignIn()) {
        SY.startLogin {
            L.www("token = $it")
        }
//        Launcher.startActivity(SignInActivity::class)
        false
    } else true
}