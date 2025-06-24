package inject.annotation

/**
 * Describes the type of incremental annotation processing the annotated processor is capable of.
 *
 *
 * This annotation must be placed on a concrete class implementing [ ]. A processor described as [ ][IncrementalType.DYNAMIC] will have to implement [ ][javax.annotation.processing.Processor.getSupportedOptions] returning zero or
 * one of the [predefined][IncrementalType.getProcessorOption] looked for by Gradle.
 *
 *
 * The annotation processor will generate the appropriate `META-INF/gradle/incremental.annotation.processors` descriptor file describing all annotated
 * processors.
 */
@MustBeDocumented
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Incremental(val value: IncrementalType)