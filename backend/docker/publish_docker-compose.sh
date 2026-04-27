#!/bin/bash
set -e

if [ -z "$1" ]; then
    echo "Uso: ./publish_docker-compose.sh <DOCKERHUB_USER>"
    exit 1
fi

DOCKERHUB_USER="$1"
COMPOSE_ARTIFACT="${DOCKERHUB_USER}/zendashop-compose:0.2.0"

echo "Publishing docker-compose.yml as OCI artifact: ${COMPOSE_ARTIFACT}"
docker compose publish "${COMPOSE_ARTIFACT}" --with-env
echo "Published successfully!"
echo ""
echo "Run the app with:"
echo "  docker compose -f oci://${COMPOSE_ARTIFACT} up"