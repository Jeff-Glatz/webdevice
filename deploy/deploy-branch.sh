#!/bin/bash
# Only deploy branch builds
if [ "${TRAVIS_BRANCH}" != "${TRAVIS_TAG}" ]; then
  echo "Performing a deploy on ${TRAVIS_BRANCH}"
  mvn -e -B -ntp -s deploy/settings.xml -P ossrh deploy
fi
