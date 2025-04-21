package convention.ext

import org.gradle.api.artifacts.VersionCatalog

/**
 * 查找PluginId
 */
fun VersionCatalog.findPluginId(alias: String) = this.findPlugin(alias).get().get().pluginId

fun VersionCatalog.findDependency(alias: String) = this.findLibrary(alias).get()

fun VersionCatalog.findStringVersion(alias: String) = this.findVersion(alias).get().toString()
fun VersionCatalog.findIntVersion(alias: String) = this.findStringVersion(alias).toInt()