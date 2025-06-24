package vector.app.databinding.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import vector.app.databinding.BindingInflater
import vector.app.databinding.R
import vector.app.dialog.DialogEx

/**
 * 拥有完整生命周期管理的dialog
 */
abstract class DBDialogEx(context: Context?, themeId: Int) : DialogEx(context, themeId), BindingInflater {

    /**
     * 二段构造显式声明, 为了子类重写的时候能根据提示选择只有一个[context]参数的构造方法
     */
    constructor(context: Context?) : this(context, R.style.Theme_Dialog)

    final override fun createContentView(inflater: LayoutInflater): View {
        val binding = createBinding(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }
}