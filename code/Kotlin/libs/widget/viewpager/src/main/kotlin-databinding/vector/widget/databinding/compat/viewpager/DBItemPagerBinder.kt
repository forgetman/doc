package vector.widget.databinding.compat.viewpager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import sugar.ext.lifecycleOwner
import sugar.util.ReflectUtil
import vector.app.ext.view.toLayoutInflater
import vector.util.GenericUtil
import vector.widget.compat.viewpager.ItemPagerBinder
import kotlin.reflect.KClass

/**
 * [androidx.databinding.ViewDataBinding]专用Binder
 */
abstract class DBItemPagerBinder<T, VDB : ViewDataBinding> :
    ItemPagerBinder<T, DBItemPagerBinder.ViewHolder<VDB>>() {

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

    override fun onBindViewHolder(holder: ViewHolder<VDB>, item: T) {
        val binding = holder.binding ?: return

        val existOwner = binding.root.lifecycleOwner
        if (existOwner != null && binding.lifecycleOwner != existOwner) {
            binding.lifecycleOwner = existOwner
        }

        onBindBinding(item, binding)
    }

    open fun createBinding(parent: ViewGroup): VDB {
        val binding = ReflectUtil.getMethod(
            vdbClz.java.name,
            INJECT_INFLATE,
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(null, parent.toLayoutInflater(), parent, false)

        @Suppress("UNCHECKED_CAST")
        return binding as VDB
    }

    protected abstract fun onBindBinding(item: T, binding: VDB)

    open class ViewHolder<VDB : ViewDataBinding>(itemView: View) :
        ItemPagerBinder.ViewHolder(itemView) {
        var binding: VDB? = null

        constructor(binding: VDB) : this(binding.root) {
            this.binding = binding
        }
    }
}