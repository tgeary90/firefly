FROM java:openjdk-8-jre-alpine
MAINTAINER tgeary90

COPY ./app-etl-*.jar /
COPY *.sh /

#RUN apt-get update && \
# apt-get upgrade -y && \
# apt-get install -y procps ENV HOSTNAME=sparrowhawk

EXPOSE 9002

CMD ["/bin/sh", "start_app-etl.sh"]

