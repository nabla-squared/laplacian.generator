#!/usr/bin/env bash
set -e
SCRIPT_BASE_DIR=$(cd $"${BASH_SOURCE%/*}" && pwd)
PROJECT_BASE_DIR=$(cd $SCRIPT_BASE_DIR && cd .. && pwd)

LF=$'\n'

GRADLE_PROJECT_DIR=$SCRIPT_BASE_DIR/build/laplacian
GRADLE_FILE=$GRADLE_PROJECT_DIR/build.gradle.kts
SETTINGS_FILE=$GRADLE_PROJECT_DIR/settings.gradle.kts
GRADLE_RUNTIME_DIR=$GRADLE_PROJECT_DIR/gradle/wrapper

MODEL_FILES=()
TEMPLATE_FILES=()
TARGET_DIR=$PROJECT_BASE_DIR/generated
LOCAL_REPO_PATH=$PROJECT_BASE_DIR/../mvn-repo
REMOTE_REPO_PATH='https://raw.github.com/nabla-squared/mvn-repo/master'
RAW_HOST='https://raw.githubusercontent.com/nabla-squared/laplacian.generator/master'

HELP=
VERBOSE=
PLUGINS=
MODULES=

main () {
  PLUGINS=$(plugin_def 'laplacian:laplacian.generator:1.0.0')
  MODULES=$(module_def implementation 'laplacian:laplacian.generator:1.0.0')
  mkdir -p $GRADLE_PROJECT_DIR

  while getopts 'hv-:' OPTION;
  do
    case $OPTION in
    -)
      case $OPTARG in
      target-dir)
        TARGET_DIR="$PROJECT_BASE_DIR/${!OPTIND}"; OPTIND=$(($OPTIND+1))
        ;;
      model-files)
        MODEL_FILES+=("${!OPTIND}"); OPTIND=$(($OPTIND+1))
        ;;
      template-files)
        TEMPLATE_FILES+=("${!OPTIND}"); OPTIND=$(($OPTIND+1))
        ;;
      schema)
        PLUGINS="$PLUGINS$LF    $(plugin_def ${!OPTIND})"; OPTIND=$(($OPTIND+1))
        ;;
      template)
        MODULES="$MODULES$LF    $(module_def template ${!OPTIND})"; OPTIND=$(($OPTIND+1))
        ;;
      esac
      ;;
    h) HELP='help' ;;
    v) VERBOSE='verbose' ;;
    esac
  done

  [ ! -z $VERBOSE ] && set -x
  cd $SCRIPT_BASE_DIR
  [ ! -z $HELP ] && show_usage && exit 0
  gradle_file
  settings_file
  install_gradle_runtime
  (
    cd $GRADLE_PROJECT_DIR
    ./gradlew \
      --stacktrace \
      --build-file $GRADLE_FILE \
      --settings-file $SETTINGS_FILE \
      --project-dir $GRADLE_PROJECT_DIR \
      laplacianGenerate \
      ${VERBOSE:+-i}
  )
}

settings_file () {
  cat <<END > $SETTINGS_FILE
pluginManagement {
    repositories {
        maven(url = "${LOCAL_REPO_PATH}")
        maven(url = "${REMOTE_REPO_PATH}")
        gradlePluginPortal()
        jcenter()
    }
}
END
}

gradle_file () {
  cat <<END > $GRADLE_FILE
import laplacian.gradle.task.LaplacianGenerateTask
import laplacian.gradle.task.LaplacianGenerateExtension
import laplacian.gradle.task.generate.ModelSpec
import laplacian.gradle.task.generate.TemplateSpec

plugins {
    \`maven-publish\`
    \`java-gradle-plugin\`
    kotlin("jvm") version "1.3.10"
    $PLUGINS
}
repositories {
    maven(url = "${LOCAL_REPO_PATH}")
    maven(url = "${REMOTE_REPO_PATH}")
    jcenter()
}
dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    $MODULES
}
configure<LaplacianGenerateExtension> {
  target.set(project.file("${TARGET_DIR}"))
$( set_model_files )
$( set_template_files )
}
END
}

set_model_files () {
  [ ${#MODEL_FILES[@]} -eq 0 ] && MODEL_FILES+='./model'
  printf "  modelSpec.get().from(File(\"${PROJECT_BASE_DIR}/%s\"))\n" "${MODEL_FILES[@]}"
}

set_template_files () {
  [ ${#TEMPLATE_FILES[@]} -eq 0 ] && TEMPLATE_FILES+='./template'
  printf "  templateSpec.get().from(File(\"${PROJECT_BASE_DIR}/%s\"))\n" "${TEMPLATE_FILES[@]}"
}

plugin_def () {
  IFS=':';
  local tokens=( $1 )
  echo "id(\"${tokens[1]}\") version \"${tokens[2]}\""
}

module_def () {
  IFS=':';
  local type=$1
  local module=$2
  echo "$type(\"$2\")"
}


show_usage () {
cat << END
Usage: $(basename "$0") [OPTION]...

  --model-dir PATH
    PATH of the directory where the model yaml files reside (default: \$PROJECT_BASE/model)

  --template-dir PATH
    PATH of the directory where the template files reside (default: \$PROJECT_BASE/template)

  --target-dir PATH
    PATH of the directory under which the generated resources are put (default: \$PROJECT_BASE/model)

  --local-repo PATH
    PATH of local repository where the maven modules hosted. (default: \$PROJECT_BASE/../mvn-repo)

  -h
    Display this help message.

  -v
    Verbose output
END
}

install_gradle_runtime () {
  [ -f $GRADLE_PROJECT_DIR/gradlew ] || (
    mkdir -p $GRADLE_RUNTIME_DIR
    curl -Ls -o $GRADLE_PROJECT_DIR/gradlew $RAW_HOST/gradlew
    curl -Ls -o $GRADLE_RUNTIME_DIR/gradle-wrapper.jar $RAW_HOST/gradle/wrapper/gradle-wrapper.jar
    curl -Ls -o $GRADLE_RUNTIME_DIR/gradle-wrapper.properties $RAW_HOST/gradle/wrapper/gradle-wrapper.properties
    chmod 755 $GRADLE_PROJECT_DIR/gradlew
  )
}

main "$@"
