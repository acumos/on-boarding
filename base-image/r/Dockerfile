# Creates base image suitable for running models with R

FROM ubuntu:16.04
RUN echo "nameserver 8.8.8.8" | tee /etc/resolv.conf > /dev/null

ADD . /

#install R version 3.6.3
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys E298A3A825C0D65DFD57CBB651716619E084DAB9
RUN echo "deb http://cloud.r-project.org/bin/linux/ubuntu xenial-cran35/" | tee -a /etc/apt/sources.list

# put update and install on same RUN cmd to avoid Docker layer caching
RUN apt-get update && apt-get -y install r-base libprotobuf-dev libcurl4-gnutls-dev libxml2-dev libprotoc-dev build-essential libcurl4-gnutls-dev libxml2-dev libssl-dev libpq-dev

#RUN Rscript /acumos.R
RUN Rscript /packages.R
