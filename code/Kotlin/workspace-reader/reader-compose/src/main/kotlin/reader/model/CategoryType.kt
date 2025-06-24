package reader.model

enum class CategoryType(val id: String, val desc: String) {
    FANTASY("1", "玄幻奇幻"), // 幻想-奇幻
    KNIGHT("2", "武侠仙侠"), // 骑士-侠客
    ROMANCE("3", "都市言情"), // 浪漫-言情
    MILITARY("4", "历史军事"), // 浪漫-言情
    SCIENCE_FICTION("5", "科幻灵异"), // 浪漫-言情
    ONLINE("6", "网游竞技"), // 浪漫-言情
}