import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

dependencies {
    implementation(name = "kotlin-react", version = "16.13.0-pre.97-kotlin-1.3.72", group = "org.jetbrains")
    implementation(name = "kotlin-react-dom", version = "16.13.0-pre.97-kotlin-1.3.72", group = "org.jetbrains")
    implementation(name = "kotlin-styled", version = "1.0.0-pre.94-kotlin-1.3.70", group = "org.jetbrains")

    implementation(project(":publishsubscribe"))
    implementation(project(":network"))
    implementation(project(":models:persons"))
    implementation(project(":models:guestlist"))
    implementation(project(":models:uml"))
    implementation(project(":models:java"))
    implementation(project(":models:openapi"))
    implementation(project(":transformations:persons2guests"))
    implementation(project(":transformations:uml2java"))
    implementation(project(":transformations:java2openapi"))

    implementation(npm("react", "16.13.1"))
    implementation(npm("react-dom", "16.13.1"))
    implementation(npm("core-js", "3.6.5"))
    implementation(npm("styled-components", "5.1.0"))
    implementation(npm("inline-style-prefixer", "6.0.0"))
    implementation(npm("react-select", "3.1.0"))
    implementation(npm("use-force-update", "1.0.7"))
}

project.tasks.withType<KotlinWebpack> {
    devServer = devServer?.copy(open = false)
    outputFileName = "bundle.js"
}
