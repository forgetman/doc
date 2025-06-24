package vector.bindingadapter.trigger

object BindTrigger {

    object Edit {
        fun clear() = EditClearTrigger()
    }

    object Image {
        fun recycle() = ImageRecycleTrigger()
    }
}