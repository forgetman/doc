package catroom.def

/**
 * @author yuansui
 * @since 2024/7/6
 */
object Constants {
    object Ble {
        const val BYTE_LENGTH: Int = 14
        const val NUM_0 = 0
        const val NUM_1 = 1
        const val NUM_2 = 2
        const val NUM_3 = 3
        const val NUM_4 = 4
        const val NUM_5 = 5
        const val NUM_6 = 6
        const val NUM_7 = 7
        const val NUM_8 = 8
        const val NUM_9 = 9
        const val NUM_10 = 10
        const val NUM_11 = 11
        const val NUM_12 = 12
        const val NUM_13 = 13
        const val NUM_14 = 14
        const val BT_HEAD: Byte = 0XAA.toByte()
        const val BT_ADDRESS_NOT_USE: Byte = 0X00
        const val BT_ADDRESS_BLUETOOTH: Byte = 0X01
        const val BT_ADDRESS_BOARD_1: Byte = 0X02
        const val BT_SEND_LIGHT_OPEN: Byte = 0X01
        const val BT_ADDRESS_BOARD_2: Byte = 0X03
        const val BT_ADDRESS_DO_NOT_USE: Byte = 0XFF.toByte()
        const val BT_PROTOCOL_VERSION: Byte = 0X00
        const val BT_OPEN_LIGHT: Byte = 0x80.toByte()
        const val BT_FEED_CAT: Byte = 0x01.toByte()
        const val BT_AD: Byte = 0xAD.toByte()
        const val BT_AE: Byte = 0xAE.toByte()
        const val BT_AF: Byte = 0xAF.toByte()
        const val BT_1D: Byte = 0x1D.toByte()
        const val BT_64: Byte = 0x64.toByte()
        const val BT_2E: Byte = 0x2E.toByte()
        const val BT_FEED_CAT_FOOD: String = "feed_food"
        const val BT_FEED_CAT_FREEZE: String = "feed_freeze"
        const val BT_LIGHT_OPEN: String = "open_light"
        const val BT_LIGHT_CLOSE: String = "close_light"
    }

    object Room {
        const val QUANTITY_OF_ELECTRICITY: String = "quantity_of_electricity"
        const val LIGHT_STATUS: String = "light_status"
        const val AMBIENT_TYPE: String = "ambient_type"
        const val INDUCTION_TYPE: String = "induction_type"
        const val FOOD_CAT_TYPE: String = "food_cat_type"
        const val FOOD_FREEZE_TYPE: String = "food_freeze_type"
        const val CAT: String = "cat"
        const val FREEZE: String = "freeze"
        const val APP_VERSION: String = "app_version"
        const val SYSTEM_VERSION: String = "system_version"
    }
}