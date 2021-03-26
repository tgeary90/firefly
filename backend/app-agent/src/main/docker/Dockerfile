FROM java:openjdk-8-jre-alpine
MAINTAINER tgeary90

RUN mkdir /acct_files
COPY ./app-agent-*.jar /
COPY *.sh /

#RUN apt-get update && \
# apt-get upgrade -y && \
# apt-get install -y procps ENV HOSTNAME=sparrowhawk

EXPOSE 9001

ENTRYPOINT ["/bin/sh"]
CMD ["start_app-agent.sh"]

