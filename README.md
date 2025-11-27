# Snippetvault

SnippetVault is a full-stack, self-hostable web application for managing your code snippets.
It’s built as a monorepo containing three modules:

- **frontend**: A Vue 3 frontend
- **api**: Spring Boot backend
- **db**: Oracle Database

## Development

Start db
```bash
docker compose -f db/docker-compose.yaml up -d
```

Build and run
```bash
# snippetvault/
./api/mvnw clean install -DskipTests
cd ./api
./mvnw spring-boot:run
```

Build the api docker image
```bash
docker build -f ./api/Dockerfile -t snippetvault-api:latest .
```