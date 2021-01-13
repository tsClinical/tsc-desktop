---
title: README_ja
tags: []
---

# tsClinical Metadata Desktop Tools
Version 1.0.3

### 1. ライセンス

本ソフトウェアの使用条件はApache License, Version 2.0に従います。詳細は`LICENSE`ファイルを参照してください。

### 2. 機能概要
tsClinical Metadata Desktop Toolsは、臨床試験のメタデータをExcelから標準形式または個別のシステムの形式に変換する、またはその逆を行うツールです。本ツールは (1) 新薬の承認申請を支援する、および (2) [tsClinical Metadata](https://www.fujitsu.com/jp/solutions/industry/life-sciences/products/drug-development/metadata/)とのインタフェースとなる事を目的として開発されています.

|分類|メニュー|説明|
|:---|:---|:---|
|Define-XML|(1) Convert from Excel to Define-XML <br> (2) Convert from Define-XML to Excel|(1) tsClinical Metadata様式のSDTM/ADaM Spec (Excel)から、Define-XML[^1]を作成します。 <br> (2) Define-XMLから、tsClinical Metadata様式のSDTM/ADaM Spec (Excel)を作成します。|
|CRF|(1) Convert from Excel to ODM-XML <br> (2) Convert from ODM-XML to Excel <br> (3) Create CRF Spec from Datasets|(1) tsClinical Metadata様式のCRF Spec (Excel)から、ODM-XML[^2]を作成します。 <br> (2) ODM-XMLから、tsClinical Metadata様式のCRF Spec (Excel)を作成します。 <br> (3) テキスト形式のデータセットファイルおよびArchitect CRF[^3] (Excel) から、tsClinical Metadata様式のCRF Spec (Excel)を作成します。|
|eDT|(1) Create eDT Spec from Datasets|(1) テキスト形式のデータセットファイルから、tsClinical Metadata様式のeDT Spec (Excel)を作成します。|
|Validation|(1) Validate XML against XML Schema|(1) 指定したXML Schemaに対してXMLファイル（Define-XML, ODM-XML等）を検証します。|

[^1]: 本ソフトウェアがサポートしているDefine-XMLのバージョンは、"CDISC Define-XML Specification Version 2.0"および"Analysis Results Metadata Specification Version 1.0 for Define-XML Version 2"です。  
[^2]: 本ソフトウェアがサポートしているODMのバージョンは、"Specification for the Operational Data Model (ODM) Version 1.3.2"です。  
[^3]: Medidata Rave EDCのArchitect Loader Draft Spreadsheet  

\*1: 本ソフトウェアがサポートしているDefine-XMLのバージョンは、"CDISC Define-XML Specification Version 2.0"および"Analysis Results Metadata Specification Version 1.0 for Define-XML Version 2"です。  
\*2: 本ソフトウェアがサポートしているODMのバージョンは、"Specification for the Operational Data Model (ODM) Version 1.3.2"です。  
\*3: Medidata Rave EDCのArchitect Loader Draft Spreadsheet  

### 3. バイナリ配布版の動作環境
* Microsoft Windows 8.1および10
* OpenJDK 11（ソフトウェアに同梱）
* Microsoft Excel 2013以降（拡張子が.xlsxのファイルのみ）

### 4. バイナリ配布版のダウンロードおよび実行
バイナリ配布版(.zipファイル)を[Download](https://md-eval.tsclinical.global.fujitsu.com/cdisc/login)ページからダウンロードし、解凍後`tsc-desktop.bat`を実行します。

バイナリ配布版をダウンロードし`tsc-desktop.bat`を実行します。

### 5. 使用方法
本ソフトウェアの`docs`ディレクトリの`USERS-GUIDE`を参照してください。

---
Copyright (c) 2020-2021 Fujitsu Limited. All rights reserved.  
All brand names and product names in this document are registered trademarks or trademarks of their respective holders.
