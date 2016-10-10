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
  ssh -T git@github.com
}

git_switch_to() {
  BRANCH=$1
  DIR=$2
  print_message "Switching to ${BRANCH} at ${DIR}"
  git -C ${DIR} checkout ${BRANCH}
}

git_update_branch() {
  BRANCH=$1
  DIR=$2
  git_switch_to ${BRANCH} ${DIR}
  print_message "Updating branch ${BRANCH} at ${DIR}"
  git -C ${DIR} pull --rebase origin ${BRANCH}
}

git_commit_changes() {
  COMMIT_MESSAGE=$1
  DIR=$2
  print_message "Commiting changes at ${DIR} with message \"${COMMIT_MESSAGE}\""
  git -C ${DIR} add -A
  git -C ${DIR} commit -am "${COMMIT_MESSAGE}"
}

git_push() {
  BRANCH=$1
  DIR=$2
  git -C ${DIR} push origin ${BRANCH}
}

HYPERNAVI_DATA_DIR="/root/hypernavi-data"

docker run --rm -v ${HYPERNAVI_DATA_DIR}/backup:/backup --link mongo:mongo istepanov/mongodump no-cron

# git clone git@github.com:amosov-f/hypernavi-data.git

git_configure_user ${HYPERNAVI_DATA_DIR}
git_update_branch "master" ${HYPERNAVI_DATA_DIR}
git_commit_changes "data updated" ${HYPERNAVI_DATA_DIR}
git_push "master" ${HYPERNAVI_DATA_DIR}