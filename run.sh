#!/bin/bash

set -e

docker build -t order-platform-msa-mcp-service .

docker stop mcp-service > /dev/null 2>&1 || true
docker rm mcp-service > /dev/null 2>&1 || true

docker run --name mcp-service \
    --network entity-repository_order-network \
    -p 8099:8099 \
    -e DB_URL=jdbc:postgresql://postgres:5432/order_platform \
    -e DB_USERNAME=bonun \
    -e DB_PASSWORD=password \
    -e OAUTH_JWKS_URI=http://host.docker.internal:8083/oauth/jwks \
    -e AUTH_INTERNAL_AUDIENCE=internal-services \
    -e ORDER_SVC_URI=http://localhost:8084 \
    -e STORE_SVC_URI=http://localhost:8082 \
    -d order-platform-msa-mcp-service


# Check container status
docker ps -f "name=mcp-service"