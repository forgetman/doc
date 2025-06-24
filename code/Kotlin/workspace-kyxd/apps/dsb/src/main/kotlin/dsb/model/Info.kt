package dsb.model

import com.google.gson.annotations.SerializedName
import vector.EMPTY
import vector.app.adapter.DiffItem

/**
 * @author yuansui
 * @since 2019/1/17
 */
class Info : DiffItem {
    var title: String = EMPTY
    var img: String = EMPTY

    @SerializedName("add_time")
    var addTime: String = EMPTY
    var url: String = EMPTY

    @SerializedName("read_number")
    var readNumber: Int = 0 // 63

    override fun areItemsTheSame(other: Any): Boolean {
        if (other !is Info) return false
        return title == other.title
    }

    override fun areContentsTheSame(other: Any): Boolean {
        if (other !is Info) return false
        return url == other.url
                && readNumber == other.readNumber
                && img == other.img
    }
}