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

import com.fujitsu.tsc.desktop.importer.models.DefineARMDatasetModel;
import com.fujitsu.tsc.desktop.importer.models.DefineARMDisplayModel;
import com.fujitsu.tsc.desktop.importer.models.DefineARMResultModel;
import com.fujitsu.tsc.desktop.importer.models.DefineARMResultModel.DefineARMResultPk;
import com.fujitsu.tsc.desktop.importer.models.DefineCodelistModel;
import com.fujitsu.tsc.desktop.importer.models.DefineCommentModel;
import com.fujitsu.tsc.desktop.importer.models.DefineCommentModel.DefineCommentPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDatasetModel;
import com.fujitsu.tsc.desktop.importer.models.DefineDictionaryModel;
import com.fujitsu.tsc.desktop.importer.models.DefineDocumentModel;
import com.fujitsu.tsc.desktop.importer.models.DefineMethodModel;
import com.fujitsu.tsc.desktop.importer.models.DefineMethodModel.DefineMethodPk;
import com.fujitsu.tsc.desktop.importer.models.DefineModel;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorNull;
import com.fujitsu.tsc.desktop.importer.models.DefineStandardModel;
import com.fujitsu.tsc.desktop.importer.models.DefineStandardModel.DefineStandardPk;
import com.fujitsu.tsc.desktop.importer.models.DefineStandardModel.StandardType;
import com.fujitsu.tsc.desktop.importer.models.DefineStudyModel;
import com.fujitsu.tsc.desktop.importer.models.DefineValueModel;
import com.fujitsu.tsc.desktop.importer.models.DefineVariableModel;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.DefineWCPk;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.WCCondition;
import com.fujitsu.tsc.desktop.util.ExcelStyle;
import com.fujitsu.tsc.desktop.util.Utils;
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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a class to validate ODM-XML, bind ODM-XML to {@link OdmModel}, and write the {@link OdmModel} to Excel.
 * To use this class, you must call a constructor first, validateSoft() second to bind, and then generateExcel().
 */
public class DefineImporter {
	private static Logger logger = LogManager.getLogger();

	private ExcelStyle excelStyle;
	private Config config;
	private DefineModel define;
	private enum Type {SOFT, HARD};

	public DefineImporter(Config config) {
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
		Utils.setSchemaFactorySecureFeatures(sch_factory);
		SAXParserFactory sax_factory = SAXParserFactory.newInstance();
		Schema schema = null;
		if (type == Type.HARD) {
			schema = sch_factory.newSchema(this.getClass().getClassLoader().getResource("schema/xml/xml.xsd"));
		} else {
			if ("2.1.n".equals(config.d2eDefineVersion)) {
				schema = sch_factory.newSchema(this.getClass().getClassLoader().getResource("schema/2.1.n/cdisc-arm-1.0/arm1-0-0.xsd"));
			} else {
				schema = sch_factory.newSchema(this.getClass().getClassLoader().getResource("schema/2.0.0/cdisc-arm-1.0/arm1-0-0.xsd"));
			}
		}

		sax_factory.setSchema(schema);
		sax_factory.setNamespaceAware(true);
		sax_factory.setValidating(false);
		Utils.setSaxParserFactorySecureFeatures(sax_factory);
		SAXParser parser = sax_factory.newSAXParser();
		
		if (type == Type.HARD) {
			DefaultValidationHandler handler = new DefaultValidationHandler();
			return handler.getErrors();
		} else {
			DefineXmlReader handler = new DefineXmlReader(config);
			parser.parse(new File(config.d2eDataSourceLocation), handler);
			this.define = handler.getDefineModel();
			return handler.getErrors();
		}
	}
	
	public DefineModel getDefineModel() {
		return this.define;
	}
	
	public void setDefineModel(DefineModel define) {
		this.define = define;
	}
	
	public XSSFWorkbook generateWorkbook() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		excelStyle = new ExcelStyle(wb, this.config);

		/* Preliminary Update */
		define.updateVLM();	//Update Dataset and Variable
		define.updateARMDisplay();	//Update Parameter Dataset
		define.updateARMDataset();	//Update Dataset, Variable and WhereClause
		if (this.config.d2eDatasetType.equals("SDTM") && config.d2eMergeNSVtoParent) {
			define.convertToAutoSupp();	//Merge NSVs to parent datasets
			define.updateHasSupp();	//Update Has SUPP
		}
		define.updateVariableOrdinal();	//Update ordinal based on Dataset ordinal

