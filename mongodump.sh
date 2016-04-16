#!/usr/bin/env bash

GIT_COMMITER_NAME="zomb-amosov-f"
GIT_COMMITER_EMAIL="amosov.fed@yandex.ru"

print_message() {
  MESSAGE=$1
  echo "${MESSAGE_PREFIX} ${MESSAGE}"
}

git_configure_user() {
  DIR=$1
  git -C ${DIR} config user.name "${GIT_COMMITER_NAME}"
  git -C ${DIR} config user.email "${GIT_COMMITER_EMAIL}"
}

git_commit_changes() {
  COMMIT_MESSAGE=$1
  DIR=$2
  print_message "Commiting changes at ${DIR} with message \"${COMMIT_MESSAGE}\""
  git -C ${DIR} commit -am "${COMMIT_MESSAGE}"
}

git_push() {
  BRANCH=$1
  DIR=$2
  git -C ${DIR} push origin ${BRANCH}
}

HYPERNAVI_DATA_DIR="/root/hypernavi-data"

git_configure_user ${HYPERNAVI_DATA_DIR}
git_commit_changes "data updated" ${HYPERNAVI_DATA_DIR}
git_push "master" ${HYPERNAVI_DATA_DIR}