@file:Suppress("unused")

package vector.bindingadapter.bind

import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import com.google.android.material.appbar.AppBarLayout
import vector.bindingadapter.OnSeekBarProgressChanged

class MultiClickAction internal constructor() {
    var onSingleClick: ((View) -> Unit)? = null
    var onDoubleClick: ((View) -> Unit)? = null
    var onLongClick: ((View) -> Unit)? = null
}

/**
 * @author yuansui
 * @since 2018/3/6
 */
sealed class Bind {
    data class OnClick(val action: (v: View) -> Unit) : Bind()
    data class OnDebounceClick(val interval: Long? = null, val action: (v: View) -> Unit) : Bind()
    data class OnLongClick(val action: (v: View) -> Boolean) : Bind()
    data class OnMultiClick(val action: MultiClickAction.() -> Unit) : Bind()

    data class OnFocusChanged(val action: (v: View, focus: Boolean) -> Unit) : Bind()
    data class OnTouch(val action: (v: View, event: MotionEvent) -> Boolean) : Bind()
    data class OnLayout(val action: (v: View) -> Unit) : Bind()

    sealed class Text : Bind() {
        data class TextChanged(val action: TextChangedBinding.Action.() -> Unit) : Text()
        data class OnEditorAction(val action: (v: TextView, actionId: Int) -> Boolean) : Text()
    }

    sealed class Web : Bind() {
        data class OnProgressChanged(val action: (progress: Int) -> Unit) : Web()
        data class OnTitleChanged(val action: (text: String) -> Unit) : Web()

        /**
         * @return true if consume the super load
         */
        data class OnLoadingUrl(val action: (web: WebView, url: String) -> Boolean) : Web()

        data class OnLoadingScheme(val action: (web: WebView, url: String, intent: Intent) -> Boolean) :
            Web()
    }

    sealed class CompoundButton : Bind() {
        data class OnCheckedChanged(val action: (v: android.widget.CompoundButton, isChecked: Boolean) -> Unit) :
            CompoundButton()
    }

    sealed class RadioGroup : Bind() {
        data class OnCheckedChanged(val action: (index: Int, checkedId: Int) -> Unit) : RadioGroup()
    }

    sealed class AppBar {
        data class OnOffsetChanged(val action: (appBarLayout: AppBarLayout, verticalOffset: Int) -> Unit) :
            AppBar()
    }

    sealed class SeekBar {
        data class OnProgressChanged(val action: OnSeekBarProgressChanged) : SeekBar()
    }
}
