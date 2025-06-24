package pretimmediat.repo

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import pretimmediat.ext.ensureUserIdFlow
import pretimmediat.model.AppConfig
import pretimmediat.model.inputpiece.BankPiece
import pretimmediat.network.api.GlobalApi
import pretimmediat.network.api.InputPieceApi
import pretimmediat.network.createApi
import pretimmediat.network.createApiWithoutGlobal
import javax.inject.Inject

@ViewModelScoped
class InputPieceRepo @Inject constructor() {
    fun fetchBasic(userId: String?, ssid: String?) = ensureUserIdFlow(userId, ssid) {
        createApi<InputPieceApi>().info(userId, ssid)
    }.flowOn(Dispatchers.IO)

    fun fetchContact(userId: String?, ssid: String?) = ensureUserIdFlow(userId, ssid) {
        createApi<InputPieceApi>().contact(userId, ssid)
    }.flowOn(Dispatchers.IO)

    fun fetchIdCard(userId: String?, ssid: String?) = ensureUserIdFlow(userId, ssid) {
        createApi<InputPieceApi>().idCard(userId, ssid)
    }.flowOn(Dispatchers.IO)

    fun fetchBank(userId: String?, ssid: String?): Flow<List<BankPiece>> =
        ensureUserIdFlow(userId, ssid) {
            createApi<InputPieceApi>().bank(userId, ssid)
        }.flowOn(Dispatchers.IO)

    fun updateBasic(
        userId: String?,
        ssid: String?,
        familyName: String,
        givenName: String,
        birthDay: String,
        sex: String,
        sexText: String,
        email: String,
        province: String?,
        provinceCode: String?,
        city: String?,
        cityCode: String?,
    ): Flow<String> = ensureUserIdFlow(userId, ssid) {
        createApi<InputPieceApi>().uploadInfo(
            userId,
            ssid,
            familyName,
            givenName,
            birthDay,
            sex,
            sexText,
            email,
            province,
            provinceCode,
            city,
            cityCode
        )
    }.flowOn(Dispatchers.IO)

    fun uploadContact(
        userId: String?,
        ssid: String?,
        phoneNumber: String,
        name: String,
        rsName: String,
        relationshipCode: String,
        pnSec: String,
        nameSec: String,
        rsSecName: String,
        rsSecCode: String
    ) = ensureUserIdFlow(userId, ssid) {
        createApi<InputPieceApi>().uploadContact(
            userId,
            ssid,
            phoneNumber,
            name,
            rsName,
            relationshipCode,
            pnSec,
            nameSec,
            rsSecName,
            rsSecCode
        )
    }.flowOn(Dispatchers.IO)

    fun uploadIdCard(
        userId: String?,
        ssid: String?,
        idNumber: String
    ) = ensureUserIdFlow(userId, ssid) {
        createApi<InputPieceApi>().uploadIdCard(
            userId,
            ssid,
            idNumber
        )
    }.flowOn(Dispatchers.IO)

    fun uploadBank(
        userId: String?,
        ssid: String?,
        bankType: String,
        bankAccount: String,
        smsCode: String?
    ) = ensureUserIdFlow(userId, ssid) {
        createApi<InputPieceApi>().uploadBank(
            userId,
            ssid,
            bankType,
            bankAccount,
            smsCode
        )
    }.flowOn(Dispatchers.IO)

    fun updateBank(
        userId: String?,
        ssid: String?,
        bankType: String,
        bankAccount: String,
        orderId: String,
        smsCode: String?
    ) = ensureUserIdFlow(userId, ssid) {
        createApi<InputPieceApi>().updateBank(
            userId,
            ssid,
            bankType,
            bankAccount,
            orderId,
            smsCode
        )
    }.flowOn(Dispatchers.IO)

    fun fetchIdImages(userId: String?, ssid: String?) = ensureUserIdFlow(userId, ssid) {
        createApi<InputPieceApi>().idImages(userId, ssid)
    }.flowOn(Dispatchers.IO)

    fun fetchProvince() = createApi<InputPieceApi>().provinces().flowOn(Dispatchers.IO)
    fun fetchCities(provinceId: String) =
        createApi<InputPieceApi>().cities(provinceId).flowOn(Dispatchers.IO)

    fun fetchSexSelection() =
        createApi<GlobalApi>().appConfig(AppConfig.Map.SEX).flowOn(Dispatchers.IO)

    fun fetchRelationship() =
        createApi<GlobalApi>().appConfig(AppConfig.Map.RELATIONSHIP).flowOn(Dispatchers.IO)

    fun fetchSecRelationship() =
        createApi<GlobalApi>().appConfig(AppConfig.Map.SEC_RELATIONSHIP).flowOn(Dispatchers.IO)

    fun fetchBankType() =
        createApi<GlobalApi>().appConfig(AppConfig.Map.BANK_ACCOUNT_TYPE).flowOn(Dispatchers.IO)

    fun uploadBigJson(userId: String?, ssid: String?, json: String) =
        ensureUserIdFlow(userId, ssid) {
            createApiWithoutGlobal<GlobalApi>().uploadBigJson(userId, ssid, json)
                .flowOn(Dispatchers.IO)
        }

    fun uploadIdCardFront(userId: String?, ssid: String?, path: String) =
        ensureUserIdFlow(userId, ssid) {
            createApi<InputPieceApi>().uploadImage(
                userId,
                ssid,
                InputPieceApi.IMAGE_TYPE_FRONT,
                path
            )
        }.flowOn(Dispatchers.IO)

    fun uploadFace(userId: String?, ssid: String?, path: String) = ensureUserIdFlow(userId, ssid) {
        createApi<InputPieceApi>().uploadImage(
            userId,
            ssid,
            InputPieceApi.IMAGE_TYPE_FACE,
            path
        )
    }.flowOn(Dispatchers.IO)
}