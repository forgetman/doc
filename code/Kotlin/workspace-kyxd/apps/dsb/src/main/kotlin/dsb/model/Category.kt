package dsb.model

import com.google.gson.annotations.SerializedName

/**
 * @author yuansui
 * @since 2019/1/24
 */
class Category {
    @SerializedName("category_id")
    var id: Int = 0

    @SerializedName("category_name")
    var name: String? = null
}