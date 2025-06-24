package test.ui.activity

import android.os.Process
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import coroutine.flow.launchIn
import coroutine.flow.mediator.MediatorFlow
import vector.validator.CustomEditValidator
import vector.validator.NotEmptyEditValidator
import vector.validator.RegexEditValidator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import logger.L
import test.databinding.ActivityFormBinding
import test.ext.addBackIcon
import test.model.MultiUserRepo
import test.model.TestEnum
import test.model.User
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.bindingadapter.bind.Bind
import vector.datastore.preference.asEnumFlow
import vector.datastore.preference.asObjectFlow
import vector.ext.Regex
import vector.ext.toast

/**
 * @author yuansui
 * @since 2019/11/25
 */
class FormActivity : SimpleDBActivityEx() {

    val notEmptyValidator1 = NotEmptyEditValidator(false)
    val notEmptyValidator2 = NotEmptyEditValidator(false)
    val customValidator = CustomEditValidator(false) {
        !it.isNullOrEmpty()
    }
    val phoneValidator = RegexEditValidator(false, Regex.Rule.MOBILE_CN)

    val enable = MediatorFlow(
        notEmptyValidator1,
        notEmptyValidator2,
        customValidator,
        phoneValidator
    ) { accumulator, value ->
        accumulator && value
    }.stateIn(lifecycleScope, SharingStarted.WhileSubscribed(), false)

    private val multiUserRepo = MultiUserRepo()

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityFormBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.mid.addText("验证")
        appBar.addBackIcon(this)
    }

    override fun initializeContentView() {
        multiUserRepo.name.asFlow().onEach {
            L.www("form multi name = $it, pid = ${Process.myPid()}")
        }.launchIn(this)

        multiUserRepo.testEnum.asEnumFlow<TestEnum>().onEach {
            L.www("form multi enum name = $it, pid = ${Process.myPid()}")
        }.launchIn(this)

        multiUserRepo.user.asObjectFlow<User>().onEach { L.www("form multi object name = $it") }
            .launchIn(this)
    }

    val onClick = Bind.OnClick {
        toast("点击了")
    }

    override fun enableHideKeyboardWhenFocusChanged(): Boolean {
        return true
    }
}