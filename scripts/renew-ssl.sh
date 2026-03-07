#!/bin/bash
# Run via cron on the server: 0 3 * * 1 /opt/itookthese/scripts/renew-ssl.sh
docker run --rm \
  -v /etc/letsencrypt:/etc/letsencrypt \
  -v /var/www/certbot:/var/www/certbot \
  certbot/certbot renew --quiet

cd /opt/itookthese
docker compose -f docker-compose.prod.yml exec nginx nginx -s reload
