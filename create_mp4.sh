#!/bin/sh
checksum="$1"

input_file="photos/${checksum}"
output_file="videos/${checksum}.mp4"

if test -e "${output_file}"; then exit 21; fi

temporary_file="videos/${checksum}.temporary.mp4"

if test -f "${temporary_file}" && test -w "${temporary_file}"; then rm "${temporary_file}" || exit 22; fi

if test -e "${temporary_file}"; then exit 23; fi

echo "Converting input file [${input_file}] to mp4 output file [${output_file}]..."

ffmpeg -n -i "${input_file}" -pix_fmt yuv420p -preset slow "${temporary_file}"
export result="$?"

if ! test "${result}" == "0"; then (echo "ffmpeg failed with exit code [${result}]" >&2); exit 24; fi

mv "${temporary_file}" "${output_file}" || exit 25

echo "Finished converting input file [${input_file}] to mp4 output file [${output_file}]."
