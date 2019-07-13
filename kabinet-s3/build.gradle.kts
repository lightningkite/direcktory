import com.lightningkite.konvenience.gradle.*
import java.util.Properties

plugins {
    kotlin("multiplatform")
    `maven-publish`
    id("org.jetbrains.dokka") version "0.9.17"
}

buildscript {
    repositories {
        mavenLocal()
        maven("https://dl.bintray.com/lightningkite/com.lightningkite.krosslin")
    }
    dependencies {
        classpath("com.lightningkite:konvenience:+")
    }
}
apply(plugin = "com.lightningkite.konvenience")

repositories {
    jcenter()
    mavenLocal()
    mavenCentral()
    maven("https://dl.bintray.com/lightningkite/com.lightningkite.krosslin")
}

val versions = Properties().apply {
    load(project.file("versions.properties").inputStream())
}

group = "com.lightningkite"
version = versions.getProperty("kabinet")

kotlin {
    sources() {
        main {
            dependency(standardLibrary)
            dependency(projectOrMavenDashPlatform("com.lightningkite", "kommon", versions.getProperty("kommon")))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "lokalize", versions.getProperty("lokalize")))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "kabinet-api", versions.getProperty("kabinet")))
        }
        test {
            dependency(testing)
            dependency(testingAnnotations)
        }
        isJvm.sources {
            main {
                dependencies {
                    api("com.amazonaws:aws-java-sdk-s3:1.11.+")
                }
            }
        }
//        isJs.sources {}
//        isNative.sources {}
    }

//    dokka(project) {
//        this.outputFormat = "html"
//    }
}

publishing {
    doNotPublishMetadata()
    repositories {
        bintray(
                project = project,
                organization = "lightningkite",
                repository = "com.lightningkite.krosslin"
        )
    }

    appendToPoms {
        github("lightningkite", project.name)
        licenseMIT()
        developers {
            developer {
                id.set("UnknownJoe796")
                name.set("Joseph Ivie")
                email.set("joseph@lightningkite.com")
                timezone.set("America/Denver")
                roles.set(listOf("architect", "developer"))
                organization.set("Lightning Kite")
                organizationUrl.set("http://www.lightningkite.com")
            }
        }
    }
}
