plugins {
    id("org.jetbrains.kotlin.js") version "1.3.50"
}

group = "de.joshuagleitze"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
}

kotlin.target.browser { }