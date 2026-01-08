plugins {
    kotlin("jvm") version "2.0.21"
}
val kotlinVersion = "2.0.21"
val agpVersion = "8.6.1"
repositories {
    maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    google()
    mavenCentral()
}
dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
    implementation("com.android.tools.build:gradle-api:${agpVersion}")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
}
