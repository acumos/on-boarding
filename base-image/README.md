# Acumos On-Boarding Base Images

This directory holds docker files to build images used when creating a micro service.
These images require many Linux packages and the apt-get steps sometimes fail randomly. 
Staging base images reduces the time of image creation and the chance of random failure.
