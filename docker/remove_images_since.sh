#!/bin/sh
docker rmi $(docker images --filter since="$1" -q)
