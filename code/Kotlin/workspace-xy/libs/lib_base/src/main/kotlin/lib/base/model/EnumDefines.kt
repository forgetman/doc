package lib.base.model

/**
 * Workspace共同属性
 */
enum class TMoveDirection {
    EMoveNone,
    EMoveLeft,
    EMoveRight,
    EMoveUp,
    EMoveDown
}

enum class TDrawState {
    EDrawAll,
    EDrawDragging,
    EDrawForwardScrolling,

    //		EDrawReturnScrolling,
    EDrawSwitchYear,
    EDrawClear,
    EDrawCoverSettingIcon
}

enum class TDragDirection {
    EDragNone,
    EDragHorizontal,
    EDragVertical
}

enum class TGestureType {
    ENone,
    ESingleMove, // 单指滑动
    EMultiplePrepare, // 双指捏合
    EMultipleUp,
    EMultipleDown
}

/**
 * Workspace共同属性
 */
// 滑动前景效果
enum class TScrollAnimType {
    EGradient, // 淡入淡出
    EStack
    // 层叠
}

// 月图年切换效果
enum class TSwitchYearAnimType {
    EGradient, // 渐变
    EMove, // 平移
    EStack // 层叠
}

enum class TTouchRegion {
    ETouchNone,
    ETouchDate,
    ETouchYear
}

enum class FirstDayType {
    MONDAY,
    SUNDAY
}
