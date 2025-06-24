package compat.context.def

enum class ReceiverFlags {
    /**
     * 映射[android.content.Context.RECEIVER_VISIBLE_TO_INSTANT_APPS], minSdk = 26
     */
    RECEIVER_VISIBLE_TO_INSTANT_APPS,

    /**
     * 映射[android.content.Context.RECEIVER_EXPORTED], minSdk = 33
     */
    RECEIVER_EXPORTED,

    /**
     * 映射[android.content.Context.RECEIVER_NOT_EXPORTED], minSdk = 33
     */
    RECEIVER_NOT_EXPORTED
}