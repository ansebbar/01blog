#!/bin/bash
docker run --name postgres-spring -e POSTGRES_USER=bloguser -e POSTGRES_PASSWORD=blogpass -e POSTGRES_DB=blogdb -d -p 5432:5432 postgres:latest

#to exex container and enter the db sec 
#docker exec -it postgres-spring psql -U postgres -d springdb

