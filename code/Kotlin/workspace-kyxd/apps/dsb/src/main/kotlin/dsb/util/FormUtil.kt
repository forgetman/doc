package dsb.util

import android.widget.TextView
import androidx.annotation.LayoutRes
import dsb.Bus
import dsb.EventId
import dsb.R
import dsb.ext.checkSignIn
import dsb.ext.withWebParams
import image.api.load
import lib.base.model.Form
import vector.bindingadapter.GridLayoutSet
import vector.widget.ImageView

/**
 * @author yuansui
 * @since 2019-05-28
 */
object FormUtil {
    fun getGridLayoutSet(
        form: Form,
        @LayoutRes layoutId: Int,
        layoutType: GridLayoutSet.LayoutType
    ) =
        GridLayoutSet().apply {
            id = layoutId
            this.layoutType = layoutType

            onDataSet = { view ->
                val iv = view.findViewById<ImageView>(R.id.form_grid_layout_iv)
                iv.load {
                    source(form.icon)
                }

                val tv = view.findViewById<TextView>(R.id.form_grid_layout_tv)
                tv.text = form.title
            }

            onClick = { _ ->
                if (!form.needLogin || checkSignIn()) {
                    Bus.get().send(EventId.LAUNCH_WEB, form.url?.withWebParams())
                }
            }
        }
}