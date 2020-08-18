import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

dependencies {
    val kotlinReactVersion = "16.13.1-pre.110-kotlin-1.3.72"
    implementation(name = "kotlin-react", version = kotlinReactVersion, group = "org.jetbrains")
    implementation(name = "kotlin-react-dom", version = kotlinReactVersion, group = "org.jetbrains")
    val kotlinCssVersion = "1.0.0-pre.111-kotlin-1.4.0"
    implementation(name = "kotlin-styled", version = kotlinCssVersion, group = "org.jetbrains")
    implementation(name = "kotlin-css-js", version = kotlinCssVersion, group = "org.jetbrains")
    implementation(name = "kotlin-extensions", version = "1.0.1-pre.110-kotlin-1.3.72", group = "org.jetbrains")
    implementation(name = "kotlinx-html-js", version = "0.7.1", group = "org.jetbrains.kotlinx")

    implementation(project(":publishsubscribe"))
    implementation(project(":network"))
    implementation(project(":models:persons"))
    implementation(project(":models:guestlist"))
    implementation(project(":models:uml"))
    implementation(project(":models:java"))
    implementation(project(":models:openapi"))
    implementation(project(":models:turingmachine"))
    implementation(project(":models:primitives"))
    implementation(project(":transformations:persons2guests"))
    implementation(project(":transformations:uml2java"))
    implementation(project(":transformations:java2openapi"))
    implementation(project(":transformations:busybeaver3"))
    implementation(project(":transformations:incrementing"))

    val reactVersion = "16.13.1"
    implementation(npm("react", reactVersion))
    implementation(npm("react-dom", reactVersion))
    implementation(npm("react-is", reactVersion))
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
