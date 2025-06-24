package dsb.model

import com.google.gson.annotations.SerializedName

/**
 * @author yuansui
 * @since 2019/2/21
 */
class AppUpdate {
    var title: String? = null

    @SerializedName("update_notes")
    var notes: String? = null
}