#!/bin/bash

LOG_FILE="/var/log/habitpay/deploy.log"

main() {
    sudo -u ec2-user git checkout -- docker-compose.yaml
    sudo -u ec2-user git checkout -- conf/nginx.conf
    sudo -u ec2-user git pull origin main

    sudo -u ec2-user git -C ../env pull origin main
}

main >> "$LOG_FILE" 2>&1