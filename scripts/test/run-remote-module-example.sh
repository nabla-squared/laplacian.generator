#!/usr/bin/env bash
set -e
set -x
BASE_DIR=$(cd $"${BASH_SOURCE%/*}" && pwd)
PROJECT_ROOT_DIR="$BASE_DIR/../../.."
MODULE_URL='https://raw.githubusercontent.com/nabla-squared/laplacian.generator/2.0/subprojects/cli/src/test/resources/presentation-1.0.0.zip'
DST_DIR="$PROJECT_ROOT_DIR/build/dest"
LAPLACIAN_CACHE_DIR=~/.laplacian/cache

rm -rf $LAPLACIAN_CACHE_DIR
(cd $PROJECT_ROOT_DIR
  pwd
  ./gradlew run --args="generate --source $MODULE_URL --destination $DST_DIR"
)

tree $LAPLACIAN_CACHE_DIR
tree $DST_DIR
