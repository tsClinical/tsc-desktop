---
title: README
tags: []
---

# tsClinical Metadata Desktop Tools
Version 1.0.3

### 1. License

Licensed under the Apache License, Version 2.0. See the `LICENSE` file in the top directory for details.

### 2. Functional Overview
|アイコン|分類|メニュー|説明|
|:---:|:---|:---|:---|
|![](./resources/icons8/icons8-code-file-50.png)|Define-XML|(1) Convert from Excel to Define-XML <br> (2) Convert from Define-XML to Excel|(1) tsClinical Metadata様式のSDTM/ADaM Spec (Excel)から、Define-XML$$^{*1}$$を作成します。 <br> (2) Define-XMLから、tsClinical Metadata様式のSDTM/ADaM Spec (Excel)を作成します。|
|![](./resources/icons8/icons8-treatment2-50.png)|CRF|(1) Convert from Excel to ODM-XML <br> (2) Convert from ODM-XML to Excel <br> (3) Create CRF Spec from Datasets|(1) tsClinical Metadata様式のCRF Spec (Excel)から、ODM-XML$$^{*2}$$を作成します。 <br> (2) ODM-XMLから、tsClinical Metadata様式のCRF Spec (Excel)を作成します。 <br> (3) テキスト形式のデータセットファイルおよびArchitect CRF$$^{*3}$$(Excel) から、tsClinical Metadata様式のCRF Spec (Excel)を作成します。|
|![](./resources/icons8/icons8-data-sheet-50.png)|eDT|(1) Create eDT Spec from Datasets|(1) テキスト形式のデータセットファイルから、tsClinical Metadata様式のeDT Spec (Excel)を作成します。|
|![](./resources/icons8/icons8-check-document-50.png)|Validation|(1) Validate XML against XML Schema|(1) 指定したXML Schemaに対してXMLファイル（Define-XML, ODM-XML等）を検証します。|

\*1: 本ソフトウェアがサポートしているDefine-XMLのバージョンは、"CDISC Define-XML Specification Version 2.0"および"Analysis Results Metadata Specification Version 1.0 for Define-XML Version 2"です。  
\*2: 本ソフトウェアがサポートしているODMのバージョンは、"Specification for the Operational Data Model (ODM) Version 1.3.2"です。  
\*3: Medidata Rave EDCのArchitect Loader Draft Spreadsheet  

### 3. 動作環境
* Microsoft Windows 8.1および10
* OpenJDK 11（ソフトウェアに同梱）
* Microsoft Excel 2013以降（拡張子が.xlsxのファイルのみ）

### 4. 起動方法
バイナリ版をダウンロードし`tsc-desktop.bat`を実行します。

### 5. 機能上の注意事項
本ソフトウェアの機能上の注意事項を以下に記します。

#### 5.1. 全般
* 基本的にExcelに記載された情報はDefine-XMLやODM-XMLにそのまま反映されます。このため、Excelに正しい情報が記入されていることを十分に確認してください。
* 本ソフトウェアではXML Schemaに対する検証を行うツールとして、CDISCが発行している文書"XML Schema Validation for Define.xml"に記載されているXerces 2.9.0を採用しています。
* XML Schemaに対する検証でエラーが抽出されない場合でも、Define-XMLやODM-XMLの仕様に完全に準拠していることを保証するものではありません。

#### 5.2. Define-XML
##### 5.2.1. 生成されるDefine-XMLの各要素・属性について
* 各要素のOID属性は本ソフトウェアが定めた構文に従って自動的に割当てます。
* ODM要素のxmlns:xsiおよびxsi:schemalocation属性は出力されません。
* ODM要素のCreationDateTime属性は本ソフトウェアが自動的に作成日時を割当てます。
* 複数言語のTranslatedText要素を並列して作成することができません。
* 以下の項目に対応するTranslatedText要素は常にxml:lang="en"です。  
(1) DATASETシートのTranslatedText列またはDescription列  
(2) VARIABLEシートのLabel列  
(3) VARIABLEまたはVALUEシートのPredecessor/Derivation列（※Origin=”Predecessor”の場合のみ）
* def:AnnotatedCRF, def:SupplementalDoc要素内にはdef:PDFPageRef要素を作成しません。
* ItemDef要素のSASFieldName属性はデフォルトではName属性と同じものが割当てられます。
* def:Origin要素に含まれるdef:DocumentRef要素は１つです。
* def:Origin要素内にはOrigin="Predecessor"の場合のみDescription要素を作成します。
* def:PDFPageRef要素にはFirstPage, LastPage属性を作成しません。
* RangeCheck要素は常にSoftHard="Soft"です。
* MethodDef要素のName属性は"Algorithm to derive" に変数名をつけた値を設定します。
* WhereClauseDef要素に対応するdef:CommentDef要素（WhereClauseDef要素のdef:CommentOID属性で参照されているdef:CommentDef要素）内にはdef:DocumentRef要素を作成しません。

