#!/usr/bin/env bash

if [[ ! -d "$1" ]]
then
  echo "not a directory: [$1]"
  exit 1
fi

clear; docker build -t photobackup-server . && docker run -it --rm photobackup-server -v "$1":/storage
