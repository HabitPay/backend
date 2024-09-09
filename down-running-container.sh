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

    log "Turning off $target container..."
    sudo docker compose -p $APPLICATION down "$target"
    log "Complete to turn of $target container."
}

main() {
    local is_blue_running=$(docker container inspect blue --format='{{json .State.Status}}' | sed 's/"//g')
    local is_green_running=$(docker container inspect green --format='{{json .State.Status}}' | sed 's/"//g')
    local blue_start_time=$(docker container inspect blue --format='{{.State.StartedAt}}')
    local green_start_time=$(docker container inspect green --format='{{.State.StartedAt}}')

    if [ "$is_blue_running" = "running" ] && [ "$blue_start_time" "<" "$green_start_time" ]; then
        log "Blue container started first."
        if healthcheck "$GREEN_CONTAINER/$HEALTHCHECK_API"; then
            down blue
        else
            log_error "Failed to down blue. Exiting..."
            exit 1
        fi
    elif [ "$is_green_running" = "running" ] && [ "$green_start_time" "<" "$blue_start_time" ]; then
        log "Green container started first."
        if healthcheck "$BLUE_CONTAINER/$HEALTHCHECK_API"; then
            down green
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