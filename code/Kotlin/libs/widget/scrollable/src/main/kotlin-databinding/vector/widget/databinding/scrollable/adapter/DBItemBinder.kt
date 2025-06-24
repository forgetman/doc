package vector.widget.databinding.scrollable.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import sugar.ext.lifecycleOwner
import sugar.util.ReflectUtil
import vector.app.databinding.R
import vector.app.ext.view.toLayoutInflater
import vector.util.GenericUtil
import vector.widget.scrollable.adapter.ItemViewHolder
import vector.widget.scrollable.adapter.binder.ItemBinder
import kotlin.reflect.KClass

abstract class AbstractBindingViewHolder<VDB : ViewDataBinding>(itemView: View) :
    ItemViewHolder(itemView) {
    var binding: VDB? = null

    constructor(binding: VDB) : this(binding.root) {
        this.binding = binding
    }
}

/**
 * [ViewDataBinding]专用Binder
 */
abstract class DBItemBinder<T, VDB : ViewDataBinding> :
    ItemBinder<T, DBItemBinder.ViewHolder<VDB>>() {

    companion object {
        const val INJECT_INFLATE = "inflate"
    }

    @Suppress("UNCHECKED_CAST")
    private val vdbClz: KClass<VDB> by lazy {
        GenericUtil.getClassType(this::class, ViewDataBinding::class) as? KClass<VDB>
            ?: throw IllegalStateException("can not find VDB class")
    }

    override fun createViewHolder(parent: ViewGroup): ViewHolder<VDB> {
        val binding = createBinding(parent)

        /**
         * 设置view的lifecycleOwner
         * 尝试寻找父布局的
         * PS: 这里不能设置binding的lifecycleOwner, 会提早调用executePendingBindings()
         */
        parent.findViewTreeLifecycleOwner()?.run {
            binding.root.lifecycleOwner = this
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<VDB>, item: T, position: Int) {
        val binding = holder.binding ?: return

        // 把item和binding的view进行绑定, 方便自定义的view可以通过binding获取item
        holder.itemView.setTag(R.id.binding_item, item)

        onBindBinding(item, binding, position)

        val existOwner = binding.root.lifecycleOwner
        if (existOwner != null && binding.lifecycleOwner != existOwner) {
            binding.lifecycleOwner = existOwner
        }
    }

    open fun createBinding(parent: ViewGroup): VDB {
        /**
         * inflate方式效率, 多次数据对比如下
         * 次数: 10000
         * 单位: 毫秒
         * 1. 直接使用类名调用inflate方法, 如(XxxBinding.inflate), 消耗时间 3867/3316/2943/3037
         * 2. 使用反射调用inflate方法, 消耗时间 4951/3742/3829/3854
         * 结论: 实际使用可以忽略不计
         *
         * XxxBinding.inflate(inflater: LayoutInflater, parent: ViewGroup, attachToRoot: Boolean)
         */
        val bindingMethod = ReflectUtil.getMethod(
            vdbClz,
            INJECT_INFLATE,
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        bindingMethod.isAccessible = true
        val binding = bindingMethod.invoke(null, parent.toLayoutInflater(), parent, false)

        @Suppress("UNCHECKED_CAST")
        return binding as VDB
    }

    protected abstract fun onBindBinding(item: T, binding: VDB, position: Int)

    class ViewHolder<VDB : ViewDataBinding>(binding: VDB) : AbstractBindingViewHolder<VDB>(binding)

    @Suppress("UNCHECKED_CAST")
    fun View.getBindingItem(): T? {
        var found: Any? = getTag(R.id.binding_item)
        if (found != null) return found as? T?
        var parent: ViewParent? = this.parent
        while (found == null && parent is View) {
            val parentView = parent
            found = parentView.getTag(R.id.binding_item) as? T?
            parent = parentView.getParent()
        }
        return found as? T?
    }
}