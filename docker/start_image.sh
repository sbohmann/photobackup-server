#!/usr/bin/env bash

if [[ ! -d ./storage ]]
then
    echo "Missing directory or directory link called 'storage' inside local directory [$(pwd)]"
    exit 1
fi

clear; reset; ./run_image.sh storage "$@"
