package pretimmediat.network.api

import eth.annotation.method.Post
import eth.annotation.param.Header
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow
import pretimmediat.network.ParamsName

/**
 * 统计接口
 */
interface StatsApi {

    /**
     * @param eventName 事件名称
     * @param lbs 纬度+","+经度
     * @param equipmentBrand 手机厂商
     * @param equipmentType 手机型号
     * @param imei deviceId
     * @param gaid google aid
     * @param uuid deviceId
     * @param osVersion 操作系统版本
     * @param userAgent 用户代理
     * @param mobileLanguage 手机语言
     * @param gpsInfo 同lbs
     * @param versionCode 版本号
     * @param versionName 版本名
     */
    @Post("tightThirst/divideTaxChemistry")
    fun public(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("plainListUnfortunateFather") eventName: String,
        @Query("lazyBathtubForeignRecord") lbs: String,
        @Query("loudSpeechPoisonousLife") equipmentBrand: String,
        @Query("enoughDisappointmentFrogUnusualMouthful") equipmentType: String,
        @Query("gentleFingernail") imei: String,
        @Query("lemonAnotherNonBasket") gaid: String,
        @Query("greatPunctuationUnusualSchool") uuid: String,
        @Query("dustyDirtEvening") osVersion: String,
        @Query("reasonablePianistFright") userAgent: String,
        @Query("eitherPoorGloveDeparture") mobileLanguage: String,
        @Query("ashamedRingCommunistForestSingleEvening") gpsInfo: String,
        @Query("smokeCustomerDiscountMercifulBeast") versionCode: String,
        @Query("electricAdventureCarefulFairNeighbour") versionName: String,
    ): Flow<String>

    /**
     * @param eventName 事件名称
     * @param lbs 纬度+","+经度
     * @param equipmentBrand 手机厂商
     * @param equipmentType 手机型号
     * @param imei deviceId
     * @param gaid google aid
     * @param uuid deviceId
     * @param osVersion 操作系统版本
     * @param userAgent 用户代理
     * @param mobileLanguage 手机语言
     * @param gpsInfo 同lbs
     * @param versionCode 版本号
     * @param versionName 版本名
     * @param appSsid 盘号
     * @param custInfoId 用户id
     */
    @Post("tightThirst/exciteArabHimself")
    fun risk(
        @Header(ParamsName.USER_ID) userId: String?,
        @Header(ParamsName.APP_SSID_1) ssid: String?,
        @Query("plainListUnfortunateFather") eventName: String,
        @Query("lazyBathtubForeignRecord") lbs: String,
        @Query("loudSpeechPoisonousLife") equipmentBrand: String,
        @Query("enoughDisappointmentFrogUnusualMouthful") equipmentType: String,
        @Query("gentleFingernail") imei: String,
        @Query("lemonAnotherNonBasket") gaid: String,
        @Query("greatPunctuationUnusualSchool") uuid: String,
        @Query("dustyDirtEvening") osVersion: String,
        @Query("reasonablePianistFright") userAgent: String,
        @Query("eitherPoorGloveDeparture") mobileLanguage: String,
        @Query("ashamedRingCommunistForestSingleEvening") gpsInfo: String,
        @Query("smokeCustomerDiscountMercifulBeast") versionCode: String,
        @Query("electricAdventureCarefulFairNeighbour") versionName: String,
        @Query("familiarShabbyBattlePower") custInfoId: String,
        @Query("flatSuite") appSsid: String = "288",
    ): Flow<String>
}