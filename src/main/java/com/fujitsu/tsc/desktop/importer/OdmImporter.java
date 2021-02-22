/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.*;

import com.fujitsu.tsc.desktop.importer.models.OdmCodelistModel;
import com.fujitsu.tsc.desktop.importer.models.OdmConditionModel;
import com.fujitsu.tsc.desktop.importer.models.OdmEventFormModel;
import com.fujitsu.tsc.desktop.importer.models.OdmEventModel;
import com.fujitsu.tsc.desktop.importer.models.OdmFieldModel;
import com.fujitsu.tsc.desktop.importer.models.OdmFormModel;
import com.fujitsu.tsc.desktop.importer.models.OdmMethodModel;
import com.fujitsu.tsc.desktop.importer.models.OdmModel;
import com.fujitsu.tsc.desktop.importer.models.OdmStudyModel;
import com.fujitsu.tsc.desktop.importer.models.OdmUnitModel;
import com.fujitsu.tsc.desktop.util.ExcelStyle;
import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.ErrorInfo;
import com.fujitsu.tsc.desktop.validator.DefaultValidationHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import java.lang.String;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * This is a class to validate ODM-XML, bind ODM-XML to {@link OdmModel}, and write the {@link OdmModel} to Excel.
 * To use this class, you must call a constructor first, validateSoft() second to bind, and then generateExcel().
 */
public class OdmImporter {
	private static Logger logger = Logger.getLogger("com.fujitsu.tsc.desktop");

	private ExcelStyle excelStyle;
	private Config config;
	private OdmModel odm;
	private enum Type {SOFT, HARD};

	public OdmImporter(Config config) {
		this.config = config;
	}

	public List<ErrorInfo> validateHard() throws SAXException, ParserConfigurationException, IOException {
		logger.info("Hard validation in progress...");
		return parse(Type.HARD);
	}

	public List<ErrorInfo> validateSoft() throws SAXException, ParserConfigurationException, IOException {
		logger.info("Soft validation in progress...");
		return parse(Type.SOFT);
	}

	private List<ErrorInfo> parse(Type type) throws SAXException, ParserConfigurationException, IOException {

		SchemaFactory sch_factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		SAXParserFactory sax_factory = null;
		Schema schema = null;
		
		if (type == Type.HARD) {
			sax_factory = SAXParserFactory.newInstance("org.apache.xerces.jaxp.SAXParserFactoryImpl", null);
//			schema = sch_factory.newSchema(new File("./schema/hard/cdisc-odm-1.3.2/ODM1-3-2.xsd"));
			schema = sch_factory.newSchema(this.getClass().getClassLoader().getResource("schema/hard/cdisc-odm-1.3.2/ODM1-3-2.xsd"));
		} else {
			sax_factory = SAXParserFactory.newInstance();
//			schema = sch_factory.newSchema(new File("./schema/soft/cdisc-odm-1.3.2/ODM1-3-2.xsd"));
			schema = sch_factory.newSchema(this.getClass().getClassLoader().getResource("schema/soft/cdisc-odm-1.3.2/ODM1-3-2.xsd"));
		}

		sax_factory.setSchema(schema);
		sax_factory.setNamespaceAware(true);
		sax_factory.setValidating(false);
		SAXParser parser = sax_factory.newSAXParser();
		
		if (type == Type.HARD) {
			DefaultValidationHandler handler = new DefaultValidationHandler();
			return handler.getErrors();
		} else {
			OdmXmlReader handler = new OdmXmlReader(config, "", "");
			parser.parse(new File(config.o2eOdmLocation), handler);
			this.odm = handler.getOdmModel();
			return handler.getErrors();
		}
	}
	
	public OdmModel getOdmModel() {
		return this.odm;
	}
	
	public void setOdmModel(OdmModel odm) {
		this.odm = odm;
	}
	
	public void generateExcel() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		excelStyle = new ExcelStyle(wb);

		writeStudySheet(wb, odm);
		writeUnitSheet(wb, odm);
		writeEventSheet(wb, odm);
		writeEventFormSheet(wb, odm);
		writeFormSheet(wb, odm);
		odm.updateFieldFormName();
		writeFieldSheet(wb, odm);
		writeCodelistSheet(wb, odm);
		writeMethodSheet(wb, odm);
		writeConditionSheet(wb, odm);

