package fund.model

import vector.bindingadapter.onBind.Bind

/**
 * @author yuansui
 * @since 2018/11/23
 */
class InputInfo {

    val onPhoneTextChanged = Bind.Edit.onTextChanged { s, start, before, count ->

    }

    val onCaptchaTextChanged = Bind.Edit.onTextChanged { s, start, before, count ->

    }

    val onActionClick = Bind.OnClick {

    }

    val onVoiceClick = Bind.OnClick {

    }
}