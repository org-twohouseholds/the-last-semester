#!/bin/sh

# stop containers
# Windows: run with Git Bash

set -e # stop on fail
cd "$(dirname "$0")" # server/
docker compose down
