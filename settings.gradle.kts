rootProject.name = "transformationnetwork-simulator"

pluginManagement {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

include(":publishsubscribe")
include(":metametamodel")
include(":modeltransformation")
include(":changemetamodel")
include(":changerecording")
include(":network")
include(":models:persons")
include(":models:guestlist")
include(":models:openapi")
include(":models:uml")
include(":models:java")
include(":transformations:persons2guests")
include(":transformations:uml2java")
include(":transformations:java2openapi")
include(":simulator")

