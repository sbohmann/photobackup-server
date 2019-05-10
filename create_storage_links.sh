#!/usr/bin/env bash

storage_directory=$1

if [[ ! -d "$storage_directory" ]]
then
    echo "not a directory: [$storage_directory]"
    exit 1
fi

names = assets upload photos thumbnails videos

for name in "@names"
do
    subdirectory="$storage_directory/$name"
    if [[ ! -d "$subdirectory" ]]
    then
        echo "not a directory: [$subdirectory]"
        exit 2
    fi
    ln -s "$subdirectory" "$name"
done
