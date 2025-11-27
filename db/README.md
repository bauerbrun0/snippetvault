## Building the custom oracle docker image
```bash
docker build -f Dockerfile -t oracle-xe-snippetvault:latest .
```

## Run compose file containing the local custom docker image
```bash
docker compose -f docker-compose-local-image.yaml up -d
```