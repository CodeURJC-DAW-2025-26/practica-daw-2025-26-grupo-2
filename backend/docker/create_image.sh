#!/bin/bash
set -e

if [ -z "$1" ]; then
  echo "Uso: ./create_image.sh <nombre_imagen>"
  echo "Ejemplo: ./create_image.sh micuenta/zendashop:0.0.1"
  exit 1
fi

cd "$(dirname "$0")/../.."

docker build -t "$1" -f backend/docker/Dockerfile .