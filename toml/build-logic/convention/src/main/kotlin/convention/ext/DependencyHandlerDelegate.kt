package convention.ext

import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

fun DependencyHandlerDelegate.addImplementation(dependencyNotation: Any) {
    add("implementation", dependencyNotation)
}

fun DependencyHandlerDelegate.addDebugImplementation(dependencyNotation: Any) {
    add("debugImplementation", dependencyNotation)
}

fun DependencyHandlerDelegate.addKsp(dependencyNotation: Any) {
    add("ksp", dependencyNotation)
}