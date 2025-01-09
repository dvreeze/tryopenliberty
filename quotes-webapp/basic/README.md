# Try-openliberty, quotes-webapp, basic

See [Try-openliberty README](../../README.md) for general remarks applying to this Maven project.

Before running this project (for the first time), first create a PostgreSQL database, and make sure the
database server is running.

For example, see [how-to-use-the-postgres-docker-official-image](https://www.docker.com/blog/how-to-use-the-postgres-docker-official-image/)
for using a containerized PostgreSQL database. Also see [postgres image](https://hub.docker.com/_/postgres).

## Creating a containerized PostgreSQL database (for the first time)

Assuming a containerized PostgreSQL database server is used, the following (low level rather naive) bootstrapping procedure could
be followed (assuming a running "Docker environment"):

```shell
docker pull postgres:latest
# Check the existence of this Docker image with the following command
docker image ls

# Better: docker run --name some-postgres -e POSTGRES_PASSWORD_FILE=/run/secrets/postgres-passwd -d postgres
docker run --name some-postgres -e POSTGRES_PASSWORD=mysecretpassword -d postgres
# Check the existence of this Docker container (and the port it listens on) with the following command
docker container ls -f name=postgres

# Copy SQL files to the container (when in the root of the git repository)
docker cp ./quotes-webapp/basic/sql/create_tables.sql some-postgres:/tmp
docker cp ./quotes-webapp/basic/sql/fill_tables.sql some-postgres:/tmp

# Open a shell inside the postgres container
docker exec -it some-postgres bin/bash
```

Then, in the bash shell inside the postgres container (due to command "docker exec -it"), enter the following
commands:

```shell
# There is always postgres user "postgres"
psql -U postgres

# Inside the "psql" interactive session, run the SQL scripts to create/fill the database
\i /tmp/create_tables.sql
\i /tmp/fill_tables.sql
```

Without using a volume the data will get lost once the container is removed.