##### 5.2.2. SDTM/ADaM Spec用Excelファイルへの入力について
* Excelファイルの各セルの書式設定（標準形式）には「標準」または「文字列」を選択してください。これ以外の書式（「日付」や「ユーザ定義」、SUM関数等の計算式等）を設定した場合、Excelに表示されている文字とは異なる値がDefine-XMLに反映される可能性があります。
* DOCUMENTシートに複数のAnnotatedCRFを設定し、VARIABLE/VALUEシートで参照するAnnotatedCRFを指定できますが（Origin="CRF"の場合）、CDISC提供のスタイルシート（`define-2-0-0.xsl` 2015年1月16日版）が複数のAnnotatedCRFに対応していないため、正しく表示されるためにはスタイルシートを編集する必要があります。
* VARIABLE/VALUEシートにおいて、DocumentIDに記載されている情報は、Predecessor/Derivationが記載されている場合はMethodのDocumentとして、Commentが記載されている場合はCommentのDocumentとして扱われます。両方に記載されている場合はCommentのDocumentとして扱われます。
* ExcelテンプレートのVARIABLEまたはVALUEシートに"SASFieldName"列を追加することにより、Name属性とは異なる値をSASFieldName属性に出力することができます。
* ExcelテンプレートのCODELISTシートに"SASFormatName"列を追加することにより、CodeList要素のSASFormatName属性をDefine-XMLに出力することができます。
* 以下の手順でExcelに新たな列を追加すればItemRef要素のRoleおよびRoleCodeListOID属性をDefine-XMLに出力することができます。  
(1)	VARIABLEシートに"Role"列を追加。  
(2)	VARIABLEシートに"Role Codelist"または"Role codelist"列を追加。  
(3)	CODELISTシートにRole用のコードリストを定義。  
* 以下の手順でExcelに新たな列を追加すればFormalExpression要素のContext属性およびテキストをDefine-XMLに出力することができます。  
(1)	VARIABLEまたはVALUEシートに"FormalExpression Context"または"Formal expression context"列を追加。  
(2)	VARIABLEまたはVALUEシートに"FormalExpression Text"または"Formal expression"列を追加。  
* 以下の情報についてはExcelの１つのセル内に「;」で区切ることで複数の情報を列記することができます。

|シート名|列名|
|:---|:---|
|VALUE|WhereClauseValue|
|DATASET, VARIABLE, VALUE, RESULT1|DocumentID, Document Page Type, Document Page Reference|
|RESULT1|Leaf ID, Leaf Page Type, Leaf Page Reference, Documentation ID, Documentation Page Type, Documentation Page Reference, Programming Code Document ID, Programming Code Document Page Type, Programming Code Document Page Reference|
|RESULT2|Analysis Variable|

* 上記の区切り文字を変更したい場合は、本ソフトウェアの`properties`フォルダにある`main.properties`ファイルをテキストエディタで開き、"valueDelimiter"の値を変更します。
CRF Page Reference列に複数のCRFページを記載する場合は、Define-XMLの規程通り、「;」ではなくスペースで区切ってください（例：「9 14 22」）。

##### 5.2.3. SUPPQUALについて
* SDTMの非標準変数（SUPPQUALで表現される変数）を、SDTMの標準変数と同様にExcelのVARIABLEシートに記載することができます。機能の詳細は`DesktopTools_Mapping_Design.pdf`の「Excel-Define.xml対応表 (SUPPQUALに関する処理)」をご確認ください。また、本機能に対応したExcelテンプレートも合わせて同梱されています。

##### 5.2.4. Define-XMLのインポート（Excelの生成）について
* Descriptionタグ、Decodeタグ内に複数のTranslatedTextタグあった場合、最初に登場したものがExcelに登録され、2番目以降に登場したものは反映されません。
* Define-XML生成機能が常にxml:lang="en"と判定するTranslatedText要素（前述）については、Define-XMLに記載のxml:langの値はExcelに反映されません。
* インポートするDefine-XMLに重大なエラーがあった場合、エラーが表示され、Excelは生成されません。重大なエラーはdxgenerator.jar内のschema/hardに含まれるXMLスキーマにより検証しています。主な検証内容は以下の通りです。  
  - deflne.xmlが整形式(well-formed)でない
  - XMLスキーマに存在しない要素が存在している
  - 必須であるOID項目が省略されている、またはOIDの参照先に対応するOIDが存在しない
