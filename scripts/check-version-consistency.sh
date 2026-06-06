#!/bin/bash
GRADLE_VER=$(grep "^version=" gradle.properties | cut -d= -f2)
NPM_VER=$(node -p "require('./frontend/package.json').version")
CHART_VER=$(grep "^version:" helm-deployment/Chart.yaml | awk '{print $2}')
CHART_APP_VER=$(grep "^appVersion:" helm-deployment/Chart.yaml | tr -d '"' | awk '{print $2}')

ERRORS=0

if [ "$GRADLE_VER" != "$NPM_VER" ]; then
  echo "Version mismatch: gradle.properties=$GRADLE_VER, package.json=$NPM_VER"
  ERRORS=1
fi

if [ "$GRADLE_VER" != "$CHART_VER" ]; then
  echo "Version mismatch: gradle.properties=$GRADLE_VER, Chart.yaml version=$CHART_VER"
  ERRORS=1
fi

if [ "$GRADLE_VER" != "$CHART_APP_VER" ]; then
  echo "Version mismatch: gradle.properties=$GRADLE_VER, Chart.yaml appVersion=$CHART_APP_VER"
  ERRORS=1
fi

if [ "$ERRORS" -ne 0 ]; then
  exit 1
fi

echo "All versions consistent: $GRADLE_VER"
