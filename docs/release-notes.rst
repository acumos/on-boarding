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

=========================
On-Boarding Release Notes
=========================

These release notes cover the on-boarding common library and the on-boarding application
for public use, which are released together.

Version 5.0.0, 12 April 2021
----------------------------
* Add "deploy" parameter in onboarding API `ACUMOS-4302 <https://jira.acumos.org/browse/ACUMOS-4302>`_
* Update on-boarding base-r image with new install procédure `ACUMOS-4325 <https://jira.acumos.org/browse/ACUMOS-4325>`_

Version 4.6.4, 10 Sept 2020
---------------------------
* Common Data Service client at version 3.1.1
* Write script to on-board model URI by CLI `ACUMOS-4213 <https://jira.acumos.org/browse/ACUMOS-4213>`_
* Modelname and URI fixes for "CLI dockerized model URI" `ACUMOS-4266 <https://jira.acumos.org/browse/ACUMOS-4266>`_
* Modifying on-boarding base-r image to speed up the creation of R model microservice `ACUMOS-4218 <https://jira.acumos.org/browse/ACUMOS-4218>`_

Version 4.6.3, 29 May 2020
--------------------------
* Common Data Service client at version 3.1.1
* Add acumos dependancies in base-r-image `ACUMOS-3861 <https://jira.acumos.org/browse/ACUMOS-3861>`_

Version 4.6.2, 20 May 2020
--------------------------
* Common Data Service client at version 3.1.1
* Wrong docker proxy port `ACUMOS-4146 <https://jira.acumos.org/browse/ACUMOS-4146>`_


Version 4.6.1, 15 May 2020
--------------------------
* Common Data Service client at version 3.1.1
* Update onboarding-base-r tag https://gerrit.acumos.org/r/c/on-boarding/+/7930

Version 4.6.0, 08 May 2020
--------------------------
* Common Data Service client at version 3.1.1
* <IST><Onboarding>Error displayed while executing R model `ACUMOS-3861 <https://jira.acumos.org/browse/ACUMOS-3861>`_
* Create Tosca files (TOSCA, TOSCAPROTOBUF) based on protobuf file, for pre-dockerized model `ACUMOS-4045 <https://jira.acumos.org/browse/ACUMOS-4045>`_

Version 4.5.0, 3 April 2020
---------------------------
* Common Data Service client at version 3.1.1
* TOSCAModelGeneratorClient version 2.0.8
* Create Tosca files (TOSCA, TOSCAPROTOBUF) based on protobuf file, for pre-dockerized model `ACUMOS-4045 <https://jira.acumos.org/browse/ACUMOS-4045>`_
* Create Tosca files (TOSCA, TOSCAPROTOBUF) based on protobuf file, for dockerized model URI `ACUMOS-4046 <https://jira.acumos.org/browse/ACUMOS-4046>`_


Version 4.4.0, 16 March 2020
----------------------------
* Common Data Service client at version 3.1.1
* While on-boarding error model, the error label is not visible in model description page `ACUMOS-3855 <https://jira.acumos.org/browse/ACUMOS-3855>`_

Version 4.3.0, 20 Feb 2020
--------------------------
* Common Data Service client at version 3.1.1
* Take into account code artifact in on-boarding process `ACUMOS-3777 <https://jira.acumos.org/browse/ACUMOS-3777>`_
* Enrich pre-dockerized models `ACUMOS-3144 <https://jira.acumos.org/browse/ACUMOS-3144>`_

Version 4.2.0, 31 Jan 2020
--------------------------
* Common Data Service client at version 3.1.1
* Null pointer fixes when onboarding using CLI https://gerrit.acumos.org/r/c/on-boarding/+/6308
* Sonar code coverage for Obdr `ACUMOS-3954 <https://jira.acumos.org/browse/ACUMOS-3954>`_

Version 4.1.0, 21 Jan 2020
--------------------------
* Enrich message response with Docker URI `ACUMOS-3771 <https://jira.acumos.org/browse/ACUMOS-3771>`_

Version 3.8.1, 23 Dec 2019
--------------------------
* Common Data Service client at version 3.1.0
* Security Verification at version 1.2.2
* miss new logging library `ACUMOS-3847 <https://jira.acumos.org/browse/ACUMOS-3847>`_

