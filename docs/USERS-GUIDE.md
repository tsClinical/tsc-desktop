---
title: USERS-GUIDE
tags: []
---

# User's Guide - tsClinical Metadata Desktop Tools
Version 1.1.0

### 1. Menus
#### 1.1. Convert from Excel to Define-XML
![](https://github.com/tsClinical/tsc-desktop/raw/master/docs/images/image001.jpg)

|Field|Description|
|:---|:---|
|Define-XML Version|The version of Define-XML to create. Select from "2.0.0" or "2.1.n".|
|Dataset Type|Select from "SDTM", "ADaM", or "SEND".|
|Include Result Metadata|Available only when the Dataset Type is "ADaM". Checking this option will include Analysis Results Metadata within Define-XML.|
|XML Encoding|Character encoding of Define-XML to create. Define-XML is created using the selected encoding with the encoding attribute in the XML header. Select from "UTF-8", "ISO-8859-1" or "Shift_JIS".|
|Stylesheet Location (.xsl)|Relative path to the Define-XML stylesheet. The value is assigned to the href attribute of the Define-XML stylesheet reference.|
|Data Source Location|Select an Excel file (.xlsx only) to create Define-XML from. Templates are available in the `excel`folder.|
|Output Location|Select a folder in which Define-XML is created. The default file name "define.xml" is automatically set, but it can be changed.|

* See `Mapping_Design_Define.xlsx` for the mapping rules between Excel and Define-XML.
* Cases and spaces are ignored when reading Excel column names. (i.e. "DataType", "Data Type" and "Data type" are all accepted as a valid column name.)
* Some columns in the Excel file permit multiple values separated by ";" within a cell. A user can change the delimiter by opening `main.properties` in the `properties` folder using a text editor and updating the "valueDelimiter" property. Multiple CRF page numbers in the CRF Page Reference column must be separated by a single space, instedad of ";", in accordance with the Define-XML specification (e.g. "9 14 22"). Refer to `Mapping_Design_Define.xlsx` to find which columns permit multiple values.
* A user should configure "Format Cells" of Excel cells using "General" or "Text". Using other formants (e.g. "Date", "Custom" or functions such as SUM) may result in values different from those shown in Excel exported into the Define-XML.
* The tool supports two ways of describing SDTM Non-Standard Variables (NSVs) in Excel - (1) describe SUPP-- datasets, and (2) describe NSVs in the same way as standard variables in a parent dataset. In case of (2), the tool automatically creates dataset and variable metadata of SUPP-- datasets by referencing the following columns. See the SUPPQUAL sheet of `Mapping_Design_Define.xlsx` for more details.
  * "Has SUPP" column in the DATASET sheet (dataset and variable metadata of SUPP-- is created when Has SUPP="Yes")
  * "Is SUPP" column in the VARIABLE sheet (QVAL value-level metadata is created when Is SUPP="Yes")
* The tool supports two ways of describing methods and comments in Excel - (1) describe methods/comments in each sheet such as DATASET, VARIABLE and VALUE, and (2) describe methods/comments in the METHOD/COMMENT sheet respectively and link from each sheet using MethodOID/CommentOID.
* The "No Data" column, supported in Define-XML 2.1.n, can also be used for Define-XML 2.0.0. For 2.0.0, datasets/variables/VLMs are not exported to Define-XML when "No Data" is Yes.
* The "Repeat N" column can be used to replicate repeating variables (e.g. TSVALn, COVALn and RACEn). For example, when the "Repeat N" value of the COVAL variable is 5, then the tool will create COVAL, COVAL1, ..., COVAL5 in Define-XML.

#### 1.2. Convert from Define-XML to Excel
![](https://github.com/tsClinical/tsc-desktop/raw/1.1.0/docs/images/image002.jpg)

|Field|Description|
|:---|:---|
|Define-XML Version|The version of Define-XML to import. Select from "2.0.0" or "2.1.n".|
|Dataset Type|Select from "SDTM", "ADaM", or "SEND".|
|Load Methods/Comments to Separate Sheets|Check to import methods and comments into the METHOD and COMMENT sheets respectively. Uncheck to import them into each sheet (DATASET, VARIABLE, VALUE, etc.)|
|Merge NSV to Parent Dataset|Check to import Non-Standard Variables (NSVs) into parent datasets. Uncheck to import NSVs into SUPP-- datasets.|
|Data Source Location|Select an Define-XML file (.xml only) to import.|
|Output Location|Select a folder in which an Excel file is created. The default file name "define.xlsx" is automatically set, but it can be changed.|

* See `Mapping_Design_Define.xlsx` for the mapping rules between Excel and Define-XML.
* The import process stops if a fatal error is found in Define-XML. "Fatal" errors are defined in XML schema files in the `src/main/resources/schema/hard` folder. The fatal errors include:
  * The Define-XML is not well-formed.
  * A required OID is missing, or an OID refers to an invalid (non-existing) target.
* Make sure the Excel file designated in Output Location is not accessed by another program (e.g. the file is already open), otherwise the import will fail.
* When "Load Methods/Comments to Separate Sheets" is checked, the tool imports methods/comments into the METHOD/COMMENT sheet respectively and MethodOID/CommentOID into each sheet (DATASET, VARIABLE, VALUE, etc.).
* When "Merge NSV to Parent Dataset" is checked, the tool imports SDTM Non-Standard Variables (NSVs) into Excel as variables under the parent dataset (not under the "SUPP--" dataset) as below. See the SUPPQUAL sheet of `Mapping_Design_Define.xlsx` for more details.
  * Assign "Yes" to the "Has SUPP" column in the DATASET sheet.
  * Assign "Yes" to the "Is SUPP" column in the VARIABLE sheet.

#### 1.3. Convert from XML to HTML
![](https://github.com/tsClinical/tsc-desktop/raw/1.1.0/docs/images/image008.jpg)

|Field|Description|
|:---|:---|
|XML File Location (.xml)|Select an XML file to be converted to HTML.|
|Style Sheet Location (.xsl)|Select a style sheet to be applied to the XML file|
|Output Location|Select a folder in which an HTML file is created. The default file name "define.html" is automatically set, but it can be changed.|

* This tool simply applies the given style sheet to the given XML file to produce an HTML file.

#### 1.4. Convert from Excel to ODM-XML
![](https://github.com/tsClinical/tsc-desktop/raw/master/docs/images/image003.jpg)

|Field|Description|
|:---|:---|
|ODM Version|The version of ODM-XML to create. Only "1.3.2" can be selected.|
|XML Encoding|Character encoding of ODM-XML to create. ODM-XML is created using the selected encoding with the encoding attribute in the XML header. Only "UTF-8" can be selected.|
|Stylesheet Location (.xsl)|Relative path to the ODM-XML stylesheet. The value is assigned to the href attribute of the ODM-XML stylesheet reference. * This field can be left blank when a stylesheet is not available or applicable.|
|Data Source Location|Select an Excel file (.xlsx only) to create ODM-XML from. Templates are available in the `excel`folder.|
|Output Location|Select a folder in which ODM-XML is created. The default file name "odm.xml" is automatically set, but it can be changed.|

* See `Mapping_Design_ODM.xlsx` for the mapping rules between Excel and ODM-XML.
* Some columns in the Excel file permit multiple values separated by ";" within a cell. A user can change the delimiter by opening `main.properties` in the `properties` folder using a text editor and updating the "valueDelimiter" property. Refer to `Mapping_Design_Define.xlsx` to find which columns permit multiple values.
* A user should configure "Format Cells" of Excel cells using "General" or "Text". Using other formants (e.g. "Date", "Custom" or functions such as SUM) may result in values different from those shown in Excel exported into the ODM-XML.
* The tool is designed to export CRF metadata described in Excel into ODM-XML. The tool does NOT export subject data into ODM-XML.
* The following elements/attributes are not created:

|Elements not created|Attributes not created|
|:---|:---|
|/ODM|Granularity, Archival, PriorFileOID|
|/ODM/Description|-|
|/ODM/Study/BasicDefinitions/MeasurementUnit/Alias|-|
|/ODM/Study/MetaDataVersion/Include|-|
|/ODM/Study/MetaDataVersion/Protocol/Alias|-|
|/ODM/Study/MetaDataVersion/FormDef/ArchiveLayout|PresentationOID|
|/ODM/Study/MetaDataVersion/ItemGroupDef|Domain, Origin, Role, Purpose, Comment|
|/ODM/Study/MetaDataVersion/ItemGroupDef/ItemRef|Role, RoleCodeListOID|
|/ODM/Study/MetaDataVersion/ItemDef|SDSVarName, Origin, Comment|
|/ODM/Study/MetaDataVersion/ItemDef/ExternalQuestion|-|
|/ODM/Study/MetaDataVersion/CodeList/ExternalCodeList|-|
|/ODM/Study/MetaDataVersion/Presentation|-|
|/ODM/AdminData|-|
|/ODM/ReferenceData|-|
|/ODM/ClinicalData|-|
|/ODM/Association|-|
|/ODM/ds:Signature|-|

* The OID attribute of each element is designated based on the corresponding ID column in Excel, depending on the "oidMode" property in `main.properties`. Refer to [main.properties](#main.properties) for more details.
* In the UNIT sheet, fill out one row per Symbol when a unit ID has multiple Symbols for multiple languages. Note that "ID" and "Name" are required for each Symbol.
* Enter Name (not ID) in "Event Name" and "Form Name" in the EVENTxFORM sheet, and "Form Name" and "Unit Name" in the "FIELD" sheet.
* When ItemGroup is not defined for each Form (each form starts with a row where Level is 1) in the FIELD sheet, the tool automatically generates ItemGroup in ODM-XML because it is required. In such case, the tool sets FormOID in "ID", "DEFAULT_n" (n is a sequential number) in "Name", the same values as those in the FORM sheet in "Mandatory" and "Repeating".

#### 1.5. Convert from ODM-XML to Excel
![](https://github.com/tsClinical/tsc-desktop/raw/master/docs/images/image004.jpg)

|Field|Description|
|:---|:---|
|ODM Version|The version of ODM-XML to import. Only "1.3.2" can be selected.|
|Data Source Location|Select an ODM-XML file (.xml only) to import.|
|Output Location|Select a folder in which an Excel file is created. The default file name "odm.xlsx" is automatically set, but it can be changed.|

* See `Mapping_Design_ODM.xlsx` for the mapping rules between Excel and ODM-XML.
* The import process stops if a fatal error is found in ODM-XML. "Fatal" errors are defined in XML schema files in the src/main/resources/schema/hard folder. The fatal errors include:
The ODM-XML is not well-formed.
A required OID is missing, or an OID refers to an invalid (non-existing) target.
* Make sure the Excel file designated in Output Location is not accessed by another program (e.g. the file is already open), otherwise the import will fail.

#### 1.6. Create CRF Spec from Datasets
![](https://github.com/tsClinical/tsc-desktop/raw/master/docs/images/image005.jpg)

|Field|Description|
|:---|:---|
|Architect CRF Location (.xlsx)|Select an Architect Loader Draft Spreadsheet file (.xlsx only) of Medidata Rave EDC.[\*1]|
|Datasets Text Files|Select dataset files (text files in table format) exported out of EDC.|
|# of Header Lines|Number of header rows of Datasets Text Files (1 or above). Required when Datasets Text Files are entered.|
|Character Encoding	Datasets|Character encoding of Datasets Text Files. Select from "UTF-8", "ISO-8859-1" or "Shift_JIS". Required when Datasets Text Files are entered.|
|Delimiter|A character used as a delimiter of Datasets Text Files (a single character or a tab (\t)). Required when Datasets Text Files are entered.|
|Text Qualifier|A special character that encloses each column value of Datasets Text Files. Select from " or ' if applicable, or "(None)" if not applicable. Required when Datasets Text Files are entered.|
|Output Location|Select a folder in which an Excel file is created. The default file name "crf_spec.xlsx" is automatically set, but it can be changed.|

* A user can create CRF Spec from either or both Architect CRF and/or Datasets Text Files.
* When both Architect CRF and Datasets Text Files are entered, the tool creates a list of Fields based on Datasets Text Files (i.e. fields defined in Architect CRF but not found in Datasets are not created in the CRF Spec). Properties of each field is set from Architect CRF if available.
* See `Mapping_Design_Architect.xlsx`[\*2] for the mapping table between CRF Spec and Architect CRF.
* The maximum records to be considered when Codelists are derived from a dataset are 10,000 records.
* Only the first record is considered when DataTypes are derived from a dataset

[\*1] This field is disabled for the OSS version of the tool.
[\*2] This document is not available on GitHub.

#### 1.7. Create eDT Spec from Datasets
![](https://github.com/tsClinical/tsc-desktop/raw/master/docs/images/image006.jpg)

|Field|Description|
|:---|:---|
|Type|Select a type of eDT from a list.|
|# of Header Lines|Number of header rows of the text file (1 or above).|
|Character Encoding|Character encoding of the text file. Select from "UTF-8", "ISO-8859-1" or "Shift_JIS".|
|Delimited/Fixed Width|Select a type of the text file. Only "Delimited" can be selected.|
|Delimiter|A character used as a delimiter of the text file (a single character or a tab (\t)).|
|Text Qualifier|A special character that encloses each column value of the text file. Select from " or ' if applicable, or "(None)" if not applicable.|
|Data Source Location|Select a dataset file (a text file in table format) exported out of eDT.|
|Output Location|Select a folder in which an Excel file is created. The default file name "edt_spec.xlsx" is automatically set, but it can be changed.|

* The maximum records to be considered when Codelists are derived from a dataset are 10,000 records.
* Only the first record is considered when DataTypes are derived from a dataset.

#### 1.8. Validate XML against XML Schema
![](https://github.com/tsClinical/tsc-desktop/raw/master/docs/images/image007.jpg)

|Field|Description|
|:---|:---|
|XML File Location (.xml)|Select a XML file to validate.|
|Schema Location (.xsd)|Select a XML Schema file which a XML file is validated against.|

* The tool uses Xerces 2.9.0 as a XML Schema validation engine, which is listed in the "XML Schema Validation for Define.xml" document published by CDISC.
* Note that no errors with XML Schema validation does not mean complete conformance to Define-XML or ODM-XML specifications.

### 2. main.properties

A user can configure the tool by editing the `main.properties` file (created automatically at initial startup) in the `properties` folder using a text editor. The `main.properties` file is updated with settings on screens when the tool is closed. A user usually do not need to edit the file because settings on screens precede configurations in this file.

|Property|Description|
|:---|:---|
|defineStudyTableName|Name of the Excel sheet that describes Study information for Define-XML.|
|defineStandardTableName|Name of the Excel sheet that describes Standard information for Define-XML.|
|defineDocumentTableName|Name of the Excel sheet that describes Document information for Define-XML.|
|defineDatasetTableName|Name of the Excel sheet that describes Domain/Dataset information for Define-XML.|
|defineVariableTableName|Name of the Excel sheet that describes Variable information for Define-XML.|
|defineValueTableName|Name of the Excel sheet that describes Value information for Define-XML.|
|defineResult1TableName|Name of the Excel sheet that describes information about Analysis Results Metadata (Result Displays and Analysis Results) for Define-XML.|
|defineResult2TableName|Name of the Excel sheet that describes information about Analysis Results Metadata (Analysis Datasets and Where Clauses) for Define-XML.|
|defineDictionaryTableName|Name of the Excel sheet that describes Dictionary information for Define-XML.|
|defineCodelistTableName|Name of the Excel sheet that describes Codelist information for Define-XML.|
|defineMethodTableName|Name of the Excel sheet that describes Method information for Define-XML.|
|defineCommentTableName|Name of the Excel sheet that describes Comment information for Define-XML.|
|valueDelimiter|A character used as a value delimiter to separate multiple values in a cell of the Excel file for Define-XML. The default value is ";".|
|e2dDefineVersion|Equal to "Define-XML Version" on the Convert from Excel to Define-XML screen.|
|e2dDatasetType|Equal to "Dataset Type" on the Convert from Excel to Define-XML screen.|
|e2dIncludeResultMetadata|Equal to "Include Result Metadata" on the Convert from Excel to Define-XML screen. Permitted values are "true" (include) or "false" (do not include).|
|e2dXmlEncoding|Equal to "XML Encoding" on the Convert from Excel to Define-XML screen.|
|e2dStylesheetLocation|Equal to "Stylesheet Location (.xsl)" on the Convert from Excel to Define-XML screen.|
|e2dDataSourceLocation|Equal to "Data Source Location" on the Convert from Excel to Define-XML screen.|
|e2dOutputLocation|Equal to "Output Location" on the Convert from Excel to Define-XML screen.|
|d2eDatasetType|Equal to "Dataset Type" on the Convert from Define-XML to Excel screen.|
|d2eSeparateSheet|Equal to "Load Methods/Comments to Separate Sheets" on the Convert from Define-XML to Excel screen.|
|d2eMergeNSVtoParent|Equal to "Merge NSV to Parent Dataset" on the Convert from Define-XML to Excel screen.|
|d2eDataSourceLocation|Equal to "Data Source Location" on the Convert from Define-XML to Excel screen.|
|d2eOutputLocation|Equal to "Output Location" on the Convert from Define-XML to Excel screen.|
|x2hXmlLocation|Equal to "XML File Location (.xml)" on the Convert from XML to HTML screen.|
|x2hXslLocation|Equal to "Style Sheet Location (.xsl)" on the Convert from XML to HTML screen.|
|x2hOutputLocation|Equal to "Output Location" on the Convert from XML to HTML screen.|
|odmStudyTableName|Name of the Excel sheet that describes Study information for ODM-XML.|
|odmUnitTableName|Name of the Excel sheet that describes Unit information for ODM-XML.|
|odmEventTableName|Name of the Excel sheet that describes Event information for ODM-XML.|
|odmEventFormTableName|Name of the Excel sheet that describes Event x Form information (i.e. relationship between Events and Forms) for ODM-XML.|
|odmFormTableName|Name of the Excel sheet that describes Form information for ODM-XML.|
|odmFieldTableName|Name of the Excel sheet that describes Field (ItemGroup and Item) information for ODM-XML. The value in the Level column indicates Itemgroup (when the value is "1") or Item (when the value is "1").|
|odmCodelistTableName|Name of the Excel sheet that describes Codelist information for ODM-XML.|
|odmMethodTableName|Name of the Excel sheet that describes Method information for ODM-XML.|
|odmConditionTableName|Name of the Excel sheet that describes Condition information for ODM-XML.|
|oidMode|The property is used only for ODM-XML generation and accepts "EXACT" or "NATIVE". "EXACT" indicates ID columns in Excel is copied exactly to OID attributes in ODM-XML. "NATIVE" indicates OID attributes in ODM-XML is generated in the tool's native format based on ID columns in Excel. The default value is "EXACT".|
|e2oOdmVersion|Equal to "ODM Version" on the Convert from Excel to ODM-XML screen.|
|e2oXmlEncoding|Equal to "XML Encoding" on the Convert from Excel to ODM-XML screen.|
|e2oStylesheetLocation|Equal to "Stylesheet Location (.xsl)" on the Convert from Excel to ODM-XML screen.|
|e2oDataSourceLocation|Equal to "Data Source Location" on the Convert from Excel to ODM-XML screen.|
|e2oOutputLocation|Equal to "Output Location" on the Convert from Excel to ODM-XML screen.|
|o2eOdmVersion|Equal to "ODM Version" on the Convert from ODM-XML to Excel screen.|
|o2eOdmLocation|Equal to "Data Source Location" on the Convert from ODM-XML to Excel screen.|
|o2eOutputLocation|Equal to "Output Location" on the Convert from ODM-XML to Excel screen.|
|crfArchitectLocation|Equal to "Architect CRF Location (.xlsx)" on the Create CRF Spec from Datasets screen.|
|crfSourceFiles|Equal to "Datasets Text Files" on the Create CRF Spec from Datasets screen.|
|crfHeaderCnt|Equal to "# of Header Lines" on the Create CRF Spec from Datasets screen.|
|crfEncoding|Equal to "Character Encoding" on the Create CRF Spec from Datasets screen.|
|crfDelimiter|Equal to "Delimiter" on the Create CRF Spec from Datasets screen.|
|crfTextQualifier|Equal to "Text Qualifier" on the Create CRF Spec from Datasets screen.|
|crfOutputLocation|Equal to "Output Location" on the Create CRF Spec from Datasets screen.|
|edtGeneralTableName|Name of the Excel sheet that describes General information for eDT Spec.|
|edtColumnTableName|Name of the Excel sheet that describes Column information for eDT Spec.|
|edtCodelistTableName|Name of the Excel sheet that describes Codelist information for eDT Spec.|
|edtType|Equal to "Type" on the Create eDT Spec from Datasets screen.|
|edtHeaderCnt|Equal to "# of Header Lines" on the Create eDT Spec from Datasets screen.|
|edtEncoding|Equal to "Character Encoding" on the Create eDT Spec from Datasets screen.|
|edtDelimitedOrFixed|Equal to "Delimited/Fixed Width" on the Create eDT Spec from Datasets screen.|
|edtDelimiter|Equal to "Delimiter" on the Create eDT Spec from Datasets screen.|
|edtTextQualifier|Equal to "Text Qualifier" on the Create eDT Spec from Datasets screen.|
|edtDataSourceLocation|Equal to "Data Source Location" on the Create eDT Spec from Datasets screen.|
|edtOutputLocation|Equal to "Output Location" on the Create eDT Spec from Datasets screem.|
|validateXmlLocation|Equal to "XML File Location (.xml)" on the Validate XML against XML Schema screen.|
|validateSchemaLocation|Equal to "Schema Location (.xsd)" on the Validate XML against XML Schema screen.|

---
Copyright (c) 2020-2022 Fujitsu Limited. All rights reserved.  
All brand names and product names in this document are registered trademarks or trademarks of their respective holders.