* 生成されたExcelのVARIABLE/VALUEシートの「CodeList」項目、VALUEシートの「Value Key」、「W Value Key」およびRESULT1/RESULT2シートの「Result Key」「W Result Key」項目の値は、Define-XML内では定義されていないため、OIDの値をそのまま設定しています。ただし、本ソフトウェア(Define xml Genarator)によって生成されたDefine-XMLについては、OIDのPrefixを自動で取り除いた値が表示されます（ODMタグのSourceSystem属性の値が「tsClinical Define.xml Generator」と一致するかどうかで判断しています）。
•	Output Locationに設定したExcelファイルが開かれているなど、他のプログラムからアクセスされている場合は書き込みができません。

#### 5.3. CRF
##### 5.3.1.	生成されるODMの各要素・属性について
* 本ソフトウェアはExcelに記述されたメタデータをODM形式で出力することを目的としたソフトウェアです。本ソフトウェアを用いて臨床試験データをODM形式で出力することはできません。
* 各要素のOID属性はExcelファイルのID列に指定した値を基に出力されます。詳細はUser's Guideの「OID_MODE」について確認してください。
* ODM要素のCreationDateTime属性は本ソフトウェアが自動的に作成日時を割当てます。
* 複数言語のTranslatedText要素を並列して作成することができません（/ODM/Study/BasicDefinitions/MeasurementUnit/Symbol/TranslatedTextを除く）。
* 以下の要素・属性は出力されません。

|要素名|属性名|
|:---|:---|
|/ODM|Granularity, Archival, PriorFileOID|
|/ODM/Description|-|
|/ODM/Study/BasicDefinitions/MeasurementUnit/Alias|-|
|/ODM/Study/MetaDataVersion/Include|-|
|/ODM/Study/MetaDataVersion/Protocol/Alias|-|
|/ODM/Study/MetaDataVersion/FormDef/ArchiveLayout|PresentationOID
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

##### 5.3.2. CRF用Excelファイルへの入力について
* Excelファイルの各セルの書式設定（標準形式）には「標準」または「文字列」を選択してください。これ以外の書式（「日付」や「ユーザ定義」、SUM関数等の計算式等）を設定した場合、Excelに表示されている文字とは異なる値がODM-XMLに反映される可能性があります。
* UNITシートでは1つのIDに対し複数言語のSymbolを定義する場合は、1つのSymbolに対して1行で記載してください。この時、「ID」および「Name」も記載してください。
* EVENTxFORMシートの「Event Name」、「Form Name」、およびFIELDシートの「Form Name」、「Unit Name」には、（IDではなく）それぞれNameを記載してください。
* FIELDシートの各フォームにItemGroupの定義が含まれていない（各フォームに対して「Level」の値が0から開始している）場合、本ソフトウェアは自動的にItemGroupを作成します。この時、「ID」はFormOID、「Name」は"DEFAULT_n"（nは連番）、「Mandatory」および「Repeating」はFORMシートと同じ値を使用します。
* FIELDシートの「Range Check」は以下の書式で記載してください。
Comparator Value1; Value2; …  
例： IN Related; Possibly Related
※「;」はVALUE_DELIMITERで指定した区切り文字
* CODELISTシートの「Codelist Code」または「Code」を記入した場合、自動的にAlias要素が作成されます。このとき、Alias要素のContext属性の値は「"nci:ExtCodeID」が設定されます。
* METHODシートおよびCONDITIONシートに複数のFormal Expressionを定義する場合は、1つのFormal Expressionに対して1行で記載してください。この時、「ID」および「Name」も記載してください。
* FIELDシートには複数の「Range Check」または「Formal Expression」を定義することができません。
* 以下の情報についてはExcelの１つのセル内に「;」で区切ることで複数の情報を列記することができます。

|シート名|列名|
|:---|:---|
|EVENT|Alias Context, Alias Name|
|FORM|Alias Context, Alias Name|
|FIELD|Unit Name, Alias Context, Alias Name|
|CODELIST|Alias Context, Alias Name|
|METHOD|Alias Context, Alias Name|
|CONDITION|Alias Context, Alias Name|

##### 5.3.3. Create CRF Spec from Datasetsについて
* Architect CRFまたはDatasets Text Filesのどちらか、または両方を指定してCRF Specを作成することができます。
* Architect CRFとDatasets Text Filesの両方を指定した場合、Fieldの一覧はDataset Text Filesの内容が優先します（データセットに無いArchitect CRFのFieldはCRF Specに作成されません）。各Fieldのプロパティの内容やCodelistの内容はArchitect CRFの内容が優先します。


---
Copyright (c) 2020-2021 Fujitsu Limited. All rights reserved.