Version 3.8.0, 13 Dec 2019
--------------------------
* Common Data Service client at version 3.1.0
* Security Verification at version 1.2.1
* License-Manager-Client Library at version 1.4.3 
* Onboarding should download model runner from nexus and package into the model docker image for H2O and java models. `ACUMOS-3758 <https://jira.acumos.org/browse/ACUMOS-3758>`_


Version 3.6.0, 7 Nov 2019
-------------------------
* Common Data Service client at version 3.0.0
* YML changes - "security":{"verificationEnableFlag":"<Boolean>"}
* Update LMCL to 1.4.1 and Update SV Client to 1.2.0 `ACUMOS-3668 <https://jira.acumos.org/browse/ACUMOS-3668/>`_
* IST2 - Onboarding block calling SV with a flag `ACUMOS-3676 <https://jira.acumos.org/browse/ACUMOS-3676/>`_
* <IST><Portal Marketplace> Version display format is not consistent `ACUMOS-3656 <https://jira.acumos.org/browse/ACUMOS-3656/>`_
* correcting typo mistake `ACUMOS-3675 <https://jira.acumos.org/browse/ACUMOS-3675/>`_

Version 3.5.0, 11 Oct 2019
--------------------------
* Common Data Service client at version 3.0.0
* YML changes - "security":{"verificationApiUrl":"<securityverificationurl>"}
* Onboarding - Add calls to LicenseProfile.validate api : `ACUMOS-3337 <https://jira.acumos.org/browse/ACUMOS-3337/>`_
* Ability to run SV license scan at completion of onboarding :  `ACUMOS-3394 <https://jira.acumos.org/browse/ACUMOS-3394/>`_
* Fix the Revision Version of Components :  `ACUMOS-3529 <https://jira.acumos.org/browse/ACUMOS-3529/>`_
* On-boarding - Java Code upgrade to Java 11 or 12 :  `ACUMOS-3328 <https://jira.acumos.org/browse/ACUMOS-3328/>`_


Version 3.4.0, 3 Oct 2019
-------------------------
* Common Data Service client at version 3.0.0
* As a User , I want to see an Enhance on-boarding processes to allow choice of new model vs new revision : `ACUMOS-1216 <https://jira.acumos.org/browse/ACUMOS-1216/>`_


Version 3.2.0, 19 Sept 2019
---------------------------
* Common Data Service client at version 3.0.0


Version 3.1.0, 30 Aug 2019
--------------------------
* Common Data Service client at version 2.2.6
* Take into account c/c++ model in on-boarding process : `ACUMOS-3107 <https://jira.acumos.org/browse/ACUMOS-3107/>`_
* Take into account java model from Spark in on-boarding process : `ACUMOS-3130 <https://jira.acumos.org/browse/ACUMOS-3130/>`_
* <Asynchronous Microservice> Errored model is getting onboarded successfully : `ACUMOS-3022 <https://jira.acumos.org/browse/ACUMOS-3022/>`_

Version 3.0.0, 21 Aug 2019
--------------------------
* Common Data Service client at version 2.2.6
* attach a license profile as JSON during on-boarding with Artifact Type LI : `ACUMOS-3171 <https://jira.acumos.org/browse/ACUMOS-3171/>`_


Version 2.16.0, 18 July 2019
----------------------------
* Common Data Service client at version 2.2.4
* Log files generated in application should display logs as per the log standardization : `ACUMOS-2923 <https://jira.acumos.org/browse/ACUMOS-2923/>`_
* code coverage : `ACUMOS-3224 <https://jira.acumos.org/browse/ACUMOS-3224/>`_

Version 2.15.0, 20 June 2019
----------------------------
* Common Data Service client at version 2.2.4
* Microservice entry is remaining InProgress after completing onboarding process : `ACUMOS-3012 <https://jira.acumos.org/browse/ACUMOS-3012/>`_
* Async MSGen Notification logs not getting generated : `ACUMOS-3088 <https://jira.acumos.org/browse/ACUMOS-3088/>`_

Version 2.14.0, 30 May 2019
---------------------------
* Common Data Service client at version 2.2.4
* Test on licence file name : `ACUMOS-2955 <https://jira.acumos.org/browse/ACUMOS-2955/>`_

