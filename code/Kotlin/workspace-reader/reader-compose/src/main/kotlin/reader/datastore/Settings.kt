package reader.datastore

import reader.def.FontSize
import reader.def.FontType
import reader.def.LineSpacingType
import reader.def.ReadTheme
import vector.datastore.DataStoreOwner
import vector.datastore.preference.booleanPreference
import vector.datastore.preference.enumPreference
import vector.ext.DayNightMode

object Settings : DataStoreOwner by DataStoreOwner("settings") {
    val fontSize by enumPreference(FontSize.LEVEL4)
    val lineSpacingType by enumPreference(LineSpacingType.LEVEL1)
    val fontType by enumPreference(FontType.DEFAULT)
    val readTheme by enumPreference(ReadTheme.THEME1)
//    val shelfStyle by enumPreference(ShelfLayoutStyle.GRID)

    // TODO: 暂时关闭夜间模式, 等待夜间模式的皮肤设计完成
    val dayNightMode by enumPreference(DayNightMode.DAY)

    val clickLeftToNextPage by booleanPreference(false)
    val brightnessAdjustable by booleanPreference(false)
}