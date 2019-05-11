#!/usr/bin/env bash

storage_directory=$1

if [[ ! -d "$storage_directory" ]]
then
    echo "not a directory: [$storage_directory]"
    exit 1
fi

sub_path="$storage_directory/backup/photobackup-server"

if [[ ! -d "$sub_path" ]]
then
    if ! mkdir -p "$sub_path"
    then
        echo "unable to create path [$sub_path], exit code: $?"
        exit 2
    fi
fi

names="assets upload photos thumbnails videos"

for name in ${names}
do
    subdirectory="$sub_path/$name"

    if [[ ! -d "$subdirectory" ]]
    then
        if ! mkdir "$subdirectory"
        then
            echo "unable to create directory [$subdirectory], exit code: $?"
            exit 3
       fi
    fi

    if ! ln -s "$subdirectory" "$name"
    then
        echo "unable to create link from [$subdirectory] to [$name] in [$(pwd)], exit code: $?"
        exit 4
    fi
done
