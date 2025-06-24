@file:Suppress("unused")

package vector.app.dialog

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import vector.app.androidview.R
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import androidx.annotation.FloatRange
import androidx.annotation.GravityInt
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import sugar.ext.getAnnotation
import sugar.ext.ifNotNull
import sugar.ext.throwIfNull
import vector.app.UIHost
import vector.app.delegate.InitializeDelegate
import vector.app.delegate.InitializeInitializeDelegateImpl
import vector.app.delegate.performContentViewInitialize
import vector.app.delegate.performDataInitialize
import vector.app.ext.getStrategyInflater
import vector.app.fitter.FitStrategy
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.util.WRAP_CONTENT

/**
 * 拥有完整生命周期管理的dialog
 */
abstract class DialogEx(context: Context?, themeId: Int) :
    AppCompatDialog(context.throwIfNull("context can not be null"), themeId),
    UIHost,
    InitializeDelegate by InitializeInitializeDelegateImpl() {

    /**
     * 二段构造显式声明, 为了子类重写的时候能根据提示选择只有一个[context]参数的构造方法
     */
    constructor(context: Context?) : this(context, R.style.Theme_Dialog)

    private lateinit var contentView: View

    override val uiView: View?
        get() = contentView

    private var onShowListener: DialogInterface.OnShowListener? = null
    private var onDismissListener: DialogInterface.OnDismissListener? = null

    /**
     * 如果要调整宽高的参数可以在这里处理
     */
    open val params: ViewGroup.LayoutParams?
        get() = null

    open val marginStart: Int?
        get() = null

    open val marginEnd: Int?
        get() = null

    open val marginTop: Int?
        get() = null

    open val marginBottom: Int?
        get() = null

    @get:GravityInt
    open val gravity: Int?
        get() = null


    override fun getLayoutInflater(): LayoutInflater {
        val annotation = getAnnotation(FitStrategy::class)
        return this.context.getStrategyInflater(annotation)
    }


    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contentView = createContentView(layoutInflater)

        val contentParams = LayoutParamsFactory.viewGroupMargin(MATCH_PARENT, MATCH_PARENT)
        contentParams.setMargins(marginStart ?: 0, marginTop ?: 0, marginEnd ?: 0, marginBottom ?: 0)
        setContentView(contentView, contentParams)
        window?.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        setParams(params)

        startInitializeFlow()
    }

    override fun onStop() {
        super.onStop()

        onDismissListener?.onDismiss(this)
    }

    override fun startInitializeFlow() {
        performDataInitialize()
        performContentViewInitialize()

        clearInitializeFlowListeners()
    }

    final override fun initializeSystemBar() {
        // dialog没有status bar和navigation bar
    }

    private fun setParams(p: ViewGroup.LayoutParams?) {
        if (p == null) return

        val attrs = window?.attributes ?: return

        var hasChange = false
        if (p.width != WRAP_CONTENT) {
            attrs.width = p.width
            hasChange = true
        }
        if (p.height != WRAP_CONTENT) {
            attrs.height = p.height
            hasChange = true
        }

        gravity?.let {
            attrs.gravity = it
        }

        if (hasChange) window?.attributes = attrs
    }

    abstract fun createContentView(inflater: LayoutInflater): View

    /**
     * 设置类型
     * @param type 如果type是 [LayoutParams.TYPE_SYSTEM_ALERT]
     * 需要添加权限[Manifest.permission.SYSTEM_ALERT_WINDOW]
     */
    fun setType(type: Int) {
        @Suppress("DEPRECATION")
        if (type == LayoutParams.TYPE_SYSTEM_ALERT) {
            require(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.SYSTEM_ALERT_WINDOW
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                "请在manifest添加权限 " + Manifest.permission.SYSTEM_ALERT_WINDOW
            }
        }
        window?.setType(type)
    }

    /**
     * 设置空白处黑暗度
     *
     * @param amount 0-1.0, 0为全透明. 1为全黑
     */
    fun setDimAmount(@FloatRange(from = 0.0, to = 1.0) amount: Float) {
        window?.setDimAmount(amount)
    }

    /**
     * 设置对齐方式, 默认居中
     *
     * @param gravity [Gravity]
     */
    fun setGravity(gravity: Int) {
        window?.setGravity(gravity)
    }

    /**
     * 设置弹出动画方式
     *
     * @param dialogStyleId
     */
    fun setAnimation(@StyleRes dialogStyleId: Int) {
        window?.setWindowAnimations(dialogStyleId)
    }

    class Builder {
        var context: Context? = null
        var view: View? = null

        @FloatRange(from = 0.0, to = 1.0)
        var dimAmount: Float? = null

        var gravity: Int? = null

        @StyleRes
        var dialogStyleId: Int? = null

        internal fun build() = object : DialogEx(context) {
            override fun createContentView(inflater: LayoutInflater): View {
                return uiView ?: throw NullPointerException("builder view can not be null")
            }

            override fun initializeContentView() {
                dimAmount.ifNotNull {
                    setDimAmount(it)
                }

                gravity.ifNotNull {
                    setGravity(it)
                }

                dialogStyleId.ifNotNull {
                    setAnimation(it)
                }
            }
        }
    }
}

fun buildDialog(action: (DialogEx.Builder) -> Unit): DialogEx {
    val builder = DialogEx.Builder()
    action(builder)
    return builder.build()
}