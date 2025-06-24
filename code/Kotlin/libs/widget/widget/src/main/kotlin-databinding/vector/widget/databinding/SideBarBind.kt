package vector.widget.databinding

import vector.widget.OnTouchLetterListener

sealed class SideBarBind {
    data class OnTouchLetter(val action: OnTouchLetterListener) : SideBarBind()
}