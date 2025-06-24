package pretimmediat.model.inputpiece

import com.google.gson.annotations.SerializedName

/**
 * 进件页: 银行
 *
 * @param collectionType 收款类型 "1"---银行卡, "2"---钱包.
 * @param bankAccountTypeCode collectionType="1", 数据字典bankAccountType对应的code
 * @param baTypeDesc collectionType="1", 账户类型, Account Type, Tipo de cuenta
 * @param baNumber collectionType="1", 银行账户, Bank account, Cuenta bancaria. collectionType="2", 电子钱包账号, Wallet account, Cuenta de Billetera Electrónica
 */
data class BankPiece(
    @SerializedName("arcticJobMainMixture")
    val collectionType: String?,
    @SerializedName("literaryChangeableAdmissionKeyboard")
    val bankAccountTypeCode: String?,
    @SerializedName("juniorAsianRelativeBadCarpet")
    val baTypeDesc: String?,
    @SerializedName("afraidPronunciationDigestFurnishedLondon")
    val baNumber: String?,
)