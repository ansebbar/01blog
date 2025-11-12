#!/bin/bash
docker run --name postgres-spring -e POSTGRES_PASSWORD=pass -e POSTGRES_DB=springdb -d -p 5432:5432 postgres:15

#to exex container and enter the db sec 
#docker exec -it postgres-spring psql -U postgres -d springdb
