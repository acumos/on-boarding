# Creates base image suitable for running models with R

FROM ubuntu:16.04
RUN echo "nameserver 8.8.8.8" | tee /etc/resolv.conf > /dev/null

# put update and install on same RUN cmd to avoid Docker layer caching
RUN apt-get update && apt-get -y install r-base libprotobuf-dev libcurl4-gnutls-dev libxml2-dev libprotoc-dev build-essential libcurl4-gnutls-dev libxml2-dev libssl-dev libpq-dev
