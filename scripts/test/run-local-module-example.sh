#!/usr/bin/env bash
set -e
set -x
BASE_DIR=$(cd $"${BASH_SOURCE%/*}" && pwd)
PROJECT_ROOT_DIR=$BASE_DIR/../../..
MODULE_SOURCE_DIR=$BASE_DIR/basic-example
MODULE_FILE_PATH=$PROJECT_ROOT_DIR/build/libs/presentation-1.0.0.zip
DST_DIR=$PROJECT_ROOT_DIR/build/dest
LAPLACIAN_CACHE_DIR=~/.laplacian/cache

rm -rf $LAPLACIAN_CACHE_DIR
rm -f $MODULE_FILE_NAME
(cd $MODULE_SOURCE_DIR
  zip $MODULE_FILE_PATH -r template -r model
)

(cd $PROJECT_ROOT_DIR
  ./gradlew run --args="generate --source $MODULE_FILE_PATH --destination $DST_DIR"
)

tree $LAPLACIAN_CACHE_DIR
tree $DST_DIR
