#!/usr/bin/env bash
set -e
set -x
BASE_DIR=$(cd $"${BASH_SOURCE%/*}" && pwd)
TEMPLATE_DIR="$BASE_DIR/presentation/template"
MODEL_DIR="$BASE_DIR/presentation/model"
ADDITIONAL_TEMPLATE_DIR="$BASE_DIR/presentation/additional-template"
ADDITIONAL_SRC_DIR="$BASE_DIR/additional-content"
DST_DIR="$BASE_DIR/dest"
PLUGIN_ROOT_DIR="$BASE_DIR/presentation/plugin"

(cd $BASE_DIR
  laplacian generate \
    --model $MODEL_DIR \
    --template $TEMPLATE_DIR \
    --template $ADDITIONAL_TEMPLATE_DIR \
    --destination $DST_DIR \
    --no-cache
)
tree $DST_DIR
