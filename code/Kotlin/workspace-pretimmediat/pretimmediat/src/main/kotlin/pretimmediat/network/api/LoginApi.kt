package pretimmediat.network.api

import eth.annotation.BooleanHeaders
import eth.annotation.BooleanMap
import eth.annotation.method.Post
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow
import pretimmediat.model.AccountInfo
import pretimmediat.model.Captcha
import pretimmediat.network.ParamsName
import pretimmediat.property.Properties
import vector.datastore.preference.sync

interface LoginApi {

    @Post("/nursing/remainPassiveGeneration")
    @BooleanHeaders(
        BooleanMap(ParamsName.V_FLAG_1, true)
    )
    fun captcha(@Query("swiftCleverSaleswomanGermanQuality") phoneNo: String): Flow<Captcha>

    @Post("/nursing/imagineThoseGym")
    @BooleanHeaders(
        BooleanMap(ParamsName.V_FLAG_1, true)
    )
    fun login(
        @Query("swiftCleverSaleswomanGermanQuality") phoneNo: String,
        @Query("moreBallBlueElectronicVegetable") code: String,
        @Query("lemonAnotherNonBasket") gaid: String = Properties.gaid.sync().get(),
        @Query("properCheerIndeedNationality") appInstanceId: String = Properties.appInstanceId.sync().get(),
        @Query("blindGraduationPersonalBlankArm") afId: String = Properties.afId.sync().get(),
    ): Flow<AccountInfo>
}