package inject.annotation

/**
 * Describes the type of incremental annotation processing a processor is capable of.
 *
 *
 * See [the
 * Gradle documentation](https://docs.gradle.org/4.8-rc-2/userguide/java_plugin.html#sec:incremental_annotation_processing) for more information on each type.
 */
enum class IncrementalType(private val hasProcessorOption: Boolean) {
    /**
     * 隔离模式是指将注解处理器的处理过程隔离开来，以避免并发问题和状态污染
     * 在隔离模式下，每个处理器实例都会分配一个独立的 ProcessorContext 对象，用于存储处理器的状态和上下文信息
     */
    ISOLATING(true),

    /**
     * 聚合模式是指将注解处理器的处理过程聚合在一起，
     * 以避免重复扫描和处理相同的代码。
     * 在聚合模式下，每个处理器实例都会共享同一个 ProcessingEnvironment 对象，用于存储处理器的状态和上下文信息
     */
    AGGREGATING(true),

    /**
     * 动态模式是指可以在运行时动态添加、删除和替换注解处理器的处理逻辑和配置信息。
     * 在动态模式下，每个处理器实例都可以根据需要进行动态调整，以满足不同的需求和场景
     */
    DYNAMIC(false);

    /**
     * Returns the specific value that Gradle looks for in [ ][javax.annotation.processing.Processor.getSupportedOptions] for [ ][.DYNAMIC] incremental annotation processors.
     *
     * @throws UnsupportedOperationException if called on the [.DYNAMIC] constant.
     */
    val processorOption: String
        get() {
            if (!hasProcessorOption) {
                throw UnsupportedOperationException()
            }
            return "org.gradle.annotation.processing." + name.lowercase()
        }
}