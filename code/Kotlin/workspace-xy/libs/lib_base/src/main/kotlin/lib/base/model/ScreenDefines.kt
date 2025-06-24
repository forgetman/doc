package lib.base.model

enum class ScreenType {
    T_NONE,
    T_DEFAULT,

    // low
    T_240X320,
    T_240X400,

    // medium
    T_320X480,

    // high
    T_480X800,
    T_480X854,
    T_540X960,
    T_600X1024,

    // xhigh
    T_640X960,
    T_720X1184,
    T_720X1280,
    T_800X1232,
    T_800X1205, // Nexus 7 pad
    T_800X1280
}

object ScreenDefines {


    fun getScreeType(width: Int, height: Int): ScreenType {
        // 给一个默认的分辨率
        return ScreenType.T_DEFAULT
    }

    /**
     * 匹配宽高
     * @return
     */
    private fun matchWH(inW: Int, inH: Int, matchW: Int, matchH: Int): Boolean {
        return inW == matchW && inH == matchH || inW == matchH && inH == matchW
    }
}
