#!/usr/bin/env sh

GRADLE_CLEAN=true
GRADLE_INSTALL=true

clean_old_docker_artifacts() {
    docker stop rd-profile-sync
    docker stop rd-profile-sync-db

    docker rm rd-profile-sync
    docker rm rd-profile-sync-db

    docker rmi hmcts/rd-profile-sync
    docker rmi hmcts/rd-profile-sync-db


    docker volume rm rd-profile-sync_rd-profile-sync-db-volume
}

execute_script() {

  clean_old_docker_artifacts

  cd $(dirname "$0")/..

  ./gradlew clean assemble

  export SERVER_PORT="${SERVER_PORT:-8093}"

  chmod +x bin/*

  docker-compose build
}

execute_script