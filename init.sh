#!/usr/bin/env bash

set -e

BUILD_GRADLE=./build.gradle.kts
BUILD_SETTINGS=./settings.gradle.kts

set -x

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

echo $SHELL

[ ! -f laplacian-module.y?ml ] \
&& cat > laplacian-module.yml <<END_OF_FILE
project:
  group: my-group
  name: ${PWD##*/}
  type: generator
  version: "1.0.0"
END_OF_FILE

gradle lM --stacktrace

mkdir -p model
mkdir -p template

