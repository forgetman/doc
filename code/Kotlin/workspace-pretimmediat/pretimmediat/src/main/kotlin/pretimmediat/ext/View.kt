package pretimmediat.ext

import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import vector.app.ext.view.doOnApplyWindowInsets

fun View.adaptImeResizeChanged() {
    doOnApplyWindowInsets { v, insets, _ ->
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        // 计算键盘高度重新设置视图的布局参数
        v.updatePadding(bottom = ime.bottom)
        insets
    }
}