		/* Write out */
		writeStudySheet(wb, define);
		writeStandardSheet(wb, define);
		writeDocumentSheet(wb, define);
		writeDatasetSheet(wb, define);
		writeVariableSheet(wb, define);
		writeValueSheet(wb, define);
		if (this.config.d2eDatasetType.equals("ADaM")) {
			writeResult1Sheet(wb, define);
			writeResult2Sheet(wb, define);
		}
		writeDictionarySheet(wb, define);
		writeCodelistSheet(wb, define);
		if (config.d2eSeparateSheet) {
			writeMethodSheet(wb, define);
			writeCommentSheet(wb, define);
		}
		
		return wb;

//		FileOutputStream out = new FileOutputStream((String)config.d2eOutputLocation);
//		wb.write(out);
//		out.close();
	}

	public void writeStudySheet(XSSFWorkbook wb, DefineModel define) {
		logger.info("Generating " + config.defineStudyTableName + " Sheet");
		XSSFSheet sheet = wb.createSheet(config.defineStudyTableName);
		DefineStudyModel study = define.getStudy();
		DefineCommentModel comment = define.get(new DefineCommentPk(study.comment_oid));
		String[][] cells = {
			{"Property Name",                "Property Value"},
			{"FileOID",                      study.file_oid},
			{"AsOfDateTime",                 study.as_of_date_time},
			{"Originator",                   study.originator},
			{"Context",                      study.context},
			{"StudyOID",                     study.study_oid},
			{"StudyName",                    study.study_name},
			{"StudyDescription",             (StringUtils.isEmpty(study.study_description) ? study.study_name : study.study_description)},
			{"ProtocolName",                 study.protocol_name},
			{"MetaDataOID",                  study.metadata_oid},
			{"MetaDataName",                 study.metadata_name},
			{"MetaDataDescription",          study.metadata_description},
			{"DefineVersion",                study.define_version},
			{"StandardName",                 study.standard_name},
			{"StandardVersion",              study.standard_version},
			{"CommentOID",                   (comment != null && config.d2eSeparateSheet ? comment.oid : "")},
			{"Comment",                      (comment == null || config.d2eSeparateSheet ? "" : comment.comment_text)},
			{"Language",                     (comment == null || config.d2eSeparateSheet ? "" : comment.comment_lang)},
			{"DocumentID",                   (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentIdString(config.valueDelimiter))},
			{"Document Page Type",           (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTypeString(config.valueDelimiter))},
			{"Document Page Reference",      (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageRefString(config.valueDelimiter))},
			{"Document First Page",          (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentFirstPageString(config.valueDelimiter))},
			{"Document Last Page",           (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentLastPageString(config.valueDelimiter))},
			{"Document Page Title",          (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTitleString(config.valueDelimiter))},
			{"User Note 1",                  ""},
			{"User Note 2",                  ""}
		};
		for (int i = 0; i < cells.length; i++) {
			Row row = sheet.createRow(i);
			row.createCell(0).setCellValue(cells[i][0]);
			row.createCell(1).setCellValue(cells[i][1]);
		}
		excelStyle.setStyleDefine_StudySheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeStandardSheet(XSSFWorkbook wb, DefineModel define) {
		logger.info("Generating " + config.defineStandardTableName + " Sheet");
		XSSFSheet sheet = wb.createSheet(config.defineStandardTableName);
		List<DefineStandardModel> standards = define.listSortedStandard();
		/* Create a header row */
		String[] header = { "Name", "Type", "Publishing Set", "Version", "Status", "CommentOID", "Comment", "Language", "DocumentID", "Document Page Type", "Document Page Reference", "Document First Page", "Document Last Page", "Document Page Title", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(DefineStandardModel standard : standards) {
			DefineCommentModel comment = define.get(new DefineCommentPk(standard.comment_oid));
			row = sheet.createRow(row_num++);
			String[] data = {
					standard.standard_name,
					standard.standard_type.name(), 
					standard.publishing_set,
					standard.standard_version,
					standard.standard_status,
					(comment != null && config.d2eSeparateSheet ? comment.oid : ""),
					(comment == null || config.d2eSeparateSheet ? "" : comment.comment_text),
					(comment == null || config.d2eSeparateSheet ? "" : comment.comment_lang),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentIdString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTypeString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageRefString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentFirstPageString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentLastPageString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTitleString(config.valueDelimiter)),
					standard.user_note1,
					standard.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleDefine_StandardSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeDocumentSheet(XSSFWorkbook wb, DefineModel define) {
		logger.info("Generating " + config.defineDocumentTableName + " Sheet");
		XSSFSheet sheet = wb.createSheet(config.defineDocumentTableName);
		List<DefineDocumentModel> documents = define.listSortedDocument();
		/* Create a header row */
		String[] header = { "ID", "Type", "href", "Title", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(DefineDocumentModel document : documents) {
			row = sheet.createRow(row_num++);
			String[] data = { document.document_id, document.document_type.name(), document.document_href, document.document_title, document.user_note1, document.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleDefine_DocumentSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeDatasetSheet(XSSFWorkbook wb, DefineModel define) {
		logger.info("Generating " + config.defineDatasetTableName + " Sheet");
		XSSFSheet sheet = wb.createSheet(config.defineDatasetTableName);
		List<DefineDatasetModel> datasets = define.listSortedDataset();
		/* Create a header row */
		String[] header = { "Domain", "Dataset Name", "Has SUPP", "Description", "No Data", "SASDatasetName", "Repeating", "IsReferenceData", "Purpose", "Standard", "Structure", "Class", "Subclass", "CommentOID", "Comment", "Language", "DocumentID", "Document Page Type", "Document Page Reference", "Document First Page", "Document Last Page", "Document Page Title", "Alias", "Leaf href", "Leaf Title", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(DefineDatasetModel dataset : datasets) {
			DefineCommentModel comment = define.get(new DefineCommentPk(dataset.comment_oid));
			DefineStandardModel standard = define.get(new DefineStandardPk(dataset.standard_oid));
			row = sheet.createRow(row_num++);
			String[] data = {
					(StringUtils.isEmpty(dataset.domain) ? dataset.dataset_name : dataset.domain),
					dataset.dataset_name,
					dataset.has_supp.name(),
					dataset.description,
					(dataset.has_no_data == null ? "" : dataset.has_no_data.name()),
					dataset.sas_dataset_name,
					dataset.repeating.name(),
					dataset.is_reference_data.name(),
					dataset.purpose,
					(standard == null ? "" : standard.name(StandardType.IG)),
					dataset.structure,
					dataset.dataset_class,
					dataset.dataset_subclass,
					(comment != null && config.d2eSeparateSheet ? comment.oid : ""),
					(comment == null || config.d2eSeparateSheet ? "" : comment.comment_text),
					(comment == null || config.d2eSeparateSheet ? "" : comment.comment_lang),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentIdString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTypeString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageRefString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentFirstPageString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentLastPageString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTitleString(config.valueDelimiter)),
					dataset.alias_name,
					dataset.leaf_href,
					dataset.leaf_title,
					dataset.user_note1,
					dataset.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleDefine_DatasetSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeVariableSheet(XSSFWorkbook wb, DefineModel define) {
		logger.info("Generating " + config.defineVariableTableName + " Sheet");
		XSSFSheet sheet = wb.createSheet(config.defineVariableTableName);
		List<DefineVariableModel> variables = define.listSortedVariable();
		/* Create a header row */
		String[] header = { "Dataset Name", "Variable Name", "Is SUPP", "Repeat N", "Label", "No Data", "Non Standard", "Mandatory", "Key Sequence", "Sort Order", "DataType", "Length", "SignificantDigits", "SASFieldName", "DisplayFormat", "Codelist", "Origin", "Source", "Evaluator", "CRF ID", "CRF Page Type", "CRF Page Reference", "CRF First Page", "CRF Last Page", "CRF Page Title", "Has VLM", "MethodOID", "Derivation Type", "Predecessor/Derivation", "CommentOID", "Comment", "Language", "DocumentID", "Document Page Type", "Document Page Reference", "Document First Page", "Document Last Page", "Document Page Title", "Role", "Role Codelist", "FormalExpression Context", "FormalExpression Text", "Alias Context", "Alias Name", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(DefineVariableModel variable : variables) {
			DefineMethodModel method = define.get(new DefineMethodPk(variable.method_oid));
			DefineCommentModel comment = define.get(new DefineCommentPk(variable.comment_oid));
			row = sheet.createRow(row_num++);
			String[] data = { 
					variable.dataset_name,
					variable.variable_name,
					variable.is_supp.name(),
					(variable.repeat_n > 0 ? String.valueOf(variable.repeat_n) : ""),
					variable.variable_label,
					(variable.has_no_data == null ? "" : variable.has_no_data.name()),
					(variable.is_non_standard == null ? "" : variable.is_non_standard.name()),
					variable.mandatory.name(),
					variable.key_sequence,
					variable.sort_order,
					variable.data_type,
					variable.length,
					variable.significant_digits,
					variable.sas_field_name,
					variable.display_format,
					variable.codelist,
					variable.origin,
					variable.source,
					variable.evaluator,
					variable.crf_id,
					variable.crf_page_type,
					variable.crf_page_reference,
					variable.crf_first_page,
					variable.crf_last_page,
					variable.crf_page_title,
					variable.has_vlm.name(),
					(method != null && config.d2eSeparateSheet ? method.oid : ""),
					(method != null && !config.d2eSeparateSheet ? method.method_type : ""),
					(StringUtils.isEmpty(variable.predecessor) ? (method == null || config.d2eSeparateSheet ? "" : method.description) : variable.predecessor),
					(comment != null && config.d2eSeparateSheet ? comment.oid : ""),
					(comment == null || config.d2eSeparateSheet ? "" : comment.comment_text),
					(comment == null || config.d2eSeparateSheet ? "" : comment.comment_lang),
					(method != null && !config.d2eSeparateSheet ? method.getDocumentIdString(config.valueDelimiter) : (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentIdString(config.valueDelimiter))),
					(method != null && !config.d2eSeparateSheet ? method.getDocumentPageTypeString(config.valueDelimiter) : (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTypeString(config.valueDelimiter))),
					(method != null && !config.d2eSeparateSheet ? method.getDocumentPageRefString(config.valueDelimiter) : (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageRefString(config.valueDelimiter))),
					(method != null && !config.d2eSeparateSheet ? method.getDocumentFirstPageString(config.valueDelimiter) : (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentFirstPageString(config.valueDelimiter))),
					(method != null && !config.d2eSeparateSheet ? method.getDocumentLastPageString(config.valueDelimiter) : (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentLastPageString(config.valueDelimiter))),
					(method != null && !config.d2eSeparateSheet ? method.getDocumentPageTitleString(config.valueDelimiter) : (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTitleString(config.valueDelimiter))),
					variable.role,
					variable.role_codelist,
					(method == null ? "" : method.formal_expression_context),
					(method == null ? "" : method.formal_expression),
					variable.alias_context,
					variable.alias_name,
					variable.user_note1,
					variable.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleDefine_VariableSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeValueSheet(XSSFWorkbook wb, DefineModel define) {
		logger.info("Generating " + config.defineValueTableName + " Sheet");
		XSSFSheet sheet = wb.createSheet(config.defineValueTableName);
		List<DefineValueModel> values = define.listSortedValue();
		/* Create a header row */
		String[] header = { "Dataset Name", "Variable Name", "Value Name", "Value Key", "Label", "No Data", "Mandatory", "DataType", "Length", "SignificantDigits", "SASFieldName", "DisplayFormat", "Codelist", "Origin", "Source", "CRF ID", "CRF Page Type", "CRF Page Reference", "CRF First Page", "CRF Last Page", "CRF Page Title", "MethodOID", "Derivation Type", "Predecessor/Derivation", "CommentOID", "Comment", "Language", "DocumentID", "Document Page Type", "Document Page Reference", "Document First Page", "Document Last Page", "Document Page Title", "FormalExpression Context", "FormalExpression Text", "Alias Context", "Alias Name", "User Note 1", "User Note 2",
				"WhereClauseGroupID", "WhereClauseDataset", "WhereClauseVariable", "WhereClauseOperator", "WhereClauseValue", "WhereClause CommentOID", "WhereClause Comment", "WhereClause Language"};
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(DefineValueModel value : values) {
			DefineMethodModel method = define.get(new DefineMethodPk(value.method_oid));
			DefineCommentModel comment = define.get(new DefineCommentPk(value.comment_oid));
			List<DefineWCPk> where_clause_pks = (value.where_clause_pks.isEmpty() ? new ArrayList<>() : value.where_clause_pks);
			for (int i = 0; i == 0 || i < where_clause_pks.size(); i++) {	//Run at least once even if WhereClause is empty
				DefineWCPk where_clause_pk = (where_clause_pks.isEmpty() ? null : where_clause_pks.get(i));
				DefineWCModel wc = (where_clause_pk == null ? null : define.get(where_clause_pk));
				DefineCommentModel wc_comment = (wc == null ? null : define.get(new DefineCommentPk(wc.comment_oid)));
				List<WCCondition> wc_conditions = (wc == null || wc.wc_conditions.isEmpty() ? new ArrayList<>() : wc.wc_conditions);
				for (int j = 0; j == 0 || j < wc_conditions.size(); j++) {	//Run at least once even if WhereClause condition is empty
					WCCondition wc_condition = (wc_conditions.isEmpty() ? null : wc_conditions.get(j));
					row = sheet.createRow(row_num++);
					String[] data = {
							value.dataset_name,
							value.variable_name,
							value.value_name,
							value.value_key,
							value.value_label,
							(value.has_no_data == null ? "" : value.has_no_data.name()),
							value.mandatory.name(),
							value.data_type,
							value.length,
							value.significant_digits,
							value.sas_field_name,
							value.display_format,
							value.codelist,
							value.origin,
							value.source,
							value.crf_id,
							value.crf_page_type,
							value.crf_page_reference,
							value.crf_first_page,
							value.crf_last_page,
							value.crf_page_title,
							(method != null && config.d2eSeparateSheet ? method.oid : ""),
							(method != null && !config.d2eSeparateSheet ? method.method_type : ""),
							(StringUtils.isEmpty(value.predecessor) ? (method == null || config.d2eSeparateSheet ? "" : method.description) : value.predecessor),
							(comment != null && config.d2eSeparateSheet ? comment.oid : ""),
							(comment == null || config.d2eSeparateSheet ? "" : comment.comment_text),
							(comment == null || config.d2eSeparateSheet ? "" : comment.comment_lang),
							(method != null && !config.d2eSeparateSheet ? method.getDocumentIdString(config.valueDelimiter) : (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentIdString(config.valueDelimiter))),
							(method != null && !config.d2eSeparateSheet ? method.getDocumentPageTypeString(config.valueDelimiter) : (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTypeString(config.valueDelimiter))),
							(method != null && !config.d2eSeparateSheet ? method.getDocumentPageRefString(config.valueDelimiter) : (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageRefString(config.valueDelimiter))),
							(method != null && !config.d2eSeparateSheet ? method.getDocumentFirstPageString(config.valueDelimiter) : (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentFirstPageString(config.valueDelimiter))),
							(method != null && !config.d2eSeparateSheet ? method.getDocumentLastPageString(config.valueDelimiter) : (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentLastPageString(config.valueDelimiter))),
							(method != null && !config.d2eSeparateSheet ? method.getDocumentPageTitleString(config.valueDelimiter) : (comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTitleString(config.valueDelimiter))),
							(method != null && !config.d2eSeparateSheet ? method.formal_expression_context : ""),
							(method != null && !config.d2eSeparateSheet ? method.formal_expression : ""),
							value.alias_context,
							value.alias_name,
							value.user_note1,
							value.user_note2,
							(wc == null ? "" : wc.group_id),
							(wc_condition == null ? "" : wc_condition.dataset_name),
							(wc_condition == null ? "" : wc_condition.variable_name),
							(wc_condition == null ? "" : wc_condition.operator),
							(wc_condition == null ? "" : Utils.join(wc_condition.values, config.valueDelimiter)),
							(wc_comment != null && config.d2eSeparateSheet ? wc_comment.oid : ""),
							(wc_comment == null || config.d2eSeparateSheet ? "" : wc_comment.comment_text),
							(wc_comment == null || config.d2eSeparateSheet ? "" : wc_comment.comment_lang) };
					for (int k = 0; k < header.length; k++) {
						row.createCell(k).setCellValue(data[k]);
					}
				}
			}
		}
		excelStyle.setStyleDefine_ValueSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeResult1Sheet(XSSFWorkbook wb, DefineModel define) {
		logger.info("Generating " + config.defineResult1TableName + " Sheet");
		XSSFSheet sheet = wb.createSheet(config.defineResult1TableName);
		List<DefineARMDisplayModel> arm_displays = define.listSortedARMDisplay();
		/* Create a header row */
		String[] header = { "Display Name", "Display Description", "Display Language", "Leaf ID", "Leaf Page Type", "Leaf Page Reference", "Leaf First Page", "Leaf Last Page", "User Note 1", "User Note 2",
				"Result Key", "Result Description", "Result Language", "ParameterOID Dataset", "Analysis Reason", "Analysis Purpose", "Documentation ID", "Documentation Page Type", "Documentation Page Reference", "Documentation First Page", "Documentation Last Page", "Documentation Text", "Documentation Language", "Programming Code Context", "Programming Code Text", "Programming Code Document ID", "Programming Code Document Page Type", "Programming Code Document Page Reference", "Programming Code Document First Page", "Programming Code Document Last Page", "Datasets CommentOID", "Datasets Comment", "Datasets Language", "Datasets DocumentID", "Datasets Document Page Type", "Datasets Document Page Reference", "Datasets Document First Page", "Datasets Document Last Page"};
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(DefineARMDisplayModel arm_display : arm_displays) {
			List<DefineARMResultPk> arm_result_pks = (arm_display.arm_result_pks.isEmpty() ? new ArrayList<>() : arm_display.arm_result_pks);
			for (int i = 0; i == 0 || i < arm_result_pks.size(); i++) {
				DefineARMResultModel arm_result = (arm_result_pks.isEmpty() ? null : define.get(arm_result_pks.get(i)));
				DefineCommentModel dataset_comment = (arm_result == null ? null : define.get(new DefineCommentPk(arm_result.dataset_comment_oid)));
				row = sheet.createRow(row_num++);
				String[] data = { arm_display.display_name,
						arm_display.display_desc,
						arm_display.display_lang,
						arm_display.getDocumentIdString(config.valueDelimiter),
						arm_display.getDocumentPageTypeString(config.valueDelimiter),
						arm_display.getDocumentPageRefString(config.valueDelimiter),
						arm_display.getDocumentFirstPageString(config.valueDelimiter),
						arm_display.getDocumentLastPageString(config.valueDelimiter),
						arm_display.user_note1,
						arm_display.user_note2,
						(arm_result == null ? "" : arm_result.result_key),
						(arm_result == null ? "" : arm_result.result_desc),
						(arm_result == null ? "" : arm_result.result_lang),
						(arm_result == null ? "" : arm_result.param_dataset),
						(arm_result == null ? "" : arm_result.analysis_reason),
						(arm_result == null ? "" : arm_result.analysis_purpose),
						(arm_result == null ? "" : arm_result.getDocumentationIdString(config.valueDelimiter)),
						(arm_result == null ? "" : arm_result.getDocumentationPageTypeString(config.valueDelimiter)),
						(arm_result == null ? "" : arm_result.getDocumentationPageRefString(config.valueDelimiter)),
						(arm_result == null ? "" : arm_result.getDocumentationFirstPageString(config.valueDelimiter)),
						(arm_result == null ? "" : arm_result.getDocumentationLastPageString(config.valueDelimiter)),
						(arm_result == null ? "" : arm_result.docm_text),
						(arm_result == null ? "" : arm_result.docm_lang),
						(arm_result == null ? "" : arm_result.prog_code_context),
						(arm_result == null ? "" : arm_result.prog_code_text),
						(arm_result == null ? "" : arm_result.getCodeIdString(config.valueDelimiter)),
						(arm_result == null ? "" : arm_result.getCodePageTypeString(config.valueDelimiter)),
						(arm_result == null ? "" : arm_result.getCodePageRefString(config.valueDelimiter)),
						(arm_result == null ? "" : arm_result.getCodeFirstPageString(config.valueDelimiter)),
						(arm_result == null ? "" : arm_result.getCodeLastPageString(config.valueDelimiter)),
						(dataset_comment != null && config.d2eSeparateSheet ? dataset_comment.oid : ""),
						(dataset_comment == null || config.d2eSeparateSheet ? "" : dataset_comment.comment_text),
						(dataset_comment == null || config.d2eSeparateSheet ? "" : dataset_comment.comment_lang),
						(dataset_comment == null || config.d2eSeparateSheet ? "" : dataset_comment.getDocumentIdString(config.valueDelimiter)),
						(dataset_comment == null || config.d2eSeparateSheet ? "" : dataset_comment.getDocumentPageTypeString(config.valueDelimiter)),
						(dataset_comment == null || config.d2eSeparateSheet ? "" : dataset_comment.getDocumentPageRefString(config.valueDelimiter)),
						(dataset_comment == null || config.d2eSeparateSheet ? "" : dataset_comment.getDocumentFirstPageString(config.valueDelimiter)),
						(dataset_comment == null || config.d2eSeparateSheet ? "" : dataset_comment.getDocumentLastPageString(config.valueDelimiter))};
				for (int j = 0; j < header.length; j++) {
					row.createCell(j).setCellValue(data[j]);
				}
			}
		}
		excelStyle.setStyleDefine_Result1Sheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeResult2Sheet(XSSFWorkbook wb, DefineModel define) {
		logger.info("Generating " + config.defineResult2TableName + " Sheet");
		XSSFSheet sheet = wb.createSheet(config.defineResult2TableName);
		List<DefineARMDatasetModel> arm_datasets = define.listSortedARMDataset();
		/* Create a header row */
		String[] header = { "Display Name", "Result Key", "Dataset Name", "Analysis Variable", "User Note 1", "User Note 2",
				"WhereClauseDataset", "WhereClauseVariable", "WhereClauseOperator", "WhereClauseValue", "WhereClause CommentOID", "WhereClause Comment", "WhereClause Language"};
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(DefineARMDatasetModel arm_dataset : arm_datasets) {
			DefineWCPk where_clause_pk = (arm_dataset.where_clause_pk == null ? null : arm_dataset.where_clause_pk);
			DefineWCModel wc = (where_clause_pk == null ? null : define.get(where_clause_pk));
			DefineCommentModel wc_comment = (wc == null ? null : define.get(new DefineCommentPk(wc.comment_oid)));
			List<WCCondition> wc_conditions = (wc == null || wc.wc_conditions.isEmpty() ? new ArrayList<>() : wc.wc_conditions);
			for (int j = 0; j == 0 || j < wc_conditions.size(); j++) {	//Run at least once even if WhereClause condition is empty
				WCCondition wc_condition = (wc_conditions.isEmpty() ? null : wc_conditions.get(j));
				row = sheet.createRow(row_num++);
				String[] data = { arm_dataset.display_name,
						arm_dataset.result_key,
						arm_dataset.dataset_name,
						Utils.join(arm_dataset.analysis_variables, config.valueDelimiter),
						arm_dataset.user_note1,
						arm_dataset.user_note2,
						(wc_condition == null ? "" : wc_condition.dataset_name),
						(wc_condition == null ? "" : wc_condition.variable_name),
						(wc_condition == null ? "" : wc_condition.operator),
						(wc_condition == null ? "" : Utils.join(wc_condition.values, config.valueDelimiter)),
						(wc_comment != null && config.d2eSeparateSheet ? wc_comment.oid : ""),
						(wc_comment == null || config.d2eSeparateSheet ? "" : wc_comment.comment_text),
						(wc_comment == null || config.d2eSeparateSheet ? "" : wc_comment.comment_lang) };
				for (int k = 0; k < header.length; k++) {
					row.createCell(k).setCellValue(data[k]);
				}
			}
		}
		excelStyle.setStyleDefine_Result2Sheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeDictionarySheet(XSSFWorkbook wb, DefineModel define) {
		logger.info("Generating " + config.defineDictionaryTableName + " Sheet");
		XSSFSheet sheet = wb.createSheet(config.defineDictionaryTableName);
		List<DefineDictionaryModel> dictionaries = define.listSortedDictionary();
		/* Create a header row */
		String[] header = { "Dictionary ID", "Name", "DataType", "Version", "ref", "href", "CommentOID", "Comment", "Language", "DocumentID", "Document Page Type", "Document Page Reference", "Document First Page", "Document Last Page", "Document Page Title", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(DefineDictionaryModel dictionary : dictionaries) {
			DefineCommentModel comment = define.get(new DefineCommentPk(dictionary.comment_oid));
			row = sheet.createRow(row_num++);
			String[] data = { dictionary.dictionary_id,
					dictionary.dictionary_name,
					dictionary.data_type,
					dictionary.dictionary_version,
					dictionary.dictionary_ref,
					dictionary.dictionary_href,
					(comment != null && config.d2eSeparateSheet ? comment.oid : ""),
					(comment == null || config.d2eSeparateSheet ? "" : comment.comment_text),
					(comment == null || config.d2eSeparateSheet ? "" : comment.comment_lang),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentIdString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTypeString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageRefString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentFirstPageString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentLastPageString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTitleString(config.valueDelimiter)),
					dictionary.user_note1,
					dictionary.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleDefine_DictionarySheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeCodelistSheet(XSSFWorkbook wb, DefineModel define) {
		logger.info("Generating " + config.defineCodelistTableName + " Sheet");
		XSSFSheet sheet = wb.createSheet(config.defineCodelistTableName);
		List<DefineCodelistModel> codelists = define.listSortedCodelist();
		/* Create a header row */
		String[] header = { "Codelist ID", "Codelist Code", "Codelist Label", "DataType", "SASFormatName", "Standard", "CommentOID", "Comment", "Language", "DocumentID", "Document Page Type", "Document Page Reference", "Document First Page", "Document Last Page", "Document Page Title", "Code", "User Code", "Order Number", "Rank", "ExtendedValue", "Submission Value", "Decode", "Decode Language", "Alias Context", "Alias Name", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(DefineCodelistModel codelist : codelists) {
			DefineCommentModel comment = define.get(new DefineCommentPk(codelist.comment_oid));
			DefineStandardModel standard = define.get(new DefineStandardPk(codelist.standard_oid));
			row = sheet.createRow(row_num++);
			String[] data = { codelist.codelist_id, //
					codelist.codelist_code, //
					codelist.codelist_label, //
					codelist.data_type, //
					codelist.sas_format_name, //
					(standard == null ? "" : standard.name(StandardType.CT)),
					(comment != null && config.d2eSeparateSheet ? comment.oid : ""),
					(comment == null || config.d2eSeparateSheet ? "" : comment.comment_text),
					(comment == null || config.d2eSeparateSheet ? "" : comment.comment_lang),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentIdString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTypeString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageRefString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentFirstPageString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentLastPageString(config.valueDelimiter)),
					(comment == null || config.d2eSeparateSheet ? "" : comment.getDocumentPageTitleString(config.valueDelimiter)),
					codelist.code, //
					"",
					(codelist.order_number > 0 ? codelist.order_number.toString() : ""), //
					(codelist.rank > 0 ? codelist.rank.toString() : ""), //
					(codelist.extended_value == null ? "" : codelist.extended_value.name()), //
					codelist.submission_value, //
					codelist.decode, //
					codelist.xml_lang, //
					codelist.alias_context, //
					codelist.alias_name, //
					codelist.user_note1, //
					codelist.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleDefine_CodelistSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeMethodSheet(XSSFWorkbook wb, DefineModel define) {
		logger.info("Generating " + config.defineMethodTableName + " Sheet");
		XSSFSheet sheet = wb.createSheet(config.defineMethodTableName);
		List<DefineMethodModel> methods = define.listSortedMethod();
		/* Create a header row */
		String[] header = { "OID", "Name", "Type", "Description", "Language", "DocumentID", "Document Page Type", "Document Page Reference", "Document First Page", "Document Last Page", "Document Page Title", "FormalExpression Context", "FormalExpression Text", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(DefineMethodModel method : methods) {
			row = sheet.createRow(row_num++);
			String[] data = { method.oid,
					method.method_name,
					method.method_type,
					method.description,
					method.description_lang,
					method.getDocumentIdString(config.valueDelimiter),
					method.getDocumentPageTypeString(config.valueDelimiter),
					method.getDocumentPageRefString(config.valueDelimiter),
					method.getDocumentFirstPageString(config.valueDelimiter),
					method.getDocumentLastPageString(config.valueDelimiter),
					method.getDocumentPageTitleString(config.valueDelimiter),
					method.formal_expression_context,
					method.formal_expression,
					method.user_note1,
					method.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleDefine_MethodSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}

	public void writeCommentSheet(XSSFWorkbook wb, DefineModel define) {
		logger.info("Generating " + config.defineCommentTableName + " Sheet");
		XSSFSheet sheet = wb.createSheet(config.defineCommentTableName);
		List<DefineCommentModel> comments = define.listSortedComment();
		/* Create a header row */
		String[] header = { "OID", "Comment", "Language", "DocumentID", "Document Page Type", "Document Page Reference", "Document First Page", "Document Last Page", "Document Page Title", "User Note 1", "User Note 2" };
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
		/* Create data rows */
		int row_num = 1;
		for(DefineCommentModel comment : comments) {
			row = sheet.createRow(row_num++);
			String[] data = { comment.oid,
					comment.comment_text,
					comment.comment_lang,
					comment.getDocumentIdString(config.valueDelimiter),
					comment.getDocumentPageTypeString(config.valueDelimiter),
					comment.getDocumentPageRefString(config.valueDelimiter),
					comment.getDocumentFirstPageString(config.valueDelimiter),
					comment.getDocumentLastPageString(config.valueDelimiter),
					comment.getDocumentPageTitleString(config.valueDelimiter),
					comment.user_note1,
					comment.user_note2 };
			for (int i = 0; i < header.length; i++) {
				row.createCell(i).setCellValue(data[i]);
			}
		}
		excelStyle.setStyleDefine_CommentSheet(sheet);
		excelStyle.setColumnWidth(sheet);
	}
}
