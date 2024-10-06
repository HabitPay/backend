#!/bin/bash

APPLICATION=habitpay
LOG_FILE="/var/log/$APPLICATION/deploy.log"
HEALTHCHECK_API="actuator/health"
BLUE_CONTAINER="backend.habitpay.internal:8080"
GREEN_CONTAINER="backend.habitpay.internal:8081"

log() {
    local msg="$1"
    printf '[INFO] %s %s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$msg"
}

log_error() {
    local msg="$1"
    printf '[ERROR] %s %s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$msg" >&2
}

healthcheck() {
    local container_endpoint=$1
    local max_retries=10
    local sleep_time=5
    local retries=0

    while [ $retries -lt $max_retries ]; do
        local container_status=$(wget -qO- $container_endpoint | jq -r '.status')
        if [ "$container_status" = "UP" ]; then
            log "$container_endpoint is running."
            return 0
        else
            log "$container_endpoint is not running. Retrying..."
            sleep $sleep_time
            retries=$((retries+1))
        fi
    done

    return 1
}

down() {
    local target=$1

    log "Stopping $target container..."
    if ! sudo docker stop "$target"; then
        log_error "Failed to stop $target container"
        return 1
    fi

    log "Removing $target container..."
    if ! sudo docker rm "$target"; then
        log_error "Failed to remove $target container"
        return 1
    fi

    log "Successfully stopped and removed $target container."
}

main() {
    local is_blue_running=$(sudo docker container inspect blue --format='{{json .State.Status}}' | sed 's/"//g')
    local is_green_running=$(sudo docker container inspect green --format='{{json .State.Status}}' | sed 's/"//g')
    local blue_start_time=$(sudo docker container inspect blue --format='{{.State.StartedAt}}')
    local green_start_time=$(sudo docker container inspect green --format='{{.State.StartedAt}}')

    if [ "$is_blue_running" = "running" ] && [ "$blue_start_time" "<" "$green_start_time" ]; then
        log "Blue container started first."
        if healthcheck "$GREEN_CONTAINER/$HEALTHCHECK_API"; then
            down blue || { log_error "Failed to stop blue container. Exiting..."; exit 1; }
        else
            log_error "Failed to down blue. Exiting..."
            exit 1
        fi
    elif [ "$is_green_running" = "running" ] && [ "$green_start_time" "<" "$blue_start_time" ]; then
        log "Green container started first."
        if healthcheck "$BLUE_CONTAINER/$HEALTHCHECK_API"; then
            down green || { log_error "Failed to stop green container. Exiting..."; exit 1; }
        else
            log_error "Failed to down green. Exiting..."
            exit 1
        fi
    else
        log "Application is not running. Exiting..."
    fi

    docker image prune -a -f
}

main >> "$LOG_FILE" 2>&1