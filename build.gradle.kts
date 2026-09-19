// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        // O AGP 9 traz Kotlin embutido na versao 2.2.10 por padrao. Aqui subimos o
        // Kotlin Gradle Plugin (compilador) para 2.4.20, compativel com as bibliotecas
        // (Compose, etc.) compiladas com Kotlin 2.4.
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.20")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
