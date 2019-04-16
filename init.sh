#!/usr/bin/env bash

set -e

BUILD_GRADLE=./build.gradle.kts
BUILD_SETTINGS=./settings.gradle.kts
HOST=https://raw.githubusercontent.com/nabla-squared/laplacian.generator/master
GRADLEW_DIR=gradle/wrapper

#set -x

cat > $BUILD_GRADLE <<'END_OF_FILE'
plugins {
    java
    maven
    id("laplacian.generator") version "1.0.0"
}
repositories {
    maven(url = "../mvn-repo/")
    maven(url = "https://raw.github.com/nabla-squared/mvn-repo/master/")
    jcenter()
}
END_OF_FILE

cat > $BUILD_SETTINGS <<'END_OF_FILE'
pluginManagement {
    repositories {
		maven(url = "../mvn-repo/")
        maven(url = "https://raw.github.com/nabla-squared/mvn-repo/master/")
        gradlePluginPortal()
        jcenter()
    }
}
END_OF_FILE

[ ! -f laplacian-module.y?ml ] \
&& cat > laplacian-module.yml <<END_OF_FILE
project:
  group: my-group
  name: ${PWD##*/}
  type: generator
  version: "1.0.0"
END_OF_FILE

[ ! -f gradlew ] \
&& mkdir -p $GRADLEW_DIR \
&& curl -o gradlew $HOST/gradlew \
&& curl -o $GRADLEW_DIR/gradle-wrapper.jar $HOST/$GRADLE_DIR/gradle-wrapper.jar \
&& curl -o $GRADLEW_DIR/gradle-wrapper.properties $HOST/$GRADLE_DIR/gradle-wrapper.properties \
&& chmod 755 gradlew

./gradlew lM --stacktrace

mkdir -p model
mkdir -p template

