#!/usr/bin/env bash
set -e
PROJECT_BASE_DIR=$(cd $"${BASH_SOURCE%/*}/../" && pwd)
SCRIPT_BASE_DIR="$PROJECT_BASE_DIR/scripts"

GIT_IGNORE_FILE="$PROJECT_BASE_DIR/.gitignore"
GIT_IGNORE_REPO_URL="https://raw.githubusercontent.com/github/gitignore/master"

main () {
  new_file
  ignore "Global/Archives"
  ignore "Global/Backup"
  ignore "Global/Linux"
  ignore "Global/macOS"
  ignore "Global/Windows"
  ignore "Global/MicrosoftOffice"
  ignore "Global/Vim"
  ignore "Global/VisualStudioCode"
  ignore "Global/Eclipse"
  ignore "Java"
  ignore "Gradle"
  ignore "Kotlin"
}

new_file() {
  rm -rf "$GIT_IGNORE_FILE"
  echo 'dist/' >> $GIT_IGNORE_FILE
  echo '.idea/' >> $GIT_IGNORE_FILE
  echo '.project' >> $GIT_IGNORE_FILE
  echo '' >> $GIT_IGNORE_FILE
}

ignore() {
  local ignore_target=$1
  echo "## $ignore_target" >> $GIT_IGNORE_FILE
  curl -Ls "$GIT_IGNORE_REPO_URL/${ignore_target}.gitignore" >> $GIT_IGNORE_FILE
  echo '' >> $GIT_IGNORE_FILE
}

main