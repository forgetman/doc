package vector.widget.databinding.viewpager

import android.view.View
import vector.widget.compat.viewpager.OnDirection
import vector.widget.compat.viewpager.OnIntent
import vector.widget.compat.viewpager.OnScrollStateChanged
import vector.widget.compat.viewpager.OnScrolled
import vector.widget.compat.viewpager.OnSelected

sealed class ViewPagerBind {
    data class OnPageScrollStateChanged(val action: OnScrollStateChanged) : ViewPagerBind()
    data class OnPageScrolled(val action: OnScrolled) : ViewPagerBind()
    data class OnPageSelected(val action: OnSelected) : ViewPagerBind()
    data class OnPageDirection(val action: OnDirection) : ViewPagerBind()
    data class OnPageIntent(val action: OnIntent) : ViewPagerBind()

    data class OnItemClick(val action: (itemView: View, position: Int) -> Unit) : ViewPagerBind()
    data class OnItemDoubleClick(val action: (itemView: View, position: Int) -> Unit) : ViewPagerBind()
    data class OnItemLongClick(val action: (itemView: View, position: Int) -> Unit) : ViewPagerBind()

    data class OnDataChanged(val action: () -> Unit) : ViewPagerBind()
}