package dsb.ext

import lib.base.Sp
import lib.base.model.User
import vector.ext.bufferString


fun String.withWebParams(): String =
    plus(bufferString {
        if (indexOf("?") != 0) {
            append("&")
        } else {
            append("?")
        }
//            append("city_id=${App.currCity?.id}")
        append("&mobile=${User.get().mobile}")
        append("&token=${Sp.getToken()}")
        append("&navbar=1")
    })