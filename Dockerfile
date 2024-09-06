FROM amazoncorretto:17-alpine3.19-jdk

RUN apk update && apk add dumb-init

WORKDIR /usr/app

COPY build/libs/*.jar app.jar

COPY ./entrypoint.sh ./

ENTRYPOINT [ "/usr/bin/dumb-init", "--" ]

CMD [ "/bin/sh", "./entrypoint.sh", "deploy" ]