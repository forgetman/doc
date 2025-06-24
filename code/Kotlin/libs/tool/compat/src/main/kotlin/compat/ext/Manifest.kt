package compat.ext

internal val String.Companion.READ_PRIVILEGED_PHONE_STATE: String
    get() = "android.permission.READ_PRIVILEGED_PHONE_STATE"

internal val String.Companion.CARRIER_PRIVILEGES: String
    get() = "carrier privileges"