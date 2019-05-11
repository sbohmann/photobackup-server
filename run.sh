#!/bin/sh
echo "fetching photobackup-server gradle version..."
version=$(gradle -no-daemon properties -q | grep "version:" | awk '{print $2}') || exit 1
echo "photobackup-server gradle version: $version"
java -Djava.library.path="libraries:/usr/lib:/usr/local/lib" -jar "build/libs/photobackup-server-${version}.jar"
