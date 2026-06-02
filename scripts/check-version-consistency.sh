#!/bin/bash
GRADLE_VER=$(grep "^version=" gradle.properties | cut -d= -f2)
NPM_VER=$(node -p "require('./frontend/package.json').version")

if [ "$GRADLE_VER" != "$NPM_VER" ]; then
  echo "Version mismatch: gradle.properties=$GRADLE_VER, package.json=$NPM_VER"
  exit 1
fi
