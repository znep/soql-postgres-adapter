#!/bin/bash
set -e

# Starts the soql-pg-adapter service.
REALPATH=$(python -c "import os; print(os.path.realpath('$0'))")
BINDIR=$(dirname "$REALPATH")

CONFIG=${SODA_CONFIG:-"$BINDIR"/../configs/soql-postgres-adapter.conf}

JARFILE=$("$BINDIR"/build.sh "$@" | grep '^store-pg: ' | sed 's/^store-pg: //')

"$BINDIR"/run_migrations.sh

java -Djava.net.preferIPv4Stack=true -Dconfig.file="$CONFIG" -jar "$JARFILE"
