#!/usr/bin/env bash

if [[ ! -d "$1" ]]
then
  echo "not a directory: [$1]"
  exit 1
fi

absolute_path="$(readlink -f "$1")"

shift

docker build -t photobackup-server . && docker run -it -v "$absolute_path":/storage -p 8080:8080 --rm photobackup-server "$@"
