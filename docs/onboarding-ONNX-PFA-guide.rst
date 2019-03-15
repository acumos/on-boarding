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

==============================
On-Boarding ONNX and PFA Model
==============================

**1: What is ONNX and PFA ?**
-----------------------------

ONNX : (Open Neural Network eXchange) is a library available in some Deep Learning Framework that
allows to import and export Deep Learning models from different AI framework. If you export a model
under the ONNX format you will be able to import it in and use it in many others Deep Learning
framework. Please have a look at https://github.com/onnx/tutorials to know more. 

PFA : (Portable format analytics) is a language developped by the Data mining Group to help datascientist
to deploy their model in production. Please have a look at http://dmg.org/pfa/docs/motivation/ to know
more.


**2: How to onboard ONNX or PFA model ?**
-----------------------------------------

Onboard an ONNX or PFA model is really simple you just have to use the on-boarding web page to upload and
then onboard the model. Acumos portal will automatically detect the format of the model (ONNX or PFA) and
you only have to fill the name of the model and optionally add a licence file (license.json).

The process of on-boarding, for ONNX and PFA, in Boreas is reduced to create a solution Id and upload the model.
There are no micro-service, nor tosca file, nor metadata file, nor protobuf file created.





.. |image0|
.. |image1| image:: ./media/HighLevelFlow.png
   :width: 6.26806in
   :height: 1.51389in
