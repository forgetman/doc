package dsb.design.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import dsb.databinding.DialogServiceProgressBinding
import dsb.design.ui.adapter.ServiceProgressAdapter
import dsb.model.GroupServProgress
import dsb.model.ServProgress
import live.Live
import vector.app.databinding.dialog.DBDialogEx
import vector.bindingadapter.bind.Bind
import vector.app.os.dp
import vector.util.LayoutParamsFactory

/**
 * 服务进度
 * @author yuansui
 * @since 2020-06-23
 */
class ServiceProgressDialog(context: Context?) : DBDialogEx(context) {

    val data = Live<List<GroupServProgress>>()
    val adapter = ServiceProgressAdapter()

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(304.dp.toPx(context), 520.dp.toPx(context))

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        val binding = DialogServiceProgressBinding.inflate(layoutInflater)
        binding.owner = this
        return binding
    }

    override fun flowOfSetup() {
        dismissOnTouchOutside(true)

        val list = mutableListOf<GroupServProgress>()

        val g = GroupServProgress()
        g.addChild(ServProgress())
        g.addChild(ServProgress())
        g.addChild(ServProgress())
        g.addChild(ServProgress())
        g.addChild(ServProgress())
        list.add(g)
        list.add(g)
        list.add(g)

        data.value = list
    }

    val onCloseClick = Bind.OnClick {
        dismiss()
    }

}