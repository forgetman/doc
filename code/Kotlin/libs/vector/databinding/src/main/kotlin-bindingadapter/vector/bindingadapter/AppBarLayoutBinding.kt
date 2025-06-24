package vector.bindingadapter

import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.AppBarLayout
import vector.bindingadapter.bind.Bind

/**
 * @author moguangjian
 * @date 2019/2/27
 */
object AppBarLayoutBinding {
    private const val ON_OFFSET_CHANGED = BINDING_PREFIX + "appBarLayout_onOffsetChanged"

    @JvmStatic
    @BindingAdapter(ON_OFFSET_CHANGED)
    fun addOnOffsetChangedListener(
        appBarLayout: AppBarLayout,
        binding: Bind.AppBar.OnOffsetChanged
    ) {
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { view, verticalOffset ->
            binding.action(view, verticalOffset)
        })
    }
}