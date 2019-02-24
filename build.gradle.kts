group = "laplacian"
version = "1.0.0"

plugins {
	`maven-publish`
    `java-gradle-plugin`
    kotlin("jvm") version "1.3.10"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.yaml:snakeyaml:1.22")
    implementation("com.github.jknack:handlebars:4.1.0")
    implementation("org.atteo:evo-inflector:1.2.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.2.0")
}

val sourcesJar by tasks.creating(Jar::class.java) {
    classifier = "sources"
	from(sourceSets.main.get().allSource)
}

tasks.withType<Test> {
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
            url = uri("$projectDir/../maven2/")
        }
    }
    publications.create("mavenJava", MavenPublication::class.java) {
        from(components["java"])
        artifact(sourcesJar)
    }
}
