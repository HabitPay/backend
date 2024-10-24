#!/bin/sh

echo "[Current Mode]: $1"

development() {
    status_output=$(./gradlew --status)
    pid=$(echo "$status_output" | grep "BUSY" | awk '{print $1}')

    if [ -n "$pid" ]; then
        echo "Gradle Daemon (PID: $pid) is busy, stopping..."
        kill -9 $pid
        sleep 1  # Daemon이 완전히 종료되기를 기다립니다.
    else
        echo "No active Gradle Daemon found."
    fi

    ./gradlew --status

    ./gradlew --build-cache --parallel -t bootRun --args='--spring.profiles.active=dev'
}

deploy() {
    java -jar -Dspring.profiles.active=prod app.jar
}

if [ $1 = "deploy" ]; then
    deploy
else
    development
fi
