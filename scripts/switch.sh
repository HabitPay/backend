#!/bin/bash

APPLICATION_NAME="habitpay"
LOG_FILE="/var/log/$APPLICATION_NAME/switch.log"
BLUE_CONTAINER="blue"
GREEN_CONTAINER="green"
PORT_NUMBER=8080
HEALTHCHECK_API="actuator/health"
NGINX_CONFIGURATION_FILE="../conf/nginx.conf"

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
    local max_retries=12
    local sleep_time=5
    local retries=0

    while [ $retries -lt $max_retries ]; do
        local container_status=$(docker exec -t nginx wget -qO- $container_endpoint | jq -r '.status')
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

switch() {
    local current=$1
    local target=$2

    log "Switching from $current to $target..."
    sed -i "s|http://$current|http://$target|" $NGINX_CONFIGURATION_FILE
    docker exec nginx nginx -s reload
    log "Complete to switch container. ($current -> $target)"
}

main() {
    local is_blue_running=$(grep -q "http://blue" $NGINX_CONFIGURATION_FILE && echo "running")
    local is_green_running=$(grep -q "http://green" $NGINX_CONFIGURATION_FILE && echo "running")

    if [ "$is_blue_running" = "running" ]; then
        log "Blue container is running."
        healthcheck "$GREEN_CONTAINER:$PORT_NUMBER/$HEALTHCHECK_API"
        switch blue green
    elif [ "$is_green_running" = "running" ] ; then
        log "Green container is running."
        healthcheck "$BLUE_CONTAINER:$PORT_NUMBER/$HEALTHCHECK_API"
        switch green blue
    else
        log_error "Neither container is running. Exiting..."
        exit 1
    fi
}

main >> "$LOG_FILE" 2>&1