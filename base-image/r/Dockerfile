FROM ubuntu:16.04

WORKDIR /app
ADD . /app

ENV http_proxy $acumos_docker_proxy
RUN echo $http_proxy
RUN echo "nameserver 8.8.8.8" | tee /etc/resolv.conf > /dev/null

RUN apt-get update
RUN apt-get -y install r-base & apt-get -y install libprotobuf-dev & apt-get -y install libcurl4-gnutls-dev & apt-get -y install libxml2-dev & apt-get -y install libprotoc-dev & apt-get -y install build-essential libcurl4-gnutls-dev libxml2-dev libssl-dev & apt-get -y install libpq-dev