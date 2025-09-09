#!/bin/sh
set -e

# This script runs on first database initialization only (when the PGDATA is empty)
# It creates an optional schema if POSTGRES_SCHEMA is provided.

if [ -n "$POSTGRES_SCHEMA" ]; then
  echo "Creating schema '$POSTGRES_SCHEMA' in database '$POSTGRES_DB'..."
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE SCHEMA IF NOT EXISTS "$POSTGRES_SCHEMA" AUTHORIZATION "$POSTGRES_USER";
    -- Optionally set default privileges for the owner on the schema
    GRANT ALL ON SCHEMA "$POSTGRES_SCHEMA" TO "$POSTGRES_USER";
EOSQL
else
  echo "POSTGRES_SCHEMA not set; skipping schema creation."
fi
