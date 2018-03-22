FROM python:{0}
MAINTAINER trianta@research.att.com

RUN mkdir /app 
WORKDIR /app  
 
ADD . /app

ENV http_proxy {3}
RUN echo $http_proxy
RUN echo "nameserver 8.8.8.8" | tee /etc/resolv.conf > /dev/null

#--proxy http://pb00365848:!PB123456a%40@10.254.40.121:8080
RUN pip install  pytest-runner
RUN apt-get clean && apt-get update 
RUN apt-get -y install libgtk2.0-dev

RUN pip install -r /app/requirements.txt --extra-index-url {1} --trusted-host {2} --find-links /app && \
   pip install acumos_dcae_model_runner --find-links /app --process-dependency-links

ENV http_proxy=""
RUN echo $http_proxy

ENV DCAEPORT=10000
EXPOSE $DCAEPORT

ENTRYPOINT ["acumos_dcae_model_runner"]
CMD ["/app"]