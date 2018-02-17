FROM base-r:latest

WORKDIR /app
ADD . /app

ENV http_proxy {0}
RUN echo $http_proxy

EXPOSE 8100 8101
#install packages
RUN Rscript packages.R
RUN Rscript acumos.R

#running APPS
CMD ["Rscript","run.R"]