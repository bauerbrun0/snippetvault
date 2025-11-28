# Snippetvault

SnippetVault is a full-stack, self-hostable web application for managing your code snippets.
It’s built as a monorepo containing three modules:

- **frontend**: A Vue 3 frontend
- **api**: Spring Boot backend
- **db**: Oracle Database

## Usage

Create a `docker-compose.yaml` file with the following content (change environment variables as needed):
```yaml
services:
  oracle:
    image: 'bauerbrun0/oracle-xe-snippetvault:latest'
    environment:
      - 'ORACLE_ALLOW_REMOTE=true'
      - 'DB_USER=snippetvault'
      - 'DB_PASS=secret'
    volumes:
      - 'oracle_data:/u01/app/oracle'
    ports:
      - '1521:1521'

  api:
    image: bauerbrun0/snippetvault-api:latest
    depends_on:
      - oracle
    environment:
      SPRING_DATASOURCE_URL: jdbc:oracle:thin:@//oracle:1521/XE
      SPRING_DATASOURCE_USERNAME: snippetvault
      SPRING_DATASOURCE_PASSWORD: secret

      JWT_SECRET: ZXhhbXBsZS1zZWNyZXQta2V5LXRvLWJlLXJlcGxhY2VkLXdpdGgtYS1sb25nZXItYW5kLXNlY3VyZXItb25l
      JWT_EXPIRATION: 36000000

      APP_FRONTEND: http://localhost:8081
      APP_ADMINUSER: admin
      APP_ADMINPASSWORD: password
    ports:
      - "8080:8080"

  frontend:
    image: bauerbrun0/snippetvault-frontend:latest
    depends_on:
        - api
    environment:
      VITE_API_BASE_URL: "http://localhost:8080/api"
    ports:
      - "8081:80"
volumes:
  oracle_data:
    driver: local
```
Then run:
```bash
docker compose up -d
```

To access the application, open your browser and navigate to `http://localhost:8081`.
API documentation is available at `http://localhost:8080/swagger-ui/index.html`.

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