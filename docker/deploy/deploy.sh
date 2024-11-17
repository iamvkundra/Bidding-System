#!/bin/bash

# Get the project root directory (two levels up from the script)
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

# Create necessary directories
mkdir -p "${PROJECT_ROOT}/logs"

# Build and start the services
echo "Building and starting services..."
docker compose -f docker-compose.yaml build
docker compose -f docker-compose.yaml up -d

# Wait for the application to start
echo "Waiting for application to start..."
sleep 30

# Check if the application is healthy
echo "Checking application health..."
curl -f http://localhost:8080/actuator/health || echo "Application might not be healthy"

# Show logs
echo "Showing application logs..."
docker compose logs bidding-service