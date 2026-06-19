#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${AGEGO_API_DIR:-/opt/agego/api}"
BRANCH="${AGEGO_BRANCH:-server}"
SERVICE="${AGEGO_SERVICE:-agego-api}"

cd "$APP_DIR"
git fetch origin "$BRANCH"
git checkout "$BRANCH"
git pull --ff-only origin "$BRANCH"
npm ci --omit=dev
node --check src/server.js
sudo systemctl restart "$SERVICE"
sudo systemctl --no-pager --full status "$SERVICE"