		FileOutputStream out = new FileOutputStream((String)config.o2eOutputLocation);
		wb.write(out);
		out.close();
	}

	public void writeStudySheet(XSSFWorkbook wb, OdmModel odm) {
		logger.info("Generating STUDY Sheet");
		XSSFSheet sheet = wb.createSheet("STUDY");
		OdmStudyModel study = odm.getStudy();
		String[][] cells = {
			{"Property Name",                "Property Value"},
			{"Source ID",                    ""},
			{"ODMVersion",                   study.odm_version},
			{"FileType",                     study.file_type},
			{"FileOID",                      study.file_oid},
			{"AsOfDateTime",                 study.as_of_date_time},
			{"Originator",                   study.originator},
			{"StudyOID",                     study.study_oid},
			{"StudyName",                    study.study_name},
			{"StudyDescription",             (StringUtils.isEmpty(study.study_description) ? study.study_name : study.study_description)},
			{"ProtocolName",                 study.protocol_name},
			{"MetaDataOID",                  study.metadata_oid},
			{"MetaDataName",                 study.metadata_name},
			{"MetaDataDescription",          study.metadata_description},
			{"ProtocolDescription",          study.protocol_description},
			{"ProtocolDescription xml:lang", study.protocol_description_lang},
			{"Source System",                study.source_system},
			{"Dataset Type",                 "ODM"},
			{"# of Header Lines",            ""},
			{"Dataset Character Encoding",   ""},
			{"Dataset Delimiter",            ""},
			{"Dataset Text Qualifier",       ""},
			{"Date Format",                  ("DDworks21/EDC plus".equals(study.source_system) ? "YYYY/MM/DD" : "")},
			{"Unknown Date/Time Text",       ("DDworks21/EDC plus".equals(study.source_system) ? "UN" : "")},
			{"User Note 1",                  ""},
			{"User Note 2",                  ""}
		};
		for (int i = 0; i < cells.length; i++) {
			Row row = sheet.createRow(i);
			row.createCell(0).setCellValue(cells[i][0]);
			row.createCell(1).setCellValue(cells[i][1]);
		}
		excelStyle.setStyleOdm_StudySheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeUnitSheet(XSSFWorkbook wb, OdmModel odm) {
		logger.info("Generating UNIT Sheet");
		XSSFSheet sheet = wb.createSheet("UNIT");
		List<OdmUnitModel> units = odm.listUnit();
		/* Create a header row */
		String[] header = { "ID", "Name", "Symbol", "xml:lang", "Alias Context", "Alias Name", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(OdmUnitModel unit : units) {
			row = sheet.createRow(row_num++);
			String[] data = { unit.unit_id, unit.unit_name, unit.symbol, unit.xml_lang, unit.alias_context, unit.alias_name, unit.user_note1, unit.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleOdm_UnitSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeEventSheet(XSSFWorkbook wb, OdmModel odm) {
		logger.info("Generating EVENT Sheet");
		XSSFSheet sheet = wb.createSheet("EVENT");
		List<OdmEventModel> events = odm.listEvent();
		/* Create a header row */
		String[] header = { "ID", "Name", "Mandatory", "Repeating", "Type", "Category", "Description", "xml:lang", "Alias Context", "Alias Name", "CollectionExceptionCondition", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(OdmEventModel event : events) {
			row = sheet.createRow(row_num++);
			String[] data = { event.event_id, event.event_name, event.mandatory, event.repeating, event.event_type, event.category, event.description, event.xml_lang, event.alias_context, event.alias_name, event.collection_exception_cnd, event.user_note1, event.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleOdm_EventSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeEventFormSheet(XSSFWorkbook wb, OdmModel odm) {
		logger.info("Generating EVENTxFORM Sheet");
		XSSFSheet sheet = wb.createSheet("EVENTxFORM");
		List<OdmEventFormModel> eventforms = odm.listEventForm();
		/* Create a header row */
		String[] header = {"Event Name", "Form Name", "Mandatory", "CollectionExceptionCondition", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(OdmEventFormModel eventform : eventforms) {
			row = sheet.createRow(row_num++);
			String[] data = { eventform.event_name, eventform.form_name, eventform.mandatory, eventform.collection_exception_cnd, eventform.user_note1, eventform.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleOdm_EventFormSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeFormSheet(XSSFWorkbook wb, OdmModel odm) {
		logger.info("Generating FORM Sheet");
		XSSFSheet sheet = wb.createSheet("FORM");
		List<OdmFormModel> forms = odm.listForm();
		/* Create a header row */
		String[] header = { "ID", "Name", "Repeating", "Description", "xml:lang", "PdfFileName", "Alias Context", "Alias Name", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(OdmFormModel form : forms) {
			row = sheet.createRow(row_num++);
			String[] data = { form.form_id, form.name, form.repeating, form.description, form.xml_lang, form.pdf_file, form.alias_context, form.alias_name, form.user_note1, form.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleOdm_FormSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeFieldSheet(XSSFWorkbook wb, OdmModel odm) {
		logger.info("Generating FIELD Sheet");
		XSSFSheet sheet = wb.createSheet("FIELD");
		List<OdmFieldModel> fields = odm.listField();
		/* Create a header row */
		String[] header = {"Form Name", "ID", "Item Name", "Level", "Mandatory", "Key Sequence", "Repeating", "IsReferenceData", "Question", "Question xml:lang", "DataType", "Length", "SignificantDigits", "SAS Name", "Description", "Description xml:lang", "Unit Name", "Codelist", "RangeCheck", "SoftHard", "RangeCheck Error Message", "Formal Expression Context", "Formal Expression", "Derivation", "CollectionExceptionCondition", "Alias Context", "Alias Name", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(OdmFieldModel field : fields) {
			row = sheet.createRow(row_num++);
			String[] data = { field.form_name, //
					field.field_id, //
					field.name, //
					field.level.toString(), //
					field.mandatory, //
					field.key_sequence, //
					field.repeating, //
					field.is_reference_data, //
					field.question, //
					field.question_xml_lang, //
					field.data_type, //
					(field.length > 0 ? field.length.toString() : ""), //
					(field.significant_digits > 0 ? field.significant_digits.toString() : ""), //
					field.sas_name, //
					field.description, //
					field.description_xml_lang, //
					field.crf_unit, //
					field.crf_codelist, //
					field.range_check, //
					field.soft_hard, //
					field.range_check_error, //
					field.formal_expression_context, //
					field.formal_expression, //
					field.derivation, //
					field.collection_exception_cnd, //
					field.alias_context, //
					field.alias_name, //
					field.user_note1, //
					field.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleOdm_FieldSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeCodelistSheet(XSSFWorkbook wb, OdmModel odm) {
		logger.info("Generating CODELIST Sheet");
		XSSFSheet sheet = wb.createSheet("CODELIST");
		List<OdmCodelistModel> codelists = odm.listCodelist();
		/* Create a header row */
		String[] header = { "Codelist ID", "Codelist Code", "Codelist Label", "DataType", "SASFormatName", "Code", "User Code", "Decode", "xml:lang", "Order Number", "Rank", "ExtendedValue", "Submission Value", "Alias Context", "Alias Name", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(OdmCodelistModel codelist : codelists) {
			row = sheet.createRow(row_num++);
			String[] data = { codelist.codelist, //
					codelist.codelist_code, //
					codelist.codelist_label, //
					codelist.data_type, //
					codelist.sas_format_name, //
					codelist.code, //
					codelist.user_code, //
					codelist.decode, //
					codelist.xml_lang, //
					(codelist.order_number > 0 ? codelist.order_number.toString() : ""), //
					(codelist.rank > 0 ? codelist.rank.toString() : ""), //
					codelist.extended_value, //
					codelist.submission_value, //
					codelist.alias_context, //
					codelist.alias_name, //
					codelist.user_note1, //
					codelist.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleOdm_CodelistSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeMethodSheet(XSSFWorkbook wb, OdmModel odm) {
		logger.info("Generating METHOD Sheet");
		XSSFSheet sheet = wb.createSheet("METHOD");
		List<OdmMethodModel> methods = odm.listMethod();
		/* Create a header row */
		String[] header = { "ID", "Name", "Type", "Description", "xml:lang", "Formal Expression Context", "Formal Expression", "Alias Context", "Alias Name", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(OdmMethodModel method : methods) {
			row = sheet.createRow(row_num++);
			String[] data = { method.method_id, method.method_name, method.method_type, method.description, method.xml_lang, method.formal_expression_context, method.formal_expression, method.alias_context, method.alias_name, method.user_note1, method.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleOdm_MethodSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeConditionSheet(XSSFWorkbook wb, OdmModel odm) {
		logger.info("Generating CONDITION Sheet");
		XSSFSheet sheet = wb.createSheet("CONDITION");
		List<OdmConditionModel> conditions = odm.listCondition();
		/* Create a header row */
		String[] header = { "ID", "Name", "Description", "xml:lang", "Formal Expression Context", "Formal Expression", "Alias Context", "Alias Name", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(OdmConditionModel condition : conditions) {
			row = sheet.createRow(row_num++);
			String[] data = { condition.condition_id, condition.condition_name, condition.description, condition.xml_lang, condition.formal_expression_context, condition.formal_expression, condition.alias_context, condition.alias_name, condition.user_note1, condition.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleOdm_ConditionSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}
}
