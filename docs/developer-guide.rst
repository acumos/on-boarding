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

===========================
On-Boarding Developer Guide
===========================

This is the developers guide to Onboarding.

**1: introduction, what is Onboarding ?**
-----------------------------------------

Acumos is intended to enable the use of a wide range of tools and
technologies in the development of machine learning models including
support for both open sourced and proprietary toolkits.

The goal of Onboarding is : 

- Provide an ingestion interface for various types of models to enter
 the  Acumos machine learning platform. 
- Create artifacts required by Acumos, like for example the TOSCA file
 that is needed to use the model in the Acumos Design Studio.
- Create adequate identifiers like for example solution_ID
- Create the docker image

The solution for accommodating a myriad of different model types is to
provide a custom wrapping library for each runtime. The wrapper 
will encapsulate the complexity surrounding the serialization and
deserialization of models. Additionally, the wrapper will provide a 
common native interface for invoking the inner model.

**2: Target Users **
--------------------
This guide is targeted towards the open source user community that:

1. Intends to understand the backend functionality of the Onboarding.

2. Intends to contribute code to enhance the functionality of the Onboarding.

**3: AI framework **
--------------------

Athena release of Acumos is able to onboard Machine Learning model coming 
from the folowing AI framework

1. H20 (java language)

2. Native java

3. R

4. TensorFlow(python)

5. Scikit Learn (python)

Three differents on-boarding Acumos client have been developped to allow users to 
onboard their models

:doc: `Acumos R client developper guide <acumos-r-client>`
:doc: `Acumos Java client developper guide <acumos-java-client>`
:doc: `Acumos python client developper guide <acumos-python-client>`

Each of these onboarding client create a model bundle that contains a Metadata file, a protobuf 
file and the model itself and upload this model bundle in the Acumos onboarding server.

**4: Onboarding Design Architecture**
-------------------------------------

|image0|

The modeler will create model using various technologies (toolkits) and
use the  Acumos client library to upload model to platform. Acumos
onboarding server exposes REST interface, which is used by client
library for uploading the model to platform.

**5: Onboarding Low Level Design**
----------------------------------

Modeler/Data scientist creates model using toolkit. Modeler uses
Acumos-client-library to push the model to  Acumos platform. The client
library uploads model and metadata file to  Acumos onboarding
server. Onboarding server creates docker image of model and push to nexus
docker registry.It also creates solution, puts model and metadata
artifact to repository.

|image2|

**6: Onboarding Use Case**
--------------------------

Below, the data scientist’s model is wrapped to produce a standardized
native model. Depending on the input model, only a subset of standard model interfaces may be supported.

Acumos can then generate a microservice however it wishes. The
underlying generic server can only interface with the inner model via
the wrapper. This decoupling allows us to iterate upon and improve the
wrapper independently of Acumos.

|image3|

**7: Onboarding Model Artifact**
--------------------------------

Model artifacts must provide sufficient metadata that enables  Acumos to instantiate runtimes,
generate microservices, and validate microservice compositions. The proposed solution is to split
the model artifact into public and private  components.

- Public

- Understood by  Acumos. Includes metadata on:

- Model methods and signatures

- Runtime information

- Private

- Opaque to  Acumos but understood by the wrapper library.

- Includes: Serialized model

- Auxiliary artifacts required by wrapper library

- Auxiliary artifacts required by model

By splitting the artifact into public and private pieces, the wrapper
library has the freedom to independently iterate and improve.

|image4|

**8: Onboarding Setup**
-----------------------

Steps:

1. Clone the code from Gerrit Repo:

Repo URL: https://gerrit.acumos.org

Under the dashboard page we have list of Projects,select Onboarding
Project and clone this project by using below clone command:

git clone https://<GERRIT_USER_NAME>@gerrit.acumos.org/r/on-boarding.git

2. After cloning import this project in your recommended IDE like STS.

3. Take the maven update so that you can download all the required
   dependencies for the Onboarding Project.

4. After doing maven update you can run or debug the code by using
   Spring Boot App but before that we need to set the Environment
   Variables in our IDE tool for local testing and if you want to read
   the environment variables once you deployed your code on the dev or
   IST server than you need to set all the environment variables in
   system-integration Project.

