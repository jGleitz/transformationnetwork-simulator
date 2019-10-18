dependencies {
    implementation(name = "kotlin-react-dom", version = "16.9.0-pre.85-kotlin-1.3.50", group = "org.jetbrains")
    implementation(project(":example-models:persons"))
}

kotlin {
    sourceSets["main"].dependencies {
        implementation(npm("react", "16.9.0"))
        implementation(npm("react-dom", "16.9.0"))
        implementation(npm("core-js", "3.3.2"))
    }
    target {

    }
}