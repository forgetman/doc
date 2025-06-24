package vector.bindingadapter

import android.graphics.drawable.Drawable
import android.widget.SeekBar
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import vector.app.util.Res
import vector.bindingadapter.bind.Bind


typealias OnSeekBarProgressChanged = (view: SeekBar, progress: Int, fromUser: Boolean) -> Unit

/**
 * @author yuansui
 * @since 2020/11/23
 */
object SeekBarBinding {

    private const val PROGRESS = BINDING_PREFIX + "seekBar_progress"
    private const val ON_PROGRESS_CHANGED = BINDING_PREFIX + "seekBar_onProgressChanged"

    private const val ENABLE = BINDING_PREFIX + "seekBar_enable"

    private const val PROGRESS_DRAWABLE = BINDING_PREFIX + "seekBar_progressDrawable"
    private const val PROGRESS_DRAWABLE_ID = BINDING_PREFIX + "seekBar_progressDrawableId"

    @JvmStatic
    @BindingAdapter(ON_PROGRESS_CHANGED, PROGRESS + ATTR_CHANGED_SUFFIX, requireAll = false)
    fun setOnProgressChanged(
        view: SeekBar,
        onProgressChanged: Bind.SeekBar.OnProgressChanged?,
        attrChange: InverseBindingListener?
    ) {
        view.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                onProgressChanged?.action?.invoke(view, progress, fromUser)
                attrChange?.onChange()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    @JvmStatic
    @BindingAdapter(PROGRESS)
    fun setProgress(view: SeekBar, progress: Int) {
        if (view.progress == progress) return
        view.progress = progress
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = PROGRESS)
    fun getProgress(view: SeekBar): Int {
        return view.progress
    }

    @JvmStatic
    @BindingAdapter(ENABLE)
    fun setEnable(view: SeekBar, enable: Boolean) {
        view.isEnabled = enable
    }

    @JvmStatic
    @BindingAdapter(PROGRESS_DRAWABLE)
    fun setProgressDrawable(view: SeekBar, drawable: Drawable) {
        view.progressDrawable = drawable
    }

    @JvmStatic
    @BindingAdapter(PROGRESS_DRAWABLE_ID)
    fun setProgressDrawable(view: SeekBar, drawableId: Int) {
        val drawable = Res.getDrawable(view.context, drawableId) ?: return
        view.progressDrawable = drawable
    }
}