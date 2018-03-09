FROM python:3.6.3
#MAINTAINER trianta@research.att.com

RUN mkdir /app
WORKDIR /app

ADD . /app

RUN pip install -r requirements.txt --extra-index-url {1} --trusted-host {2}


ENV DCAEPORT=10000
EXPOSE $DCAEPORT

CMD ["python", "runner.py"]
