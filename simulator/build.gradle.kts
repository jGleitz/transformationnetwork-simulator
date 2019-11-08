import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

dependencies {
    implementation(name = "kotlin-react-dom", version = "16.9.0-pre.87-kotlin-1.3.50", group = "org.jetbrains")
    implementation(name = "kotlin-styled", version = "1.0.0-pre.85-kotlin-1.3.50", group = "org.jetbrains")
    implementation(project(":metametamodel"))
    implementation(project(":modeltransformation"))
    implementation(project(":models:persons"))
    implementation(project(":models:guestlist"))
    implementation(project(":transformations:persons2guests"))
}

kotlin {
    sourceSets["main"].dependencies {
        implementation(npm("react", "16.9.0"))
        implementation(npm("react-dom", "16.9.0"))
        implementation(npm("core-js", "3.3.2"))
        implementation(npm("styled-components", "4.4.0"))
        implementation(npm("inline-style-prefixer", "5.1.0"))
    }
}

project.tasks.withType<KotlinWebpack> {
    devServer = devServer?.copy(open = false)
}