#!/usr/bin/env bash

set -e
BUILD_GRADLE=./build.gradle.kts
BUILD_SETTINGS=./settings.gradle.kts
REPO=https://github.com/nabla-squared/laplacian.generator/raw/master
GRADLEW_DIR=gradle/wrapper

set -x

cat > $BUILD_GRADLE <<END_OF_FILE
plugins {
    java
    maven
    id("laplacian.generator") version "1.0.0"
}
repositories {
    maven(url = "../mvn-repo/")
    maven(url = "$REPO")
    jcenter()
}
END_OF_FILE

cat > $BUILD_SETTINGS <<END_OF_FILE
pluginManagement {
    repositories {
		maven(url = "../mvn-repo/")
        maven(url = "$REPO")
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
&& curl -Ls -o gradlew $REPO/gradlew \
&& curl -Ls -o $GRADLEW_DIR/gradle-wrapper.jar $REPO/$GRADLEW_DIR/gradle-wrapper.jar \
&& curl -Ls -o $GRADLEW_DIR/gradle-wrapper.properties $REPO/$GRADLEW_DIR/gradle-wrapper.properties \
&& chmod 755 gradlew

./gradlew lM --stacktrace

mkdir -p model
mkdir -p template

