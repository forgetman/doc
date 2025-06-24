package test

import vector.util.Dir

val Dir.downloadCacheDir: String
    get() = mkCacheDir("download")
