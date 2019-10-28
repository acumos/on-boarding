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
created. Acumos desn't request a license file during the on-boarding, if needed modelers can add a
license file in their docker image model before the on-boarding.


**2 : How to onboard a docker URI model ?**
-------------------------------------------

Acumos allows users to save all their docker image model URI. For each dockerised models, that have
been previously stored by modelers in docker repo like Docker Hub for example, modelers have just to
use the "Onboard dockerised model URI" panel in the "on-boarding model" page of the Acumos portal.
In this panel, type the name of the model and the Host, optionally you can fill the port an the tag.

It is also possible to on-board a licence file associated with your docker URI model. Just drag and
drop or browse your licence file to on-board it. The license profile file name must be "license.json", if the
license profile file extension is not 'json' the license profile on-boarding will not be possible and if the name is
not 'license' Acumos will rename your license profile file as license.json and you will see your license profile file
named as license.json in the artifacts table. If you upload a new version of your license profile after
on-boarding, a number revision will be added to the name of your license profile file like : "license-2.json".
To help user create the license profile file expected by Acumos
a license profile editor user guide is available here : `License profile editor user guide <../../submodules/license-manager/docs/user-guide-license-profile-editor.html>`_


The process of on-boarding a docker URI model in Acumos is reduced to create a solution Id, save the
URI and if needed associate a license file with this URI. There are no micro-service, nor tosca file
, nor metadata file, nor protobuf file created.






















.. |image0|
.. |image1| image:: ./media/HighLevelFlow.png
   :width: 6.26806in
   :height: 1.51389in
