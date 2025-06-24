package eth.model

import java.io.IOException

data class Error(val code: String, val message: String?, var e: Throwable? = null)

class EthException(val code: String, message: String? = "", cause: Throwable? = null) :
    IOException(message, cause)