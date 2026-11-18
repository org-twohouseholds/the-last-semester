#!/bin/sh

# Windows: run with Git Bash

set -e # stop on fail
cd "$(dirname "$0")" # server/

for secret in db_pw db_tester_pw; do
  if [ ! -s "secrets/$secret" ]; then # non-existent or empty
    echo "Missing secrets/$secret." >&2
    exit 1
  fi
done
if grep -q "['\\]" secrets/db_tester_pw; then
  echo "secrets/db_tester_pw must not contain ' or \\." >&2
  exit 1
fi

../gradlew :server:app:installDist # build game server
docker compose up --build -d # start containers (detached mode)
