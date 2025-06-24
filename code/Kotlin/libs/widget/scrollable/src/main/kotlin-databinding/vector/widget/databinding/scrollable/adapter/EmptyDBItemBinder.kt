package vector.widget.databinding.scrollable.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import sugar.util.ReflectUtil
import vector.app.ext.view.toLayoutInflater
import vector.util.GenericUtil
import vector.widget.scrollable.adapter.binder.EmptyItemBinder
import kotlin.reflect.KClass

/**
 * @author yuansui
 * @since 2021/5/3
 */
abstract class EmptyDBItemBinder<VDB : ViewDataBinding> :
    EmptyItemBinder<EmptyDBItemBinder.ViewHolder<VDB>>() {

    @Suppress("UNCHECKED_CAST")
    private val vdbClz: KClass<VDB> by lazy {
        GenericUtil.getClassType(this::class, ViewDataBinding::class) as? KClass<VDB>
            ?: throw IllegalStateException("can not find VDB class")
    }

    final override fun createViewHolder(parent: ViewGroup): ViewHolder<VDB> {
        val binding = createBinding(parent)
        return ViewHolder(binding)
    }

    final override fun onBindViewHolder(holder: ViewHolder<VDB>) {
        onBindBinding(holder.binding ?: return)
    }

    open fun createBinding(parent: ViewGroup): VDB {
        val binding = ReflectUtil.getMethod(
            vdbClz.java.name,
            DBItemBinder.INJECT_INFLATE,
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(null, parent.toLayoutInflater(), parent, false)

        @Suppress("UNCHECKED_CAST")
        return binding as VDB
    }

    open fun onBindBinding(binding: VDB) {}

    class ViewHolder<VDB : ViewDataBinding>(binding: VDB) : AbstractBindingViewHolder<VDB>(binding)
}