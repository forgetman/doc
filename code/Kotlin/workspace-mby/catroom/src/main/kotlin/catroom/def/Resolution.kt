package catroom.def

enum class Resolution(val width: Int, val height: Int) {
    P240(320, 240),
    P480(640, 480),
    P720(1280, 720),
    P1080(1920, 1080),
}