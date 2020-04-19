group = "top.qiyuey.demo"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.72" apply false
    id("com.google.protobuf") version "0.8.12" apply false
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    idea
}

subprojects {
    buildscript {
        extra["protobufVersion"] = "3.11.4"
    }
    apply(plugin = "io.spring.dependency-management")
    dependencyManagement {
        imports {
            mavenBom("org.jetbrains.kotlin:kotlin-bom:1.3.72")
            mavenBom("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.3.5")
            mavenBom("io.grpc:grpc-bom:1.28.1")
        }
        dependencies {
            dependency("io.grpc:grpc-kotlin-stub:0.1.1")
            dependency("javax.annotation:javax.annotation-api:1.3.2")
        }
    }
}