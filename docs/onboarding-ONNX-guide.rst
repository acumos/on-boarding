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

=================================
On-Boarding ONNX Model user guide
=================================

**1: What is ONNX ?**
---------------------

ONNX : (Open Neural Network eXchange) is a library available in some Deep Learning Framework that
allows to import and export Deep Learning models from different AI framework. If you export a model
under the ONNX format you will be able to import it in and use it in many others Deep Learning
framework. Please have a look at https://github.com/onnx/tutorials to know more. 

**2: How to onboard ONNX model ?**
----------------------------------

To Onboard an ONNX model you must use the acumos4onnx python library. This librairy is available on 
`Pypi <https://pypi.org/>`_. Thanks to this librairy you can onboard your model by CLI or by WEB. This libairy
allow you also to test & run your ONNX model before on-boarding 

With Web on-boarding You can on-board your model with a license profile, you just have to browse your license profile
file or drag and drop it. The license profile file name must be : license.json. If the license file extension is not
'json' the license profile on-boarding will not be possible and if the name is not 'license' Acumos will rename your
license profile file as license.json and you will see your license profile file named as license.json in the artifacts
table. If you upload a new version of your license profile after on-boarding, a number revision will be added to the
name of your license file like : "license-2.json". To help user create the license profile file expected by Acumos
a license profile editor user guide is available here :
`License profile editor user guide <../../submodules/license-manager/docs/user-guide-license-profile-editor.html>`_
