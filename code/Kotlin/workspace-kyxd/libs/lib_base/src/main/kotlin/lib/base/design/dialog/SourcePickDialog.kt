package lib.base.design.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import lib.base.databinding.DialogSourcePickBinding
import sugar.ext.NoArgBlock
import vector.app.databinding.dialog.DBDialogEx
import vector.bindingadapter.bind.Bind
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT

/**
 * @author yuansui
 * @since 2018/8/11
 */
class SourcePickDialog(context: Context?) : DBDialogEx(context) {

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, WRAP_CONTENT)

    var onCameraClick: NoArgBlock? = null
    var onAlbumClick: NoArgBlock? = null

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = DialogSourcePickBinding.inflate(layoutInflater)
        binding.owner = this
        return binding
    }

    override fun flowOfSetup() {
        setGravity(Gravity.BOTTOM)
        setDimAmount(0.5f)

        dismissOnTouchOutside(true)
    }

    val clickCamera = Bind.OnClick {
        onCameraClick?.invoke()
        dismiss()
    }

    val clickAlbum = Bind.OnClick {
        onAlbumClick?.invoke()
        dismiss()
    }

    val clickCancel = Bind.OnClick {
        dismiss()
    }
}