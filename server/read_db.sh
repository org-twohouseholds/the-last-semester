#!/bin/sh

set -e # stop on fail
cd "$(dirname "$0")" # server/
docker compose exec db sh -c 'MYSQL_PWD="$(cat /run/secrets/db_pw)" exec mysql -u user the_last_semester -e "SELECT id, username FROM account;"'
