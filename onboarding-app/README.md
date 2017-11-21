# Cognita Onboarding Service

This micro service is a Spring-Boot application for on-boarding component in the Cognita platform.

The code for creating docker-image for provided model and meta-data is implemented using docker-java. 

## Build Prerequisites

1. Java version 1.8
2. Maven version 3
3. Connectivity to Maven Central (for most jars)

## Run Prerequisites

1. Java version 1.8  
2. A valid application.properties file.
Configure 'application.properties' file located at 'src/main/resources/application.properties'. Provide valid docker host, config, certPath. For now ignore docker registry configuration.  
~~~
server.contextPath=/onboarding-app
api.base.path=/onboarding-app/api
logging.file=logs/onboarding-app.log
docker.host=192.168.99.100
docker.port=2376
docker.config=../.docker
docker.tls.verify=true
docker.tls.certPath=../.docker/machine/certs
docker.api.version=1.23
docker.registry.url=https://index.docker.io/v1/
docker.registry.username=admin
docker.registry.password=admin123
docker.registry.email=admin@cognita.com
docker.max_total_connections=1
docker.max_per_route_connections=
~~~

#################################################

## How to run it with Docker
Assume you already have Docker installed. See https://docs.docker.com/installation/.

First, clone the project and build locally:

~~~
git clone https://pbhogan1@bitbucket.org/cognita_dev/on-boarding.git
cd on-boarding/onboarding-sample-services
mvn clean package docker:build
~~~
Optionally, docker image for onboarding-sample-services can be created using Dockerfile. Dockerfile for project is already committed at "src/main/docker". Create a folder 'onboarding-service' and copy Dockerfile along with project jar file.
Run following command to create image:
~~~
Go to 'onboarding-service' folder in docker
docker build -t onboarding-app .
~~~    

Run demo onbording application in Docker container:
~~~
docker run -p 8080:8080 --name onbording-app
~~~

You can check the log by
~~~
docker logs onbording-app
~~~

Open http://localhost:8080/onbording-app in browser and you should see the message. If you are using Boot2Docker in Mac OSX or Windows pc 
find ip by *boot2docker ip* and replace _localhost_ to _boot2docker ip_.

#################################################

## How to run it with Java
Assume you already have Java installed. Java version 1.8

Build jar file of project.
Run demo onboarding application using java as follows:
~~~
java -jar onboarding-app.jar
~~~

#################################################

## Example docker configuration scripts 

A few helper scripts were created to manage the manually created docker image
on the jump server.  They are included in `bin` and function as below. 
Kept as sample/starter reference by Eric (7/3/17).

* `bin/e1-init.sh` - script to create a new image from public reference
* `bin/e1-update.sh` - script to update image with needed software
* `bin/e1-connect.sh` - sample script to connect to running docker instance


## Release Notes

Build 1.0.0-SNAPSHOT, 29 June 2017
 - Initial sample
 - Work in progress


