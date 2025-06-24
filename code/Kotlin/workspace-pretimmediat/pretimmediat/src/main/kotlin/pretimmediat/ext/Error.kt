package pretimmediat.ext

import pretimmediat.model.PieceUploadError

@Throws(PieceUploadError::class)
fun <T> T?.throwUploadError(type: PieceUploadError.Type): T {
    when {
        this == null -> throw PieceUploadError(type)
        this is CharSequence -> {
            val trim = this.trim()
            if (trim.isEmpty()) {
                throw PieceUploadError(type)
            } else {
                @Suppress("UNCHECKED_CAST")
                return trim as T
            }
        }

        else -> return this
    }
}