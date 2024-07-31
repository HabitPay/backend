FROM openjdk:17-jdk-alpine

RUN apt-get update && apt-get install -y dumb-init

WORKDIR /usr/app

COPY ./entrypoint.sh ./

ENTRYPOINT [ "/usr/bin/dumb-init", "--" ]

CMD [ "/bin/sh", "./entrypoint.sh", "deploy" ]