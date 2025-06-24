package lib.base

import android.graphics.Color
import java.util.*

object Constants {

    val KNumTensDivider = 10
    val KNumThousandsDivider = 1000
    val KNumHundredsDivider = 100

    // 农历范围
    val KLunarEndFirst = 1901
    val KLunarEndLast = 2100

    val KMonthCount = 12
    val KMonthMaxIndex = 11
    val KMonthMinIndex = 0
    val KUsingMonthCount = 3
    val KMaxDayCount = 31

    val KStartMonth = Calendar.JANUARY
    val KEndMonth = Calendar.DECEMBER

    val KWeekRowOneLineCount = 6 // 并列周数为1时画6行
    val KWeekRowTwoLineCount = 3 // 并列周数2时画3行
    val KWeekRowOneDayCount = 7
    val KWeekRowTwoDayCount = 14

    val KYearWheel = 12 // 12年为一周期

    val KIntervalBetweenDateAndCal = 1900

    val KFadeAniDuration = 250
    val KPushAniDuration = 250

    val KNotScale = 1.0f

    val KWeekPrefix = "week_desc"

    /**
     * Activity
     */
    val KScaleActHeight = 0.85
    val KScaleActWidth = 0.98

    /**
     * Animation
     */

    val KRedrawInterval = 30
    val KRedrawMsg = 1
    val KDelayChangeBgMsg = 2
    val KDelayMilliSecond = 50

    /**
     * Intent
     */
    val KIntendStopAni = "stopAni"

    /**
     * DayInfos
     */
    val KWeekDayDefPrefix = "星期"
    val KWeekDefStr = "周"
    val KYearDefStr = "年"
    val KMonthDefStr = "月"
    val KDayDefStr = "日"
    val KDateSplit = "-"
    val KOrderPrefix = "第"
    val KLunarStr = "农历"

    /**
     * Custom color, 只有桌面使用
     */
    val KSolarRed = 184
    val KSolarGreen = 221
    val KSolarBlue = 122
    val KFestivalRed = 252
    val KFestivalGreen = 142
    val KFestivalBlue = 130

    // 节气颜色
    val KSolarTermColor = Color.rgb(KSolarRed, KSolarGreen, KSolarBlue)

    // 节日颜色
    val KFestivalColor = Color.rgb(KFestivalRed, KFestivalGreen, KFestivalBlue)

    /**
     * Intent extra key
     */
    val KExtraKeyYear = "year"
    val KExtraKeyOrgMonth = "orgMonth"

    /**
     * 操作相关
     */
    val KMinSwitchYearDis = 50
    val KMinPinchDis = 100f
    val KDragYScale = 1.2f
    val KFlingMinDis = 30
    val KFlingMinVelocity = 200
    val KMinCanDragDis = 30
    val KMinSwitchCityDis = 50
    val KMinSwitchPageDis = 70

    val KFakeImei = "35278404110901162"

    /**
     * Url info
     */
    val KAndroidMarketPackageName = "com.android.vending"
    val KUrlHttp = "http://"

    /**
     * Skin installer
     */
    val ENCODING_8859_1 = "8859_1"
    val ENCODING_GB2312 = "GB2312"
    val KDefaultSkinPath = "DefaultSkinPath"
    val KDefaultSkinName = "默认皮肤"

    // Finish msg
    //	int KSkinInstallSucceed = 1;
    //	int KSkinInstallFailed = 0;
    val KFinishReloadSkin = 10


    // 并列周数
    val KWeekRowCountOne = 1
    val KWeekRowCountTwo = 2

    // package receiver
    val KSystemPackage = "package:"


    val KIntentWidgetStyle = "WidgetStyle"

    // About Skin unzip
    val KSkinUnzipDateFormat = "MMddHHmmss"
    val KSkinFolderDateSplit = "_"
    val KZipping = 0
    val KZipSucceed = 1
    val KZipFailed = -1

    // Intent
    val KIntentSkinDownloadFinish = "SkinDownloadFinish"
    val KIntentActivateSv = 102
}