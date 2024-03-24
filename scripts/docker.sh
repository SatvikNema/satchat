if [ -d "$HOME/docker/postgres3/data" ]; then
    mkdir -p ~/docker/postgres3/data
fi

docker compose -f ../docker/postgres.yaml up -d

rc=$?
if [ $rc -ne 0 ] ; then
  echo Could not spinup postgres docker container, exit code [$rc]; exit $rc
fi

SQL_DDL_FILE_PATH="../src/main/resources/setup.sql"
export PGPASSWORD=postgres123
psql postgresql://localhost:5435/postgres -U postgres -f "$SQL_DDL_FILE_PATH"

rc=$?
if [ $rc -ne 0 ] ; then
  echo Could not setup database ddl, exit code [$rc]; exit $rc
fi