import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

dependencies {
    implementation(name = "kotlin-react-dom", version = "16.9.0-pre.91-kotlin-1.3.61", group = "org.jetbrains")
    implementation(name = "kotlin-styled", version = "1.0.0-pre.91-kotlin-1.3.61", group = "org.jetbrains")

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

    implementation(npm("react", "16.9.0"))
    implementation(npm("react-dom", "16.9.0"))
    implementation(npm("core-js", "3.3.2"))
    implementation(npm("styled-components", "4.4.0"))
    implementation(npm("inline-style-prefixer", "5.1.0"))
    implementation(npm("react-select", "3.0.8"))
    implementation(npm("use-force-update", "1.0.7"))
}

project.tasks.withType<KotlinWebpack> {
    devServer = devServer?.copy(open = false)
    outputFileName = "bundle.js"
}
