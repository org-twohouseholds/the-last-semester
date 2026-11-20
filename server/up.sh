#!/bin/sh

# Windows: run with Git Bash

set -e # stop on fail
cd "$(dirname "$0")" # server/

for secret in db_pw db_tester_pw db_root_pw; do
  if [ ! -s "secrets/$secret" ]; then # non-existent or empty
    echo "Missing secrets/$secret." >&2
    exit 1
  fi
done

../gradlew :server:app:installDist # build game server
docker compose up --build -d # start containers (detached mode)
