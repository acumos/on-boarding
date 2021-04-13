.. ===============LICENSE_START============================================================
.. Acumos CC-BY-4.0
.. ========================================================================================
.. Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ========================================================================================
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
.. ===============LICENSE_END=============================================================

======================
On-Boarding User Guide
======================

This is the users guide to Onboarding.

**1: introduction - What is Onboarding?**
-----------------------------------------

Acumos is intended to enable the use of a wide range of tools and technologies in the development
of machine learning models including support for both open sourced and proprietary toolkits.

The goal of Onboarding is to provide an ingestion interface, by web or CLI(command line interface)
for various types of models and to create required artifacts and identifiers to enter the Acumos
platform.

#. Legacy models

 You can on-board models developped in Java 8 or 9, Python>=3.6, <3.7, R>=3.4.4 and sourced from toolkits
 such as Scikit, TensorFlow, H2O, and R. You can choose to create or not the microservice at the end
 of the on-boarding process. If user choose to not create the microservice at the end of on-boarding he
 can create it later.

 In short, our goals for these kinds of models are to generate or provide all the necessary materials
 required to use these kinds of models with the others components of Acumos like:

 - Tosca file for Design studio
 - Represent model I/O such for microservice generation
 - SolutionID for CDS
 - Licence file for licensing management

#. Acumos capabilities by models type

 This table sum-up all the Acumos capabilities available for each kinds of model

 +------------------+--------------------------+----------------+--------------+-----------------------+-------------+
 | Model            | Micro-service generation | Design studio  | Market place | on-board with license | onboarding  |
 +==================+==========================+================+==============+=======================+=============+
 | R model          | Available                | Available      | Available    | Available             | WEB and CLI |
 +------------------+--------------------------+----------------+--------------+-----------------------+-------------+
 | Pyhton model     | Available                | Available      | Available    | Available             | WEB and CLI |
 +------------------+--------------------------+----------------+--------------+-----------------------+-------------+
 | Java model       | Available                | Available      | Available    | Available             | WEB and CLI |
 +------------------+--------------------------+----------------+--------------+-----------------------+-------------+
 | ONNX model       | Available                | Available      | Available    | Available             | WEB and CLI |
 +------------------+--------------------------+----------------+--------------+-----------------------+-------------+
 | PFA model        | Not available            | Not available  | Available    | Available             | WEB only    |
 +------------------+--------------------------+----------------+--------------+-----------------------+-------------+
 | Dockerized model | Not applicable           | Not available  | Available    | Available             | WEB only    |
 +------------------+--------------------------+----------------+--------------+-----------------------+-------------+
 | URI model        | Not applicable           | Not applicable | Available    | Available             | WEB only    |
 +------------------+--------------------------+----------------+--------------+-----------------------+-------------+
 | C++ model        | Available                | Not applicable | Available    | Available             | WEB only    | 
 +------------------+--------------------------+----------------+--------------+-----------------------+-------------+

**3: Onboarding models built in R, Java or python language**
------------------------------------------------------------

For these three different languages it exists three on-boarding acumos clients to use. The client will build a model bundle
composed of differents files requested by Acumos. Once the model bundle is created you can choose to on-board it by CLI
or WEB. By CLI on-boarding you will have to use a push() function that belongs to the Acumos on-boardign client, if you choose
Web on-boarding you have to use the "ON-BOARDING MODEL" panel of you own Acumos instance. Whatever the kind of on-boarding, by 
WEB or by CLI, you can choose to trigger or not the launch of the micro-service generation at the end of the on-boarding process.

Whatever the acumos client  you used, you will be prompted to provide your credentials in this way : "your_acumos_login":"your_api_token".
Your api token can be retrieved in your own acumos instance, after authentication, in your acumos settings. After a successful CLI
onboarding with Micro-service creation, the message response will display the acumos Docker URI that can be used to load the
Acumos docker image model in your local Docker registry.

Please refer to the following user guide :

`Acumos R client user guide <../../acumos-r-client/docs/onboarding-r-guide.html>`_

`Acumos Python client user guide <https://pypi.org/project/acumos/>`_

`Acumos Java client user guide <../../acumos-java-client/docs/onboarding-java-guide.html>`_

`Acumos C++ client user guide <../../acumos-c-client/docs/onboarding-Cpp-guide.html>`_


**4: Onboarding ONNX and PFA models**
-------------------------------------

Onboard PFA model consists only of an upload (web_onboarding) of the model as there is no micro-service creation for the moment.

Please refer to the following user guide to on-board ONNX models `Acumos ONNX client user guide <https://pypi.org/project/onnx4acumos/>`_

**5: On-Boarding docker image model or docker URI model**
---------------------------------------------------------

You can create models in the language of your choice then dockerize your models yourelves and onboard these dockerized models
or dockerized model URIs. Of course for these kinds of models the microservice generation process is never used.

Please refer to the following user-guide

`On-Boarding docker image model or docker URI model user guide <onboarding-pre_dockerised_and_URI_guide.html>`_

**5: On-Boarding model with a license file**
--------------------------------------------

You can on-board your model with a license (Except for dockerized models as we assume that modelers will embed their licence
in their Docker image). During Web on-boarding you can choose "Upload" a license file, "Select licence Profil" or "Create New",
please refer to licensing documentation for the details. With CLI on-boarding only the "Upload" of a license file is possible 
alongside the model model bundle.

Whatever the case, CLI or WEB on-boarding :

 - If the license file extension is not 'json' the license on-boarding will not be possible.
 - During the on-boarding, your license file will be renamed and you will see your license file as "license-1.0.0.json" in the artifact table.
 - If you on-board a new version of your model through the portal, the license number revision will be increased like that "license-1.0.1.json" to follow the model number revision.

Whatever the kinds of models :

- New solution is created in common database for a new model.
- Existing solution is updated with, a new revision. Revision is updated with artefact details and those artefacts are uploaded to nexus maven repository.

**6: Deploying model after on-boarding**
----------------------------------------

Thanks to deploy parameter, you can trigger the launch of a jenkins job in an external jenkins server. This  parameter is optional and set to False by default. 
Please refers to client on-boarding user guide to have more details.