Version 2.13.0, 8 May 2019
--------------------------
* Common Data Service client at version 2.2.2
* Logs are not displayed as per the standardization : `ACUMOS-2779 <https://jira.acumos.org/browse/ACUMOS-2779/>`_
* Add non configurable parameters to application.properties file : `ACUMOS-2872 <https://jira.acumos.org/browse/ACUMOS-2872/>`_

Version 2.12.0, 19 April 2019
-----------------------------
* Common Data Service client at version 2.2.1
* Modify documentation in accordance with EPIC 762 : `ACUMOS-2276 <https://jira.acumos.org/browse/ACUMOS-2276/>`_
* Model image creator should use new Python model runner : `ACUMOS-1559 <https://jira.acumos.org/browse/ACUMOS-1559/>`_
* Onboarding app run containerized process as unprivileged user : `ACUMOS-2772 <https://jira.acumos.org/browse/ACUMOS-2772/>`_

Version 2.11.0, 12 April 2019
-----------------------------
* API and on-boarding process for pre-dockerised model : `ACUMOS-2436 <https://jira.acumos.org/browse/ACUMOS-2436/>`_
* Logging Standardization - Onboarding : `ACUMOS-2324 <https://jira.acumos.org/browse/ACUMOS-2324/>`_

Version 2.10.0, 29 March 2019
-----------------------------
* Common Data Service client at version 2.1.2
* Aynchrounous Microservice generation response handling in Onboarding : `ACUMOS-2625 <https://jira.acumos.org/browse/ACUMOS-2625/>`_
* Microservices code refactoring for asynchronous processing : `ACUMOS-2626 <https://jira.acumos.org/browse/ACUMOS-2626/>`_

Version 2.9.0, 22 March 2019
----------------------------
* Common Data Service client at version 2.1.2
* onnx onboarding issues : `ACUMOS-2635 <https://jira.acumos.org/browse/ACUMOS-2635/>`_

Version 2.8.0, 18 March 2019
----------------------------
* Common Data Service client at version 2.1.2
* check license.json file name and correct spelling of license : `ACUMOS-2616 <https://jira.acumos.org/browse/ACUMOS-2616/>`_
* On-boarding fails to create TOSCA artifacts but declares success anyhow : `ACUMOS-2619 <https://jira.acumos.org/browse/ACUMOS-2619/>`_
* On-boarding task Status is not getting updated : `ACUMOS-2620 <https://jira.acumos.org/browse/ACUMOS-2620/>`_
* On-boarding task SolutionId and RevisionId are showing as null : `ACUMOS-2622 <https://jira.acumos.org/browse/ACUMOS-2622/>`_

Version 2.7.0, 8 March 2019
---------------------------
* Common Data Service client at version 2.1.2
* Onboarding to check license file name : `ACUMOS-2586 <https://jira.acumos.org/browse/ACUMOS-2586/>`_
* Show "jwtToken" and "Upload Artifact" in output log file : `ACUMOS-2488 <https://jira.acumos.org/browse/ACUMOS-2488/>`_


Version 2.6.0, 4 March 2019
---------------------------
* Common Data Service client at version 2.1.1
* Fix the c_step result and c_task logic from onboarding : `ACUMOS-2588 <https://jira.acumos.org/browse/ACUMOS-2588/>`_
* MOB revise calls to CDS to publish Onboarding History : `ACUMOS-2402 <https://jira.acumos.org/browse/ACUMOS-2402/>`_

Version 2.4.0, 13 February 2019
-------------------------------
* Common Data Service client at version 2.0.7
* APIs modification in accodance with EPIC 762 : `ACUMOS-2275 <https://jira.acumos.org/browse/ACUMOS-2275/>`_
* Modify Onboarding legacy API in accordance with EPIC 2107 : `ACUMOS-2262 <https://jira.acumos.org/browse/ACUMOS-2262/>`_

