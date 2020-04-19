import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm")
    id("com.google.protobuf")
    `maven-publish`
}

group = "top.qiyuey.demo"
version = "1.0-SNAPSHOT"

dependencies {
    // kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    // grpc
    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-stub")
    implementation("io.grpc:grpc-kotlin-stub")
    // java
    if (JavaVersion.current().isJava9Compatible) {
        implementation("javax.annotation:javax.annotation-api")
    }
}

protobuf {
    val versions = dependencyManagement.managedVersions
    protoc {
        artifact = "com.google.protobuf:protoc:${extra["protobufVersion"]}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${versions["io.grpc:protoc-gen-grpc-java"]}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${versions["io.grpc:grpc-kotlin-stub"]}"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach { task ->
            task.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

// https://github.com/google/protobuf-gradle-plugin/issues/109
sourceSets {
    main {
        java.srcDir("build/generated/source/proto/main/grpc")
        java.srcDir("build/generated/source/proto/main/grpckt")
        java.srcDir("build/generated/source/proto/main/java")
        java.srcDir("src/main/proto")
    }
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            pom {
                licenses {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
                developers {
                    developer {
                        id.set("qiyuey")
                        name.set("Yu Chuan")
                        email.set("qiyuey@vip.qq.com")
                    }
                }
            }
        }
    }
}