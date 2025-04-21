@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://csspeechstorage.blob.core.windows.net/maven/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        google()
        mavenCentral()
    }

    buildVersionCatalogs()
}

rootProject.name = "gradle-build-logics"
include(":convention")

fun DependencyResolutionManagement.buildVersionCatalogs() {
    versionCatalogs {
        val path = "tomls"
        val tomlSuffix = ".toml"
        val libSuffix = "Libs"
        fileTree(path).forEach { file ->
            var baseName = file.name.removeSuffix(tomlSuffix)
            val parts = baseName.split(".")
            val catalogName = buildString {
                for (part in parts) {
                    if (isNotEmpty()) {
                        append(part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
                    } else {
                        append(part)
                    }
                }
                append(libSuffix).toString()
            }
            create(catalogName) {
                from(files(file.path))
            }
        }
    }
}