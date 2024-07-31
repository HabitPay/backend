FROM amazoncorretto:17-al2023-jdk

RUN yum -y install wget
RUN ARCH=$(uname -m) && \
    if [ "$ARCH" = "x86_64" ]; then \
        wget -O /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.5/dumb-init_1.2.5_x86_64 && \
        chmod +x /usr/local/bin/dumb-init; \
    elif [ "$ARCH" = "aarch64" ]; then \
        wget -O /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.5/dumb-init_1.2.5_arm64.deb && \
        chmod +x /usr/local/bin/dumb-init; \
    else \
        echo "Unsupported architecture: $ARCH"; exit 1; \
    fi

RUN chmod +x /usr/local/bin/dumb-init

WORKDIR /usr/app

COPY ./entrypoint.sh ./

ENTRYPOINT [ "/usr/local/bin/dumb-init", "--" ]

CMD [ "/bin/sh", "./entrypoint.sh", "deploy" ]