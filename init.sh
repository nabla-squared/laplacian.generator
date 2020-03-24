#!/usr/bin/env bash

set -e
RAW_HOST=https://raw.githubusercontent.com/nabla-squared/laplacian.template.project.base/master
set -x

main () {
  show_processing_message
  install_laplacian
  show_end_message
}

install_laplacian () {
  local SCRIPTS_DIR=scripts
  local LAPLACIAN_GENERATOR=$SCRIPTS_DIR/laplacian-generate.sh
  local PROJECT_GENERATOR=$SCRIPTS_DIR/laplacian-project-generate.sh
  mkdir -p ./$SCRIPTS_DIR && (
    curl -Ls -o ./$LAPLACIAN_GENERATOR $RAW_HOST/template/$LAPLACIAN_GENERATOR
    curl -Ls -o ./$PROJECT_GENERATOR $RAW_HOST/template/$PROJECT_GENERATOR
    chmod 755 ./$LAPLACIAN_GENERATOR ./$PROJECT_GENERATOR
  )
}

show_processing_message () {
  echo "Installing Laplacian Generator scripts.."
}

show_end_message () {
  echo    ".. Finished."
}

main "$@"
