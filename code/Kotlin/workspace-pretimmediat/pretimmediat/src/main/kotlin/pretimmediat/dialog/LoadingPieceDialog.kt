package pretimmediat.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pretimmediat.R
import vector.app.dialog.DialogEx
import vector.app.ext.bind.bindView
import vector.ext.inflate
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.widget.ProgressView

/**
 * 进件页专用dialog
 */
open class LoadingPieceDialog(context: Context?) : DialogEx(context) {

    private val progressView by bindView<ProgressView>(R.id.progress_view)

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, MATCH_PARENT)

    override fun createContentView(inflater: LayoutInflater): View {
        return inflater.inflate(R.layout.dialog_loading_piece)
    }

    override fun initializeContentView() {
        setCanceledOnTouchOutside(false)
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