#!/usr/bin/env bash

set -e
GROUP_NAME=nabla-squared
GENERATOR_NAME=laplacian.generator
BRANCH=master
REPO_BRANCH=master
REPO=https://raw.github.com/$GROUP_NAME/mvn-repo/$REPO_BRANCH
RAW_HOST=https://raw.githubusercontent.com/$GROUP_NAME/$GENERATOR_NAME/$BRANCH
MODEL_DIR=./model
TEMPLATE_DIR=./template
TARGET_DIR=./target
SCRIPT_DIR=./script/$GENERATOR_NAME
GENERATOR_SCRIPT=$SCRIPT_DIR/generate.sh
GRADLE_DIR=$SCRIPT_DIR/gradle
GRADLEW_DIR=$GRADLE_DIR/gradle/wrapper
BUILD_GRADLE=$GRADLE_DIR/build.gradle.kts
BUILD_SETTINGS=$GRADLE_DIR/settings.gradle.kts

main () {
  show_begin_message
  create_directory_structure
  create_build_gradle
  create_build_settings
  install_gradle_runtime
  create_generator_script
  show_end_message
}

create_directory_structure () {
  mkdir -p $MODEL_DIR $TEMPLATE_DIR $TARGET_DIR $SCRIPT_DIR $GRADLE_DIR $GRADLEW_DIR
}

create_build_gradle () {
cat > $BUILD_GRADLE << END_OF_FILE
import laplacian.gradle.task.LaplacianGenerateTask
import laplacian.gradle.task.generate.ModelSpec
import laplacian.gradle.task.generate.TemplateSpec

plugins {
    java
    maven
    id("laplacian.generator") version "1.0.0"
}
repositories {
    maven(url = "$REPO")
    jcenter()
}
val target_dir: String
    get() = extra["laplacian.generator.target"]?.toString() ?: "./target"

val model_dir: String
    get() = extra["laplacian.generator.model"] ?.toString() ?: "./model"

val template_dir: String
    get() = extra["laplacian.generator.template"] ?.toString() ?: "./template"

tasks.register<LaplacianGenerateTask>("generate") {
    ModelSpec(project).also { spec ->
        modelSpec.set(spec)
        spec.from(File(model_dir))
    }
    TemplateSpec(project).also { spec ->
        templateSpec.set(spec)
        spec.from(File(template_dir))
    }
    target.set(File(target_dir))
    prepare()
}
END_OF_FILE
}

create_build_settings () {
cat > $BUILD_SETTINGS <<END_OF_FILE
pluginManagement {
    repositories {
        maven(url = "$REPO")
        gradlePluginPortal()
        jcenter()
    }
}
END_OF_FILE
}

install_gradle_runtime () {
  [ ! -f  $GRADLEW_DIR ] && (
    mkdir -p $GRADLEW_DIR
    curl -Ls -o $GRADLE_DIR/gradlew $RAW_HOST/gradlew
    curl -Ls -o $GRADLEW_DIR/gradle-wrapper.jar $RAW_HOST/gradle/wrapper/gradle-wrapper.jar
    curl -Ls -o $GRADLEW_DIR/gradle-wrapper.properties $RAW_HOST/gradle/wrapper/gradle-wrapper.properties
    chmod 755 $GRADLE_DIR/gradlew
  )
}

create_generator_script () {
  cat > $GENERATOR_SCRIPT << 'END_OF_FILE'
#!/usr/bin/env bash
set -e
SCRIPT_BASE_DIR=$(cd $"${BASH_SOURCE%/*}" && pwd)
PROJECT_BASE_DIR=$(cd $SCRIPT_BASE_DIR && cd ../.. && pwd)

generator=generate
model=$PROJECT_BASE_DIR/model
template=$PROJECT_BASE_DIR/template
dest=$PROJECT_BASE_DIR/target
help=
verbose=


while getopts 'm:t:d:g:hv' OPTION;
do
  case $OPTION in
  m) model=$OPTARG ;;
  t) template=$OPTARG ;;
  d) dest=$OPTARG ;;
  g) generator=$OPTARG ;;
  h) help='help' ;;
  v) verbose='verbose' ;;
  esac
done

main () {
  [ ! -z $verbose ] && set -x
  cd $SCRIPT_BASE_DIR
  [ ! -z $help ] && show_usage && exit 0
  (
    cd ./gradle
    ./gradlew \
      --stacktrace \
      $generator \
      ${verbose:+-i} \
      ${model:+-Plaplacian.generator.model=}$model \
      ${template:+-Plaplacian.generator.template=}$template \
      ${dest:+-Plaplacian.generator.target=}$dest
  )
}

show_usage () {
cat << END
Usage: $(basename "$0") [OPTION]...

  -m VALUE    The directory which contains model yaml files (default: \$PROJECT_DIR/model)
  -t VALUE    The directory which contains template files (default: \$PROJECT_DIR/template)
  -d VALUE    The destination directory of this generation task (default: \$PROJECT_DIR/target)
  -h          Display this help message.
  -v          Verbose output
END
}

main
END_OF_FILE
  chmod 755 $GENERATOR_SCRIPT
}

show_begin_message () {
  echo "Installing $GENERATOR_NAME script.."
}

show_end_message () {
  echo    ".. finished."
  echo    "Run the following command on your terminal:"
  echo -e "$ \x1B[32m$GENERATOR_SCRIPT\x1B[0m"
  $GENERATOR_SCRIPT -h
}

main

