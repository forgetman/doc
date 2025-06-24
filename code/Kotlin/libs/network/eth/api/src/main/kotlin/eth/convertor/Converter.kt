package eth.convertor

import eson.Eson
import eth.model.Response

/**
 *
 * @author : GuoXuan
 * @since : 2019/5/24
 */
interface Converter {

    @Throws(Throwable::class)
    fun <T> onResponse(response: Response, eson: Eson): T?

}