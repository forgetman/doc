package lib.baidu

/**
 * @author yuansui
 * @since 2019/1/23
 */
class Gps {
    var longitude: String? = null
    var latitude: String? = null

    var province: String? = null
    var city: String? = null
    var district: String? = null
}

class LocationResult {
    var success: Boolean = false
    var gps: Gps? = null
}