package vector.app.ext

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import vector.app.fitter.FitResources
import vector.app.fitter.FitStrategy
import vector.app.fitter.Mode
import vector.app.os.Dimension
import vector.app.os.DimensionSize
import vector.app.config.Config
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT

fun Context.inflateSpace(height: Dimension): View {
    return Space(this).apply {
        layoutParams = LayoutParamsFactory.viewGroup(MATCH_PARENT, height.toPx(this@inflateSpace))
    }
}

fun Context.inflateSpace(size: DimensionSize): View {
    return Space(this).apply {
        layoutParams = LayoutParamsFactory.viewGroup(
            size.width(this@inflateSpace),
            size.height(this@inflateSpace)
        )
    }
}

fun Context.inflate(res: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false): View {
    return cloneLayoutInflater().inflate(res, parent, attachToRoot)
}

fun Context.createResourceContext(mode: Mode = Config.fit().mode): Context {
    return ResourceContext(mode, this)
}

fun Context.cloneLayoutInflater(): LayoutInflater = LayoutInflater.from(this).cloneInContext(this)

class ResourceContext(private val mode: Mode, base: Context) : ContextWrapper(base) {

    override fun getResources(): Resources {
        return FitResources.get(mode, baseContext.resources)
    }
}

fun Context.getStrategyInflater(strategy: FitStrategy?): LayoutInflater {
    val inflater = LayoutInflater.from(this)
    return if (strategy != null) {
        if (strategy.value != Config.fit().mode) {
            val newContext = createResourceContext(strategy.value)
            inflater.cloneInContext(newContext)
        } else {
            inflater
        }
    } else {
        inflater
    }
}