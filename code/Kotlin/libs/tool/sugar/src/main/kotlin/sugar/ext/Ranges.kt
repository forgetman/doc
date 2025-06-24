package sugar.ext

infix fun Float.until(to: Float): FloatRange {
    if (to <= Float.MIN_VALUE) return FloatRange.EMPTY
    return FloatRange(this, to - 0.1f)
}

class FloatRange(start: Float, endInclusive: Float) :
    FloatProgression(start, endInclusive, 1f),
    ClosedRange<Float> {

    companion object {
        /** An empty range of values of type Float. */
        val EMPTY: FloatRange = FloatRange(1f, 0f)
    }

    private val _start = start
    private val _endInclusive = endInclusive
    override val start: Float get() = _start
    override val endInclusive: Float get() = _endInclusive

    override fun contains(value: Float): Boolean = value in _start.._endInclusive
    override fun isEmpty(): Boolean = !(_start <= _endInclusive)

    override fun equals(other: Any?): Boolean {
        return other is FloatRange && (isEmpty() && other.isEmpty() ||
                _start == other._start && _endInclusive == other._endInclusive)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else 31 * _start.hashCode() + _endInclusive.hashCode()
    }

    override fun toString(): String = "$_start..$_endInclusive"
}

open class FloatProgression constructor(
    start: Float,
    endInclusive: Float,
    /**
     * The step of the progression.
     */
    val step: Float
) : Iterable<Float> {
    init {
        if (step == 0f) throw kotlin.IllegalArgumentException("Step must be non-zero.")
        if (step == Float.MIN_VALUE) throw kotlin.IllegalArgumentException("Step must be greater than Float.MIN_VALUE to avoid overflow on negation.")
    }

    /**
     * The first element in the progression.
     */
    val first: Float = start

    /**
     * The last element in the progression.
     */
    val last: Float = getProgressionLastElement(start, endInclusive, step)

    override fun iterator(): FloatIterator = FloatProgressionIterator(first, last, step)

    /** Checks if the progression is empty. */
    open fun isEmpty(): Boolean = if (step > 0f) first > last else first < last

    override fun equals(other: Any?): Boolean =
        other is FloatProgression && (isEmpty() && other.isEmpty() ||
                first == other.first && last == other.last && step == other.step)

    override fun hashCode(): Int {
        val number = if (isEmpty()) -1f else (31f * (31f * first + last) + step)
        return number.toInt()
    }

    override fun toString(): String =
        if (step > 0f) "$first..$last step $step" else "$first downTo $last step ${-step}"
}

class FloatProgressionIterator(first: Float, last: Float, val step: Float) : FloatIterator() {
    private val finalElement = last
    private var hasNext: Boolean = if (step > 0) first <= last else first >= last
    private var next = if (hasNext) first else finalElement

    override fun hasNext(): Boolean = hasNext

    override fun nextFloat(): Float {
        val value = next
        if (value == finalElement) {
            if (!hasNext) throw kotlin.NoSuchElementException()
            hasNext = false
        } else {
            next += step
        }
        return value
    }
}

private fun getProgressionLastElement(start: Float, end: Float, step: Float): Float = when {
    step > 0 -> if (start >= end) end else end - differenceModulo(end, start, step)
    step < 0 -> if (start <= end) end else end + differenceModulo(start, end, -step)
    else -> throw kotlin.IllegalArgumentException("Step is zero.")
}

private fun differenceModulo(a: Float, b: Float, c: Float): Float {
    return mod(mod(a, c) - mod(b, c), c)
}

// a mod b (in arithmetical sense)
private fun mod(a: Float, b: Float): Float {
    val mod = a % b
    return if (mod >= 0) mod else mod + b
}