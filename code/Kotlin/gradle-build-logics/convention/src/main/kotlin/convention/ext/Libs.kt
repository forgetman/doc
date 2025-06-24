package convention.ext

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.commonLibs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("commonLibs")

internal val Project.hiltLibs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("hiltLibs")

internal val Project.kotlinLibs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("kotlinLibs")

internal val Project.androidxLibs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("androidxLibs")

internal val Project.androidxComposeLibs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("androidxComposeLibs")

internal val Project.googleLibs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("googleLibs")