Version 2.3.0, 31 January 2019
------------------------------
* On-boarding fails when using CDS 2.0, need version 2.0.4 : `ACUMOS-2415 <https://jira.acumos.org/browse/ACUMOS-2415/>`_
* API for ONNX, PFA models : `ACUMOS-2242 <https://jira.acumos.org/browse/ACUMOS-2242/>`_
* Create new on-boarding process for ONNX, PFA : `ACUMOS-2247 <https://jira.acumos.org/browse/ACUMOS-2247/>`_
* MOB update for CDS 2.0.4 data and toolkit related changes : `ACUMOS-2379 <https://jira.acumos.org/browse/ACUMOS-2379/>`_

Version 2.2.0, 9 January 2019
-----------------------------
* There's no version controlled Swagger API spec for the onboarding server, and existing docs are inconsistent : `ACUMOS-522 <https://jira.acumos.org/browse/ACUMOS-522/>`_
* Show onboarding component version in output log file : `ACUMOS-1934 <https://jira.acumos.org/browse/ACUMOS-1934/>`_

Version 2.1.0, 21 December 2018
-------------------------------
* Incorrect Protobuf.json and TGIF.json generated for nested messages : `ACUMOS-2272 <https://jira.acumos.org/browse/ACUMOS-2272/>`_

Version 2.0.0, 11 December 2018
-------------------------------
* CDS clients pass request ID from front-end thru in client calls : `ACUMOS-1801 <https://jira.acumos.org/browse/ACUMOS-1801/>`_
* Onboarding doesn't detect failure to validate user via API token : `ACUMOS-2039 <https://jira.acumos.org/browse/ACUMOS-2039/>`_

Version 1.39.0, 11 October 2018
-------------------------------
* provide logs to the user with onboarding result fails for onboarding failure scenario : `ACUMOS-1830 <https://jira.acumos.org/browse/ACUMOS-1830/>`_
* TOSCA m.g.c. generates extra UUID in Nexus repository path : `ACUMOS-1845 <https://jira.acumos.org/browse/ACUMOS-1845/>`_
* Onboarding log file indicates failures on successfull onboarding and different model : `ACUMOS-1879 <https://jira.acumos.org/browse/ACUMOS-1879/>`_
* Spelling mistake in onboarding logs : `ACUMOS-1839 <https://jira.acumos.org/browse/ACUMOS-1839/>`_

Version 1.38.0, 04 October 2018
-------------------------------
* Common Data Service client at version 1.18.2
* TOSCA model generator client at version 1.33.1
* Artifacts from Onboarding contain ID and suffix strings in their names (they should not) : `ACUMOS-1736 <https://jira.acumos.org/browse/ACUMOS-1736/>`_
* Model not onboarding through Build For ONAP feature : `ACUMOS-1639 <https://jira.acumos.org/browse/ACUMOS-1639/>`_
* Provide logs to the user with onboarding results : `ACUMOS-956 <https://jira.acumos.org/browse/ACUMOS-956/>`_

Version 1.37.0, 27 September 2018
---------------------------------
* API Token authentication is not working : `ACUMOS-1771 <https://jira.acumos.org/browse/ACUMOS-1771/>`_
* GenericJava model on-boarding via web is getting fails at dockerize : `ACUMOS-1786 <https://jira.acumos.org/browse/ACUMOS-1786/>`_

Version 1.36.1, 21 September 2018
---------------------------------
* Common Data Service client at version 1.18.1
* TOSCA model generator client at version 0.0.33
* Need log standardization and consistency on-boarding : `ACUMOS-622 <https://jira.acumos.org/browse/ACUMOS-622/>`_
* Upgrade Java server components to Spring-Boot 1.5.16.RELEASE : `ACUMOS-1754 <https://jira.acumos.org/browse/ACUMOS-1754/>`_

Version 1.36.0, 21 September 2018
---------------------------------
* TOSCA model generator client at version 0.0.33
* Need log standardization and consistency on-boarding : `ACUMOS-622 <https://jira.acumos.org/browse/ACUMOS-622/>`_
* on-boarding: Fix RST compile warnings : `ACUMOS-1754 <https://jira.acumos.org/browse/ACUMOS-1754/>`_

