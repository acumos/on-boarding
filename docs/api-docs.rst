.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2018 <YOUR COMPANY NAME>. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by <YOUR COMPANY NAME>
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
..      http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================
.. PLEASE REMEMBER TO UPDATE THE LICENSE ABOVE WITH YOUR COMPANY NAME AND THE CORRECT YEAR

==================================
Application Programming Interfaces
==================================

It exists four API used by the Model On-Boarding project. Two to authenticate users before on-boarding an
two others to push models bundle and models under ONNX or PFA format in Acumos.

Regarding authentication you can use one or the other to authenticate yourself.

Regarding the push API, one is dedicated to models built with R, python or Java, and the other one is
dedicated to ONNX and PFA model.

API Group 1
===========

**Validate API-Token API** : This API provide an API Token (available in the user settings) that can be
used to onboard a model.

- Portal will expose  validateApiToken

- URL=http://{HOST}/auth/validateApiToken

- input:apiToken , Username

- output:ResponseDetail  -- "Valid Token" for success /  "Validation Failed" for failure

- ResponseBody: UserId for success only

Portal Webonboarding will  pass access_token = username:apitoken in the header  "Authorization"
Request to Onboarding Onboarding will use the Header Info to get the Username + apitoken


**Authentication API** : This API provides a JWT token that can be used to onboard a model

- URL=http://hostname:ACUMOS_ONBOARDING_PORT/onboarding-app/v2/auth

- Method = GET.

- input : User_Name, Password.

- output : authentication token.

- hostname : the hostname of the machine in which Acumos have been installed.

- ACUMOS_ONBOARDING_PORT : You can retrieve the value of this variable in the acumos-env.sh file.

- Description : Checks User Name & password to provide an authentication token.

API Group 2
===========

**Push model bundle API** : This API is used to on-board the model bundle in Acumos for R, Python or Java models by WEB or CLI on-boarding

- URL=http://hostname:ACUMOS_ONBOARDING_PORT/onboarding-app/v2/models

- Method = POST

- data Params :

        - model (Required - file for model bundle model.zip to onboard, Parameter Type - formdata)
        - metadata (Required - model.protobuf file for model to onboard, Parameter Type - formdata)
        - schema (Required - metadata.json file for model, Parameter Type - formdata)
        - license (optional parameter - license.json associated with model, Parameter Type - formdata)
        - Authorization(Optional - jwt token or username:apitoken, Parameter Type - header)
        - isCreateMicroservice (Optional - boolean value to trigger microservice generation, default=true, Parameter Type - header)
        - deploy (Optional - boolean parameter to trigger deployment of the microservice, default=false, Parameter Type - header)
        - tracking_id (Optional - UUID for tracking E2E transaction from Portal to onboarding to microservice generation, Parameter Type - header)
        - provider (Optional - for portal authentication, Parameter Type - header)
        - shareUserName (Optional - User Name for sharing the model as co-owner, Parameter Type - header)
        - modName (Optional - Model Name to be used as display name else Model name from metadata is used, Parameter Type - header)
        - deployment_env (Optional - Identify deployment environment for model as DCAE or non-DCAE, default is non-DCAE, Parameter Type - header)
        - Request-ID (Optional - UUID received from Portal else generated for tracking transaction in CDS, Parameter Type - header)

- hostname : the hostname of the machine in which Acumos have been installed.

- ACUMOS_ONBOARDING_PORT : You can retrieve the value of this variable in the acumos-env.sh file.

- Description : Upload the model bundle on the on-boarding server.


**Push model API** : This API is used to onboard Docker URI model 

- URL=http://hostname:ACUMOS_ONBOARDING_PORT/onboarding-app/v2/advancedModel

- Method = POST

- data params :

        - model (Optional - file for model to onboard - ONNX/PFA file, Parameter Type - formdata)
        - license (optional parameter - license.json associated with model, Parameter Type - formdata)
        - modelname (Required - Model Name to be used as display name, Parameter Type - header)
        - Authorization (jwt token or username:apitoken, Parameter Type - header)
        - isCreateMicroservice (boolean value to trigger microservice generation, default=false, Parameter Type - header)
        - deploy (Optional - boolean parameter to trigger deployment of the microservice, default=false, Parameter Type - header)
        - dockerfileURL (Optional - if docker URL is given then file is not necessary, Parameter Type - header)
        - provider (optional parameter - for portal authentication, Parameter Type - header)
        - tracking_id (optional parameter - UUID for tracking E2E transaction from Portal to onboarding to microservice generation, Parameter Type - header)
        - Request-ID (optional parameter - UUID received from Portal else generated for tracking transaction in CDS, Parameter Type - header)
        - shareUserName (optional parameter - User Name for sharing the model as co-owner, Parameter Type - header)

- hostname : the hostname of the machine in which Acumos have been installed.

- ACUMOS_ONBOARDING_PORT : You can retrieve the value of this variable in the acumos-env.sh file



Swagger
=======

You can also access to a swagger to test the API independantly of the onboarding client. This swagger is located at : https://namespace/onboarding-app/swagger-ui.html

"namespace" is the value of namespace variable you put in global_value.yaml file
