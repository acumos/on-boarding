FROM ubuntu:latest

WORKDIR /app
ADD . /app

ENV http_proxy http://10.1.0.6:3128
RUN echo $http_proxy
RUN echo "nameserver 8.8.8.8" | tee /etc/resolv.conf > /dev/null

RUN apt-get update
RUN apt-get -y install r-base
RUN apt-get -y install libprotobuf-dev
RUN apt-get -y install libcurl4-gnutls-dev
RUN apt-get -y install libxml2-dev
RUN apt-get -y install libprotoc-dev
RUN apt-get -y install build-essential libcurl4-gnutls-dev libxml2-dev libssl-dev
RUN apt-get -y install libpq-dev