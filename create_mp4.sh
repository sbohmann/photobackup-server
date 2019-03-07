#!/bin/sh
checksum="$1"

input_file="photos/${checksum}"
output_file="videos/${checksum}.mp4"

if test -e "${output_file}"; then exit 21; fi

attempt_file="${output_file}.attempt"

if test -f "${attempt_file}" && test -w "${attempt_file}"; then rm "${attempt_file}" || exit 22; fi

if test -e "${attempt_file}"; then exit 23; fi

echo "Converting input file [${input_file}] to mp4 output file [${output_file}]..."

ffmpeg -n -i "${input_file}" "${attempt_file}"; result = "$?"

if ! test "$result" == "0"; then (echo "ffmpeg failed with exit code ${result}" >&2); exit 24; fi

mv "${attempt_file}" "${output_file}" || exit 25

echo "Finished converting input file [${input_file}] to mp4 output file [${output_file}]."
