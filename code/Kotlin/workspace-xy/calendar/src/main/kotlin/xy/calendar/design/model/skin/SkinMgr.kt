package xy.calendar.design.model.skin


import vector.app.util.Res
import xy.calendar.R

object SkinMgr {
    val KSkinNamePortrait = Res.getString(R.string.setting_skin_default)
    const val KSkinEngineVersion = 1

    /**
     * SkinName
     */
    var skinName = ""

    /**
     * SkinAuthor
     */
    var skinAuthor = ""

    /**
     * SkinDesp
     */
    var skinDesp = ""

    /**
     * SkinVersion
     */
    var skinVersion = ""

    var year = YearSkin()
    var day = DaySkin()
    var month = MonthSkin()
    var clock = ClockSkin()
}
