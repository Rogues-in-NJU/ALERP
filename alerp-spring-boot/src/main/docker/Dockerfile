FROM openjdk:8-jre-alpine

RUN adduser -D -s /bin/sh web
RUN mkdir /workspace && chown web:web /workspace

WORKDIR /workspace

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown ise:ise entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]

ADD alerp-spring-boot*.jar alerp-spring-boot.jar