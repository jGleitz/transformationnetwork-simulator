plugins {
    kotlin("js") version "1.4.10"
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

allprojects {
    apply {
        // must use `apply` in `subprojects`, `plugins` does not work
        plugin("idea")
        plugin("org.jetbrains.kotlin.js")
    }

    dependencies {
        testImplementation(kotlin("test-js"))
    }

    kotlin {
        js {
            browser()
            useCommonJs()
        }
    }

    idea {
        module {
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }
}
