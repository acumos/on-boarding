# Dockerfile for Argus container

FROM ubuntu:16.04

ENV DEBIAN_FRONTEND=noninteractive
WORKDIR /app
ADD . /app
RUN echo 'Building Argus MS Image...'

RUN dpkg-divert --local --rename --add /sbin/initctl
RUN ln -sf /bin/true /sbin/initctl
#Dev
ENV http_proxy http://10.1.0.6:3128
#Dev
ENV https_proxy http://10.1.0.6:3128
#IST
#ENV http_proxy http://10.2.0.4:3128
#IST2
#ENV http_proxy http://10.2.0.7:3128
#Demo
#ENV http_proxy http://10.3.0.4:3128
RUN echo $http_proxy

#RUN apt-get update
# clean and update sources
RUN apt-get clean && apt-get update
RUN apt-get -y install mysql-server
RUN apt-get -y install default-jdk 
RUN apt-get -y install build-essential
RUN apt-get -y install --force-yes cpanminus && apt-get clean

RUN ["cpanm","--notest", "LWP::UserAgent", "JSON"] ; exit 0

ADD ./bootstrap.sh /opt/bootstrap.sh
ADD ./model_runner.py /app/model_runner.py
RUN chmod 755 /opt/bootstrap.sh
RUN chmod 755 /app/model_runner.py
#ADD ./model.tar.gz /app/model.tar.gz

ENV http_proxy=""
RUN echo $http_proxy
ENV https_proxy=""
RUN echo $https_proxy

EXPOSE 3306

CMD ["/bin/bash", "/opt/bootstrap.sh"]
CMD ["/usr/bin/python3.5", "/app/model_runner.py"]