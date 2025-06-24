package pretimmediat.model.product

import com.google.gson.annotations.SerializedName

/**
 * 还款支付信息
 * @param browserOpen 为1时浏览器打开,其他打开app内webView
 */
class PayInfo(
    @SerializedName("northLegUnhappySoutheast")
    val url: String,
    @SerializedName("centralClinicAnotherDriverRedCrop")
    val browserOpen: String,
) {
    val shouldUseBrowser
        get() = browserOpen == "1"
}