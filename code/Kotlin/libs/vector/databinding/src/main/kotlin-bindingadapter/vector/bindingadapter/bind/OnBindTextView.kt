package vector.bindingadapter.bind

typealias AfterTextChangedAction = (s: CharSequence?) -> Unit
typealias BeforeTextChangedAction = (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit
typealias OnTextChangedAction = (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit

class TextChangedBinding(internal val action: Action) {

    class Action {
        internal var before: BeforeTextChangedAction? = null
        internal var after: AfterTextChangedAction? = null
        internal var on: OnTextChangedAction? = null

        fun before(action: BeforeTextChangedAction) {
            before = action
        }

        fun after(action: AfterTextChangedAction) {
            after = action
        }

        fun on(action: OnTextChangedAction) {
            on = action
        }
    }
}