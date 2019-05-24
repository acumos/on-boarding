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
you only have to fill the name of the model. 

You can on-board your model with a license, you just have to browse your license file or drag and drop it.
The license file name must be : license.json. If the license file extension is not 'json' the license
on-boarding will not be possible and if the name is not 'license' Acumos will rename your license file as
license.json and you will see your license file named as license.json in the artifacts table. If you upload
a new version of your license after on-boarding, a number revision will be added to the name of your license
file like : "license-2.json". To help user create the license file expected by Acumos a license editor is
available on the web : `Acumos license editor <https://acumos-license-editor.stackblitz.io/#/>`_

The process of on-boarding, for ONNX and PFA, in Boreas is reduced to create a solution Id and upload the model.
There are no micro-service, nor tosca file, nor metadata file, nor protobuf file created.


.. |image0|
.. |image1| image:: ./media/HighLevelFlow.png
   :width: 6.26806in
   :height: 1.51389in
