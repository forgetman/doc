@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("gradle-build-logics")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.PREFER_PROJECT // 使用这个模式后, 会直接使用module的仓库(假设存在)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }

    buildVersionCatalogs()
}

fun DependencyResolutionManagement.buildVersionCatalogs() {
    versionCatalogs {
        val path = "gradle-build-logics/tomls"
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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "Kotlin"

/****************************************
 * vector
 ****************************************/
include(
    ":libs:vector:core",
    ":libs:vector:compose",
    ":libs:vector:databinding"
)

/****************************************
 * widget
 ****************************************/
include(":libs:widget:widget")
include(":libs:widget:indicator")
include(":libs:widget:photoview")
include(":libs:widget:swiperefresh")
include(":libs:widget:scrollable")
include(":libs:widget:viewpager")

/****************************************
 * annotation_processing
 ****************************************/
include(
//    ":libs:annotation_processing:apt:annotation",
//    ":libs:annotation_processing:apt:compiler",
//    ":libs:annotation_processing:apt:compiler_incremental",
    ":libs:annotation_processing:kapt:annotation",
    ":libs:annotation_processing:kapt:compiler"
)

/****************************************
 * other
 ****************************************/
include(":libs:eson")

/****************************************
 * network
 ****************************************/
include(
    ":libs:network:eth:api",
    ":libs:network:eth:okhttp",
    ":libs:network:okhttp_ext",
    ":libs:network:eos:api",
    ":libs:network:eos:okhttp",
)

/****************************************
 * bus
 ****************************************/
include(
    ":libs:bus:busapi",
    ":libs:bus:flowbus",
//    ":libs:bus:liveBus",
//    ":libs:bus:rxbus",
)

/****************************************
 * image
 ****************************************/
include(
    ":libs:image:api",
    ":libs:image:coil",
//    ":libs:image:glide"
)

/****************************************
 * tool
 ****************************************/
include(
    ":libs:tool:live",
    ":libs:tool:coroutine",
    ":libs:tool:sugar",
    ":libs:tool:bluetooth",
    ":libs:tool:logger:api",
    ":libs:tool:logger:logcat",
    ":libs:tool:logger:logan",
    ":libs:tool:logger:xlog",

//    ":libs:tool:render:renderscript",
    ":libs:tool:render:aar",

    ":libs:tool:compat",
    ":libs:tool:trigger",

//    ":libs:aop:annotation",
//    ":libs:aop:runtime",
)

/****************************************
 * function
 ****************************************/
include(":libs:function:tts")

///****************************************
// * 闲云(不适合mvvm的模式)
// ****************************************/
//include(":workspace-xy:libs:lib_base")
//include(":workspace-xy:calendar")

/***************************************
 * xh
 ****************************************/
//include(":workspace-live:xh")

/***************************************
 * reader
 ****************************************/
include(":workspace-reader:reader")

/***************************************
 * 外包-大社宝(已停运)
 ****************************************/
//include ':workspace-kyxd:apps:dsb'
////include ':workspace-kyxd:apps:fund'
//include ':workspace-kyxd:libs:lib_base'
//include ':workspace-kyxd:libs:lib_baidu'
//include ':workspace-kyxd:libs:lib_umeng'
//include ':workspace-kyxd:libs:lib_umeng:aars'
//include ':workspace-kyxd:libs:lib_jg'
//include ':workspace-kyxd:libs:lib_udesk'
//include ':workspace-kyxd:libs:lib_sy253'
//include ':workspace-kyxd:libs:lib_sy253_sdk'

/***************************************
 * test
 ****************************************/
include(":workspace-test:test")
include(":workspace-test:test-compose")

/***************************************
 * 外包-喵不易(已停运)
 ****************************************/
//include(":workspace-mby:catroom")
//include(":workspace-mby:iot:hub:device-java")
//include(":workspace-mby:iot:hub:device-android")
//include(":workspace-mby:iot:explorer:device-java")
//include(":workspace-mby:iot:explorer:device-android")

/***************************************
 * 外包-贝宁贷款(已停运)
 ****************************************/
include(":workspace-pretimmediat:pretimmediat")