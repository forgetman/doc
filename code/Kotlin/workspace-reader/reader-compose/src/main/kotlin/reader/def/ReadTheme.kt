package reader.def

import androidx.core.graphics.toColorInt

enum class ReadTheme(
    val title: String,
    val text: String,
    val background: String
) {
    THEME1("#8d8e93", "#0a0b13", "#ffffff"),
    THEME2("#8d8e93", "#0a0b13", "#f7f0de"),
    THEME3("#8d8e93", "#0a0b13", "#cdefce"),
    THEME4("#979797", "#8d8e93", "#000000");

    fun titleColor(): Int = title.toColorInt()
    fun textColor(): Int = text.toColorInt()
    fun backgroundColor(): Int = background.toColorInt()
}