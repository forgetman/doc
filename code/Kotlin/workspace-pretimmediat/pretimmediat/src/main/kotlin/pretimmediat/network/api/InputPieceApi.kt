package pretimmediat.network.api

import eth.annotation.method.Post
import eth.annotation.method.Upload
import eth.annotation.param.File
import eth.annotation.param.Header
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow
import pretimmediat.model.Region
import pretimmediat.model.inputpiece.BankCaptcha
import pretimmediat.model.inputpiece.BankPiece
import pretimmediat.model.inputpiece.CheckFirst
import pretimmediat.model.inputpiece.ContactPiece
import pretimmediat.model.inputpiece.IdCardImagePiece
import pretimmediat.model.inputpiece.IdCardPiece
import pretimmediat.model.inputpiece.InfoPiece
import pretimmediat.network.ParamsName

/**
 * 进件页Api
 */
interface InputPieceApi {

    companion object {
        const val PAGE_TYPE_INFO = 1
        const val PAGE_TYPE_EMERGENCY_CONTACT = 3
        const val PAGE_TYPE_ID_CARD = 4
        const val PAGE_TYPE_BANK = 5

        const val IMAGE_TYPE_FRONT = "00"
        const val IMAGE_TYPE_FACE = "02"
    }

    /**
     * 获取基本信息
     */
    @Post("/essay/payRecentChalk")
    fun info(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("singlePostageKneeFather") pageType: Int = PAGE_TYPE_INFO
    ): Flow<InfoPiece>

    /**
     * 上传基本信息
     */
    @Post("/essay/describeGreyBench")
    fun uploadInfo(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("plainNobodyNonYourSuchRag") familyName: String,
        @Query("suddenStick") givenName: String,
        @Query("arabicInternationalFleshStream") birthDay: String,
        @Query("anotherTortoiseCuriousCompanionFellow") sex: String,
        @Query("reasonableProductionMatterFriedDumpling") sexText: String,
        @Query("sharpKilometreRestaurant") email: String,
        @Query("palePatternPartTheatreRealDepartment") province: String?,
        @Query("briefTrickDeliciousCrewDeparture") provinceCode: String?,
        @Query("briefLuckyMethodGallery") city: String?,
        @Query("contentHerCuriousStomach") cityCode: String?,
        @Query("singlePostageKneeFather") pageType: Int = PAGE_TYPE_INFO
    ): Flow<String>

    /**
     * 联系人信息
     */
    @Post("/essay/payRecentChalk")
    fun contact(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("singlePostageKneeFather") pageType: Int = PAGE_TYPE_EMERGENCY_CONTACT
    ): Flow<ContactPiece>

    @Post("/essay/describeGreyBench")
    fun uploadContact(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("aggressiveSadnessDeafIllness") phoneNumber: String,
        @Query("swiftFactoryPossibleChineseMeans") name: String,
        @Query("thickCollege") rsName: String,
        @Query("fondInstrument") relationshipCode: String,
        @Query("naturalHarmfulNestInlandHong") pnSec: String,
        @Query("racialLectureMid") nameSec: String,
        @Query("unusualAuthorPyramidGovernment") rsSecName: String,
        @Query("excellentMarriagePolitics") rsSecCode: String,
        @Query("singlePostageKneeFather") pageType: Int = PAGE_TYPE_EMERGENCY_CONTACT
    ): Flow<String>

    /**
     * id信息
     */
    @Post("/essay/payRecentChalk")
    fun idCard(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("singlePostageKneeFather") pageType: Int = PAGE_TYPE_ID_CARD
    ): Flow<IdCardPiece>

    @Post("/essay/describeGreyBench")
    fun uploadIdCard(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("latestChipsProudHarbour") idNumber: String,
        @Query("singlePostageKneeFather") pageType: Int = PAGE_TYPE_ID_CARD
    ): Flow<String>

    @Post("/essay/dreamJapanesePlate")
    fun bank(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("singlePostageKneeFather") pageType: Int = PAGE_TYPE_BANK
    ): Flow<List<BankPiece>>

    /**
     * 首次保存银行信息
     */
    @Post("/essay/shapeTaxFile")
    fun uploadBank(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("literaryChangeableAdmissionKeyboard") bankAccountType: String,
        @Query("afraidPronunciationDigestFurnishedLondon") baNumber: String,
        @Query("moreBallBlueElectronicVegetable") smsCode: String? = null,
        @Query("arcticJobMainMixture") collectionType: String = "2",
        @Query("goodBirdSubtraction") orderType: String = "1",
        @Query("singlePostageKneeFather") pageType: Int = PAGE_TYPE_BANK
    ): Flow<String>

    @Post("/essay/forbidEnergeticCentre")
    fun checkFirst(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("darkMonkeyNecessaryFlesh") ogAppssid: String?,
        @Query("singlePostageKneeFather") pageType: Int
    ): Flow<CheckFirst>

    /**
     * 更新银行卡信息
     * @param orderFailedAddFlag 默认传1
     * @param orderId 订单ID
     */
    @Post("/essay/askRadioactiveNew")
    fun updateBank(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("literaryChangeableAdmissionKeyboard") bankAccountType: String,
        @Query("afraidPronunciationDigestFurnishedLondon") baNumber: String,
        @Query("extraordinaryPetThunderstormLateDusk") orderId: String,
        @Query("moreBallBlueElectronicVegetable") smsCode: String?,
        @Query("convenientRocketChineseFridge") orderFailedAddFlag: String = "1",
        @Query("arcticJobMainMixture") collectionType: String = "2",
        @Query("goodBirdSubtraction") orderType: String = "1",
        @Query("singlePostageKneeFather") pageType: Int = PAGE_TYPE_BANK
    ): Flow<String>

    /**
     * @param parentId 获取省默认值传-1, 获取市传省返回的regionId, 获取区传市返回的regionId
     * @param level 获取省默认值传1, 获取市默认值传2, 获取区默认值传3
     */
    @Post("/tightThirst/studyDearPocket")
    fun provinces(
        @Query("maleHungryAuthor") parentId: String = "-1",
        @Query("unsafeSquirrelDance") level: String = "1"
    ): Flow<List<Region>>

    @Post("/tightThirst/studyDearPocket")
    fun cities(
        @Query("maleHungryAuthor") parentId: String,
        @Query("unsafeSquirrelDance") level: String = "2"
    ): Flow<List<Region>>

    @Post("/essay/blockComfortableHomework")
    fun idImages(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("singlePostageKneeFather") pageType: Int = PAGE_TYPE_ID_CARD
    ): Flow<IdCardImagePiece>

    /**
     * @param type 00 正面 01 反面 02 人脸
     */
    @Upload("/essay/fetchTightBar")
    fun uploadImage(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("cubicHobbySweatYard") type: String,
        @File("activeNobodyConstantBuddhist") imagePath: String
    ): Flow<String>

    /**
     * 银行卡弹窗验证码
     */
    @Post("/nursing/learnCrowdedChildhood")
    fun bankConfirmCaptcha(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("swiftCleverSaleswomanGermanQuality") phoneNumber: String
    ): Flow<BankCaptcha>
}