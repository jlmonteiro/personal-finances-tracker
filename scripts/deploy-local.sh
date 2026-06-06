#!/bin/bash
set -e

# Colors and icons
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

info()  { echo -e "${BLUE}ℹ️  $1${NC}"; }
ok()    { echo -e "${GREEN}✅ $1${NC}"; }
warn()  { echo -e "${YELLOW}⚠️  $1${NC}"; }
err()   { echo -e "${RED}❌ $1${NC}"; exit 1; }
step()  { echo -e "\n${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; echo -e "${BLUE}🔧 $1${NC}"; echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; }

# Configuration
PROJECT_DIR="$HOME/personal-finances-tracker"
NAMESPACE="finances"
RELEASE_NAME="finances"
INGRESS_HOST="home.monteiro.net"
BACKEND_IMAGE="ghcr.io/jlmonteiro/personal-finances-tracker/backend"
FRONTEND_IMAGE="ghcr.io/jlmonteiro/personal-finances-tracker/frontend"
VERSION=$(grep "^appVersion:" "$PROJECT_DIR/helm-deployment/Chart.yaml" | tr -d '"' | awk '{print $2}')

# Database settings
DB_URL="jdbc:postgresql://internal-postgresql-rw.postgres:5432/homedb?currentSchema=finances"
DB_SCHEMA="finances"
DB_USERNAME="home_user"
DB_PASSWORD="home_user"

export KUBECONFIG="$HOME/.kube/config"

echo -e "\n${GREEN}🚀 Personal Finances Tracker — Local Deploy${NC}"
echo -e "${GREEN}   Version: ${VERSION}${NC}\n"

# Step 1: Build backend image
step "Building backend image"
docker build -f "$PROJECT_DIR/backend/backend.dockerfile" \
  -t "$BACKEND_IMAGE:$VERSION" \
  "$PROJECT_DIR" || err "Backend build failed"
ok "Backend image built: $BACKEND_IMAGE:$VERSION"

# Step 2: Build frontend image
step "Building frontend image"
docker build -f "$PROJECT_DIR/frontend/frontend.dockerfile" \
  -t "$FRONTEND_IMAGE:$VERSION" \
  "$PROJECT_DIR/frontend" || err "Frontend build failed"
ok "Frontend image built: $FRONTEND_IMAGE:$VERSION"

# Step 3: Import images into k3s
step "Importing images into k3s"
docker save "$BACKEND_IMAGE:$VERSION" | sudo k3s ctr images import - || err "Failed to import backend image"
ok "Backend image imported"
docker save "$FRONTEND_IMAGE:$VERSION" | sudo k3s ctr images import - || err "Failed to import frontend image"
ok "Frontend image imported"

# Step 4: Create namespace
step "Preparing namespace"
kubectl create namespace "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -
ok "Namespace '$NAMESPACE' ready"

# Step 5: Install or upgrade chart
step "Deploying Helm chart"
if helm status "$RELEASE_NAME" -n "$NAMESPACE" &>/dev/null; then
  info "Existing release found — upgrading..."
  helm upgrade "$RELEASE_NAME" "$PROJECT_DIR/helm-deployment" \
    --namespace "$NAMESPACE" \
    --set backend.image.pullPolicy=Never \
    --set frontend.image.pullPolicy=Never \
    --set ingress.host="$INGRESS_HOST" \
    --set backend.config.SPRING_DATASOURCE_URL="$DB_URL" \
    --set backend.config.SPRING_DATASOURCE_DEFAULT_SCHEMA="$DB_SCHEMA" \
    --set secrets.databaseUsername="$DB_USERNAME" \
    --set secrets.databasePassword="$DB_PASSWORD"
  kubectl rollout restart deployment/"$RELEASE_NAME-backend" -n "$NAMESPACE"
  kubectl rollout restart deployment/"$RELEASE_NAME-frontend" -n "$NAMESPACE"
  ok "Chart upgraded"
else
  info "No existing release — installing..."
  helm install "$RELEASE_NAME" "$PROJECT_DIR/helm-deployment" \
    --namespace "$NAMESPACE" \
    --set backend.image.pullPolicy=Never \
    --set frontend.image.pullPolicy=Never \
    --set ingress.host="$INGRESS_HOST" \
    --set backend.config.SPRING_DATASOURCE_URL="$DB_URL" \
    --set backend.config.SPRING_DATASOURCE_DEFAULT_SCHEMA="$DB_SCHEMA" \
    --set secrets.databaseUsername="$DB_USERNAME" \
    --set secrets.databasePassword="$DB_PASSWORD"
  ok "Chart installed"
fi

# Step 6: Wait for rollout
step "Waiting for pods to be ready"
kubectl rollout status deployment/"$RELEASE_NAME-backend" -n "$NAMESPACE" --timeout=120s || warn "Backend not ready within timeout"
kubectl rollout status deployment/"$RELEASE_NAME-frontend" -n "$NAMESPACE" --timeout=60s || warn "Frontend not ready within timeout"

# Step 7: Show status
step "Deployment status"
kubectl get pods -n "$NAMESPACE" -o wide
echo ""
kubectl get svc -n "$NAMESPACE"
echo ""
kubectl get ingress -n "$NAMESPACE"

echo -e "\n${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}🎉 Deploy complete!${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}\n"
