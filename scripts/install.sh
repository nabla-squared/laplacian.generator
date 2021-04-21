#!/usr/bin/env bash

set -e
# set -x

RELEASE_VERSION="1.0.0"
RELEASE_BASE_URL="https://github.com/nabla-squared/laplacian.generator/releases/download/v${RELEASE_VERSION}"
CLI_DIST_FILE_NAME="laplacian-generator-cli-${RELEASE_VERSION}"
CLI_DIST_URL="${RELEASE_BASE_URL}/${CLI_DIST_FILE_NAME}.tar.gz"
CORE_PLUGIN_FILE_NAME="laplacian-generator.core-plugin-${RELEASE_VERSION}.jar"
CORE_PLUGIN_DIST_URL="${RELEASE_BASE_URL}/${CORE_PLUGIN_FILE_NAME}"

INSTALL_DIR_NAME=".laplacian"
INSTALL_DIR="${HOME}/${INSTALL_DIR_NAME}"
DISTRIBUTION_DIR="${INSTALL_DIR}/dist"
PLUGIN_DIR="${INSTALL_DIR}/plugin"
LAPLACIAN_HOME_DIR="${INSTALL_DIR}/dist/${CLI_DIST_FILE_NAME}"

INSTALL_DIR_WARNING="The Laplacian distribution was already installed under [${INSTALL_DIR}].
The all content of this folder will be wiped out. Continue? (y/N): "
INSTALLATION_MESSAGE="Installing Laplacian Generator v${RELEASE_VERSION} ..."
INSTALLATION_END_MESSAGE="...Finished."

main () {
  show_processing_message || die
  check_install_dir || die
  install_laplacian_cli || die
  install_core_plugins || die
  show_end_message || die
}

die() {
  echo "Installation failed."
  exit 1
}

show_processing_message () {
  echo "$INSTALLATION_MESSAGE"
}

check_install_dir() {
  if [[ -d $INSTALL_DIR ]]
  then
    read -p "${INSTALL_DIR_WARNING}" confirm </dev/tty && [[ $confirm == [yY] || $confirm == [yY][eE][sS] ]] || exit
  fi
  rm -rf $INSTALL_DIR &&
  mkdir -p $INSTALL_DIR
}

install_laplacian_cli() {
  mkdir -p $DISTRIBUTION_DIR && \
  cd $DISTRIBUTION_DIR && \
  (curl -Ls $CLI_DIST_URL | tar xvz)
}

install_core_plugins() {
  mkdir -p $PLUGIN_DIR && \
  curl -Ls -o "${PLUGIN_DIR}/${CORE_PLUGIN_FILE_NAME}" "${CORE_PLUGIN_DIST_URL}"
}

show_end_message () {
  echo "${INSTALLATION_END_MESSAGE}"
  if which laplacian
  then
    exit 0
  fi
  cat << EOF
  Add the following entries to you .bash_profile or .bashrc to persist the PATH setting:

  export LAPLACIAN_HOME="${LAPLACIAN_HOME_DIR}"
  export PATH="\$PATH:\$LAPLACIAN_HOME/bin"
EOF
}

main "$@"
