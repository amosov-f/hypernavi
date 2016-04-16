#!/usr/bin/env bash

GIT_COMMITER_NAME="zomb-amosov-f"
GIT_COMMITER_EMAIL="amosov.fed@yandex.ru"

git_configure_user() {
  DIR=$1
  git -C ${DIR} config user.name "${GIT_COMMITER_NAME}"
  git -C ${DIR} config user.email "${GIT_COMMITER_EMAIL}"
}

git_configure_user /root/hypernavi-data
cd /root/hypernavi-data
git -C commit -am "data updated"
git -C push origin master