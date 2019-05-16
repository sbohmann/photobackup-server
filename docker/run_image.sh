#!/usr/bin/env bash

if [[ ! -d "$1" ]]
then
  echo "not a directory: [$1]"
  exit 1
fi

absolute_path="$(readlink -f "$1")"

shift

photobackup_server_version=1.0.7

echo "Running docker image sbohmann/photobackup-server:$photobackup_server_version"

docker run -it -v "$absolute_path":/storage -p 8080:8080 --rm "sbohmann/photobackup-server:$photobackup_server_version" "$@"
