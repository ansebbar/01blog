run:
	./mvnw spring-boot:run
db:
	docker run --name postgres-spring -e POSTGRES_USER=bloguser -e POSTGRES_PASSWORD=blogpass -e POSTGRES_DB=blogdb -d -p 5432:5432 postgres:latest
clean:
	./mvnw clean
build:
	./mvnw package
