// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("org.jetbrains.compose") version "1.6.0" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}

// 仓库配置已移至 settings.gradle.kts