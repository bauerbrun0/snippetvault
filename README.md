# Snippetvault

SnippetVault is a full-stack, self-hostable web application for managing your code snippets.
It’s built as a monorepo containing three modules:

- **frontend**: A Vue 3 frontend
- **api**: Spring Boot backend
- **db**: Oracle Database

## Development

### Start db
```bash
docker compose -f db/docker-compose.yaml up -d
```

### Build and run api
```bash
./api/mvnw clean install -DskipTests
cd ./api
./mvnw spring-boot:run
```

### Run frontend
```bash
cd ./frontend
bun run dev
```

## Building the Docker Images

### Build the db docker image
```bash
cd ./db
docker build -f Dockerfile -t oracle-xe-snippetvault:latest .
cd ..
```

### Build the api docker image
```bash
docker build -f ./api/Dockerfile -t snippetvault-api:latest .
```

### Build the frontend docker image
```bash
cd ./frontend
docker build -f ./Dockerfile -t snippetvault-frontend:latest .
cd ..
```

### Run the db, api, and frontend with local images
```bash
docker compose -f ./docker/docker-compose-local-image.yaml up -d
```