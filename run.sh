#!/bin/sh

version=$(gradle -no-daemon properties -q | grep "version:" | awk '{print $2}') && java -Djava.library.path="libraries:/usr/lib:/usr/local/lib" -jar "build/libs/photobackup-server-${version}.jar"
