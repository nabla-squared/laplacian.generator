#!/usr/bin/env bash

set -e
RAW_HOST=https://raw.githubusercontent.com/laplacian/laplacian.generator/master
set -x

main () {
  show_processing_message
  install_laplacian
  show_end_message
}

install_laplacian () {
  local SCRIPTS_DIR=scripts
  local SCRIPT_PATH=$SCRIPTS_DIR/laplacian-generate.sh

  mkdir -p ./$SCRIPTS_DIR && (
    curl -Ls -o ./$SCRIPT_PATH $RAW_HOST/$SCRIPT_PATH
    curl -Ls -o ./.gitignore $RAW_HOST/.gitignore
    chmod 755 ./$SCRIPT_PATH
  )
}

show_processing_message () {
  echo "Installing Laplacian Generator scripts.."
}

show_end_message () {
  echo    ".. Finished."
}

main "$@"

