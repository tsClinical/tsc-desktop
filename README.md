---
title: README
tags: []
---

# tsClinical Metadata Desktop Tools
Version 1.1.1

### Table of Contents
[1 License](#1-license)  
[2 Functional Overview](#2-functional-overview)  
[3 System Requirements of Binary Distribution](#3-system-requirements-of-binary-distribution)  
[4 Dependencies](#4-dependencies)  
[5 Trademarks and Logos](#5-trademarks-and-logos)  
[6 Downloading and Running Binary Distribution](#6-downloading-and-running-binary-distribution)  
[7 How to Use](#7-how-to-use)  

### 1 License

Licensed under the Apache License, Version 2.0. See the `LICENSE` file in the top directory for details.

### 2 Functional Overview
tsClinical Metadata Desktop Tools is a tool that transforms clinical study metadata from Excel to standard formats or those of specific systems, and vice verca. The tool has been developed (1) to assist regulatory submissions of new drugs and (2) to interface with [tsClinical Metadata](https://www.fujitsu.com/jp/solutions/industry/life-sciences/products/drug-development/metadata/).

|Icon|Menu|Submenu|Explanation|
|:---|:---|:---|:---|
|![](https://github.com/tsClinical/tsc-desktop/raw/master/resources/material-icons/custom_integration_instructions_black_48dp.png)|Define-XML|(1) Convert from Excel to Define-XML <br> (2) Convert from Define-XML to Excel <br> (3) Convert from XML to HTML|(1) The submenu allows you to create Define-XML [\*1] from SDTM/ADaM Spec (Excel) in the tsClinical Metadata format. <br> (2) The submenu allows you to create SDTM/ADaM Spec (Excel) in the tsClinical Metadata format from Define-XML. <br> (3) The submenu allows you to create an HTML file from Define-XML and a style sheet.|
|![](https://github.com/tsClinical/tsc-desktop/raw/master/resources/material-icons/custom_ballot_black_48dp.png)|CRF|(1) Convert from Excel to ODM-XML <br> (2) Convert from ODM-XML to Excel <br> (3) Create CRF Spec from Datasets|(1) The submenu allows you to create ODM-XML [\*2] from CRF Spec (Excel) in the tsClinical Metadata format. <br> (2) The submenu allows you to create CRF Spec (Excel) in the tsClinical Metadata format from ODM-XML <br> (3) The submenu allows you to create CRF Spec (Excel) in the tsClinical Metadata format from dataset files in the text format and Architect CRF (Excel). [\*3]\[*4]|
|![](https://github.com/tsClinical/tsc-desktop/raw/master/resources/material-icons/custom_table_view_black_48dp.png)|eDT|(1) Create eDT Spec from Datasets|(1) The submenu allows you to create eDT Spec (Excel) in the tsClinical Metadata format from a dataset file in the text format.|
|![](https://github.com/tsClinical/tsc-desktop/raw/master/resources/material-icons/custom_fact_check_black_48dp.png)|Validation|(1) Validate XML against XML Schema|(1) The submenu allows you to validate a XML file (e.g. Define-XML, ODM-XML) against its XML schema.|

[\*1] Define-XML versions supported by this software are 2.0.0, 2.1.n and "Analysis Results Metadata Specification Version 1.0 for Define-XML Version 2".  
[\*2] ODM version supported by this software is 1.3.2.  
[\*3] Architect Loader Draft Spreadsheet by Medidata Rave EDC  
[\*4] Functionality regarding Architect CRF is not available on GitHub.

### 3 System Requirements of Binary Distribution
* Microsoft Windows 10 and 11
* Microsoft Excel 2013 and later (the file extension must be .xlsx)

### 4 Dependencies
The binary distribution contains the following resources. AdoptOpenJDK is avaiable under the GPLv2 + Classpath Exception license. All the other resources are available under the Apache License, Version 2.0.
* Eclipse Temurin 11.0.18
* Material Design Icons
* commons-beanutils-1.9.4.jar
* commons-codec-1.10.jar
* commons-collections-3.2.2.jar
* commons-collections4-4.4.jar
* commons-io-2.8.0.jar
* commons-lang3-3.11.jar
* commons-logging-1.2.jar
* commons-text-1.10.0.jar
* curvesapi-1.04.jar
* log4j-1.2.17.jar
* opencsv-5.3.jar
* poi-3.17.jar
* poi-ooxml-3.17.jar
* poi-ooxml-schemas-3.17.jar
* stax-api-1.0.1.jar
* xml-apis-1.3.04.jar
* xmlbeans-2.6.0.jar

### 5 Trademarks and Logos
Fujitsu Limited reserves all rights to trademakrs and logos included in the binary distribution.

### 6 Downloading and Running Binary Distribution
Download the binary distribution (.zip file) from the [Download](https://md-eval.tsclinical.global.fujitsu.com/cdisc/public/dl) page, unzip and run `tsc-desktop.bat` or `tsc-desktop.exe`. The EXE file is signed with a digital signature so that the Microsoft SmartScreen on Windows 10 or later does not show warning when  executing the tool.

### 7 How to Use
See `USERS-GUIDE` in the `docs` directory of this software.

---
Copyright (c) 2020-2023 Fujitsu Limited. All rights reserved.  
All brand names and product names in this document are registered trademarks or trademarks of their respective holders.
