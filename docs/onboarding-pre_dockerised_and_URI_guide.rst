.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
.. http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

==================================================
On-Boarding docker image model or docker URI model
==================================================

**1: How to onboard a docker image model ?**
--------------------------------------------

Acumos allows users to onboard their docker image models. Each model dockerised outside acumos by
modelers can be onboarded in Acumos. You just have to use the "Onboard dockerised model" panel in
the "on-boarding model" page of the Acumos portal. In this panel just type the name of the model and
you will received the Acumos image reference to be used to push your docker image model in Acumos.
This Acumos image reference looks like : 

<acumos_domain>:<docker_proxy_port>/modelname_soultion_id:tag

Then users have to follows th thre steps depicted here : 

1 : Authenticate in the Acumos docker registry

docker login https://<acumos_domain>:<docker_proxy_port> -u <acumos_userid> -p <acumos_password>

2 : Tag the docker image model with the Acumos image reference

docker tag my_image_model <acumos_domain>:<docker_proxy_port>/modelname_solution_id:tag

3 : Push the model in Acumos

docker push <acumos_domain>:<docker_proxy_port>/modelname_solution_id:tag

The process of on-boarding a docker image model in Acumos is reduced to create a solution Id and
upload the model. There are no micro-service, nor tosca file, nor metadata file, nor protobuf file
created. Acumos desn't request a license file during the on-boarding, if needed modelers can add a
license file in their docker image model before the on-boarding.


**2 : How to onboard a docker URI model ?**
-------------------------------------------

Acumos allows users to save all their docker image model URI. For each dockerised models, that have
been previously stored by modelers in docker repo like Docker Hub for example, modelers have just to
use the "Onboard dockerised model URI" panel in the "on-boarding model" page of the Acumos portal.
In this panel, type the name of the model and the Host, optionally you can fill the port an the tag.

It is also possible to on-board a licence file (license.json) associated with this docker URI model.

The process of on-boarding a docker URI model in Acumos is reduced to create a solution Id, save the
URI and if needed associate a license file with this URI. There are no micro-service, nor tosca file
, nor metadata file, nor protobuf file created.






















.. |image0|
.. |image1| image:: ./media/HighLevelFlow.png
   :width: 6.26806in
   :height: 1.51389in
