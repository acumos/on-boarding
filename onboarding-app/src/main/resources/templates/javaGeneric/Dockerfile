FROM java:8
VOLUME /tmp

WORKDIR /app
ADD . /app

#set proxy
#ENV http_proxy http://10.53.130.56:8080
#RUN echo $http_proxy
RUN echo "nameserver 8.8.8.8" | tee /etc/resolv.conf > /dev/null

#Install protoc
RUN curl -OL https://github.com/google/protobuf/releases/download/v3.4.0/protoc-3.4.0-linux-x86_64.zip
RUN unzip protoc-3.4.0-linux-x86_64.zip
RUN mv bin/* /usr/local/bin/
RUN mv include/* /usr/local/include/
RUN chown root /usr/local/bin/protoc
RUN chown -R root /usr/local/include/google

#RUN echo $PATH
#set env variable for protoc if need be

#RUN echo "All setup completed, removing the proxy address from the environment"
#ENV http_proxy=""
#RUN echo $http_proxy

#expose port for service
EXPOSE {0}

#Deploy Model service
ADD getVersion.sh /app/bin/getVersion.sh
RUN chmod 777 /app/bin/getVersion.sh
ADD protoc /app/bin/protoc
RUN chmod 777 /app/bin/protoc
ADD {1} /app/app.jar
ADD {2} /app/models/model.jar
ADD default.proto /app/models/default.proto
ADD modelConfig.properties /app/models/modelConfig.properties
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.config.location=application.properties","-jar","/app/app.jar"]