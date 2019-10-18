plugins {
    kotlin("js") version "1.3.50"
    idea
}

group = "de.joshuagleitze"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    maven { setUrl("https://kotlin.bintray.com/kotlin-js-wrappers") }
}

kotlin {
    target.browser()
    sourceSets["main"].dependencies {
        implementation(npm("react", "16.9.0"))
        implementation(npm("react-dom", "16.9.0"))
        implementation(npm("core-js", "3.3.2"))
    }
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(name = "kotlin-react-dom", version = "16.9.0-pre.85-kotlin-1.3.50", group = "org.jetbrains")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}