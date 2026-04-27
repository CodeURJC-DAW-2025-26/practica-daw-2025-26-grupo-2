#!/bin/bash
set -e

if [ -z "$1" ] || [ -z "$2" ]; then
    echo "Uso: ./publish_docker-compose.sh <DOCKERHUB_USER> <IMAGE_TAG>"
    echo "Ejemplo: ./publish_docker-compose.sh samuelmelianbenito zendashop:0.0.4"
    exit 1
fi

DOCKERHUB_USER="$1"
APP_IMAGE="${DOCKERHUB_USER}/$2"
COMPOSE_ARTIFACT="${DOCKERHUB_USER}/zendashop-compose:0.4.0"

export APP_IMAGE

echo "Publishing docker-compose.yml as OCI artifact: ${COMPOSE_ARTIFACT}"
echo "  APP_IMAGE=${APP_IMAGE}"
docker compose publish "${COMPOSE_ARTIFACT}" --with-env
echo "Published successfully!"
echo ""
echo "Run the app with:"
echo "  APP_IMAGE=${APP_IMAGE} docker compose -f oci://${COMPOSE_ARTIFACT} up"