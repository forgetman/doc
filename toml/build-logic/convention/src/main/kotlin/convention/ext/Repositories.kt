package convention.ext

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler


fun RepositoryHandler.configure(project: Project) {
    google {
        content {
            includeGroupByRegex("com\\.android.*")
            includeGroupByRegex("com\\.google.*")
            includeGroupByRegex("androidx.*")
        }
    }
    mavenCentral()
    mavenRepositories().forEach {
        maven { url = project.uri(it) }
    }
}

private fun mavenRepositories() = listOf<String>(
    "https://maven.aliyun.com/nexus/content/groups/public/",
    "https://maven.aliyun.com/repository/public",
    "https://maven.aliyun.com/repository/central",
    "https://maven.aliyun.com/repository/google",
    "https://maven.aliyun.com/repository/gradle-plugin",
    "https://jitpack.io",
    "https://plugins.gradle.org/m2/",
    "https://repo1.maven.org/maven2/",
    "https://csspeechstorage.blob.core.windows.net/maven/",
    "https://oss.sonatype.org/content/repositories/snapshots/"
)