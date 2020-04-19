pluginManagement {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")

        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

rootProject.name = "demo-grpc-kotlin"

include("demo-grpc-kotlin-service")
include("demo-grpc-kotlin-server")
include("demo-grpc-kotlin-client")