pluginManagement {
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
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        // 阿里云Maven仓库（优先使用）
        maven {
            url = uri("https://maven.aliyun.com/repository/public")
        }
        maven {
            url = uri("https://maven.aliyun.com/repository/google")
        }
        maven {
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
        }
        maven {
            url = uri("https://maven.aliyun.com/repository/central")
        }
        // 官方仓库（备用）
        google()
        mavenCentral()
        gradlePluginPortal()
        // 华为HMS Maven仓库
        maven {
            url = uri("https://developer.huawei.com/repo/")
        }
        // Spring Boot仓库
        maven {
            url = uri("https://repo.spring.io/milestone")
        }
        maven {
            url = uri("https://repo.spring.io/snapshot")
        }
        // 火山引擎Maven仓库
        maven {
            url = uri("https://artifact.bytedance.com/repository/Volcengine/")
        }
    }
}

rootProject.name = "My Application"
include(":app")