Version 1.35.0, 14 September 2018
---------------------------------
* TOSCA poinitng to 0.0.31
* Cleaning code : `ACUMOS-1266 <https://jira.acumos.org/browse/ACUMOS-1266/>`_
* on-boarding Fix RST compile warnings :`ACUMOS-1317 <https://jira.acumos.org/browse/ACUMOS-1317/>`_
* Model onboarding fails for R and python : `ACUMOS-1638 <https://jira.acumos.org/browse/ACUMOS-1638/>`_
* MS logs and docker artifact file is 0kb size : `ACUMOS-1628 <https://jira.acumos.org/browse/ACUMOS-1628/>`_
* IST2: Contact Icon is not displaying at the time of user selection on shared my model screen : `ACUMOS-1583 <https://jira.acumos.org/browse/ACUMOS-1583/>`_

Version 1.34.0, 7 September 2018
--------------------------------
* Pointing to CDS-1.18.0
* MS logs and docker artifact file is 0kb size : `ACUMOS-1628 <https://jira.acumos.org/browse/ACUMOS-1628/>`_

Version 1.33.1, 1 September 2018
--------------------------------
* Patch release to update nexus client version to 2.2.1
* Update nexus client : `ACUMOS-1678 <https://jira.acumos.org/browse/ACUMOS-1678/>`_

Version 1.33.0, 31 August 2018
------------------------------
* Model onboarding fails for R and python : `ACUMOS-1638 <https://jira.acumos.org/browse/ACUMOS-1638/>`_
* MS logs and docker artifact file is 0kb size : `ACUMOS-1628 <https://jira.acumos.org/browse/ACUMOS-1628/>`_
* Onboarding fails for H20 : `ACUMOS-1629 <https://jira.acumos.org/browse/ACUMOS-1629/>`_

Version 1.32.0, 27 August 2018
------------------------------
 * Pointing to CDS-1.17.1
 * Invoke Microservice API at the end of obdr process : `ACUMOS-1537 <https://jira.acumos.org/browse/ACUMOS-1537/>`_
 * Python model runner must use -u flag when start microservice script : `ACUMOS-1416 <https://jira.acumos.org/browse/ACUMOS-1416/>`_
 * Factor MS generation out of onbaording-app : `ACUMOS-1070 <https://jira.acumos.org/browse/ACUMOS-1070/>`_
 * Remove dockerization related methods : `ACUMOS-1300 <https://jira.acumos.org/browse/ACUMOS-1300/>`_
 * Remove Add Artifact with URI : `ACUMOS-1299 <https://jira.acumos.org/browse/ACUMOS-1299/>`_
 * Refactor Onboarding Controller : `ACUMOS-1250 <https://jira.acumos.org/browse/ACUMOS-1250/>`_
 * Fix Developper level bugs : `ACUMOS-1244 <https://jira.acumos.org/browse/ACUMOS-1244/>`_
 * Refactoring on-boarding code : `ACUMOS-1243 <https://jira.acumos.org/browse/ACUMOS-1243/>`_
 * create separate branches - whithout Dockerisation and Dockerisation : `ACUMOS-1237 <https://jira.acumos.org/browse/ACUMOS-1237/>`_
 * Refactor without Dockerisation : `ACUMOS-1238 <https://jira.acumos.org/browse/ACUMOS-1238/>`_
 * Refactor Dockerisation : `ACUMOS-1239 <https://jira.acumos.org/browse/ACUMOS-1239/>`_
 * Add/Modify Unit tests :  `ACUMOS-1241 <https://jira.acumos.org/browse/ACUMOS-1241/>`_
 * E2E Validation of Refactored code : `ACUMOS-1242 <https://jira.acumos.org/browse/ACUMOS-1242/>`_
 * Refactor commonOnBoarding : `ACUMOS-1248 <https://jira.acumos.org/browse/ACUMOS-1248/>`_
 * Factor microservice generation out of onboarding-app : `ACUMOS-1394 <https://jira.acumos.org/browse/ACUMOS-1394/>`_
 
