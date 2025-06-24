package vector.widget.scrollable.delegate

interface SpanSizeDelegate {
    fun getSpanSize(position: Int, spanCount: Int): Int = 1
}