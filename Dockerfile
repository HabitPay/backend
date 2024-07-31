FROM amazoncorretto:17-al2023-jdk

RUN wget -O /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.5/dumb-init_1.2.5_arm64
RUN chmod +x /usr/local/bin/dumb-init

WORKDIR /usr/app

COPY ./entrypoint.sh ./

ENTRYPOINT [ "/usr/local/bin/dumb-init", "--" ]

CMD [ "/bin/sh", "./entrypoint.sh", "deploy" ]