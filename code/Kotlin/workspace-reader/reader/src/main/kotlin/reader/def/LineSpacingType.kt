package reader.def

import vector.app.os.Dimension
import vector.app.os.dp

enum class LineSpacingType(val dp: Int) {
    LEVEL1(15),
    LEVEL2(10),
    LEVEL3(5);

    fun toDimension(): Dimension {
        return dp.dp
    }
}