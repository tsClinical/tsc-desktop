/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.*;

import com.fujitsu.tsc.desktop.util.ExcelStyle;
import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.ErrorInfo;
import com.fujitsu.tsc.desktop.validator.ValidationResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import java.lang.String;
import java.net.URL;

public class Importer{
	private static Logger logger = Logger.getLogger("com.fujitsu.tsc.desktop");

	private ExcelStyle excelStyle;
	private Config config;
	private String sourseType;
	private boolean isMadeByFujitsu;

	private SchemaFactory scmfactory;
	private XmlReader xmlReader;

	private XmlReadContainer container;

	public Importer(Config config) throws SAXException{
		this.config = config;
		sourseType = config.d2eDatasetType;
		scmfactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
	}

	public ValidationResult validateHard() throws ParserConfigurationException, SAXException, IOException, NotOidConnectException {

		ValidationResult result = new ValidationResult();
//		Schema hardSchema = scmfactory.newSchema(new File(config.getProperty(GeneratorConfig.Parameter.SCHEMA1_SOURCE_LOCATION)));
		Schema hardSchema = null;
		if (sourseType.equals("ADAM")) {
//			hardSchema = scmfactory.newSchema(new File("./schema/hard/cdisc-arm-1.0/arm1-0-0.xsd"));
			hardSchema = scmfactory.newSchema(this.getClass().getClassLoader().getResource("schema/hard/cdisc-arm-1.0/arm1-0-0.xsd"));
		} else if (sourseType.equals("SDTM") || sourseType.equals("SEND")) {
//			hardSchema = scmfactory.newSchema(new File("./schema/hard/cdisc-define-2.0/define2-0-0.xsd"));
			hardSchema = scmfactory.newSchema(this.getClass().getClassLoader().getResource("schema/hard/cdisc-define-2.0/define2-0-0.xsd"));
		}

		//SAXParserFactory spfactory = SAXParserFactory.newInstance();
		SAXParserFactory spfactory = SAXParserFactory.newInstance("org.apache.xerces.jaxp.SAXParserFactoryImpl", null);
		spfactory.setNamespaceAware(true);
		spfactory.setValidating(false);
		spfactory.setSchema(hardSchema);

		SAXParser parser = spfactory.newSAXParser();
		xmlReader = new XmlReader();

		parser.parse(new File(config.d2eDataSourceLocation), xmlReader);
		try {
		xmlReader.checkOid();
		} catch(NotOidConnectException ex) {
			ErrorInfo error = new ErrorInfo();
			error.setMessage(ex.getMessage());
			ArrayList<ErrorInfo> errors = xmlReader.getResult().getErrors();
			errors.add(error);
			xmlReader.getResult().setErrors(errors);
			xmlReader.getResult().setResult(false);
		}
		result= xmlReader.getResult();

		return result;
	}

	public ValidationResult validateSoft() throws ParserConfigurationException, IOException, SAXException {
		ValidationResult result2 = new ValidationResult();
//		Schema softSchema = scmfactory.newSchema(new File(config.getProperty(GeneratorConfig.Parameter.SCHEMA2_SOURCE_LOCATION)));
		Schema softSchema = null;
		if (sourseType.equals("ADAM")) {
//			softSchema = scmfactory.newSchema(new File("./schema/soft/cdisc-arm-1.0/arm1-0-0.xsd"));
			softSchema = scmfactory.newSchema(this.getClass().getClassLoader().getResource("schema/soft/cdisc-arm-1.0/arm1-0-0.xsd"));
		} else if (sourseType.equals("SDTM") || sourseType.equals("SEND")) {
//			softSchema = scmfactory.newSchema(new File("./schema/soft/cdisc-arm-1.0/arm1-0-0.xsd"));
			softSchema = scmfactory.newSchema(this.getClass().getClassLoader().getResource("schema/soft/cdisc-define-2.0/define2-0-0.xsd"));
		}

		SAXParserFactory spfactory = SAXParserFactory.newInstance();
		spfactory.setNamespaceAware(true);
		spfactory.setValidating(false);
		spfactory.setSchema(softSchema);

		SAXParser parser2 = spfactory.newSAXParser();
		xmlReader = new XmlReader();

		parser2.parse(new File(config.d2eDataSourceLocation),xmlReader);
		result2 = xmlReader.getResult();

		return result2;
	}

	public void writeExcel(ValidationResult result, OutputStream out) throws IOException {

		container = result.getContainer();
		//if Define.xml is made by tsClinical Define.xml Generator, it is able to remove tags of OID
		if ("tsClinical Define.xml Generator".equals(container.getStudyDef().get("SourceSystem"))) {
			isMadeByFujitsu = true;
		}
		XSSFWorkbook wb = new XSSFWorkbook();
		excelStyle = new ExcelStyle(wb);

		writeStudySheet(wb);
		writeDocumentSheet(wb);
		writeDatasetSheet(wb);
		writeVariableSheet(wb);
		writeValueSheet(wb);

		if (sourseType.equals("ADAM")) {
			writeResult1Sheet(wb);
			writeResult2Sheet(wb);
		}

		writeDictionarySheet(wb);
		writeCodeListSheet(wb);

		wb.write(out);
//		System.out.println("end");
	}

