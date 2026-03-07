#!/bin/bash
set -e

SERVER="root@159.203.65.71"
APP_DIR="/opt/itookthese"
DOMAIN="itookthese.app"

echo "==> Syncing project to server..."
rsync -az --delete \
  --exclude '.git' \
  --exclude 'node_modules' \
  --exclude '.env' \
  --exclude 'target' \
  --exclude '.idea' \
  --exclude '.DS_Store' \
  -e ssh \
  "$(dirname "$0")/../" \
  "$SERVER:$APP_DIR/"

echo "==> Building and deploying on server..."
ssh "$SERVER" bash -s "$DOMAIN" "$APP_DIR" << 'REMOTE_SCRIPT'
DOMAIN=$1
APP_DIR=$2
cd "$APP_DIR"

# Check if SSL certs exist
if [ ! -f "/etc/letsencrypt/live/$DOMAIN/fullchain.pem" ]; then
  echo "==> SSL certs not found. Running initial setup..."

  # Use a temporary nginx config for certbot challenge
  cat > /tmp/nginx-init.conf << 'INITCONF'
server {
    listen 80;
    server_name itookthese.app www.itookthese.app;
    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }
    location / {
        return 200 'Setting up...';
        add_header Content-Type text/plain;
    }
}
INITCONF

  # Start a temporary nginx for the ACME challenge
  mkdir -p /var/www/certbot
  docker run -d --name certbot-nginx \
    -p 80:80 \
    -v /var/www/certbot:/var/www/certbot \
    -v /tmp/nginx-init.conf:/etc/nginx/conf.d/default.conf:ro \
    nginx:alpine

  sleep 2

  # Get the certificate
  docker run --rm \
    -v /etc/letsencrypt:/etc/letsencrypt \
    -v /var/www/certbot:/var/www/certbot \
    certbot/certbot certonly \
    --webroot \
    --webroot-path=/var/www/certbot \
    --email admin@$DOMAIN \
    --agree-tos \
    --no-eff-email \
    -d $DOMAIN \
    -d www.$DOMAIN

  # Clean up temp nginx
  docker stop certbot-nginx && docker rm certbot-nginx

  echo "==> SSL certs obtained!"
fi

# Copy certs into a docker volume-friendly location
echo "==> Starting application..."
docker compose -f docker-compose.prod.yml down --remove-orphans 2>/dev/null || true
docker compose -f docker-compose.prod.yml build --no-cache
docker compose -f docker-compose.prod.yml up -d

echo "==> Waiting for services to start..."
sleep 10
docker compose -f docker-compose.prod.yml ps

echo "==> Deploy complete!"
REMOTE_SCRIPT

echo "==> Done! Site should be live at https://$DOMAIN"