**9: Onboarding Technology & Framework**
----------------------------------------

-  Java 1.8

-  Spring Boot

-  Spring REST

-  Docker Java Library

**10: Onboarding – Code Walkthrough & details**
-----------------------------------------------

In Onboarding project we have template folder under resources where we
are putting all the Docker file with some other dependencies for
different Models like h20, java_generic, python, r, etc.

For example:

For Onboarding H20 model we have the h20 Docker file and requirement.txt
file attached below inside h20 folder.

Onboarding code understands this Docker file related to particular model line by line it reads the
commands and performs the action accordingly. It will download all the required dependences
accordingly. In this way we’ll Onboard Model by using this Onboarding Platform.

Note: Make sure the Docker is installed in the local Machine before try
to Onboard the model in by using our local machine Environment.

**11: Onboarding – Docker Image Creation and details**
------------------------------------------------------

The onboarding server exposes REST API for model and metadata upload.

The metadata JSON is validated for valid schema using JSON schema
validator. The model metadata is used to get the runtime version
information, for example python 2.7. This information is used to fetch
the runtime template. The runtime template contains template for
following files.

1.Dockerfile

2.requirements.txt

3.app.py

4.swagger.yaml

Below is the structure:

|image5|

The above template files are populated based on metadata JSON uploaded
by user. Onboarding server uses docker-java library for model docker
image creation. Once the docker image is created, the image is tagged
and pushed to nexus docker registry. The server uses common data
micro-services API to create solution and store model and metadata to
artifact repository.

**12: Onboarding – Model Validation Workflow**
----------------------------------------------

Following steps needs to be executed as part of model validation
workflow:

-  Onboarding server will expose a REST API for validating the model.
   The REST API will take solutionID and metadata JSON containing model
   features as input parameters.

-  The server will fetch the docker image details for the corresponding
   solution and run the model image.

-  The input metadata JSON features will be send to predict API exposed
   by model docker image and output of predict method will be returned
   as API output.

**13: Onboarding Backend API**
------------------------------

Authentication API : This API provides the basic authentication prior to Onboard any model.

- URL=http://hostname:ACUMOS_ONBOARDING_PORT/onboarding-app/v2/auth

- Method = GET.

- input : User_Name, Password.

- output : authentication token.

- hostname : the hostname of the machine in which Acumos have been installed.

- ACUMOS_ONBOARDING_PORT : You can retrieve the value of this variable in the acumos-env.sh file.

- Description : Checks User Name & password to provide an authentication token.



Push model API : This API is used for upload the model bundle in Acumos

- URL=http://hostname:ACUMOS_ONBOARDING_PORT/onboarding-app/v2/models

- Method = POST

- data Params = model bundle, authentication token (provided by Authentication API)

- hostname : the hostname of the machine in which Acumos have been installed.

- ACUMOS_ONBOARDING_PORT : You can retrieve the value of this variable in the acumos-env.sh file.

- Description : Upload the model bundle on the on-boarding server.


The previous authentication method will be soon deprecated in favor of a more robuste authentication
method based on API_token. You will need first to be authenticate on the acumos portal to retrieve
your API_token located in your profil settings and then used it in the Push model API by replace the
authentication token by : username:API_token


.. |image0_old| image:: ./media/DesignArchitecture.png
   :width: 5.64583in
   :height: 5.55208in
.. |image1_old| image:: ./media/HighLevelFlow.png
   :width: 6.26806in
   :height: 1.51389in
.. |image2| image:: ./media/LowLevelDesign.png
   :width: 6.26806in
   :height: 2.43333in
.. |image3| image:: ./media/UseCase.png
   :width: 6.26806in
   :height: 3.0375in
.. |image4| image:: ./media/ModelArtifact.png
   :width: 6.26806in
   :height: 2.5in
.. |image5| image:: ./media/DockerFileStructure.png
   :width: 3.90625in
   :height: 4.94792in
.. |image0| image:: ./media/Architecture_Diagram1.png
   :width: 7.55555in 
   :height: 7.55555in
	
  
