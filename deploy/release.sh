#!/bin/bash
echo "Releasing ${TRAVIS_TAG}"
# Switch to a non-detached branch
git switch -c release/${TRAVIS_TAG}
# Set the version to build
mvnw versions:set -e -B -ntp -DnewVersion=${TRAVIS_TAG} -DgenerateBackupPoms=false
# Build, install, test then publish the artifacts
mvnw clean deploy -e -B -ntp -settings .mvn/settings.xml -P ossrh
# Commit release changes
git add pom.xml
git commit --message "Release ${TRAVIS_TAG} (build: ${TRAVIS_BUILD_NUMBER})"
# Set the next development snapshot version
mvnw release:update-versions -e -B -ntp -P ossrh
# Commit the changes
git add pom.xml
git commit --message "Next development version (build: ${TRAVIS_BUILD_NUMBER})"
# Push changes to origin
git push -u ssh-origin master
