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
