group = "laplacian"
version = "1.0.0"

plugins {
	`maven-publish`
    `java-gradle-plugin`
    kotlin("jvm") version "1.3.21"
}

repositories {
    maven(url = "../mvn-repo/")
    maven(url = "https://raw.github.com/nabla-squared/mvn-repo/master/")
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    api("com.github.jknack:handlebars:4.1.2")
    implementation("org.yaml:snakeyaml:1.24")
    implementation("org.atteo:evo-inflector:1.2.2")
    implementation("net.sourceforge.plantuml:plantuml:1.2019.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}

val sourcesJar by tasks.creating(Jar::class.java) {
    archiveClassifier.set("sources")
	from(sourceSets.main.get().allSource)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("laplacianPlugin") {
            id = "laplacian.generator"
            implementationClass = "laplacian.gradle.GeneratorPlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri("$projectDir/../mvn-repo/")
        }
    }
    publications.create("mavenJava", MavenPublication::class.java) {
        from(components["java"])
        artifact(sourcesJar)
    }
}
