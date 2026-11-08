#!/bin/sh

# Build game server & starts containers
# Windows: run with Git Bash

set -e # stop on fail
cd "$(dirname "$0")" # server/

if [ ! -s secrets/db_pw ]; then # db_pw non-existent or empty
  echo "Missing secrets/db_pw." >&2
  exit 1
fi

../gradlew :server:installDist
docker compose up --build
