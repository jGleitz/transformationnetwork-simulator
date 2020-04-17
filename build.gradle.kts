plugins {
    kotlin("js") version "1.3.72"
    idea
}

allprojects {
    group = "de.joshuagleitze"
    version = "1.0-SNAPSHOT"

    repositories {
        jcenter()
        maven("https://kotlin.bintray.com/kotlin-js-wrappers")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

val spekVersion: String by project

subprojects {
    apply {
        // must use `apply` in `subprojects`, `plugins` does not work
        plugin("idea")
        plugin("org.jetbrains.kotlin.js")
    }

    dependencies {
        implementation(kotlin("stdlib-js"))

        testImplementation(kotlin("test-js"))
    }

    kotlin {
        target {
            browser()
            compilations.all {
                compileKotlinTask.kotlinOptions.moduleKind = "commonjs"
            }
        }
    }

    idea {
        module {
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }
}
