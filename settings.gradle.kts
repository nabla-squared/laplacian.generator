rootProject.name = "laplacian.generator"
pluginManagement {
    repositories {
        maven { url = uri("../mvn-repo/") }
        maven { url = uri("https://raw.github.com/nabla-squared/mvn-repo/master/") }
        gradlePluginPortal()
        jcenter()
    }
}
