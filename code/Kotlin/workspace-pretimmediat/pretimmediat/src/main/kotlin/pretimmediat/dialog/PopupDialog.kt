package pretimmediat.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import pretimmediat.R
import pretimmediat.adapter.PopupDialogItemBinder
import pretimmediat.databinding.DialogPopupBinding
import vector.app.databinding.dialog.DBDialogEx
import vector.app.os.dimenRes
import vector.bindingadapter.bind.Bind
import vector.app.os.dp
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT
import vector.widget.databinding.scrollable.ScrollableBind
import vector.widget.scrollable.decoration.Decoration

class PopupDialog(
    context: Context?,
    val data: List<String>,
    private val onClick: (index: Int, data: String) -> Unit
) : DBDialogEx(context) {

    val decoration = Decoration.linear {
        size = 1.dp.toPx(context)
        marginStart = 28.dp.toPx(context)
        marginEnd = 28.dp.toPx(context)
        drawBottom = false
    }
    val binder = PopupDialogItemBinder()

    override val marginStart: Int
        get() = R.dimen.margin_horizontal.dimenRes.toPx(context)

    override val marginEnd: Int
        get() = R.dimen.margin_horizontal.dimenRes.toPx(context)

    override val marginBottom: Int
        get() = 9.dp.toPx(context)

    override val gravity: Int
        get() = Gravity.BOTTOM

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)

    val onCloseClick = Bind.OnClick {
        dismiss()
    }

    val onItemClick = ScrollableBind.List.OnItemClick { _, index ->
        onClick(index, data[index])
        dismiss()
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return DialogPopupBinding.inflate(inflater).apply {
            owner = this@PopupDialog
        }
    }
}