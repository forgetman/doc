package pretimmediat.def

object Constants {

    object Bus {
        const val HOME_REFRESH_BY_APPLY = 1 // 刷新首页订单状态

        const val FORCE_UPDATE_DIALOG = 11 // 强制更新弹窗
        const val UPDATE_DIALOG = 12 // 更新弹窗
    }

    /**
     * 客服页面标识
     */
    object ServiceFlag {
        const val MAIN_UN_LOGIN = 1 // 首页未登录
        const val MAIN_LOGGED_IN = 2 // 单产品-首页已登录
        const val MAIN_MULTI_LOGGED_IN = 3 // 多产品-首页已登录
        const val ME = 4 // 我的页面
        const val PIECE_INFO = 5 // 进件页-信息
        const val PIECE_CONTACT = 6 // 进件页-联系人
        const val PIECE_ID = 7 // 进件页-ID
        const val PIECE_BANK = 8 // 进件页-银行
        const val TRIAL_CALC = 10 // 试算页
        const val MAIN_SINGLE_IN_REVIEW = 11 // 单产品-审核中
        const val MAIN_SINGLE_PROCESSING = 12 // 单产品-放款中
        const val MAIN_SINGLE_PAY_FAILED = 13 // 单产品-放款失败
        const val MAIN_SINGLE_REJECT = 14 // 单产品-审核拒绝
        const val PIECE_BANK_UPDATE = 15 // 进件页-银行-更新
        const val MAIN_SINGLE_REPAYING = 16 // 单产品-还款中
        const val MAIN_SINGLE_OVERDUE = 17 // 单产品-逾期
        const val ORDER = 18 // 订单页
    }

    object Extra {
        const val PHOTO_URI = "photo_uri"
    }
}