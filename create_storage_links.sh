#!/usr/bin/env bash -e

storage_directory=$1

if [[ ! -d "$storage_directory" ]]
then
    echo "not a directory: [$storage_directory]"
    exit 1
fi

sub_path="$storage_directory/backup/photobackup-server"

if [[ ! -d "$sub_path" ]]
then
    mkdir -p "$sub_path"
fi

names="assets upload photos thumbnails videos"

for name in ${names}
do
    subdirectory="$sub_path/$name"
    if [[ ! -d "$subdirectory" ]]
    then
        echo "not a directory: [$subdirectory]"
        exit 2
    fi
    ln -s "$subdirectory" "$name"
done
