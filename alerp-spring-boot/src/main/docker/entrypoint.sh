#!/bin/sh

echo "The application is starting..."
exec java -jar "alerp-spring-boot.jar" "$@"