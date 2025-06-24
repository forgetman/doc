package test.compose.ui.activity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coroutine.flow.launchForever
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import logger.L
import test.compose.ext.AppBar
import vector.app.compose.ui.activity.SimpleComposeActivityEx
import vector.app.compose.ext.ui.hideKeyboardOnTouchOutside

interface Validator {
    fun validate(value: String): Boolean
}

class NotEmptyValidator : Validator {
    override fun validate(value: String): Boolean = value.isNotEmpty()
}

class EmailValidator : Validator {
    override fun validate(value: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)\$")
        return emailRegex.matches(value)
    }
}

data class FormField(
    val initialValue: String = "",
    val validator: Validator
) {
    private val _isValid = mutableStateOf(false)
    val isValid: State<Boolean> = _isValid

    val value = MutableStateFlow(initialValue)

    init {
        value.onEach {
            _isValid.value = validator.validate(it)
        }.launchForever()
    }
}

/**
 * @author yuansui
 * @since 2025/6/10
 */
class FormActivity : SimpleComposeActivityEx() {

    @Composable
    override fun Content() {
        // 使用 Map 管理多个表单字段
        val formFields = remember {
            mutableMapOf(
                "name" to FormField(validator = NotEmptyValidator()),
                "email" to FormField(validator = EmailValidator())
            )
        }

        val allFieldsValid by remember {
            derivedStateOf { formFields.values.all { it.isValid.value } }
        }

        Scaffold(
            topBar = {
                AppBar(title = "表单")
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .hideKeyboardOnTouchOutside(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                formFields.forEach { (key, field) ->
                    GenericTextField(
                        label = key,
                        field = field
                    )
                }

                Button(onClick = {
                    L.www("点击了")
                }, enabled = allFieldsValid) {
                    Text(text = "点击", textAlign = TextAlign.Center)
                }
            }
        }
    }

    @Composable
    fun GenericTextField(
        label: String,
        field: FormField
    ) {
        val value by field.value.collectAsStateWithLifecycle()
        TextField(
            value = value,
            onValueChange = {
                field.value.value = it
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