	//from GUI -- writeExcel(ValidationResult result, String str, GeneratorConfig config) -str="GUI"
	public void writeExcelGui(ValidationResult result) throws FileNotFoundException, IOException, NotOidConnectException {

		container = result.getContainer();
		//if Define.xml is made by tsClinical Define.xml Generator, it is able to remove tags of OID
		if ("tsClinical Define.xml Generator".equals(container.getStudyDef().get("SourceSystem"))) {
			isMadeByFujitsu = true;//
		}
		XSSFWorkbook wb = new XSSFWorkbook();
		excelStyle = new ExcelStyle(wb);

		writeStudySheet(wb);
		writeDocumentSheet(wb);
		writeDatasetSheet(wb);
		writeVariableSheet(wb);
		writeValueSheet(wb);

		if (sourseType.equals("ADAM")) {
			writeResult1Sheet(wb);
			writeResult2Sheet(wb);
		}

		writeDictionarySheet(wb);
		writeCodeListSheet(wb);

//		System.out.println("result size:"+result.size());

		FileOutputStream out = null;
			out = new FileOutputStream((String)config.d2eOutputLocation);
			wb.write(out);
				out.close();
//				System.out.println("close");
	}

//
//	public static void main(String[] args) {
//		try {
//			OutputStream out = new ByteArrayOutputStream();
//
//			Importer importer = new Importer("SDTM", "C:\\Users\\takebayashi.eri\\Desktop\\define.xml");
//			ValidationResult result1 = importer.validateHard();
//			if (result1.getResult() != false) {
//				ValidationResult result2 = importer.validateSoft();
//				importer.writeExcel(result2, out);
//				importer.writeExcelGui(result2);
//				for (ErrorInfo ex : result2.getErrors()) {
//					System.out.println("Line:"+ex.getLine()+"Colmn:"+ex.getColumn());
//					System.out.println("Message:" + ex.getMessage());
//				}
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}

	public void writeStudySheet(XSSFWorkbook wb) {
//		errHint.setErrorHint(TagType.STUDYSHEET, "", "", "", "");
		XSSFSheet study = wb.createSheet("STUDY");
		Row row = study.createRow(0);
		row.createCell(0).setCellValue("Property Name");
		row.createCell(1).setCellValue("Property Value");

		study.createRow(1).createCell(0).setCellValue("ODMVersion");
		study.createRow(2).createCell(0).setCellValue("FileType");
		study.createRow(3).createCell(0).setCellValue("FileOID");
		study.createRow(4).createCell(0).setCellValue("AsOfDateTime");
		study.createRow(5).createCell(0).setCellValue("Originator");
		study.createRow(6).createCell(0).setCellValue("StudyOID");
		study.createRow(7).createCell(0).setCellValue("StudyName");
		study.createRow(8).createCell(0).setCellValue("StudyDescription");
		study.createRow(9).createCell(0).setCellValue("ProtocolName");
		study.createRow(10).createCell(0).setCellValue("MetaDataOID");
		study.createRow(11).createCell(0).setCellValue("MetaDataName");
		study.createRow(12).createCell(0).setCellValue("MetaDataDescription");
		study.createRow(13).createCell(0).setCellValue("DefineVersion");
		study.createRow(14).createCell(0).setCellValue("StandardName");
		study.createRow(15).createCell(0).setCellValue("StandardVersion");
		study.createRow(16).createCell(0).setCellValue("User Note 1");
		study.createRow(17).createCell(0).setCellValue("User Note 2");

		study.getRow(1).createCell(1).setCellValue(container.studyDef.get("ODMVersion"));
		study.getRow(2).createCell(1).setCellValue(container.studyDef.get("FileType"));
		study.getRow(3).createCell(1).setCellValue(container.studyDef.get("FileOID"));
		study.getRow(4).createCell(1).setCellValue(container.studyDef.get("AsOfDateTime"));
		study.getRow(5).createCell(1).setCellValue(container.studyDef.get("Originator"));
		study.getRow(6).createCell(1).setCellValue(container.studyDef.get("StudyOID"));
		study.getRow(7).createCell(1).setCellValue(container.studyDef.get("StudyName"));
		study.getRow(8).createCell(1).setCellValue(container.studyDef.get("StudyDescription"));
		study.getRow(9).createCell(1).setCellValue(container.studyDef.get("ProtocolName"));
		study.getRow(10).createCell(1).setCellValue(container.studyDef.get("MetaDataOID"));
		study.getRow(11).createCell(1).setCellValue(container.studyDef.get("MetaDataName"));
		study.getRow(12).createCell(1).setCellValue(container.studyDef.get("MetaDataDescription"));
		study.getRow(13).createCell(1).setCellValue(container.studyDef.get("DefineVersion"));
		study.getRow(14).createCell(1).setCellValue(container.studyDef.get("StandardName"));
		study.getRow(15).createCell(1).setCellValue(container.studyDef.get("StandardVersion"));
		study.getRow(16).createCell(1);
		study.getRow(17).createCell(1);



		study = excelStyle.setColumnWidth(study);
		study = excelStyle.setStyleStudySheet(study);

	}


	public void writeDocumentSheet(XSSFWorkbook wb) {

//		errHint.setErrorHint(TagType.VALUELISTDEF, hash.get(domainKey), hash.get("Variable Name"), hash.get("Value Key"), "");

		XSSFSheet document = wb.createSheet("DOCUMENT");
		Row row = document.createRow(0);
		row.createCell(0).setCellValue("ID");
		row.createCell(1).setCellValue("Type");
		row.createCell(2).setCellValue("href");
		row.createCell(3).setCellValue("Title");
		row.createCell(4).setCellValue("User Note 1");
		row.createCell(5).setCellValue("User Note 2");

		for (int i=0; i<container.leafDefList.size(); i++) {
			row = document.createRow(i+1);
			if (isMadeByFujitsu) {
				row.createCell(0).setCellValue(removeTag("LF", (String)container.leafDefList.get(i).get("ID")));
				} else {
				row.createCell(0).setCellValue((String)container.leafDefList.get(i).get("ID"));
			}
			if (container.documentDefList.get(container.leafDefList.get(i).get("ID")) != null) {
			row.createCell(1).setCellValue((String)container.documentDefList.get(container.leafDefList.get(i).get("ID")).get("Type"));
			} else {
				row.createCell(1).setCellValue("Other");
			}
				row.createCell(2).setCellValue((String)container.leafDefList.get(i).get("xlink:href"));
				row.createCell(3).setCellValue((String)container.leafDefList.get(i).get("def:title"));
		}
		document = excelStyle.setColumnWidth(document);
		document = excelStyle.setStyleDocumentSheet(document);
	}

	public void writeDatasetSheet(XSSFWorkbook wb) {
		XSSFSheet dataset = wb.createSheet("DATASET");
		Row row = dataset.createRow(0);
		row.createCell(0).setCellValue("Domain");
		row.createCell(1).setCellValue("Dataset Name");
		row.createCell(2).setCellValue("Has SUPP");
		row.createCell(3).setCellValue("Repeating");
		row.createCell(4).setCellValue("IsReferenceData");
		row.createCell(5).setCellValue("Purpose");
		row.createCell(6).setCellValue("Structure");
		row.createCell(7).setCellValue("Class");
		row.createCell(8).setCellValue("Comment");
		row.createCell(9).setCellValue("xml:lang");
		row.createCell(10).setCellValue("DocumentID");
		row.createCell(11).setCellValue("Document Page Type");
		row.createCell(12).setCellValue("Document Page Reference");
		row.createCell(13).setCellValue("Description");
		row.createCell(14).setCellValue("Alias");
		row.createCell(15).setCellValue("href");
		row.createCell(16).setCellValue("Title");
		row.createCell(17).setCellValue("User Note 1");
		row.createCell(18).setCellValue("User Note 2");

		//If DatasetName is "SUPP--", set "Yes" to column of HasSUPP
		for (int j=0; j<container.itemGroupDefList.size(); j++) {
//			System.out.println(container.itemGroupDefList.get(j).get("DatasetName"));
			if (container.itemGroupDefList.get(j).get("DatasetName") != null && container.itemGroupDefList.get(j).get("DatasetName").toString().startsWith("SUPP") && !container.itemGroupDefList.get(j).get("DatasetName").toString().equals("SUPPQUAL")) {
				String parentDomain = container.itemGroupDefList.get(j).get("DatasetName").toString().replace("SUPP", "");
				for (int k =0; k<container.itemGroupDefList.size(); k++) {
					if (container.itemGroupDefList.get(k).get("DatasetName") != null && container.itemGroupDefList.get(k).get("DatasetName").equals(parentDomain)) {
						container.itemGroupDefList.get(k).put("HasSUPP", "Yes");
					}
				}
			}
		}

		for (int i=0; i<container.itemGroupDefList.size(); i++) {
//			System.out.println(container.itemGroupDefList.get(i).get("DatasetName"));
			//If DatasetName is "SUPP--", not write in excel and column "HasSUPP" of parent Dataset is "Yes".
			if (container.itemGroupDefList.get(i).get("DatasetName") != null && container.itemGroupDefList.get(i).get("DatasetName").toString().startsWith("SUPP") && !container.itemGroupDefList.get(i).get("Domain").toString().equals("SUPPQUAL")) {
				//do nothing
			} else {
				row = dataset.createRow(i+1);
				if (sourseType.equals("ADAM")) {
					row.createCell(0).setCellValue((String)container.itemGroupDefList.get(i).get("DatasetName"));
				} else {
				row.createCell(0).setCellValue((String)container.itemGroupDefList.get(i).get("Domain"));
				}
				row.createCell(1).setCellValue((String)container.itemGroupDefList.get(i).get("DatasetName"));
				if (container.itemGroupDefList.get(i).get("HasSUPP") != null && container.itemGroupDefList.get(i).get("HasSUPP").equals("Yes")) {
					row.createCell(2).setCellValue((String)container.itemGroupDefList.get(i).get("HasSUPP"));
				} else {
			        row.createCell(2).setCellValue("No");
				}
				row.createCell(3).setCellValue((String)container.itemGroupDefList.get(i).get("Repeating"));
				row.createCell(4).setCellValue((String)container.itemGroupDefList.get(i).get("IsReferenceData"));
				row.createCell(5).setCellValue((String)container.itemGroupDefList.get(i).get("Purpose"));
				row.createCell(6).setCellValue((String)container.itemGroupDefList.get(i).get("Structure"));
				row.createCell(7).setCellValue((String)container.itemGroupDefList.get(i).get("Class"));
				if (container.itemGroupDefList.get(i).get("CommentOID") != null) {
//					checkOidReference((String)container.itemGroupDefList.get(i).get("CommentOID"), ListType.COMMENTDEF);
					row.createCell(8).setCellValue((String)container.commentDefList.get((String)container.itemGroupDefList.get(i).get("CommentOID")).get("TranslatedText"));
					row.createCell(9).setCellValue((String)container.commentDefList.get((String)container.itemGroupDefList.get(i).get("CommentOID")).get("xml:lang"));
					if (isMadeByFujitsu && container.commentDefList.get((String)container.itemGroupDefList.get(i).get("CommentOID")).get("leafID") != null) {
						String id = removeTag("LF", (String)container.commentDefList.get((String)container.itemGroupDefList.get(i).get("CommentOID")).get("leafID"));
						row.createCell(10).setCellValue(id);
					} else {
						row.createCell(10).setCellValue((String)container.commentDefList.get((String)container.itemGroupDefList.get(i).get("CommentOID")).get("leafID"));
					}
					row.createCell(11).setCellValue((String)container.commentDefList.get((String)container.itemGroupDefList.get(i).get("CommentOID")).get("PageType"));
					row.createCell(12).setCellValue((String)container.commentDefList.get((String)container.itemGroupDefList.get(i).get("CommentOID")).get("PageRefs"));
				}
				row.createCell(13).setCellValue((String)container.itemGroupDefList.get(i).get("Comment"));
				row.createCell(14).setCellValue((String)container.itemGroupDefList.get(i).get("Alias"));
				row.createCell(15).setCellValue((String)container.itemGroupDefList.get(i).get("href"));
				row.createCell(16).setCellValue((String)container.itemGroupDefList.get(i).get("Title"));
			}
		}
		dataset = excelStyle.setColumnWidth(dataset);
		dataset = excelStyle.setStyleDatasetSheet(dataset);
	}

	public void writeVariableSheet(XSSFWorkbook wb){
		XSSFSheet variable = wb.createSheet("VARIABLE");
		Row row = variable.createRow(0);
		row.createCell(0).setCellValue("Domain");
		row.createCell(1).setCellValue("Dataset Name");
		row.createCell(2).setCellValue("Variable Name");
		row.createCell(3).setCellValue("Is SUPP");
		row.createCell(4).setCellValue("Label");
		row.createCell(5).setCellValue("Mandatory");
		row.createCell(6).setCellValue("Key Sequence");
		row.createCell(7).setCellValue("DataType");
		row.createCell(8).setCellValue("Length");
		row.createCell(9).setCellValue("SignificantDigits");
		row.createCell(10).setCellValue("SASFieldName");
		row.createCell(11).setCellValue("DisplayFormat");
		row.createCell(12).setCellValue("Codelist");
		row.createCell(13).setCellValue("Origin");
		row.createCell(14).setCellValue("Derivation Type");
		row.createCell(15).setCellValue("CRF ID");
		row.createCell(16).setCellValue("CRF Page Type");
		row.createCell(17).setCellValue("CRF Page Reference");
		row.createCell(18).setCellValue("Has Value Metadata");
		row.createCell(19).setCellValue("Predecessor/Derivation");
		row.createCell(20).setCellValue("Comment");
		row.createCell(21).setCellValue("xml:lang");
		row.createCell(22).setCellValue("DocumentID");
		row.createCell(23).setCellValue("Document Page Type");
		row.createCell(24).setCellValue("Document Page Reference");
		row.createCell(25).setCellValue("Role");
		row.createCell(26).setCellValue("Role codelist");
		row.createCell(27).setCellValue("Formal expression context");
		row.createCell(28).setCellValue("Formal expression");
		row.createCell(29).setCellValue("User Note 1");
		row.createCell(30).setCellValue("User Note 2");

		String lastDataset = "lastDataset";
		int n = 0;
		for (int i=0; i<container.variableItemRefList.size(); i++) {
//			System.out.println(itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("Name"));

			//SUPPQUAL Variable is not written in excel.
			if (container.variableItemRefList.get(i).get("DatasetName").toString().startsWith("SUPP") && !container.variableItemRefList.get(i).get("DatasetName").toString().equals("SUPPQUAL")){
				//Do nothing
			} else {
				if (!lastDataset.equals((String)container.variableItemRefList.get(i).get("DatasetName"))) {
					//number of suppqual
					for (int m=0; m<container.valueItemDefIdList.size(); m++) {
						String key = container.valueItemDefIdList.get(m);
						for (int k=0; k<container.valueItemDefList.get(key).size(); k++) {
							//if SUPPQUAL Value, write in VARIABLE Sheet.
							if (container.valueItemDefList.get(key).get(k).get("DatasetName").startsWith("SUPP") && !container.valueItemDefList.get(key).get(k).get("DatasetName").equals("SUPPQUAL")) {
								if (container.valueItemDefList.get(key).get(k).get("DatasetName").replace("SUPP", "").equals(lastDataset)) {
									writeSuppVariable(wb, key, k, n);
									n = n + 1;
								}
							}
						}
					}
				}
				row = variable.createRow(i+1+n);
//				checkOidReference((String)container.variableItemRefList.get(i).get("ItemOID"), ListType.ITEMDEF);
				if (sourseType == "ADAM") {
					row.createCell(0).setCellValue((String)container.variableItemRefList.get(i).get("DatasetName"));
				} else {
					row.createCell(0).setCellValue((String)container.variableItemRefList.get(i).get("Domain"));
				}
				row.createCell(1).setCellValue((String)container.variableItemRefList.get(i).get("DatasetName"));
				row.createCell(2).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("Name"));
				row.createCell(3).setCellValue("No");
				row.createCell(4).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("TranslatedText"));
				row.createCell(5).setCellValue((String)container.variableItemRefList.get(i).get("Mandatory"));
				row.createCell(6).setCellValue((String)container.variableItemRefList.get(i).get("KeySequence"));
				row.createCell(7).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("DataType"));
				row.createCell(8).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("Length"));
				row.createCell(9).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("SignificantDigits"));
				row.createCell(10).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("SASFieldName"));
				row.createCell(11).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("DisplayFormat"));
				if ((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CodeListOID") != null) {
//when codelist refer to Dictionary, codelist is mapped from CodeList/ExternalCodeList/@Dictionary.
					if (container.dictionaryIdList.containsKey(container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CodeListOID"))) {
						row.createCell(12).setCellValue((String)container.dictionaryIdList.get(container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CodeListOID")));
					} else {
						if (isMadeByFujitsu) {
							String id = removeTag("CL", (String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CodeListOID"));
							row.createCell(12).setCellValue(id);
						} else {
							row.createCell(12).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CodeListOID"));
						}
					}
				}
				row.createCell(13).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("Type"));
				if (container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("Type") != null) {
					if (container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("Type").toString().equals("Derived")) {
						if (container.variableItemRefList.get(i).get("MethodOID") != null) {
//							checkOidReference((String)container.variableItemRefList.get(i).get("MethodOID"), ListType.METHODDEF);
							row.createCell(14).setCellValue((String)container.methodDefList.get((String)container.variableItemRefList.get(i).get("MethodOID")).get("Type"));
							row.createCell(19).setCellValue((String)container.methodDefList.get((String)container.variableItemRefList.get(i).get("MethodOID")).get("TranslatedText"));
							row.createCell(21).setCellValue((String)container.methodDefList.get((String)container.variableItemRefList.get(i).get("MethodOID")).get("xml:lang"));
						}
					} else if (container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("Type").toString().equals("Predecessor")) {
						row.createCell(19).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("Origin TranslatedText"));
					}
				}
				if (container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("leafID") != null && isMadeByFujitsu) {
					row.createCell(15).setCellValue(removeTag("LF",(String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("leafID")));
				} else {
					row.createCell(15).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("leafID"));
				}
				row.createCell(16).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("PageType"));
				row.createCell(17).setCellValue((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("PageRefs"));
				if ((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("ValueListOID") != null) {
					row.createCell(18).setCellValue("Yes");
				} else {
					row.createCell(18).setCellValue("No");
				}
				if (container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CommentOID") != null) {
//					checkOidReference((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CommentOID"), ListType.COMMENTDEF);
					row.createCell(20).setCellValue((String)container.commentDefList.get((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CommentOID")).get("TranslatedText"));
					if (container.commentDefList.get((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CommentOID")).get("TranslatedText") != null) {
						row.createCell(21).setCellValue((String)container.commentDefList.get((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CommentOID")).get("xml:lang"));
					}
					if (container.commentDefList.get((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CommentOID")).get("leafID") != null && isMadeByFujitsu) {
						String id = removeTag("LF", (String)container.commentDefList.get((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CommentOID")).get("leafID"));
						row.createCell(22).setCellValue(id);
					} else {
						row.createCell(22).setCellValue((String)container.commentDefList.get((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CommentOID")).get("leafID"));
					}
					row.createCell(23).setCellValue((String)container.commentDefList.get((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CommentOID")).get("PageType"));
					row.createCell(24).setCellValue((String)container.commentDefList.get((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CommentOID")).get("PageRefs"));
				}
				row.createCell(25).setCellValue((String)container.variableItemRefList.get(i).get("Role"));
				if (isMadeByFujitsu && container.variableItemRefList.get(i).get("RoleCodelistOID") != null) {
					row.createCell(26).setCellValue(removeTag("CL",(String)container.variableItemRefList.get(i).get("RoleCodelistOID")));
				} else {
					row.createCell(26).setCellValue((String)container.variableItemRefList.get(i).get("RoleCodelistOID"));
				}
				if (container.variableItemRefList.get(i).get("MethodOID") != null) {
//					checkOidReference((String)container.variableItemRefList.get(i).get("MethodOID"), ListType.METHODDEF);
					row.createCell(27).setCellValue((String)container.methodDefList.get((String)container.variableItemRefList.get(i).get("MethodOID")).get("Context"));
					row.createCell(28).setCellValue((String)container.methodDefList.get((String)container.variableItemRefList.get(i).get("MethodOID")).get("FormalExpression"));
				}
			}
			if (container.variableItemRefList.get(i).get("Domain") != null) {
				lastDataset = (String)container.variableItemRefList.get(i).get("DatasetName");
			}
		}
		variable = excelStyle.setColumnWidth(variable);
		variable = excelStyle.setStyleVariableSheet(variable);
	}

	/*SUPPQUAL Value is written in VARIABLE Sheet and "Is SUPP" is "Yes"
	 * This method is called in writeValueSheet.
	 * */
	public String writeSuppVariable(XSSFWorkbook wb, String key, int i, int n){

		Row row = wb.getSheet("VARIABLE").createRow(wb.getSheet("VARIABLE").getLastRowNum()+1);
//		checkOidReference((String)container.valueItemDefList.get(key).get(i).get("ItemOID"), ListType.ITEMDEF);
		row.createCell(0).setCellValue((String)container.valueItemDefList.get(key).get(i).get("Domain").substring(4, 6));
		row.createCell(1).setCellValue((String)container.valueItemDefList.get(key).get(i).get("DatasetName").replace("SUPP", ""));
		row.createCell(2).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Name"));
		row.createCell(3).setCellValue("Yes");
		row.createCell(4).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("TranslatedText"));
		row.createCell(5).setCellValue((String)container.valueItemDefList.get(key).get(i).get("Mandatory"));
		row.createCell(6).setCellValue((String)container.valueItemDefList.get(key).get(i).get("KeySequence"));
		row.createCell(7).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("DataType"));
		row.createCell(8).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Length"));
		row.createCell(9).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("SignificantDigits"));
		row.createCell(10).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("SASFieldName"));
		row.createCell(11).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("DisplayFormat"));
		if ((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CodeListOID") != null) {
			if (container.dictionaryIdList.containsKey(container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CodeListOID"))) {
				row.createCell(12).setCellValue((String)container.dictionaryIdList.get(container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CodeListOID")));
			} else {
				if (isMadeByFujitsu) {
					row.createCell(12).setCellValue(removeTag("CL",(String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CodeListOID")));
				} else {
					row.createCell(12).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CodeListOID"));
				}
			}
		}

		row.createCell(13).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Type"));
		if ((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Type") != null) {
			if (container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Type").toString().equals("Derived")) {
				if (container.valueItemDefList.get(key).get(i).get("MethodOID") != null) {
//					checkOidReference(container.valueItemDefList.get(key).get(i).get("MethodOID"), ListType.METHODDEF);
					row.createCell(14).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("Type"));
					row.createCell(19).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("TranslatedText"));
					row.createCell(21).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("xml:lang"));
				}
			} else if (container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Type").toString().equals("Predecessor")) {
				row.createCell(19).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Origin TranslatedText"));
			}

			if (container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Type").equals("CRF")){
				if (isMadeByFujitsu && (String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("leafID") != null) {
					String id = removeTag("LF", (String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("leafID"));
					row.createCell(15).setCellValue(id);
				} else {
					row.createCell(15).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("leafID"));
				}
				row.createCell(16).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("PageType"));
				row.createCell(17).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("PageRefs"));
			}
		}
		row.createCell(18).setCellValue("No");
		if (container.valueItemDefList.get(key).get(i).get("CommentOID") != null) {
//				checkOidReference((String)container.valueItemDefList.get(key).get(i).get("CommentOID"), ListType.COMMENTDEF);
				row.createCell(20).setCellValue((String)container.commentDefList.get((String)container.valueItemDefList.get(key).get(i).get("CommentOID")).get("TranslatedText"));
				row.createCell(21).setCellValue((String)container.commentDefList.get((String)container.valueItemDefList.get(key).get(i).get("CommentOID")).get("xml:lang"));
				if (isMadeByFujitsu) {
					String id = removeTag("LF", (String)container.commentDefList.get((String)container.valueItemDefList.get(key).get(i).get("CommentOID")).get("leafID"));
					row.createCell(22).setCellValue(id);
				} else {
					row.createCell(22).setCellValue((String)container.commentDefList.get((String)container.valueItemDefList.get(key).get(i).get("CommentOID")).get("leafID"));
				}
				row.createCell(23).setCellValue((String)container.commentDefList.get((String)container.valueItemDefList.get(key).get(i).get("CommentOID")).get("PageType"));
				row.createCell(24).setCellValue((String)container.commentDefList.get((String)container.valueItemDefList.get(key).get(i).get("CommentOID")).get("PageRefs"));
		}
		row.createCell(25).setCellValue((String)container.variableItemRefList.get(i).get("Role"));
		row.createCell(26).setCellValue((String)container.variableItemRefList.get(i).get("RoleCodelistOID"));
		if (container.valueItemDefList.get(key).get(i).get("MethodOID") != null) {
//			checkOidReference(container.valueItemDefList.get(key).get(i).get("MethodOID"), ListType.METHODDEF);
			row.createCell(14).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("Type"));
			row.createCell(19).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("TranslatedText"));
			row.createCell(27).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("Context"));
			row.createCell(28).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("FormalExpression"));
		}
		return (String)container.valueItemDefList.get(key).get(i).get("Domain").replace("SUPP", "");
	}

	public void writeValueSheet(XSSFWorkbook wb){
		XSSFSheet value = wb.createSheet("VALUE");
		Row row = value.createRow(0);
		row.createCell(0).setCellValue("Domain");
		row.createCell(1).setCellValue("Dataset Name");
		row.createCell(2).setCellValue("Value Name");
		row.createCell(3).setCellValue("Variable Name");
		row.createCell(4).setCellValue("Value Key");
		row.createCell(5).setCellValue("Label");
		row.createCell(6).setCellValue("Mandatory");
		row.createCell(7).setCellValue("Key Sequence");
		row.createCell(8).setCellValue("DataType");
		row.createCell(9).setCellValue("Length");
		row.createCell(10).setCellValue("SignificantDigits");
		row.createCell(11).setCellValue("SASFieldName");
		row.createCell(12).setCellValue("DisplayFormat");
		row.createCell(13).setCellValue("Codelist");
		row.createCell(14).setCellValue("Origin");
		row.createCell(15).setCellValue("Derivation Type");
		row.createCell(16).setCellValue("CRF ID");
		row.createCell(17).setCellValue("CRF Page Type");
		row.createCell(18).setCellValue("CRF Page Reference");
		row.createCell(19).setCellValue("Predecessor/Derivation");
		row.createCell(20).setCellValue("Comment");
		row.createCell(21).setCellValue("xml:lang");
		row.createCell(22).setCellValue("DocumentID");
		row.createCell(23).setCellValue("Document Page Type");
		row.createCell(24).setCellValue("Document Page Reference");
		row.createCell(25).setCellValue("Formal expression context");
		row.createCell(26).setCellValue("Formal expression");
		row.createCell(27).setCellValue("User Note 1");
		row.createCell(28).setCellValue("User Note 2");
		row.createCell(29).setCellValue("W Domain");
		row.createCell(30).setCellValue("W Dataset Name");
		row.createCell(31).setCellValue("W Variable Name");
		row.createCell(32).setCellValue("W Value Key");
		row.createCell(33).setCellValue("WhereClauseDataset");
		row.createCell(34).setCellValue("WhereClauseVariable");
		row.createCell(35).setCellValue("WhereClauseOperator");
		row.createCell(36).setCellValue("WhereClauseValue");
		row.createCell(37).setCellValue("WhereClause Comment");
		row.createCell(38).setCellValue("W xml:lang");

		//valueItemDefList is Hashtable<String valueListOID, ArrayList valueItemRef>
		//                                                             valueItemRef is ArrayList<Hashtable<String,String>>
		int j = 0;
		for (int m=0; m<container.valueItemDefIdList.size(); m++) {
			String key = container.valueItemDefIdList.get(m);
			for (int i=0; i<container.valueItemDefList.get(key).size(); i++) {
				//if SUPPQUAL Value, write in VARIABLE Sheet.
				if (container.valueItemDefList.get(key).get(i).get("DatasetName").startsWith("SUPP") && !container.valueItemDefList.get(key).get(i).get("Domain").equals("SUPPQUAL")) {

					j = j - 1;
				} else {
					row = value.createRow(i+j+1);
//					checkOidReference((String)container.valueItemDefList.get(key).get(i).get("ItemOID"), ListType.ITEMDEF);
					row.createCell(0).setCellValue((String)container.valueItemDefList.get(key).get(i).get("Domain"));
					row.createCell(1).setCellValue((String)container.valueItemDefList.get(key).get(i).get("DatasetName"));
					row.createCell(2).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Name"));
					row.createCell(3).setCellValue((String)container.valueItemDefList.get(key).get(i).get("VariableName"));
					if (isMadeByFujitsu && (String)container.valueItemDefList.get(key).get(i).get("ItemOID") != null) {
						row.createCell(4).setCellValue(removeTag("IT",(String)container.valueItemDefList.get(key).get(i).get("DatasetName"),(String)container.valueItemDefList.get(key).get(i).get("VariableName"),(String)container.valueItemDefList.get(key).get(i).get("ItemOID")));
					} else {
						row.createCell(4).setCellValue((String)container.valueItemDefList.get(key).get(i).get("ItemOID"));
					}
					row.createCell(5).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("TranslatedText"));
					row.createCell(6).setCellValue((String)container.valueItemDefList.get(key).get(i).get("Mandatory"));
//				row.createCell(7).setCellValue((String)container.valueItemDefList.get(key).get(i).get("KeySequence"));
					row.createCell(8).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("DataType"));
					row.createCell(9).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Length"));
					row.createCell(10).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("SignificantDigits"));
					row.createCell(11).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("SASFieldName"));
					row.createCell(12).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("DisplayFormat"));
					if ((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CodeListOID") != null) {
						//when codelist refer to Dictionary, codelist is mapped from CodeList/ExternalCodeList/@Dictionary.
						if (container.dictionaryIdList.containsKey(container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CodeListOID"))) {
							row.createCell(13).setCellValue((String)container.dictionaryIdList.get(container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CodeListOID")));
						} else {
							if (isMadeByFujitsu) {
								row.createCell(13).setCellValue(removeTag("CL",(String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CodeListOID")));
							} else {
								row.createCell(13).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CodeListOID"));
							}
						}
					}

					row.createCell(14).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Type"));
					if (container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Type") != null) {
						if (container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Type").toString().equals("Derived")) {
							if (container.valueItemDefList.get(key).get(i).get("MethodOID") != null) {
//								checkOidReference((String)container.valueItemDefList.get(key).get(i).get("MethodOID"), ListType.METHODDEF);
								row.createCell(15).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("Type"));
								row.createCell(19).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("TranslatedText"));
								row.createCell(21).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("xml:lang"));
							}
						} else if (container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Type").toString().equals("Predecessor")) {
							row.createCell(19).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Origin TranslatedText"));
						}
						if (container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("Type").toString().equals("CRF")){
							if (isMadeByFujitsu && (String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("leafID") != null) {
								String id = removeTag("LF", (String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("leafID"));
								row.createCell(16).setCellValue(id);
							} else {
								row.createCell(16).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("leafID"));
							}
							row.createCell(17).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("PageType"));
							row.createCell(18).setCellValue((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("PageRefs"));
						}
					}
					if (container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CommentOID") != null) {
						//						checkOidReference((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CommentOID"), ListType.COMMENTDEF);
						row.createCell(20).setCellValue((String)container.commentDefList.get((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CommentOID")).get("TranslatedText"));
						row.createCell(21).setCellValue((String)container.commentDefList.get((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(i).get("ItemOID")).get("CommentOID")).get("xml:lang"));
					}




					//WhereClause
					if (container.whereClauseDefList.get(container.valueItemDefList.get(key).get(i).get("WhereClauseOID")) != null) {
//						checkOidReference((String)container.valueItemDefList.get(key).get(i).get("WhereClauseOID"), ListType.WHERECLAUSEDEF);
						ArrayList<Hashtable<String, String>> array = container.whereClauseDefList.get(container.valueItemDefList.get(key).get(i).get("WhereClauseOID"));
						for (int k = 0; k<array.size(); k++) {
							if (k != 0) {
								row = value.createRow(i+j+k+1);
							}
							row.createCell(29).setCellValue((String)container.valueItemDefList.get(key).get(i).get("Domain"));
							row.createCell(30).setCellValue((String)container.valueItemDefList.get(key).get(i).get("DatasetName"));
							row.createCell(31).setCellValue((String)container.valueItemDefList.get(key).get(i).get("VariableName"));
							if (isMadeByFujitsu && (String)container.valueItemDefList.get(key).get(i).get("ItemOID") != null) {
								row.createCell(32).setCellValue(removeTag("IT",(String)container.valueItemDefList.get(key).get(i).get("DatasetName"),(String)container.valueItemDefList.get(key).get(i).get("VariableName"),(String)container.valueItemDefList.get(key).get(i).get("ItemOID")));
							} else {
								row.createCell(32).setCellValue((String)container.valueItemDefList.get(key).get(i).get("ItemOID"));
							}
							for (int l=0; l<container.variableItemRefList.size(); l++) {
								if (container.variableItemRefList.get(l).get("ItemOID") != null) {
									if (container.variableItemRefList.get(l).get("ItemOID").equals(array.get(k).get("def:ItemOID"))){
										row.createCell(33).setCellValue((String)container.variableItemRefList.get(l).get("DatasetName"));
									}
								}
							}
//							checkOidReference((String)array.get(k).get("def:ItemOID"), ListType.ITEMDEF);
							row.createCell(34).setCellValue((String)container.itemDefList.get(array.get(k).get("def:ItemOID")).get("Name"));
							row.createCell(35).setCellValue(array.get(k).get("Comparator"));
							row.createCell(36).setCellValue(array.get(k).get("CheckValue"));
							if (container.itemDefList.get(array.get(k).get("def:ItemOID")).get("def:CommentOID") != null) {
//								checkOidReference((String)container.itemDefList.get(array.get(k).get("def:ItemOID")).get("def:CommentOID"), ListType.COMMENTDEF);
								row.createCell(37).setCellValue((String)container.commentDefList.get(array.get(k).get("def:CommentOID")).get("TranslatedText"));
								row.createCell(38).setCellValue((String)container.commentDefList.get(array.get(k).get("def:CommentOID")).get("xml:lang"));
							}
						}
						j = j + array.size() - 1;
					}
					if (container.valueItemDefList.get(key).get(i).get("MethodOID") != null) {
						if (isMadeByFujitsu && container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("leafID") != null) {
							row.createCell(22).setCellValue(removeTag("LF",(String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("leafID")));
						} else {
							row.createCell(22).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("leafID"));
						}
						row.createCell(23).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("PageType"));
						row.createCell(24).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("PageRefs"));
						row.createCell(25).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("Context"));
						row.createCell(26).setCellValue((String)container.methodDefList.get((String)container.valueItemDefList.get(key).get(i).get("MethodOID")).get("FormalExpression"));
					}
				}
			}
			j = j + container.valueItemDefList.get(key).size();
		}
		value = excelStyle.setColumnWidth(value);
		value = excelStyle.setStyleValueSheet(value);
	}

	public void writeDictionarySheet(XSSFWorkbook wb) {
		XSSFSheet dictionary = wb.createSheet("DICTIONARY");
		Row row = dictionary.createRow(0);
		row.createCell(0).setCellValue("Dictionary ID");
		row.createCell(1).setCellValue("Name");
		row.createCell(2).setCellValue("DataType");
		row.createCell(3).setCellValue("Version");
		row.createCell(4).setCellValue("ref");
		row.createCell(5).setCellValue("href");
		row.createCell(6).setCellValue("User Note 1");
		row.createCell(7).setCellValue("User Note 2");

		for (int i=0; i<container.dictionaryDefList.size(); i++) {

			row = dictionary.createRow(i+1);
//			if (isMadeByFujitsu) {
//				String id = removeTag("CL", (String)container.dictionaryDefList.get(i).get("Dictionary"));
//				row.createCell(0).setCellValue(id);
//			} else {
				row.createCell(0).setCellValue((String)container.dictionaryDefList.get(i).get("Dictionary"));
//			}
			row.createCell(1).setCellValue((String)container.dictionaryDefList.get(i).get("CodeListName"));
			row.createCell(2).setCellValue((String)container.dictionaryDefList.get(i).get("DataType"));
			row.createCell(3).setCellValue((String)container.dictionaryDefList.get(i).get("Version"));
			row.createCell(4).setCellValue((String)container.dictionaryDefList.get(i).get("ref"));
			row.createCell(5).setCellValue((String)container.dictionaryDefList.get(i).get("href"));
		}
		dictionary = excelStyle.setColumnWidth(dictionary);
		dictionary = excelStyle.setStyleDictionarySheet(dictionary);
	}

	public void writeCodeListSheet(XSSFWorkbook wb){
		XSSFSheet codelist = wb.createSheet("CODELIST");
		Row row = codelist.createRow(0);
		row.createCell(0).setCellValue("Codelist ID");
		row.createCell(1).setCellValue("Codelist Code");
		row.createCell(2).setCellValue("Codelist Label");
		row.createCell(3).setCellValue("DataType");
		row.createCell(4).setCellValue("SASFormatName");
		row.createCell(5).setCellValue("Code");
		row.createCell(6).setCellValue("User Code");
		row.createCell(7).setCellValue("Order Number");
		row.createCell(8).setCellValue("Rank");
		row.createCell(9).setCellValue("ExtendedValue");
		row.createCell(10).setCellValue("Submission Value");
		row.createCell(11).setCellValue("Decode");
		row.createCell(12).setCellValue("xml:lang");
		row.createCell(13).setCellValue("User Note 1");
		row.createCell(14).setCellValue("User Note 2");

		for (int i=0; i<container.codeListItemDefList.size(); i++) {

			row = codelist.createRow(i+1);
			if (isMadeByFujitsu) {
				row.createCell(0).setCellValue(removeTag("CL",(String)container.codeListItemDefList.get(i).get("OID")));
			} else {
				row.createCell(0).setCellValue((String)container.codeListItemDefList.get(i).get("OID"));
			}
//			checkOidReference((String)container.codeListItemDefList.get(i).get("OID"), ListType.CODELISTDEF);
			row.createCell(1).setCellValue((String)container.codeListDefList.get((String)container.codeListItemDefList.get(i).get("OID")).get("Code"));
			row.createCell(2).setCellValue((String)container.codeListItemDefList.get(i).get("CodeListName"));
			row.createCell(3).setCellValue((String)container.codeListItemDefList.get(i).get("DataType"));
			row.createCell(4).setCellValue((String)container.codeListItemDefList.get(i).get("SASFormatName"));
			row.createCell(5).setCellValue((String)container.codeListItemDefList.get(i).get("Name"));
			row.createCell(7).setCellValue((String)container.codeListItemDefList.get(i).get("OrderNumber"));
			row.createCell(8).setCellValue((String)container.codeListItemDefList.get(i).get("Rank"));
			if (container.codeListItemDefList.get(i).get("def:ExtendedValue") != null) {
				row.createCell(9).setCellValue((String)container.codeListItemDefList.get(i).get("def:ExtendedValue"));
			} else {
				row.createCell(9).setCellValue("No");
			}
			row.createCell(10).setCellValue((String)container.codeListItemDefList.get(i).get("CodedValue"));
			row.createCell(11).setCellValue((String)container.codeListItemDefList.get(i).get("TranslatedText"));
			row.createCell(12).setCellValue((String)container.codeListItemDefList.get(i).get("xml:lang"));
		}

//enumeratedIteDefはcodeListItemDefに含�?こととしたため不�?
//		int j = container.codeListItemDefList.size();
//
//		for (int i=0; i<container.enumeratedItemDefList.size(); i++) {
//
//			row = codelist.createRow(j+i+1);
//			if (isMadeByFujitsu) {
//				row.createCell(0).setCellValue(removeTag("CL",(String)container.enumeratedItemDefList.get(i).get("OID")));
//			}
//			checkOidReference((String)container.enumeratedItemDefList.get(i).get("OID"), ListType.CODELISTDEF);
//			row.createCell(1).setCellValue((String)container.codeListDefList.get((String)container.enumeratedItemDefList.get(i).get("OID")).get("Code"));
//			row.createCell(2).setCellValue((String)container.enumeratedItemDefList.get(i).get("CodeListName"));
//			row.createCell(3).setCellValue((String)container.enumeratedItemDefList.get(i).get("DataType"));
//			row.createCell(4).setCellValue((String)container.enumeratedItemDefList.get(i).get("SASFormatName"));
//			row.createCell(5).setCellValue((String)container.enumeratedItemDefList.get(i).get("Name"));
//			row.createCell(6).setCellValue((String)container.enumeratedItemDefList.get(i).get("OrderNumber"));
//			row.createCell(7).setCellValue((String)container.enumeratedItemDefList.get(i).get("Rank"));
//			if (container.enumeratedItemDefList.get(i).get("ExtendedValue") != null) {
//				row.createCell(8).setCellValue((String)container.enumeratedItemDefList.get(i).get("ExtendedValue"));
//			} else {
//				row.createCell(8).setCellValue("No");
//			}
//			row.createCell(9).setCellValue((String)container.enumeratedItemDefList.get(i).get("CodedValue"));
//			row.createCell(10).setCellValue((String)container.enumeratedItemDefList.get(i).get("TranslatedText"));
//			row.createCell(11).setCellValue((String)container.enumeratedItemDefList.get(i).get("xml:lang"));
//		}
		codelist = excelStyle.setColumnWidth(codelist);
		codelist = excelStyle.setStyleCodelistSheet(codelist);
	}


	public void writeResult1Sheet(XSSFWorkbook wb){
		XSSFSheet result1 = wb.createSheet("RESULT1");
		Row row = result1.createRow(0);
		row.createCell(0).setCellValue("Display Name");
		row.createCell(1).setCellValue("Display Description");
		row.createCell(2).setCellValue("Display xml:lang");
		row.createCell(3).setCellValue("Leaf ID");
		row.createCell(4).setCellValue("Leaf Page Type");
		row.createCell(5).setCellValue("Leaf Page Reference");
		row.createCell(6).setCellValue("User Note 1");
		row.createCell(7).setCellValue("User Note 2");
		row.createCell(8).setCellValue("W Display Name");
		row.createCell(9).setCellValue("Result Key");
		row.createCell(10).setCellValue("Result Description");
		row.createCell(11).setCellValue("Result xml:lang");
		row.createCell(12).setCellValue("ParameterOID Dataset");
		row.createCell(13).setCellValue("Analysis Reason");
		row.createCell(14).setCellValue("Analysis Purpose");
		row.createCell(15).setCellValue("Documentation ID");
		row.createCell(16).setCellValue("Documentation Page Type");
		row.createCell(17).setCellValue("Documentation Page Reference");
		row.createCell(18).setCellValue("Documentation Text");
		row.createCell(19).setCellValue("Documentation xml:lang");
		row.createCell(20).setCellValue("Programming Code Context");
		row.createCell(21).setCellValue("Programming Code Text");
		row.createCell(22).setCellValue("Programming Code Document ID");
		row.createCell(23).setCellValue("Programming Code Document Page Type");
		row.createCell(24).setCellValue("Programming Code Document Page Reference");
		row.createCell(25).setCellValue("Datasets Comment");
		row.createCell(26).setCellValue("Datasets xml:lang");
		row.createCell(27).setCellValue("DocumentID");
		row.createCell(28).setCellValue("Document Page Type");
		row.createCell(29).setCellValue("Document Page Reference");

		String lastDisplayName = "lastDisplayName";
		for (int i=0; i<container.analysisResultList.size(); i++) {

			row = result1.createRow(i+1);
			if (lastDisplayName.equals((String)container.analysisResultList.get(i).get("DisplayName"))) {
				//cell(0)~(5) is blank.
			} else {
				row.createCell(0).setCellValue((String)container.analysisResultList.get(i).get("DisplayName"));
				row.createCell(1).setCellValue((String)container.analysisResultList.get(i).get("DisplayDescription"));
				row.createCell(2).setCellValue((String)container.analysisResultList.get(i).get("DisplayXmlLang"));
				if (isMadeByFujitsu && container.analysisResultList.get(i).get("DisplayLeafID") != null) {
					row.createCell(3).setCellValue(removeTag("LF",(String)container.analysisResultList.get(i).get("DisplayLeafID")));
				} else {
					row.createCell(3).setCellValue((String)container.analysisResultList.get(i).get("DisplayLeafID"));
				}
				row.createCell(4).setCellValue((String)container.analysisResultList.get(i).get("DisplayPageType"));
				row.createCell(5).setCellValue((String)container.analysisResultList.get(i).get("DisplayPageRefs"));
			}
			row.createCell(8).setCellValue((String)container.analysisResultList.get(i).get("DisplayName"));
			if ((String)container.analysisResultList.get(i).get("AnalysisOID") != null) {
				if (isMadeByFujitsu) {
					row.createCell(9).setCellValue(removeTag("AR",(String)container.analysisResultList.get(i).get("DisplayName"),"",(String)container.analysisResultList.get(i).get("AnalysisOID")));
				} else {
					row.createCell(9).setCellValue((String)container.analysisResultList.get(i).get("AnalysisOID"));
				}
			}
			row.createCell(10).setCellValue((String)container.analysisResultList.get(i).get("AnalysisDescription"));
			row.createCell(11).setCellValue((String)container.analysisResultList.get(i).get("xml:lang"));
			if (container.analysisResultList.get(i).get("ParameterOID") != null ) {
//				checkOidReference((String)container.analysisResultList.get(i).get("ParameterOID"), ListType.PARAMETERDEF);
			row.createCell(12).setCellValue((String)container.itemDefOIDandDatasetNameList.get(container.analysisResultList.get(i).get("ParameterOID")));
			}
			row.createCell(13).setCellValue((String)container.analysisResultList.get(i).get("AnalysisReason"));
			row.createCell(14).setCellValue((String)container.analysisResultList.get(i).get("AnalysisPurpose"));
			if (isMadeByFujitsu && container.analysisResultList.get(i).get("Documentation leafID") != null) {
				row.createCell(15).setCellValue(removeTag("LF",(String)container.analysisResultList.get(i).get("Documentation leafID")));
			} else {
				row.createCell(15).setCellValue((String)container.analysisResultList.get(i).get("Documentation leafID"));
			}
			row.createCell(16).setCellValue((String)container.analysisResultList.get(i).get("Documentation PageType"));
			row.createCell(17).setCellValue((String)container.analysisResultList.get(i).get("Documentation PageRefs"));
			row.createCell(18).setCellValue((String)container.analysisResultList.get(i).get("Documentation Description"));
			row.createCell(19).setCellValue((String)container.analysisResultList.get(i).get("Documentation xml:lang"));
			row.createCell(20).setCellValue((String)container.analysisResultList.get(i).get("ProgrammingCodeContext"));
			row.createCell(21).setCellValue((String)container.analysisResultList.get(i).get("ProgrammingCodeDescription"));
			if (isMadeByFujitsu && container.analysisResultList.get(i).get("ProgrammingCodeleafID") != null) {
				row.createCell(22).setCellValue(removeTag("LF",(String)container.analysisResultList.get(i).get("ProgrammingCodeleafID")));
			} else {
				row.createCell(22).setCellValue((String)container.analysisResultList.get(i).get("ProgrammingCodeleafID"));
			}
			row.createCell(23).setCellValue((String)container.analysisResultList.get(i).get("ProgrammingPageType"));
			row.createCell(24).setCellValue((String)container.analysisResultList.get(i).get("ProgrammingPageRefs"));
			if ((String)container.analysisResultList.get(i).get("def:CommentOID") != null) {
//				checkOidReference((String)container.analysisResultList.get(i).get("def:CommentOID"), ListType.COMMENTDEF);
				row.createCell(25).setCellValue(container.commentDefList.get((String)container.analysisResultList.get(i).get("def:CommentOID")).get("TranslatedText"));
				row.createCell(26).setCellValue(container.commentDefList.get((String)container.analysisResultList.get(i).get("def:CommentOID")).get("xml:lang"));
				row.createCell(27).setCellValue(container.commentDefList.get((String)container.analysisResultList.get(i).get("def:CommentOID")).get("leafID"));
				row.createCell(28).setCellValue(container.commentDefList.get((String)container.analysisResultList.get(i).get("def:CommentOID")).get("PageType"));
				row.createCell(29).setCellValue(container.commentDefList.get((String)container.analysisResultList.get(i).get("def:CommentOID")).get("PageRefs"));
			}
			lastDisplayName = (String)container.analysisResultList.get(i).get("DisplayName");
		}
		result1 = excelStyle.setColumnWidth(result1);
		result1 = excelStyle.setStyleResult1Sheet(result1);
	}


	public void writeResult2Sheet(XSSFWorkbook wb){
		XSSFSheet result2 = wb.createSheet("RESULT2");
		Row row = result2.createRow(0);
		row.createCell(0).setCellValue("Display Name");
		row.createCell(1).setCellValue("Result Key");
		row.createCell(2).setCellValue("Dataset Name");
		row.createCell(3).setCellValue("Analysis Variable");
		row.createCell(4).setCellValue("User Note 1");
		row.createCell(5).setCellValue("User Note 2");
		row.createCell(6).setCellValue("W Display Name");
		row.createCell(7).setCellValue("W Result Key");
		row.createCell(8).setCellValue("W Dataset Name");
		row.createCell(9).setCellValue("WhereClauseDataset");
		row.createCell(10).setCellValue("WhereClauseVariable");
		row.createCell(11).setCellValue("WhereClauseOperator");
		row.createCell(12).setCellValue("WhereClauseValue");
		row.createCell(13).setCellValue("WhereClause Comment");
		row.createCell(14).setCellValue("W xml:lang");

		int j = 0;
		for (int i=0; i<container.analysisDatasetList.size(); i++) {
			row = result2.createRow(i+j+1);
			row.createCell(0).setCellValue((String)container.analysisDatasetList.get(i).get("DisplayName"));
			if ((String)container.analysisDatasetList.get(i).get("AnalysisOID") != null) {
				if (isMadeByFujitsu) {
					row.createCell(1).setCellValue(removeTag("AR",(String)container.analysisDatasetList.get(i).get("DisplayName"),"",(String)container.analysisDatasetList.get(i).get("AnalysisOID")));
				} else {
					row.createCell(1).setCellValue((String)container.analysisDatasetList.get(i).get("AnalysisOID"));
				}
			}
			row.createCell(2).setCellValue((String)container.analysisDatasetList.get(i).get("DatasetName"));
			row.createCell(3).setCellValue((String)container.analysisDatasetList.get(i).get("Analysis Variable"));
//			checkOidReference((String)container.analysisDatasetList.get(i).get("WhereClauseOID"), ListType.WHERECLAUSEDEF);
			if (container.whereClauseDefList.get(container.analysisDatasetList.get(i).get("WhereClauseOID")) != null) {
				ArrayList<Hashtable<String, String>> array = container.whereClauseDefList.get(container.analysisDatasetList.get(i).get("WhereClauseOID"));
				for (int k = 0; k<array.size(); k++) {
					if (k != 0) {
					row = result2.createRow(i+j+k+1);
					}
					row.createCell(6).setCellValue((String)container.analysisDatasetList.get(i).get("DisplayName"));
					if ((String)container.analysisDatasetList.get(i).get("AnalysisOID") != null) {
						if (isMadeByFujitsu) {
							row.createCell(7).setCellValue(removeTag("AR",(String)container.analysisDatasetList.get(i).get("DisplayName"),"",(String)container.analysisDatasetList.get(i).get("AnalysisOID")));
						} else {
							row.createCell(7).setCellValue((String)container.analysisDatasetList.get(i).get("AnalysisOID"));
						}
					}
					row.createCell(8).setCellValue((String)container.analysisDatasetList.get(i).get("DatasetName"));
					row.createCell(9).setCellValue((String)container.analysisDatasetList.get(i).get("DatasetName"));
//					checkOidReference((String)array.get(k).get("def:ItemOID"), ListType.ITEMDEF);
					row.createCell(10).setCellValue((String)container.itemDefList.get(array.get(k).get("def:ItemOID")).get("Name"));
					row.createCell(11).setCellValue(array.get(k).get("Comparator"));
					row.createCell(12).setCellValue(array.get(k).get("CheckValue"));
					if (container.itemDefList.get(array.get(k).get("def:ItemOID")).get("def:CommentOID") != null) {
//						checkOidReference((String)container.itemDefList.get(array.get(k).get("def:ItemOID")).get("def:CommentOID"), ListType.COMMENTDEF);
						row.createCell(13).setCellValue((String)container.commentDefList.get(array.get(k).get("def:CommentOID")).get("TranslatedText"));
						row.createCell(14).setCellValue((String)container.commentDefList.get(array.get(k).get("def:CommentOID")).get("xml:lang"));
					}
				}
				j = j + array.size() - 1;
			}
		}
		result2 = excelStyle.setColumnWidth(result2);
		result2 = excelStyle.setStyleResult2Sheet(result2);
	}

//oidから�?ータを取ってくる場合に�?�?部�?以外�?�タグを取り除�?
	public String removeTag(String tag, String id) {
		String element = id;
		   //leaf
		if(tag.equals("LF")) {
			element = id.replace("LF.", "");
			//Codelist
		} else if (tag.equals("CL")) {
			element = id.replace("CL.", "");

//ValueKey用�?1.3.0ではタグを取り除かな�?
//		} else if (tag.equals("VK")) {
//			Pattern pattern = Pattern.compile("\\w+[.]");
//			Matcher matcher = pattern.matcher(id);
//			StringBuffer replacedStrBuffer = new StringBuffer();
//			int i = 0;
//			while (matcher.find() && i < 3) {
//				i++;
//				matcher.appendReplacement(replacedStrBuffer, matcher.group());
//			}
//			String base = replacedStrBuffer.toString();
//			matcher.appendTail(replacedStrBuffer);
//			element= element.replace(base, "");
//
//		} else if (tag.equals("AA")) {
//			Pattern pattern = Pattern.compile("[.]+\\w$");
//			Matcher matcher = pattern.matcher(id);
		}
		return element;
	}

	public String removeTag(String tag, String datasetName, String variableName, String id) {
		String element = id;
		if(tag.equals("IT")) {
			element = id.replace("IT."+datasetName+"."+variableName+".", "");
		} else if (tag.equals("AR")) {
			System.out.println("AR."+datasetName+".");
			element = id.replace("AR."+datasetName+".", "");
		}
		return element;
	}
}
