package pretimmediat.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import pretimmediat.R
import pretimmediat.widget.picker.PickerView
import vector.app.dialog.DialogEx
import vector.app.ext.bind.bindView
import vector.app.ext.view.setOnDebounceClickListener
import vector.ext.inflate
import vector.app.os.dp
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT

/**
 * 表单选择对话框
 */
class FormInfoSelectorDialog(
    context: Context?,
    @StringRes private val titleId: Int,
    private val contents: List<String>,
    private val initialIndex: Int = -1,
    private val autoDismiss: Boolean = true,
    private val listener: OnItemSelectedListener
) : DialogEx(context) {

    fun interface OnItemSelectedListener {
        fun callback(dialog: FormInfoSelectorDialog, index: Int, text: String)
    }

    private val tvTitle by bindView<TextView>(R.id.tv_title)
    private val tvConfirm by bindView<View>(R.id.tv_confirm)
    private val tvCancel by bindView<TextView>(R.id.tv_cancel)
    private val ivClose by bindView<View>(R.id.iv_close)
    private val layoutContents by bindView<PickerView>(R.id.layout_contents)

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)

    override val gravity: Int
        get() = Gravity.BOTTOM

    override val marginBottom: Int
        get() = 10.dp.toPx(context)

    override fun createContentView(inflater: LayoutInflater): View {
        return inflater.inflate(R.layout.dialog_form_info_selector)
    }

    override fun initializeContentView() {
        tvTitle.setText(titleId)

        ivClose.setOnDebounceClickListener {
            dismiss()
        }
        tvCancel.setOnDebounceClickListener {
            dismiss()
        }
        tvConfirm.setOnDebounceClickListener {
            listener.callback(this, layoutContents.selectedIndex, contents[layoutContents.selectedIndex])
            if (autoDismiss) dismiss()
        }

        layoutContents.apply {
            setDataList(contents)
            setCanScrollLoop(false)
            if (initialIndex != -1) {
                setSelected(initialIndex)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        layoutContents.onDestroy()
    }
}