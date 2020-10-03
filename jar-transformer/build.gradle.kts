@file:Suppress("UnstableApiUsage")

import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    id("org.jetbrains.dokka")
    signing
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

val dokkaDir = "${buildDir}/dokka"
tasks.withType<DokkaTask> {
    outputDirectory.set(file(dokkaDir))
}

val dokkaJarTaskProvider = tasks.register<Jar>("dokkaJar") {
    archiveClassifier.set("javadoc")
    from(dokkaDir)
    dependsOn("dokkaHtml")
}

val isReleaseBuild: Boolean get() = !version.toString().endsWith("-SNAPSHOT")

publishing {
    repositories {
        maven {
            url = uri(
                if (isReleaseBuild) {
                    "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                } else {
                    "https://oss.sonatype.org/content/repositories/snapshots"
                }
            )

            credentials {
                username = if (project.hasProperty("SONATYPE_NEXUS_USERNAME")) {
                    project.property("SONATYPE_NEXUS_USERNAME").toString()
                } else {
                    ""
                }
                password = if (project.hasProperty("SONATYPE_NEXUS_PASSWORD")) {
                    project.property("SONATYPE_NEXUS_PASSWORD").toString()
                } else {
                    ""
                }
            }
        }
    }

    publications {
        create<MavenPublication>("release") {
            from(components["java"])

            artifact(dokkaJarTaskProvider.get())

            pom {
                name.set("jarTransformer")
                description.set("Read and Transform the contents of jar file entry in the jar file.")
                url.set("https://github.com/nikeorever/jarTransformer")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("xianxueliang")
                        name.set("xianxueliang")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/nikeorever/jarTransformer.git")
                    developerConnection.set("scm:git:ssh://git@github.com/nikeorever/jarTransformer.git")
                    url.set("https://github.com/nikeorever/jarTransformer")
                }
            }
        }
    }
}

if (isReleaseBuild) {
    signing {
        sign(publishing.publications["release"])
    }
}

tasks.register("publishSnapshot") {
    if (!isReleaseBuild) {
        dependsOn(tasks.getByName("publish"))
    }
}

dependencies {
    testImplementation("junit:junit:4.13")
    testImplementation("com.google.truth:truth:1.0.1")
}