Version 1.30.0, 17 August 2018
------------------------------
 * Pointing to CDS-1.17.1
 * Invoke Microservice API at the end of obdr process : `ACUMOS-1537 <https://jira.acumos.org/browse/ACUMOS-1537/>`_
 * Python model runner must use -u flag when start microservice script : `ACUMOS-1416 <https://jira.acumos.org/browse/ACUMOS-1416/>`_
 * Factor microservice generation out of onboarding app : `ACUMOS-1070 <https://jira.acumos.org/browse/ACUMOS-1070/>`_
 * Remove dockeriation related methods : `ACUMOS-1300 <https://jira.acumos.org/browse/ACUMOS-1300/>`_
 * Remove Add Artifact with URI : `ACUMOS-1299 <https://jira.acumos.org/browse/ACUMOS-1299/>`_
 * Refactor Onboarding Controller : `ACUMOS-1250 <https://jira.acumos.org/browse/ACUMOS-1250/>`_
 * Fix Developper level bugs : `ACUMOS-1244 <https://jira.acumos.org/browse/ACUMOS-1244/>`_
 * Refactoring on-boarding code : `ACUMOS-1243 <https://jira.acumos.org/browse/ACUMOS-1243/>`_

Version 1.29.0, 12 July 2018
----------------------------
 * Dockerfile for Python DCAE model runner has outdated lines(ACUMOS-1263)
 * R models no longer run properly as microservices when downloading(ACUMOS-1279)

Version 1.28.0, 6 July 2018
---------------------------
 * CDS pointing to 1.15.3
 * Dockerfile for Python DCAE model runner has outdated lines(ACUMOS-1263)
 * R models no longer run properly as microservices when downloading(ACUMOS-1279)
 * My Models: Failed model name is not displayed as it is given at the time of web onboarding(ACUMOS-1157)
 * <ONAP> <Onboarding> Artifacts are not getting created properly for ONAP build(ACUMOS-709)


Version 1.27.0, 13 June 2018
----------------------------
 * R-model initial configuration missing (ACUMOS-667)
 * Several onboarding unit tests do not appear to be testing correctly (ACUMOS-562)
 * <IST><Onboarding> "Successful" miss-spelled in onboarding logs (ACUMOS-1100)
 * This build has yml changes, needs to provide rbase image name and nexus user name and password for current environment as below. "base_image": {  "rimage": "nexus3.acumos.org:10004/onboarding-base-r:1.0","dockerusername": "*****","dockerpassword": "*****"}

Version 1.26.0, 31 May 2018
---------------------------
* Onboarding server gives mysterious error when using "/" character in model name (ACUMOS-952)
* Set https_proxy ENV variable as well as http_proxy in Dockerfile (ACUMOS-965)

Version 1.25.4, 31 May 2018
---------------------------

* Set https_proxy ENV variable as well as http_proxy in Dockerfile (ACUMOS-965)

Version 1.25.3, 31 May 2018
---------------------------

* Onboarding server gives mysterious error when using "/" character in model name (ACUMOS-952)

Version 1.25.0, 29 May 2018
---------------------------

* Remove sensitive information from the onboarding log that is pushed to nexus (ACUMOS-948)

Version 1.24.0, 22 May 2018
---------------------------

* Capture Onboarding log as a new artifact (ACUMOS-751)
* Clean windows-specific code that constructs file paths (ACUMOS-818)
* TOSCA version updated to 0.0.27

Version 1.23.2, 14 May 2018
---------------------------

* Capture Onboarding log as a new artifact (ACUMOS-751)


Version 1.23.0, 10 May 2018
---------------------------

* Build for IST
* Fixes for ACUMOS-398, ACUMOS-737
* CDS pointing to 1.14.4

Version 1.22.0, 4 May 2018
---------------------------

* Build for IST
* Fixes for ACUMOS-753, ACUMOS-780, ACUMOS-782, ACUMOS-667

Version 1.21.0, 26 Apr 2018
---------------------------

* Build for IST
* Revert to acumos-nexus-client v2.0.0 (ACUMOS-665)

Version 1.20.3, 25 Apr 2018
---------------------------

* Changes for revertback process (ACUMOS-723)
* Simplify dockerfile commands (ACUMOS-667)

Version 1.20.2, 25 Apr 2018
---------------------------

* Changes for revertback process (ACUMOS-723)
* Use repaired acumos-nexus-client (ACUMOS-665)

Version 1.20.1, 20 Apr 2018
---------------------------

* removed cognita-specific code (ACUMOS-692)

Version 1.20.0, 19 Apr 2018
---------------------------

