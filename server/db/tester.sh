#!/bin/sh

# Read-only DB user for testing team

pw=$(tr -d '\r' < /run/secrets/db_tester_pw)
if [ -z "$pw" ]; then
  echo "secrets/db_tester_pw is empty or unreadable." >&2
  exit 1
fi

escaped_pw=$(printf '%s\n' "$pw" | sed -e 's/\\/\\\\/g' -e "s/'/''/g")

MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql -uroot <<EOF
CREATE USER 'tester'@'%' IDENTIFIED BY '${escaped_pw}';
GRANT SELECT ON the_last_semester.* TO 'tester'@'%';
EOF
