#!/bin/bash
echo "Releasing ${TRAVIS_TAG}"
# Prepare the release
mvnw -e -B -ntp -settings .mvn/settings.xml -P ossrh -Dtag=${TRAVIS_TAG} -DreleaseVersion=${TRAVIS_TAG} release:prepare
# Stage the release
mvnw -e -B -ntp -settings .mvn/settings.xml -P ossrh release:stage
# Push changes to origin
git push -u ssh-origin master
