rootProject.name = "laplacian.generator"
pluginManagement {
    repositories {
        maven { url = uri("../mvn-repo/") }
        maven { url = uri("https://github.com/nabla-squared/raw/mvn-repo/") }
        gradlePluginPortal()
        jcenter()
    }
}
