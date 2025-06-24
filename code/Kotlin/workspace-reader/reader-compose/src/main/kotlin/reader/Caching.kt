package reader

import vector.util.Dir

val Dir.image: String
    get() = mkCacheDir("bmp")
