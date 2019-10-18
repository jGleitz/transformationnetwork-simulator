plugins {
    id("org.jetbrains.kotlin.js") version "1.3.50"
    idea
}

group = "de.joshuagleitze"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
}

kotlin.target.browser {}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}