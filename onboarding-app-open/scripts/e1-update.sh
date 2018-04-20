#!/bin/bash

IMAGE_NAME="e1-backend"
if [ -n "$1" ]; then  #  If command-line argument present,
  IMAGE_NAME=$1
fi

# install server
apt-get update
apt-get -y install vim openssh-server aptitude
# enable ssh with passwords
sed -i -e 's/#PasswordAuthentication/PasswordAuthentication/g' /etc/ssh/sshd_config
/etc/init.d/ssh restart

# install software
#/bot/bin/upgrade.sh

# relocate directories
rmdir /home
rm /home
ln -s /e1/home /home
chmod og-rx /e1/bin

useradd -s /bin/bash -M -p c0Gn1t4 acumos # 52401

# install specific software dependencies
apt-get -y install maven

# TODO: something special to build?
cd /home/acumos/on-boarding/onboarding-sample-services
git update
mvn clean install

# launch the java object
java -Djava.security.egd=file:/dev/./urandom -jar target/onboarding-app.jar &
