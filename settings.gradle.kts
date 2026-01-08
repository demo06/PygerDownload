pluginManagement {
    repositories {
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://developer.huawei.com/repo/")
        maven("https://repo1.maven.org/maven2/")
        maven("https://jitpack.io")
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://developer.huawei.com/repo/")
        maven("https://repo1.maven.org/maven2/")
        maven("https://jitpack.io")
        mavenCentral()
        google()
    }
}

rootProject.name = "PygerDownload"
include(":app")