* Build for IST
* CDS pointing to 1.14.3 (ACUMOS-684)

Version 1.19.3, 19 Apr 2018
---------------------------

* Fix for model name size issue (ACUMOS-684)
* Removed onboarding-app folder (ACUMOS-701)

Version 1.19.2, 19 Apr 2018
---------------------------

* Fix for model name size issue (ACUMOS-684)

Version 1.19.1, 18 Apr 2018
---------------------------

* Fix for model name size issue (ACUMOS-684)

Version 1.19.0, 16 Apr 2018
---------------------------

* build for IST (ACUMOS-336)

Version 1.18.3, 16 Apr 2018
---------------------------

* Jvm space issue fix (ACUMOS-336)

Version 1.18.2, 13 Apr 2018
---------------------------

* Jvm space issue fix (ACUMOS-336)

Version 1.18.1, 10 Apr 2018
---------------------------

* Fix for uploadArtifact (ACUMOS-650)

Version 1.18.0, 5 Apr 2018
--------------------------

* Concurrent Onboarding (ACUMOS-616)

Version 1.17.2, 2 Apr 2018
--------------------------

* Concurrent Onboarding (ACUMOS-616)

Version 1.17.1, 28 Mar 2018
---------------------------

* Limit JVM memory use (ACUMOS-336)

Version 1.17.0, 26 Mar 2018
---------------------------

* dcae release (ACUMOS-548)

Version 1.16.1, 26 Mar 2018
---------------------------

* dcae refactoring (ACUMOS-548)
* Updated runner.py with new version
* Move user guide to doc repo (ACUMOS-493)
* Dcae dockerfile change (ACUMOS-417)

Version 1.16.0, 22 Mar 2018
---------------------------

* Changes done for Docker File (ACUMOS-417)

Version 1.15.4, 22 Mar 2018
---------------------------

* Docker file (ACUMOS-417)

Version 1.15.3, 22 Mar 2018
---------------------------

* Dcae artifacts (ACUMOS-417)

Version 1.15.2, 22 Mar 2018
---------------------------

* Docker file (ACUMOS-417)

Version 1.15.1, 22 Mar 2018
---------------------------

* model sharing (ACUMOS-403)

Version 1.15.0, 19 Mar 2018
---------------------------

* IST Releas 1.15.0 (ACUMOS-417)

Version 1.14.1, 19 Mar 2018
---------------------------

* Changes done for logger (ACUMOS-417)

Version 1.14.0, 16 Mar 2018
---------------------------

* changes for ist release (CD-1816)

Version 1.13.5, 16 Mar 2018
---------------------------

* DCEA changes (CD-1816)

Version 1.13.4, 15 Mar 2018
---------------------------

* Document changes (ACUMOS-405)

Version 1.13.3, 15 Mar 2018
---------------------------

* DCEA changes (CD-1816)

Version 1.13.2, 15 Mar 2018
---------------------------

* Logger changes (CD-1816)

Version 1.13.1, 14 Mar 2018
---------------------------

* Logger added (CD-1816)
* DCAE Python model (ACUMOS-186)

Version 1.13.0, 9 Mar 2018
--------------------------

* DCAE Python model (ACUMOS-186)

Version 1.12.3, 9 Mar 2018
--------------------------

* DCAE Python model (ACUMOS-186)

Version 1.12.2, 9 Mar 2018
--------------------------

* DCAE Python Models (ACUMOS-233)

Version 1.12.1, 7 Mar 2018
--------------------------

* Web onboarding (ACUMOS-233)

Version 1.12.0, 7 Mar 2018
--------------------------

* Refactor into common and application sub-projects
* Logging standards (ACUMOS-211)

Version 1.10.8, 23 Feb 2018
---------------------------

* ACUMOS-11, 13,53,213,212,203,9

Version 1.10.7, 16 Feb 2018
---------------------------

* Use case (ACUMOS-114)

Version 1.8.3, 11 Dec 2017
---------------------------

* changed on-boarding version to 1.8.3-SNAPSHOT

Version 1.7.9, 13 Dec 2017
---------------------------

*  onboarding-app-1.7.9 compatible with CDS 1.10.1

Version 1.0.0, Dec 2017
-----------------------

* Initial release
