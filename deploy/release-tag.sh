#!/bin/bash
echo "Releasing ${TRAVIS_TAG}"

# When triggered by a tag, the local git repository will be in
# a detached head mode, so configure it for pushing changes.
echo "Configuring git to release"
git config --local user.name "Travis CI"
git config --local user.email "travis@travis-ci.com"
git remote remove origin
git remote add origin git@github.com:${TRAVIS_REPO_SLUG}.git
git fetch

echo "Creating release branch: release/${TRAVIS_TAG}"
git checkout -b release/${TRAVIS_TAG}

echo "Setting POM versions to ${TRAVIS_TAG}"
mvn -e -B -ntp -s deploy/settings.xml -P ossrh -DgenerateBackupPoms=false -DnewVersion=${TRAVIS_TAG} versions:set

echo "Executing deploy goal for ${TRAVIS_TAG}"
mvn -e -B -ntp -s deploy/settings.xml -P ossrh clean deploy

echo "Pushing release/${TRAVIS_TAG}"
find . -name pom.xml -exec git add {} \;
git commit -m "Release ${TRAVIS_TAG} (build: ${TRAVIS_BUILD_NUMBER})"
git push -u origin release/${TRAVIS_TAG}

echo "Merging release/${TRAVIS_TAG} into master"
git checkout master
git pull
git merge release/${TRAVIS_TAG}

echo "Preparing next development version"
mvn -e -B -ntp -s deploy/settings.xml -P ossrh release:update-versions
mvn -e -B -ntp -s deploy/settings.xml -P ossrh -DskipTests=true clean deploy

echo "Pushing next development version to master"
find . -name pom.xml -exec git add {} \;
git commit -m "Next development version (build: ${TRAVIS_BUILD_NUMBER})"
git push -u origin master
