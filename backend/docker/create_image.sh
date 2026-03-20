#!/bin/bash

if [ -z "$1" ]; then
  echo "Uso: ./create_image.sh <nombre_imagen>"
  echo "Ejemplo: ./create_image.sh micuenta/zendashop:0.0.1"
  exit 1
fi

docker build -t "$1" -f Dockerfile ..