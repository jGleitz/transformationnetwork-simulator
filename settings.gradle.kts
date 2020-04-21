rootProject.name = "transformationnetwork-simulator"

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}

include(
    ":publishsubscribe",
    ":metametamodel",
    ":modeltransformation",
    ":changemetamodel",
    ":changerecording",
    ":network",
    ":models:persons",
    ":models:guestlist",
    ":models:openapi",
    ":models:uml",
    ":models:java",
    ":models:turingmachine",
    ":models:number",
    ":transformations:persons2guests",
    ":transformations:uml2java",
    ":transformations:java2openapi",
    ":transformations:busybeaver3",
    ":transformations:incrementing",
    ":simulator"
)

