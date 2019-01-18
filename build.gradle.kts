import laplacian.gradle.task.ProjectTemplate
import org.gradle.api.tasks.bundling.Jar
import laplacian.metamodel.MetamodelModelLoader

group = "laplacian"
version = "1.0.0"

buildscript {
    repositories {
        maven {
            url = uri("../maven2/")
        }
        maven {
            url = uri("https://bitbucket.org/nabla2/maven2/raw/master/")
        }
    }
    dependencies {
        classpath("laplacian:laplacian-gradle-plugin:1.0.0")
    }
}

defaultTasks = listOf("build")

repositories {
    jcenter()
}

plugins {
    kotlin("jvm") version "1.3.10"
	`maven-publish`
}

dependencies {
    compile(gradleApi())
    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))
    compile("org.yaml:snakeyaml:1.22")
    compile("com.github.jknack:handlebars:4.1.0")
    compile("org.atteo:evo-inflector:1.2.2")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testCompile("org.junit.jupiter:junit-jupiter-engine:5.2.0")
}

val sourcesJar by tasks.creating(Jar::class.java) {
    classifier = "sources"
	from(sourceSets.main.get().allSource)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val generateMetamodel by tasks.creating(ProjectTemplate::class.java) {
    model {
        loader(MetamodelModelLoader())
        dir("model")
        include("**/*.yml")
    }
    template {
        from("template/metamodel/src")
        into("src")
    }
    template {
        from("template/metamodel/schema")
        into("schema")
    }
    template {
        from("template/metamodel/vscode")
        into(".vscode")
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
