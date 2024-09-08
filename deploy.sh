#!/bin/bash

DOCKER_IMAGE=$1
APPLICATION=habitpay
LOG_FILE="/var/log/habitpay/deploy.log"
HEALTHCHECK_RESULT=

log() {
    local msg="$1"
    printf '[INFO] %s %s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$msg"
}

log_error() {
    local msg="$1"
    printf '[ERROR] %s %s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$msg" >&2
}

healthcheck() {
    local container=$1
    local max_retries=12
    local sleep_time=5
    local retries=0

    while [ $retries -lt $max_retries ]; do
        local is_container_running=$(docker container inspect $container --format='{{json .State.Status}}' | sed 's/"//g')
        if [ "$is_container_running" = "running" ]; then
            log "$container is running."
            HEALTHCHECK_RESULT=0
            return 0
        else
            log "$container is not running. Retrying..."
            sleep $sleep_time
            retries=$((retries+1))
        fi
    done

    HEALTHCHECK_RESULT=1
    return 1
}

switch() {
    local current=$1
    local target=$2

    log "$current is running. Turning on $target container..."
    yq -i ".services.$target.image = \"$DOCKER_IMAGE\"" docker-compose.yaml
    sudo docker compose -p $APPLICATION up "$target" -d
    healthcheck "$target"

    log "HEALTHCHECK_RESULT: $HEALTHCHECK_RESULT"

    if [ $HEALTHCHECK_RESULT -eq 0 ]; then
        log "$target is running."
    else
        log_error "Failed to run $target. Exiting..."
        exit 1
    fi
}

main() {
    log "Docker image: $DOCKER_IMAGE"

    local is_application_running=$(sudo docker compose -p $APPLICATION ls | grep running | sed 's/.*/true/')
    local is_blue_running=$(docker container inspect blue --format='{{json .State.Status}}' | sed 's/"//g')
    local is_green_running=$(docker container inspect green --format='{{json .State.Status}}' | sed 's/"//g')

    if [ "$is_application_running" = "true" ] && [ "$is_blue_running" = "running" ]; then
        log "Blue container is running."
        switch blue green
    elif [ "$is_application_running" = "true" ] && [ "$is_green_running" = "running" ]; then
        log "Green container is running."
        switch green blue
    else
        log "Application is not running. Starting the application...(with blue container)"
        yq -i ".services.blue.image = \"$DOCKER_IMAGE\"" docker-compose.yaml
        sudo docker compose -p $APPLICATION up blue -d
    fi
}

main >> "$LOG_FILE" 2>&1