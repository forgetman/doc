package compat.network.def

enum class WifiState {
    IDLE, // 开启中或关闭中
    ENABLED,
    DISABLED,
    ERROR, // 关闭过程中出现异常或没有WIFI模块
}