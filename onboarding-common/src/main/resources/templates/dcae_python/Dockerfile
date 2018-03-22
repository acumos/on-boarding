FROM python:3.6.3
MAINTAINER trianta@research.att.com

RUN mkdir /app 
WORKDIR /app   
   
ADD . /app

RUN pip install -r /app/requirements.txt --find-links /app && \
    pip install acumos_dcae_model_runner --find-links /app --process-dependency-links

ENV DCAEPORT=10000
EXPOSE $DCAEPORT

ENTRYPOINT ["acumos_dcae_model_runner"]
CMD ["/app"]