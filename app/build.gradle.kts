plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.hospitaldetails"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.hospitaldetails"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Android Lint: Fragment lifecycle rules for the UHC app.
    // Issue IDs and severities live in config/lint/lint.xml.
    lint {
        lintConfig = rootProject.file("config/lint/lint.xml")
        // Existing issues are recorded here; only NEW issues fail the build.
        // Regenerate with: ./gradlew :app:updateLintBaseline
        baseline = file("lint-baseline.xml")
        abortOnError = true          // any error/fatal issue fails the Gradle task (and CI)
        warningsAsErrors = false
        checkReleaseBuilds = true
        checkDependencies = true
        explainIssues = true
        xmlReport = true             // parsed by Jenkins Warnings NG
        htmlReport = true            // human-readable report
        sarifReport = true           // uploaded to GitHub code scanning
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // Declared explicitly so the latest fragment-lint checks are on the lint classpath.
    implementation(libs.fragment)
    lintChecks(project(":lint-checks"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.9.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.9.0")
}