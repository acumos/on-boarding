FROM ubuntu:latest
RUN echo "hello world"

WORKDIR /app
ADD . /app

#ENV http_proxy {0}
#RUN echo $http_proxy
RUN echo "nameserver 8.8.8.8" | tee /etc/resolv.conf > /dev/null

#RUN apt-get update
RUN apt-get -y install r-base
RUN apt-get -y install libprotobuf-dev
RUN apt-get -y install libcurl4-gnutls-dev
RUN apt-get -y install libxml2-dev
RUN apt-get -y install libprotoc-dev
RUN apt-get -y install build-essential libcurl4-gnutls-dev libxml2-dev libssl-dev
RUN apt-get -y install libpq-dev

EXPOSE 8100 8101
#install packages
RUN Rscript packages.R
RUN Rscript acumos.R

#RUN echo "All setup completed, removing the proxy address from the environment"
#ENV http_proxy=""
#RUN echo $http_proxy

#running APPS
CMD ["Rscript","run.R"]