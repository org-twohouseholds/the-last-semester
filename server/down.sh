#!/bin/sh

# Windows: run with Git Bash

set -e # stop on fail
cd "$(dirname "$0")" # server/
docker compose down # stop containers
