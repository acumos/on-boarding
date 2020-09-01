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

=============================================================
On-Boarding docker image model or docker URI model user guide
=============================================================

**1: How to onboard a docker image model ?**
--------------------------------------------

Acumos allows users to onboard their docker image models. Each model dockerised outside acumos by
modelers can be onboarded in Acumos. You just have to use the "Onboard dockerised model" panel in
the "on-boarding model" page of the Acumos portal. In this panel just type the name of the model and
you will received the Acumos image reference to be used to push your docker image model in Acumos.
This Acumos image reference looks like :

.. code:: bash

    <acumos_domain>:<docker_proxy_port>/modelname_soultion_id:tag

Then users have to follow the three steps depicted here :

1 : Authenticate in the Acumos docker registry

.. code:: bash

    docker login https://<acumos_domain>:<docker_proxy_port> -u <acumos_userid> -p <acumos_password>

2 : Tag the docker image model with the Acumos image reference

.. code:: bash

    docker tag my_image_model <acumos_domain>:<docker_proxy_port>/modelname_solution_id:tag

3 : Push the model in Acumos

.. code:: bash

    docker push <acumos_domain>:<docker_proxy_port>/modelname_solution_id:tag

The process of on-boarding a docker image model in Acumos is reduced to create a solution Id and
upload the model. There are no micro-service, nor tosca file, nor metadata file, nor protobuf file
created. Acumos doesn't request a license file during the on-boarding, if needed modelers can add a
license file in their docker image model before the on-boarding.

**2 : How to onboard a docker URI model ?**
-------------------------------------------

Acumos allows users to save all their docker image model URI that have been previously stored by 
modelers in docker repo like Docker Hub for example.

Modelers can on-board their docker image model URI by Web or by CLI.

Onboarding by Web :
-------------------

Modelers have just to use the "Onboard dockerised model URI" panel in the "on-boarding model" page of 
the Acumos portal. In this panel, type the name of the model and the Host on which the docker image is stored.
Optionally you can fill the port an the tag.

It is also possible to Drag and Drop or browse a protobuf file or a license file alongside the docker image 
model URI

Onboarding by CLI :
-------------------

Modelers have just to use the following script "CLI_docker_image_uri_script.py" located in 
"on-boarding/docs/script" repository.

This python script will prompt users to enter their Acumos username and password. Then it will ask users
to give a model name, the docker image URI and the host name of their own Acumos platform.

Users have also the possibility to onboard their docker model URI with licence file and/or protobuf file
 by putting these files in the same folder than the python script and say "yes" to the two last questions.

To help user create the license profile file expected by Acumos
a license profile editor user guide is available here : 
`License profile editor user guide <../../license-manager/docs/user-guide-license-profile-editor.html>`_


**3 : Common to Dockerized Model URI and Dockerized Model**
-----------------------------------------------------------

Whatever the case you can on-board a protobuf file and/or a license file associated with your model by browsing or drag and drop (These two files are optional). The protobuf file will allow you to use your model in Design Studio.




















.. |image0|
.. |image1| image:: ./media/HighLevelFlow.png
   :width: 6.26806in
   :height: 1.51389in
