#!/usr/bin/env bash
set -e
PROJECT_BASE_DIR=$(cd $"${BASH_SOURCE%/*}/../" && pwd)
VERSION='1.0.0'
GRADLE='./gradlew'

SUBPROJECTS_DIR="$PROJECT_BASE_DIR/subprojects"
DISTRIBUTION_DIR="$PROJECT_BASE_DIR/dist"
LAPLACIAN_HOME="$HOME/.laplacian"
LAPLACIAN_DIST_DIR="$LAPLACIAN_HOME/dist/laplacian-generator-cli-${VERSION}"
LAPLACIAN_PLUGIN_DIR="$LAPLACIAN_HOME/plugin"
LAPLACIAN_DIST_LIB_DIR="$LAPLACIAN_DIST_DIR/lib"
API_PROJECT_DIR="$SUBPROJECTS_DIR/api"
API_BUILT_MODULE_PATH="$API_PROJECT_DIR/build/libs/laplacian-generator-api-${VERSION}.jar"
CLI_PROJECT_DIR="$SUBPROJECTS_DIR/cli"
CLI_BUILT_MODULE_PATH="$CLI_PROJECT_DIR/build/libs/laplacian-generator-cli-${VERSION}.jar"
CLI_FAT_JAR_PATH="$CLI_PROJECT_DIR/build/distributions/laplacian-generator-cli-${VERSION}.tar.gz"
CORE_PLUGIN_PROJECT_DIR="$SUBPROJECTS_DIR/core-plugin"
CORE_PLUGIN_BUILT_MODULE_PATH="$CORE_PLUGIN_PROJECT_DIR/build/libs/laplacian-generator.core-plugin-${VERSION}.jar"

INSTALL_SCRIPT_PATH="$PROJECT_BASE_DIR/scripts/install.sh"

main() {
  # set -x
  build_api || die
  build_cli || die
  build_core_plugin || die
  update_distribution || die
  install || die
}

die() {
  echo "$0 Failed!!" 1>&2
  exit 1
}

build_api() {
  (cd $API_PROJECT_DIR
    $GRADLE publish
  )
}

build_cli() {
  (cd $CLI_PROJECT_DIR
    $GRADLE build
  )
}

build_core_plugin() {
  (cd $CORE_PLUGIN_PROJECT_DIR
    $GRADLE build
  )
}

update_distribution() {
  mkdir -p "$DISTRIBUTION_DIR" && \
  cp -f "$CLI_FAT_JAR_PATH" "$DISTRIBUTION_DIR" && \
  cp -f "$CORE_PLUGIN_BUILT_MODULE_PATH" "$DISTRIBUTION_DIR" && \
  cp -f "$INSTALL_SCRIPT_PATH" "$DISTRIBUTION_DIR"
}

install() {
  cp -f "$API_BUILT_MODULE_PATH" "$LAPLACIAN_DIST_LIB_DIR" && \
  cp -f "$CLI_BUILT_MODULE_PATH" "$LAPLACIAN_DIST_LIB_DIR" && \
  cp -f "$CORE_PLUGIN_BUILT_MODULE_PATH" "$LAPLACIAN_PLUGIN_DIR"
}

main