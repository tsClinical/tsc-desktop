# tsClinical Metadata Desktop Tools
Version 1.0.3

### 1. License

Licensed under the Apache License, Version 2.0. See the `LICENSE` file in the top directory for details.

### 2. Functional Overview
tsClinical Metadata Desktop Tools is a tool that transforms clinical study metadata from Excel to standard formats or those of specific systems, and vice verca. The tool has been developed (1) to assist regulatory submissions of new drugs and (2) to interface with [tsClinical Metadata](https://www.fujitsu.com/jp/solutions/industry/life-sciences/products/drug-development/metadata/).

|Menu|Submenu|Explanation|
|:---|:---|:---|
|Define-XML|(1) Convert from Excel to Define-XML <br> (2) Convert from Define-XML to Excel|(1) The submenu allows you to create Define-XML[^1] from SDTM/ADaM Spec (Excel) in the tsClinical Metadata format. <br> (2) The submenu allows you to create SDTM/ADaM Spec (Excel) in the tsClinical Metadata format from Define-XML.|
|CRF|(1) Convert from Excel to ODM-XML <br> (2) Convert from ODM-XML to Excel <br> (3) Create CRF Spec from Datasets|(1) The submenu allows you to create ODM-XML[^2] from CRF Spec (Excel) in the tsClinical Metadata format. <br> (2) The submenu allows you to create CRF Spec (Excel) in the tsClinical Metadata format from ODM-XML <br> (3) The submenu allows you to create CRF Spec (Excel) in the tsClinical Metadata format from dataset files in the text format and Architect CRF (Excel).[^3]|
|eDT|(1) Create eDT Spec from Datasets|(1) The submenu allows you to create eDT Spec (Excel) in the tsClinical Metadata format from a dataset file in the text format.|
|Validation|(1) Validate XML against XML Schema|(1) The submenu allows you to validate a XML file (e.g. Define-XML, ODM-XML) against its XML schema.|

[^1]: Define-XML versions supported by this software is "CDISC Define-XML Specification Version 2.0" and "Analysis Results Metadata Specification Version 1.0 for Define-XML Version 2".  
[^2]: ODM version supported by this software is "Specification for the Operational Data Model (ODM) Version 1.3.2".  
[^3]: Architect Loader Draft Spreadsheet by Medidata Rave EDC  

### 3. System Requirements of Binary Distribution
* Microsoft Windows 8.1 and 10
* AdoptOpenJDK 11.0.9 (contained in the binary distribution)
* Microsoft Excel 2013 and later (the file extension must be .xlsx)

### 4. Downloading and Running Binary Distribution
Download the binary distribution (.zip file) from the [Download](https://md-eval.tsclinical.global.fujitsu.com/cdisc/login) page, unzip and run `tsc-desktop.bat`.

### 5. How to Use
See `USERS-GUIDE` in the `docs` directory of this software.

---
Copyright (c) 2020-2021 Fujitsu Limited. All rights reserved.  
All brand names and product names in this document are registered trademarks or trademarks of their respective holders.
