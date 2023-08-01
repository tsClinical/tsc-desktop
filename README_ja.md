---
title: README_ja
tags: []
---

# tsClinical Metadata Desktop Tools
Version 1.1.3

### 1. ライセンス

本ソフトウェアの使用条件はApache License, Version 2.0に従います。詳細は`LICENSE`ファイルを参照してください。

### 2. 機能概要
tsClinical Metadata Desktop Toolsは、臨床試験のメタデータをExcelから標準形式または個別のシステムの形式に変換する、またはその逆を行うツールです。本ツールは (1) 新薬の承認申請を支援する、および (2) [tsClinical Metadata](https://www.fujitsu.com/jp/solutions/industry/life-sciences/products/drug-development/metadata/)とのインタフェースとなる事を目的として開発されています.

|アイコン|分類|メニュー|説明|
|:---|:---|:---|:---|
|![](https://github.com/tsClinical/tsc-desktop/raw/master/resources/material-icons/custom_integration_instructions_black_48dp.png)|Define-XML|(1) Convert from Excel to Define-XML <br> (2) Convert from Define-XML to Excel <br> (3) Convert from XML to HTML|(1) tsClinical Metadata様式のSDTM/ADaM Spec (Excel)から、Define-XML \[\*1]を作成します。 <br> (2) Define-XMLから、tsClinical Metadata様式のSDTM/ADaM Spec (Excel)を作成します。 <br> (3) XMLファイルとスタイルシートからHTMLファイルを作成します。|
|![](https://github.com/tsClinical/tsc-desktop/raw/master/resources/material-icons/custom_ballot_black_48dp.png)|CRF|(1) Convert from Excel to ODM-XML <br> (2) Convert from ODM-XML to Excel <br> (3) Create CRF Spec from Datasets|(1) tsClinical Metadata様式のCRF Spec (Excel)から、ODM-XML \[\*2]を作成します。 <br> (2) ODM-XMLから、tsClinical Metadata様式のCRF Spec (Excel)を作成します。 <br> (3) テキスト形式のデータセットファイルおよびArchitect CRF(Excel) \[\*3]から、tsClinical Metadata様式のCRF Spec (Excel)を作成します。\[\*4]|
|![](https://github.com/tsClinical/tsc-desktop/raw/master/resources/material-icons/custom_table_view_black_48dp.png)|eDT|(1) Create eDT Spec from Datasets|(1) テキスト形式のデータセットファイルから、tsClinical Metadata様式のeDT Spec (Excel)を作成します。|
|![](https://github.com/tsClinical/tsc-desktop/raw/master/resources/material-icons/custom_fact_check_black_48dp.png)|Validation|(1) Validate XML against XML Schema|(1) 指定したXML Schemaに対してXMLファイル（Define-XML, ODM-XML等）を検証します。|

[\*1]: 本ソフトウェアがサポートしているDefine-XMLのバージョンは、2.0.0, 2.1.nおよび"Analysis Results Metadata Specification Version 1.0 for Define-XML Version 2"です。  
[\*2] 本ソフトウェアがサポートしているODMのバージョンは、1.3.2です。
[\*3] Medidata Rave EDCのArchitect Loader Draft Spreadsheet  
[\*4] 本機能はGitHubに公開していません。

### 3. バイナリ配布版の動作環境
* Microsoft Windows 10および11
* Microsoft Excel 2013以降（拡張子が.xlsxのファイルのみ）

### 4. 依存関係
バイナリ配布版には以下のリソースが含まれています。Eclipse TemurinはGPLv2 + Classpath Exceptionライセンスに従います。その他のリソースはApache License, Version 2.0に従います。
* Eclipse Temurin 11.0.20
* Material Design Icons
* commons-beanutils-1.9.4.jar
* commons-collections4-4.4.jar
* commons-io-2.13.0.jar
* commons-lang3-3.12.0.jar
* commons-text-1.10.0.jar
* log4j-api-2.20.0.jar
* log4j-core-2.20.0.jar
* opencsv-5.8.jar
* poi-5.2.3.jar
* poi-ooxml-5.2.3.jar

### 5. 商標およびロゴ
バイナリ配布版に含まれる商標およびロゴの一切の権利は富士通株式会社に帰属します。

### 6. バイナリ配布版のダウンロードおよび実行
バイナリ配布版(.zipファイル)を[Download](https://md-eval.tsclinical.global.fujitsu.com/cdisc/public/dl)ページからダウンロードし、解凍後`tsc-desktop.bat`を実行します。または`tsc-desktop.exe`を実行します。EXEファイルは、ツールの実行時にWindows 10以降でMicrosoft SmartScreenの警告が表示されないよう、デジタル署名されています。

### 7. 使用方法
本ソフトウェアの`docs`ディレクトリの`USERS-GUIDE`を参照してください。

---
Copyright (c) 2020-2023 Fujitsu Limited. All rights reserved.  
All brand names and product names in this document are registered trademarks or trademarks of their respective holders.
