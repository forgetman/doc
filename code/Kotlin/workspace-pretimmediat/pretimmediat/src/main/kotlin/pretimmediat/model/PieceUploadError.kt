package pretimmediat.model

/**
 * 进件页数据上传错误类型
 */
class PieceUploadError(val type: Type) : Exception(type.text) {
    enum class Type(val text: String = "Veuillez compléter toutes les informations") {
        FAMILY_NAME,
        GIVEN_NAME,
        BIRTH,
        SEX,
        EMAIL,
        PROVINCE,
        CITY,
        FIRST_RELATIONSHIP,
        FIRST_PHONE_NUMBER,
        FIRST_NAME,
        SEC_RELATIONSHIP,
        SEC_PHONE_NUMBER,
        SEC_NAME,
        ID_CARD,
        ID_NUMBER,
        FACE,
        BANK_TYPE,
        BANK_ACCOUNT,
        BANK_ACCOUNT_CONFIRM("Veuillez remplir l'information du compte uniformément"),
    }
}