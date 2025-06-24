package reader.def

import vector.app.os.Dimension
import vector.app.os.dp

enum class FontSize(val dp: Int) {
    LEVEL1(10),
    LEVEL2(11),
    LEVEL3(12),
    LEVEL4(13),
    LEVEL5(14),
    LEVEL6(15),
    LEVEL7(16),
    LEVEL8(17),
    LEVEL9(18),
    LEVEL10(19),
    LEVEL11(20);

    fun toDimension(): Dimension {
        return dp.dp
    }
}