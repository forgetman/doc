package vector.ext

fun Char.isChinese(): Boolean {
    return this.code in 0x4E00..0x9FA5 // 根据字节码判断
}