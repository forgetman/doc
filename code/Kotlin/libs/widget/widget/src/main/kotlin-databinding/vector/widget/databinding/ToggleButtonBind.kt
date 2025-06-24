package vector.widget.databinding

import vector.widget.OnToggleButtonCheckedChanged

sealed class ToggleButtonBind {
    data class OnCheckedChanged(val action: OnToggleButtonCheckedChanged) : ToggleButtonBind()
}