#!/bin/bash
# Deploy a snapshot release if not tagged
if [ -z "${TRAVIS_TAG}" ]; then
  echo "Performing a snapshot build"
  mvnw deploy -e -B -ntp -settings .mvn/settings.xml -P ossrh
fi
