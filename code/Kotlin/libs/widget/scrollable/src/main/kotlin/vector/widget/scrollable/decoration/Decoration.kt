package vector.widget.scrollable.decoration

import androidx.recyclerview.widget.RecyclerView

object Decoration {
    fun linear(block: LinearAttrs.() -> Unit): RecyclerView.ItemDecoration {
        val attrs = LinearAttrs()
        block(attrs)
        return DecorationWrapper(attrs)
    }

    fun grid(block: GridAttrs.() -> Unit): RecyclerView.ItemDecoration {
        val attrs = GridAttrs()
        block(attrs)
        return DecorationWrapper(attrs)
    }
}