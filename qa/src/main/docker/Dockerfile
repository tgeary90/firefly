FROM node
MAINTAINER tgeary90

RUN mkdir /tests
RUN  apt-get update \
 && apt-get install -y --force-yes vim tree \
 && rm -rf /var/lib/apt/lists/* \
 && apt-get clean
RUN cd /tests \
 && npm config set registry http://registry.npmjs.org \
 && npm install -g mocha \
 && npm install chai-http \
 && npm install chai \
 && npm install

COPY ./content /tests

ENV HOSTNAME=tests
ENTRYPOINT ["/bin/sh"]
CMD ["/tests/e2e.sh"]

