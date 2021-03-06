pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        jcenter()
        mavenLocal()
        maven("https://dl.bintray.com/lightningkite/com.lightningkite.krosslin")
    }
}

enableFeaturePreview("GRADLE_METADATA")

include(":kabinet-api")
include(":kabinet-s3")