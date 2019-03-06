#!/bin/sh
checksum="$1"
input_file="photos/${checksum}"
output_file="videos/${checksum}.mp4"
echo ffmpeg -i "${input_file}" "${output_file}"
