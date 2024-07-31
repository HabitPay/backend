FROM amazoncorretto:17-al2023-jdk

RUN apt-get update && apt-get install -y dumb-init

WORKDIR /usr/app

COPY ./entrypoint.sh ./

ENTRYPOINT [ "/usr/bin/dumb-init", "--" ]

CMD [ "/bin/sh", "./entrypoint.sh", "deploy" ]