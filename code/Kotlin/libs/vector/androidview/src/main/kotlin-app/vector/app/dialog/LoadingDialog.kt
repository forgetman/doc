package vector.app.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vector.app.androidview.R
import vector.app.ext.bind.bindView
import vector.ext.inflate
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.widget.ProgressView

open class LoadingDialog(context: Context?) : DialogEx(context) {

    private val progressView by bindView<ProgressView>(R.id.progress_view)

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, MATCH_PARENT)

    override fun createContentView(inflater: LayoutInflater): View {
        return inflater.inflate(R.layout.layout_dialog_loading)
    }

    override fun initializeContentView() {
        setCanceledOnTouchOutside(false) // loading的消失只能由外部逻辑控制, 忽略点击事件
        setDimAmount(0.5f)
    }

    override fun onStart() {
        super.onStart()
        progressView.start()
    }

    override fun onStop() {
        super.onStop()
        progressView.stop()
    }
}