package dsb

import bus.flow.FlowBus

object EventId {
    const val SIGN_IN = 1
    const val LAUNCH_WEB = 2
    const val PASS_NEW_TIP = 3
    const val SIGN_OUT = 4 // 退出登录
    const val LOGOUT = 5 // 注销
    const val POPUP_SERVICE_PROGRESS = 6 // 弹出服务进度对话框

    const val LOCATION = 10
    const val LOCATION_CITY = 11

    const val CHANGE_CITY = 12
}

class Bus private constructor() : FlowBus() {

    companion object {
        private var bus: Bus? = null
            get() {
                if (field == null) field = Bus()
                return field
            }

        fun get(): Bus {
            return bus ?: Bus()
        }

        fun close() {
            bus?.release()
            bus = null
        }
    }
}