package sugar.ext

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

object SdkInt {
    /**
     * 5.0
     */
    const val L_21 = Build.VERSION_CODES.LOLLIPOP

    /**
     * 5.1
     */
    const val L1_22 = Build.VERSION_CODES.LOLLIPOP_MR1

    /**
     * 6.0
     */
    const val M_23 = Build.VERSION_CODES.M

    /**
     * 7.0
     */
    const val N_24 = Build.VERSION_CODES.N

    /**
     * 7.1
     */
    const val N1_25 = Build.VERSION_CODES.N_MR1

    /**
     * 8.0
     */
    const val O_26 = Build.VERSION_CODES.O

    /**
     * 8.1
     */
    const val O1_27 = Build.VERSION_CODES.O_MR1

    /**
     * 9.0
     */
    const val P_28 = Build.VERSION_CODES.P

    /**
     * 10.0
     */
    const val Q_29 = Build.VERSION_CODES.Q

    /**
     * 11.0
     */
    const val R_30 = Build.VERSION_CODES.R

    /**
     * 12.0
     */
    const val S_31 = Build.VERSION_CODES.S

    /**
     * 12.1
     */
    const val S2_32 = Build.VERSION_CODES.S_V2

    /**
     * 13.0
     */
    const val T_33 = Build.VERSION_CODES.TIRAMISU

    /**
     * 14
     */
    const val U_34 = Build.VERSION_CODES.UPSIDE_DOWN_CAKE

    /**
     * 15
     */
    const val V_35 = Build.VERSION_CODES.VANILLA_ICE_CREAM

    /**
     * 16
     */
    const val B_36 = Build.VERSION_CODES.BAKLAVA
}

/**
 * 判断sdk的最小版本
 */
@ChecksSdkIntAtLeast(parameter = 0)
fun isSdkAtLeast(api: Int): Boolean = Build.VERSION.SDK_INT >= api

/**
 * 判断sdk的最大版本
 */
fun isSdkAtMost(api: Int): Boolean = Build.VERSION.SDK_INT <= api

/**
 * 判断sdk不大于的版本
 */
fun isSdkLessThan(api: Int): Boolean = Build.VERSION.SDK_INT < api

@ChecksSdkIntAtLeast(parameter = 0, lambda = 1)
inline fun sdkFrom(api: Int, action: () -> Unit) {
    if (Build.VERSION.SDK_INT >= api) {
        action()
    }
}