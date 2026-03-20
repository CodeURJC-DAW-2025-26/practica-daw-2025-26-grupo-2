#!/bin/bash

if [ -z "$1" ]; then
  echo "Uso: ./publish_image.sh <nombre_imagen>"
  echo "Ejemplo: ./publish_image.sh micuenta/zendashop:0.0.1"
  exit 1
fi

docker push "$1"