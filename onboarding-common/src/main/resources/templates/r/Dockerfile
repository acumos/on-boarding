FROM nexus3.acumos.org:10004/onboarding-base-r:1.0

WORKDIR /app
ADD . /app

ENV http_proxy {0}
RUN echo $http_proxy
ENV https_proxy {0}
RUN echo $https_proxy

EXPOSE 8100 8101
#install packages
RUN Rscript packages.R
RUN Rscript acumos.R

ENV http_proxy=""
RUN echo $http_proxy
ENV https_proxy=""
RUN echo $https_proxy

#running APPS
CMD ["Rscript","run.R"]