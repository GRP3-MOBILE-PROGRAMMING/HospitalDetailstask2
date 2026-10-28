import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Custom Android Lint rules for the UHC app's Fragment lifecycle bugs.
// Consumed by :app through lintChecks(project(":lint-checks")).
plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.android.lint)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks.test {
    // Lint detector tests resolve android.* classes from a real SDK. CI sets ANDROID_HOME;
    // locally fall back to sdk.dir from local.properties.
    val sdkDir = System.getenv("ANDROID_HOME")
        ?: rootProject.file("local.properties").takeIf { it.exists() }?.let { file ->
            Properties().apply { file.inputStream().use { load(it) } }.getProperty("sdk.dir")
        }
    if (sdkDir != null) environment("ANDROID_HOME", sdkDir)
}

dependencies {
    // Provided by the lint runtime, so never packaged into the jar.
    compileOnly(libs.lint.api)
    compileOnly(libs.lint.checks)

    testImplementation(libs.lint.api)
    testImplementation(libs.lint.checks)
    testImplementation(libs.lint.tests)
    testImplementation(libs.junit)
}
