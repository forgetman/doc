package vector.validator

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.regex.Pattern

interface EditValidator : Validator {
    fun validate(s: CharSequence?): Boolean
}

@Suppress("FunctionName")
fun NotEmptyEditValidator(includeHint: Boolean): Validator {
    return EditValidatorImpl(includeHint) { s ->
        !s.isNullOrEmpty()
    }
}

@Suppress("FunctionName")
fun RegexEditValidator(includeHint: Boolean, regex: String?): Validator {
    return EditValidatorImpl(includeHint) { s ->
        Pattern.compile(regex ?: return@EditValidatorImpl false).matcher(s ?: return@EditValidatorImpl false).matches()
    }
}

@Suppress("FunctionName")
fun CustomEditValidator(includeHint: Boolean, validate: (s: CharSequence?) -> Boolean): Validator {
    return EditValidatorImpl(includeHint, validate)
}

private class EditValidatorImpl(
    private val includeHint: Boolean,
    private val validate: (s: CharSequence?) -> Boolean
) : EditValidator {
    private val mutableStateFlow = MutableStateFlow(false)

    private var textWatcher: TextWatcher? = null

    override fun bindView(view: View) {
        if (view !is EditText) return

        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View) {
                view.removeTextChangedListener(textWatcher)
            }

            override fun onViewAttachedToWindow(v: View) {
                if (textWatcher == null) {
                    textWatcher = object : TextWatcher {

                        override fun afterTextChanged(s: Editable?) {
                            var valid = validate(s)
                            if (includeHint) {
                                if (s.isNullOrEmpty()) {
                                    valid = validate(view.hint)
                                }
                            }

                            value = valid
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }
                    }
                }
                view.addTextChangedListener(textWatcher)
            }
        })

        value = validate(view.editableText)
    }

    override var value: Boolean
        get() = mutableStateFlow.value
        set(value) {
            mutableStateFlow.value = value
        }

    override val replayCache: List<Boolean>
        get() = mutableStateFlow.replayCache

    override suspend fun collect(collector: FlowCollector<Boolean>): Nothing {
        return mutableStateFlow.collect(collector)
    }

    override fun validate(s: CharSequence?): Boolean {
        return validate.invoke(s)
    }
}