#!/bin/bash
echo "Releasing ${TRAVIS_TAG}"
# Switch to a non-detached branch
git switch -c release/${TRAVIS_TAG}
# Prepare the release
mvnw release:prepare -e -B -ntp -settings .mvn/settings.xml -P ossrh -Dtag=${TRAVIS_TAG} -DreleaseVersion=${TRAVIS_TAG}
# Stage the release
mvnw release:stage -e -B -ntp -settings .mvn/settings.xml -P ossrh
# Push changes to origin
git push -u ssh-origin master
