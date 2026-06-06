#!/bin/bash
set -e

# Rsync project to K3s server and trigger deploy
SERVER="jorge@192.168.0.2"
REMOTE_DIR="~/personal-finances-tracker"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "📦 Syncing project to $SERVER..."
rsync -avz --delete \
  --exclude node_modules \
  --exclude dist \
  --exclude .git \
  --exclude .idea \
  --exclude .gradle \
  --exclude build \
  --exclude .kotlin \
  --exclude frontend/test-results \
  "$PROJECT_DIR/" "$SERVER:$REMOTE_DIR/"

echo "🚀 Running deploy on $SERVER..."
ssh "$SERVER" "bash $REMOTE_DIR/scripts/deploy-local.sh"
