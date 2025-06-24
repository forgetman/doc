package pretimmediat.widget

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import logger.L
import vector.app.ext.inflate

class EmailAutoCompleteTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatAutoCompleteTextView(context, attrs) {

    companion object {
        private const val LOG_TAG = "EmailAutoCompleteTextView"
        private const val EMAIL_START = "@"
    }

    private val emailSuffixes = arrayOf(
        "@qq.com", "@sina.com", "@168.com", "@gmail.com"
    )

    init {
        init(context)
    }

    private fun init(context: Context) {
        //adapter中使用默认的emailSufixs中的数据，可以通过setAdapterString来更改
        this.setAdapter(
            EmailAutoCompleteAdapter(
                context,
                pretimmediat.R.layout.layout_item_email_selection,
                emailSuffixes
            )
        )
        //使得在输入1个字符之后便开启自动完成
        this.threshold = 1
        this.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                //当该文本域重新获得焦点后，重启自动完成
                val index = text.indexOf(EMAIL_START)
                if (index != -1) {
                    showDropDown()
                }
            } else {
                //当文本域丢失焦点后，检查输入email地址的格式
//                val ev = v as EmailAutoCompleteTextView
//                val text = ev.text.toString()
//                if (!isEmailAddress(text)) {
//                    // 地址不正确
//                }
            }
        }

        val inputFilter = InputFilter { source, start, end, dest, dstart, _ ->
            for (i in start until end) {
                val char = source[i]
                when {
                    // 检测空格
                    char == ' ' -> {
                        return@InputFilter ""
                    }
                }
            }
            // 如果没有检测到回车字符，则返回 null，表示不进行任何过滤
            null
        }
        this.filters = arrayOf(inputFilter)

        setDropDownBackgroundResource(pretimmediat.R.drawable.piece_bg_email_popup)
    }

    private fun isEmailAddress(possibleEmail: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(possibleEmail).matches()
    }

    override fun replaceText(text: CharSequence) {
        //当我们在下拉框中选择一项时，android会默认使用AutoCompleteTextView中Adapter里的文本来填充文本域
        //因为这里Adapter中只是存了常用email的后缀
        //因此要重新replace逻辑，将用户输入的部分与后缀合并
        L.d(LOG_TAG, "replaceText, suffix = $text, prefix = ${getText()}")
        val index = getText().indexOf(EMAIL_START)
        if (index != -1) {
            val emailPrefix = getText().substring(0, index)
            super.replaceText(emailPrefix + text)
        } else {
            super.replaceText(text)
        }
    }

    override fun performFiltering(text: CharSequence, keyCode: Int) {
        //该方法会在用户输入文本之后调用，将已输入的文本与adapter中的数据对比，若它匹配
        //adapter中数据的前半部分，那么adapter中的这条数据将会在下拉框中出现
        val t = text.toString()
        L.d(LOG_TAG, "performFiltering, text = $t, keyCode = $keyCode")
        //因为用户输入邮箱时，都是以字母，数字开始，而我们的adapter中只会提供以类似于"@163.com"
        //的邮箱后缀，因此在调用super.performFiltering时，传入的一定是以"@"开头的字符串
        val index = t.indexOf(EMAIL_START)
        if (index == -1) {
            this.dismissDropDown()
        } else {
            super.performFiltering(t.substring(index), keyCode)
        }
    }

    private inner class EmailAutoCompleteAdapter(
        context: Context, textId: Int, strings: Array<String>
    ) : ArrayAdapter<String?>(context, textId, strings) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = convertView ?: context.inflate(pretimmediat.R.layout.layout_item_email_selection)
            v.findViewById<TextView>(pretimmediat.R.id.tv_email)?.let {
                var t = this@EmailAutoCompleteTextView.text.toString()
                val index = t.indexOf(EMAIL_START)
                if (index != -1) {
                    t = t.substring(0, index)
                }
                //将用户输入的文本与adapter中的email后缀拼接后，在下拉框中显示
                it.text = buildString {
                    append(t)
                    append(getItem(position))
                }
            }
            return v
        }
    }
}