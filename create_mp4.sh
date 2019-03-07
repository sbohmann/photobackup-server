#!/bin/sh
checksum="$1"
input_file="photos/${checksum}"
output_file="videos/${checksum}.mp4"
attempt_file="${output_file}.attempt"
echo "Converting input file [${input_file}] to mp4 output file [${output_file}]..."
ffmpeg -i "${input_file}" "${attempt_file}" || exit $?
mv "${attempt_file}" "${output_file}" || exit $?
echo "Finished converting input file [${input_file}] to mp4 output file [${output_file}]."
