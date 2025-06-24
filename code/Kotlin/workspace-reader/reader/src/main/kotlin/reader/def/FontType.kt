package reader.def

enum class FontType(val desc: String, val path: String?) {
    DEFAULT("默认", null),
    YC_QH("羿创-旗黑", "font/YC_QH.ttf"), // 羿创-旗黑
    FZ_KTJ("方正-卡通简", "font/FZ_KTJ.ttf"), // 方正-卡通简
    FZSJ_OXKAJ("方正手迹-小可爱简", "font/FZSJ_OXKAJ.ttf"), // 方正手迹-小可爱简
}