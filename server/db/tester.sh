#!/bin/sh

# Read-only DB user for testing team

set -eu

MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql -uroot <<EOF
CREATE USER 'tester'@'%' IDENTIFIED BY '$(cat /run/secrets/db_tester_pw)';
GRANT SELECT ON the_last_semester.* TO 'tester'@'%';
EOF

set +eu
