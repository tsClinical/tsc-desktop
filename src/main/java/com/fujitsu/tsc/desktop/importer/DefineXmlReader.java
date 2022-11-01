/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.fujitsu.tsc.desktop.importer.models.DefineARMDatasetModel;
import com.fujitsu.tsc.desktop.importer.models.DefineARMDatasetModel.DefineARMDatasetPk;
import com.fujitsu.tsc.desktop.importer.models.DefineARMDisplayModel;
import com.fujitsu.tsc.desktop.importer.models.DefineARMDisplayModel.DefineARMDisplayPk;
import com.fujitsu.tsc.desktop.importer.models.DefineARMResultModel;
import com.fujitsu.tsc.desktop.importer.models.DefineARMResultModel.DefineARMResultPk;
import com.fujitsu.tsc.desktop.importer.models.DefineCodelistModel;
import com.fujitsu.tsc.desktop.importer.models.DefineCodelistModel.DefineCodelistPk;
import com.fujitsu.tsc.desktop.importer.models.DefineCommentModel;
import com.fujitsu.tsc.desktop.importer.models.DefineCommentModel.DefineCommentPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDatasetModel;
import com.fujitsu.tsc.desktop.importer.models.DefineDatasetModel.DefineDatasetPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDictionaryModel;
import com.fujitsu.tsc.desktop.importer.models.DefineDictionaryModel.DefineDictionaryPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDocumentModel;
import com.fujitsu.tsc.desktop.importer.models.DefineDocumentModel.DefineDocumentPk;
import com.fujitsu.tsc.desktop.importer.models.DefineMethodModel;
import com.fujitsu.tsc.desktop.importer.models.DefineMethodModel.DefineMethodPk;
import com.fujitsu.tsc.desktop.importer.models.DefineModel;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.DocType;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.DocumentRef;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorN;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorNull;
import com.fujitsu.tsc.desktop.importer.models.DefineStandardModel;
import com.fujitsu.tsc.desktop.importer.models.DefineStandardModel.DefineStandardPk;
import com.fujitsu.tsc.desktop.importer.models.DefineStudyModel;
import com.fujitsu.tsc.desktop.importer.models.DefineValueModel;
import com.fujitsu.tsc.desktop.importer.models.DefineValueModel.DefineValuePk;
import com.fujitsu.tsc.desktop.importer.models.DefineVariableModel;
import com.fujitsu.tsc.desktop.importer.models.DefineVariableModel.DefineVariablePk;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.DefineWCPk;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.WCCondition;
import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.ErrorInfo;
import com.fujitsu.tsc.desktop.util.TscStack;

/**
 * This class binds an ODM xml file to the {@link OdmModel} object.
 */
public class DefineXmlReader extends DefaultHandler {
	
    private static Logger logger = Logger.getLogger("com.fujitsu.tsc.desktop");
	private static String MESSAGE_ORPHANED = "An orphaned element found.";
	private Config config;
	private Locator locator;
	private List<ErrorInfo> errors = new ArrayList<>();
	private TscStack path_stack = new TscStack();
	private DefineModel define = new DefineModel();
	
	/*
	 *  Variables in this section are used to tell where in the ODM xml file the SAX parser is currently parsing.
	 */
	/* Document */
	private String cached_document_id = "";	//Leaf ID
	private int cached_document_ordinal = 1;
	/* Dataset */
	private String cached_itemgroup_name = "";	//Dataset Name
	private int cached_itemgroup_ordinal = 1;
	/* Variable */
	private int cached_itemref_ordinal = 1;
	private String cached_itemdef_oid = "";
	/* Value */
	private String cached_valuelist_oid = "";
	private String cached_itemref_oid = "";
	private String cached_wc_oid = "";
	private WCCondition cached_wc_condition = null;
	/* Codelist */
	private int cached_codelist_ordinal = 1;
	private String cached_codelist_oid = "";
	private String cached_codelist_name = "";
	private String cached_codelist_datatype = "";
	private String cached_codelist_sasformat = "";
	private String cached_codelist_standard_oid = "";
	private String cached_codelist_comment_oid = "";
	private DefineCodelistModel cached_codelist = null;
	/* ARM Display */
	private int cached_arm_display_ordinal = 1;
	private DefineARMDisplayModel cached_arm_display = null;
	private int cached_arm_result_ordinal = 1;
	private DocumentRef cached_display_doc = null;
	private DefineARMResultModel cached_arm_result = null;
	/* ARM Dataset */
	private DefineARMDatasetModel cached_arm_dataset = null;
	private int cached_arm_dataset_ordinal = 1;
	private DocumentRef cached_docm_doc = null;
	private DocumentRef cached_prog_code_doc = null;
	/* Method */
	private String cached_method_id = "";
	private DocumentRef cached_method_doc = null;
	/* Comment */
	private String cached_comment_id = "";
	private DocumentRef cached_comment_doc = null;
	
	/**
	 * Constructor
	 * @param config An DefineModel object to which the Define-XML file is bound. Cannot be null.
	 */
	public DefineXmlReader(Config config){
		logger = Logger.getLogger("com.fujitsu.tsc.desktop");
		this.config = config;
	}

	public void startDocument() {
	}
	
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) {
		path_stack.push(qName);
//		logger.info("Entering into " + path_stack.getPath());
		bindAttributes(path_stack.getPath(), attr);
	}

	private void bindAttributes(String path, Attributes attr) {
		/* *** DefineStudyModel *** */
		if ("/ODM/".equals(path)) {
			DefineStudyModel study = new DefineStudyModel();
			study.file_oid = attr.getValue("FileOID");
			study.odm_version = attr.getValue("ODMVersion");
			study.file_type = attr.getValue("FileType");
			study.as_of_date_time = attr.getValue("AsOfDateTime");
			study.originator = attr.getValue("Originator");
			study.context = attr.getValue("def:Context");
			define.put(study);
		} else if ("/ODM/Study/".equals(path)) {
			DefineStudyModel study = define.getStudy();
			if (study != null) {
				study.study_oid = attr.getValue("OID");
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if ("/ODM/Study/MetaDataVersion/".equals(path)) {
			DefineStudyModel study = define.getStudy();
			if (study != null) {
				study.metadata_oid = attr.getValue("OID");
				study.metadata_name = attr.getValue("Name");
				study.metadata_description = attr.getValue("Description");
				study.define_version = attr.getValue("def:DefineVersion");
				study.comment_oid = attr.getValue("def:CommentOID");
				study.standard_name = attr.getValue("def:StandardName");
				study.standard_version = attr.getValue("def:StandardVersion");
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		/* *** DefineStandardModel *** */
		} else if ("/ODM/Study/MetaDataVersion/def:Standards/def:Standard/".equals(path)) {
			DefineStandardPk key = new DefineStandardPk(attr.getValue("OID"));
			DefineStandardModel standard = new DefineStandardModel(key);
			standard.standard_name = attr.getValue("Name");
			standard.standard_type = DefineStandardModel.StandardType.parse(attr.getValue("Type"));
			standard.publishing_set = attr.getValue("PublishingSet");
			standard.standard_version = attr.getValue("Version");
			standard.standard_status = attr.getValue("Status");
			standard.comment_oid = attr.getValue("def:CommentOID");
			define.put(key, standard);
		/* *** DefineDocumentModel *** */
		} else if ("/ODM/Study/MetaDataVersion/def:AnnotatedCRF/def:DocumentRef/".equals(path)) {
			DefineDocumentPk key = new DefineDocumentPk(attr.getValue("leafID"));
			DefineDocumentModel document = new DefineDocumentModel(key);
			document.document_type = DocType.AnnotatedCRF;
			define.put(key, document);
		} else if ("/ODM/Study/MetaDataVersion/def:SupplementalDoc/def:DocumentRef/".equals(path)) {
			DefineDocumentPk key = new DefineDocumentPk(attr.getValue("leafID"));
			DefineDocumentModel document = new DefineDocumentModel(key);
			document.document_type = DocType.SupplementalDoc;
			define.put(key, document);
		} else if ("/ODM/Study/MetaDataVersion/def:leaf/".equals(path)) {
			DefineDocumentPk key = new DefineDocumentPk(attr.getValue("ID"));
			DefineDocumentModel document = define.get(key);
			if (document == null) {
				document = new DefineDocumentModel(key);
				define.put(key, document);
				document.document_type = DocType.Other;
			}
			document.ordinal = this.cached_document_ordinal++;
			document.document_href = attr.getValue("xlink:href");
			this.cached_document_id = document.document_id;
		/* *** DefineDatasetModel *** */
		} else if ("/ODM/Study/MetaDataVersion/ItemGroupDef/".equals(path)) {
			this.cached_itemgroup_name = attr.getValue("Name");
			DefineDatasetPk key = new DefineDatasetPk(this.cached_itemgroup_name);
			DefineDatasetModel dataset = new DefineDatasetModel(key);
			dataset.ordinal = this.cached_itemgroup_ordinal++;
			dataset.domain = attr.getValue("Domain");
			dataset.dataset_oid = attr.getValue("OID");
			dataset.has_no_data = DefineModel.YorNull.parse(attr.getValue("def:HasNoData"));
			dataset.repeating = YorN.parse(attr.getValue("Repeating"));
			dataset.is_reference_data = YorN.parse(attr.getValue("IsReferenceData"));
			dataset.purpose = attr.getValue("Purpose");
			dataset.sas_dataset_name = attr.getValue("SASDatasetName");
			dataset.standard_oid = attr.getValue("def:StandardOID");
			dataset.structure = attr.getValue("def:Structure");
			dataset.dataset_class = attr.getValue("def:Class");	//Define-XML 2.0.0
			dataset.comment_oid = attr.getValue("def:CommentOID");
			define.put(key, dataset);
		} else if ("/ODM/Study/MetaDataVersion/ItemGroupDef/ItemRef/".equals(path)) {
			DefineDatasetPk dkey = new DefineDatasetPk(this.cached_itemgroup_name);
			DefineDatasetModel dataset = define.get(dkey);
			if (dataset != null) {
				DefineVariablePk key = new DefineVariablePk(this.cached_itemgroup_name, attr.getValue("ItemOID"));
				DefineVariableModel variable = new DefineVariableModel(key);
				try {
					variable.ordinal = Integer.parseInt(attr.getValue("OrderNumber"));
				} catch (Exception ex) {
					variable.ordinal = this.cached_itemref_ordinal++;
				}
				variable.mandatory = YorN.parse(attr.getValue("Mandatory"));
				variable.key_sequence = attr.getValue("KeySequence");
				variable.role = attr.getValue("Role");
				variable.role_codelist = attr.getValue("RoleCodeListOID");
				variable.method_oid = attr.getValue("MethodOID");
				variable.has_no_data = DefineModel.YorNull.parse(attr.getValue("def:HasNoData"));
				define.put(key, variable);
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemGroupDef/Alias/".equals(path)) {
			DefineDatasetPk key = new DefineDatasetPk(this.cached_itemgroup_name);
			DefineDatasetModel dataset = define.get(key);
			if (dataset != null) {
				dataset.alias_name = attr.getValue("Name");
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemGroupDef/def:leaf/".equals(path)) {
			DefineDatasetPk key = new DefineDatasetPk(this.cached_itemgroup_name);
			DefineDatasetModel dataset = define.get(key);
			if (dataset != null) {
				dataset.leaf_href = attr.getValue("xlink:href");
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemGroupDef/def:Class/".equals(path)) {
			DefineDatasetPk key = new DefineDatasetPk(this.cached_itemgroup_name);
			DefineDatasetModel dataset = define.get(key);
			if (dataset != null) {
				dataset.dataset_class = attr.getValue("Name");	//Define-XML 2.1.n			
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemGroupDef/def:Class/def:SubClass/".equals(path)) {
			DefineDatasetPk key = new DefineDatasetPk(this.cached_itemgroup_name);
			DefineDatasetModel dataset = define.get(key);
			if (dataset != null) {
				dataset.dataset_subclass = attr.getValue("Name");
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		/* *** DefineVariableModel/DefineValueModel *** */
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/".equals(path)) {
			this.cached_itemdef_oid = attr.getValue("OID");
			List<DefineVariableModel> variables = define.getVariableByOid(this.cached_itemdef_oid);
			if (!variables.isEmpty()) {	//VARIABLE
				for (DefineVariableModel variable : variables) {
					variable.variable_name = attr.getValue("Name");
					variable.data_type = attr.getValue("DataType");
					variable.length = attr.getValue("Length");
					variable.significant_digits = attr.getValue("SignificantDigits");
					variable.sas_field_name = attr.getValue("SASFieldName");
					variable.display_format = attr.getValue("def:DisplayFormat");
					variable.comment_oid = attr.getValue("def:CommentOID");
				}
			} else {
				DefineValueModel value = define.get(new DefineValuePk(this.cached_itemdef_oid));
				if (value != null) {	//VALUE
					value.value_name = attr.getValue("Name");
					value.data_type = attr.getValue("DataType");
					value.length = attr.getValue("Length");
					value.significant_digits = attr.getValue("SignificantDigits");
					value.sas_field_name = attr.getValue("SASFieldName");
					value.display_format = attr.getValue("def:DisplayFormat");
					value.comment_oid = attr.getValue("def:CommentOID");
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/CodeListRef/".equals(path)) {
			List<DefineVariableModel> variables = define.getVariableByOid(this.cached_itemdef_oid);
			if (!variables.isEmpty()) {	//VARIABLE
				for (DefineVariableModel variable : variables) {
					variable.codelist = attr.getValue("CodeListOID");
				}
			} else {
				DefineValueModel value = define.get(new DefineValuePk(this.cached_itemdef_oid));
				if (value != null) {	//VALUE
					value.codelist = attr.getValue("CodeListOID");
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/def:Origin/".equals(path)) {
			List<DefineVariableModel> variables = define.getVariableByOid(this.cached_itemdef_oid);
			if (!variables.isEmpty()) {	//VARIABLE
				for (DefineVariableModel variable : variables) {
					variable.origin = attr.getValue("Type");
					variable.source = attr.getValue("Source");
				}
			} else {
				DefineValueModel value = define.get(new DefineValuePk(this.cached_itemdef_oid));
				if (value != null) {	//VALUE
					value.origin = attr.getValue("Type");
					value.source = attr.getValue("Source");
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/Alias/".equals(path)) {
			List<DefineVariableModel> variables = define.getVariableByOid(this.cached_itemdef_oid);
			if (!variables.isEmpty()) {	//VARIABLE
				for (DefineVariableModel variable : variables) {
					variable.alias_context = attr.getValue("Context");
					variable.alias_name = attr.getValue("Name");
				}
			} else {
				DefineValueModel value = define.get(new DefineValuePk(this.cached_itemdef_oid));
				if (value != null) {	//VALUE
					value.alias_context = attr.getValue("Context");
					value.alias_name = attr.getValue("Name");
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/def:Origin/def:DocumentRef/".equals(path)) {
			List<DefineVariableModel> variables = define.getVariableByOid(this.cached_itemdef_oid);
			if (!variables.isEmpty()) {	//VARIABLE
				for (DefineVariableModel variable : variables) {
					variable.crf_id = attr.getValue("leafID");
				}
			} else {
				DefineValueModel value = define.get(new DefineValuePk(this.cached_itemdef_oid));
				if (value != null) {	//VALUE
					value.crf_id = attr.getValue("leafID");
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/def:Origin/def:DocumentRef/def:PDFPageRef/".equals(path)) {
			List<DefineVariableModel> variables = define.getVariableByOid(this.cached_itemdef_oid);
			if (!variables.isEmpty()) {
				for (DefineVariableModel variable : variables) {
					variable.crf_page_type = attr.getValue("Type");
					variable.crf_page_reference = attr.getValue("PageRefs");
					variable.crf_first_page = attr.getValue("FirstPage");
					variable.crf_last_page = attr.getValue("LastPage");
					variable.crf_page_title = attr.getValue("Title");
				}
			} else {
				DefineValueModel value = define.get(new DefineValuePk(this.cached_itemdef_oid));
				if (value != null) {	//VALUE
					value.crf_page_type = attr.getValue("Type");
					value.crf_page_reference = attr.getValue("PageRefs");
					value.crf_first_page = attr.getValue("FirstPage");
					value.crf_last_page = attr.getValue("LastPage");
					value.crf_page_title = attr.getValue("Title");
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/def:ValueListRef/".equals(path)) {
			List<DefineVariableModel> variables = define.getVariableByOid(this.cached_itemdef_oid);
			if (!variables.isEmpty()) {
				for (DefineVariableModel variable : variables) {
					variable.valuelist_oid = attr.getValue("ValueListOID");
				}
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if ("/ODM/Study/MetaDataVersion/def:ValueListDef/".equals(path)) {
			this.cached_valuelist_oid = attr.getValue("OID");
			this.cached_itemref_ordinal = 1;	//Reset
		} else if ("/ODM/Study/MetaDataVersion/def:ValueListDef/ItemRef/".equals(path)) {
			this.cached_itemref_oid = attr.getValue("ItemOID");
			DefineValuePk key = new DefineValuePk(this.cached_itemref_oid);
			DefineValueModel value = new DefineValueModel(key);
			value.valuelist_oid = this.cached_valuelist_oid;
			try {
				value.ordinal = Integer.parseInt(attr.getValue("OrderNumber"));
			} catch (Exception ex) {
				value.ordinal = this.cached_itemref_ordinal++;
			}
			value.mandatory = YorN.parse(attr.getValue("Mandatory"));
			value.key_sequence = attr.getValue("KeySequence");
			value.method_oid = attr.getValue("MethodOID");
			value.has_no_data = DefineModel.YorNull.parse(attr.getValue("def:HasNoData"));
			define.put(key, value);
		} else if ("/ODM/Study/MetaDataVersion/def:ValueListDef/ItemRef/def:WhereClauseRef/".equals(path)) {
			DefineValuePk vkey = new DefineValuePk(this.cached_itemref_oid);
			DefineValueModel value = define.get(vkey);
			DefineWCPk wc_key = new DefineWCPk(attr.getValue("WhereClauseOID"));
			value.where_clause_pks.add(wc_key);
		} else if ("/ODM/Study/MetaDataVersion/def:WhereClauseDef/".equals(path)) {
			this.cached_wc_oid = attr.getValue("OID");
			DefineWCPk key = new DefineWCPk(this.cached_wc_oid);
			DefineWCModel wc = new DefineWCModel(key);
			wc.comment_oid = attr.getValue("def:CommentOID");
			define.put(key, wc);
		} else if ("/ODM/Study/MetaDataVersion/def:WhereClauseDef/RangeCheck/".equals(path)) {
			DefineWCPk key = new DefineWCPk(this.cached_wc_oid);
			DefineWCModel wc = define.get(key);
			if (wc != null) {
				this.cached_wc_condition = new WCCondition();
				this.cached_wc_condition.variable_oid = attr.getValue("def:ItemOID");
				this.cached_wc_condition.operator = attr.getValue("Comparator");
				wc.wc_conditions.add(this.cached_wc_condition);
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		/* *** DefineDictionaryModel/DefineCodelistModel *** */
		} else if ("/ODM/Study/MetaDataVersion/CodeList/".equals(path)) {
			this.cached_codelist_oid = attr.getValue("OID");
			this.cached_codelist_ordinal = 1; //reset
			this.cached_codelist_name = attr.getValue("Name");
			this.cached_codelist_datatype = attr.getValue("DataType");
			this.cached_codelist_sasformat = attr.getValue("SASFormatName");
			this.cached_codelist_comment_oid = attr.getValue("def:CommentOID");
			this.cached_codelist_standard_oid = attr.getValue("def:StandardOID");
		} else if ("/ODM/Study/MetaDataVersion/CodeList/ExternalCodeList/".equals(path)) {
			DefineDictionaryPk key = new DefineDictionaryPk(this.cached_codelist_oid);
			DefineDictionaryModel dictionary = new DefineDictionaryModel(key);
			dictionary.dictionary_id = this.cached_codelist_oid;
			dictionary.dictionary_name = attr.getValue("Dictionary");
			dictionary.data_type = this.cached_codelist_datatype;
			dictionary.dictionary_version = attr.getValue("Version");
			dictionary.dictionary_ref = attr.getValue("ref");
			dictionary.dictionary_href = attr.getValue("href");
			dictionary.comment_oid = this.cached_codelist_comment_oid;
			define.put(key, dictionary);
		} else if("/ODM/Study/MetaDataVersion/CodeList/CodeListItem/".equals(path)) {
			DefineCodelistPk key = new DefineCodelistPk(this.cached_codelist_oid, attr.getValue("CodedValue"));
			DefineCodelistModel codelist = new DefineCodelistModel(key);
			codelist.codelist_label = this.cached_codelist_name;
			codelist.data_type = this.cached_codelist_datatype;
			codelist.sas_format_name = this.cached_codelist_sasformat;
			codelist.comment_oid = this.cached_codelist_comment_oid;
			codelist.standard_oid = this.cached_codelist_standard_oid;
			try {
				if (!StringUtils.isEmpty(attr.getValue("Rank"))) {
					codelist.rank = Integer.parseInt(attr.getValue("Rank"));
				}
				if (!StringUtils.isEmpty(attr.getValue("OrderNumber"))) {
					codelist.order_number = Integer.parseInt(attr.getValue("OrderNumber"));
					codelist.ordinal = codelist.order_number;
				}
			} catch (Exception ex) {
				codelist.ordinal = this.cached_codelist_ordinal++;
				DefineImportError error = new DefineImportError(new DefineCodelistPk(codelist.codelist_id, codelist.submission_value));
				error.putMessage("Error parsing Codelist Rank/OrderNumber attributes.");
				logger.warn(error.toString());
			}
			codelist.extended_value = YorNull.parse(attr.getValue("def:ExtendedValue"));
			define.put(key, codelist);
			this.cached_codelist = codelist;
		} else if("/ODM/Study/MetaDataVersion/CodeList/CodeListItem/Decode/TranslatedText/".equals(path)) {
			this.cached_codelist.xml_lang = attr.getValue("xml:lang");
		} else if("/ODM/Study/MetaDataVersion/CodeList/CodeListItem/Alias/".equals(path)) {
			String alias_context = attr.getValue("Context");
			if ("nci:ExtCodeID".equals(alias_context)) {
				this.cached_codelist.code = attr.getValue("Name");
			} else {
				this.cached_codelist.alias_context = alias_context;
				this.cached_codelist.alias_name = attr.getValue("Name");
			}
		} else if("/ODM/Study/MetaDataVersion/CodeList/EnumeratedItem/".equals(path)) {
			DefineCodelistPk key = new DefineCodelistPk(this.cached_codelist_oid, attr.getValue("CodedValue"));
			DefineCodelistModel codelist = new DefineCodelistModel(key);
			codelist.codelist_label = this.cached_codelist_name;
			codelist.data_type = this.cached_codelist_datatype;
			codelist.sas_format_name = this.cached_codelist_sasformat;
			try {
				if (!StringUtils.isEmpty(attr.getValue("Rank"))) {
					codelist.rank = Integer.parseInt(attr.getValue("Rank"));
				}
				if (!StringUtils.isEmpty(attr.getValue("OrderNumber"))) {
					codelist.order_number = Integer.parseInt(attr.getValue("OrderNumber"));
					codelist.ordinal = codelist.order_number;
				}
			} catch (Exception ex) {
				codelist.ordinal = this.cached_codelist_ordinal++;
				DefineImportError error = new DefineImportError(new DefineCodelistPk(codelist.codelist_id, codelist.submission_value));
				error.putMessage("Error parsing Codelist Rank/OrderNumber attributes.");
				logger.warn(error.toString());
			}
			codelist.extended_value = YorNull.parse(attr.getValue("def:ExtendedValue"));
			define.put(key, codelist);
			this.cached_codelist = codelist;
		} else if("/ODM/Study/MetaDataVersion/CodeList/EnumeratedItem/Alias/".equals(path)) {
			this.cached_codelist.code = attr.getValue("Name");
		} else if("/ODM/Study/MetaDataVersion/CodeList/Alias/".equals(path)) {
			List<DefineCodelistModel> codelists = define.getCodelistByCodelistId(this.cached_codelist_oid);
			for (DefineCodelistModel codelist : codelists) {
				codelist.codelist_code = attr.getValue("Name");
			}
		// *** Method
		} else if ("/ODM/Study/MetaDataVersion/MethodDef/".equals(path)) {
			this.cached_method_id = attr.getValue("OID");
			DefineMethodPk key = new DefineMethodPk(this.cached_method_id);
			DefineMethodModel method = new DefineMethodModel(key);
			method.method_name = attr.getValue("Name");
			method.method_type = attr.getValue("Type");
			define.put(key, method);
		} else if ("/ODM/Study/MetaDataVersion/MethodDef/Description/TranslatedText/".equals(path)) {
			DefineMethodPk key = new DefineMethodPk(this.cached_method_id);
			DefineMethodModel method = define.get(key);
			if (method != null) {
				method.description_lang = attr.getValue("xml:lang");
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if ("/ODM/Study/MetaDataVersion/MethodDef/FormalExpression/".equals(path)) {
			DefineMethodPk key = new DefineMethodPk(this.cached_method_id);
			DefineMethodModel method = define.get(key);
			if (method != null) {
				method.formal_expression_context = attr.getValue("Context");
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if ("/ODM/Study/MetaDataVersion/MethodDef/def:DocumentRef/".equals(path)) {
			DefineMethodPk key = new DefineMethodPk(this.cached_method_id);
			DefineMethodModel method = define.get(key);
			String document_id = attr.getValue("leafID");
			if (method != null && StringUtils.isNotEmpty(document_id)) {
				this.cached_method_doc = new DocumentRef(document_id);
				method.document_refs.add(this.cached_method_doc); 
			} else {
				this.cached_method_doc = null;
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if ("/ODM/Study/MetaDataVersion/MethodDef/def:DocumentRef/def:PDFPageRef/".equals(path)) {
			if (this.cached_method_doc != null) {
				this.cached_method_doc.document_page_type = attr.getValue("Type");
				this.cached_method_doc.document_page_reference = attr.getValue("PageRefs");
				this.cached_method_doc.document_first_page = attr.getValue("FirstPage");
				this.cached_method_doc.document_last_page = attr.getValue("LastPage");
				this.cached_method_doc.document_page_title = attr.getValue("Title");
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		// *** Comment
		} else if ("/ODM/Study/MetaDataVersion/def:CommentDef/".equals(path)) {
			this.cached_comment_id = attr.getValue("OID");
			DefineCommentPk key = new DefineCommentPk(this.cached_comment_id);
			DefineCommentModel comment = new DefineCommentModel(key);
			define.put(key, comment);
		} else if ("/ODM/Study/MetaDataVersion/def:CommentDef/Description/TranslatedText/".equals(path)) {
			DefineCommentPk key = new DefineCommentPk(this.cached_comment_id);
			DefineCommentModel comment = define.get(key);
			if (comment != null) {
				comment.comment_lang = attr.getValue("xml:lang");
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if ("/ODM/Study/MetaDataVersion/def:CommentDef/def:DocumentRef/".equals(path)) {
			DefineCommentPk key = new DefineCommentPk(this.cached_comment_id);
			DefineCommentModel comment = define.get(key);
			String document_id = attr.getValue("leafID");
			if (comment != null && StringUtils.isNotEmpty(document_id)) {
				this.cached_comment_doc = new DocumentRef(document_id);
				comment.document_refs.add(this.cached_comment_doc); 
			} else {
				this.cached_comment_doc = null;
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if ("/ODM/Study/MetaDataVersion/def:CommentDef/def:DocumentRef/def:PDFPageRef/".equals(path)) {
			if (this.cached_comment_doc != null) {
				this.cached_comment_doc.document_page_type = attr.getValue("Type");
				this.cached_comment_doc.document_page_reference = attr.getValue("PageRefs");
				this.cached_comment_doc.document_first_page = attr.getValue("FirstPage");
				this.cached_comment_doc.document_last_page = attr.getValue("LastPage");
				this.cached_comment_doc.document_page_title = attr.getValue("Title");
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		// *** ARM Display
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/".equals(path)) {
			DefineARMDisplayPk key = new DefineARMDisplayPk(attr.getValue("Name"));
			this.cached_arm_display = new DefineARMDisplayModel(key);
			this.cached_arm_display.ordinal = this.cached_arm_display_ordinal++;
			this.cached_arm_result_ordinal = 1;	//reset
			define.put(key, this.cached_arm_display);
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/Description/TranslatedText/".equals(path)) {
			this.cached_arm_display.display_lang = attr.getValue("xml:lang");
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/def:DocumentRef/".equals(path)) {
			String leaf_id = attr.getValue("leafID");
			if (StringUtils.isNotEmpty(leaf_id)) {
				this.cached_display_doc = new DocumentRef(leaf_id);
				this.cached_arm_display.document_refs.add(this.cached_display_doc);
			} else {
				this.cached_display_doc = null;
			}
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/def:DocumentRef/def:PDFPageRef/".equals(path)) {
			if (this.cached_display_doc != null) {
				this.cached_display_doc.document_page_type = attr.getValue("Type");
				this.cached_display_doc.document_page_reference = attr.getValue("PageRefs");
				this.cached_display_doc.document_first_page = attr.getValue("FirstPage");
				this.cached_display_doc.document_last_page = attr.getValue("LastPage");
				this.cached_display_doc.document_page_title = attr.getValue("Title");
			}
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/".equals(path)) {
			DefineARMResultPk key = new DefineARMResultPk(this.cached_arm_display.display_name, attr.getValue("OID")); 
			DefineARMResultModel result = new DefineARMResultModel(key);
			result.ordinal = this.cached_arm_result_ordinal++;
			result.param_oid = attr.getValue("ParameterOID");
			result.analysis_reason = attr.getValue("AnalysisReason");
			result.analysis_purpose = attr.getValue("AnalysisPurpose");
			this.cached_arm_display.arm_result_pks.add(key);
			this.cached_arm_result = result;
			define.put(key, this.cached_arm_result);
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/Description/TranslatedText/".equals(path)) {
			this.cached_arm_result.result_lang = attr.getValue("xml:lang");
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:AnalysisDatasets/".equals(path)) {
			this.cached_arm_result.dataset_comment_oid = attr.getValue("def:CommentOID");
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:Documentation/Description/TranslatedText/".equals(path)) {
			this.cached_arm_result.docm_lang = attr.getValue("xml:lang");
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:Documentation/def:DocumentRef/".equals(path)) {
			String document_id = attr.getValue("leafID");
			if (StringUtils.isNotEmpty(document_id)) {
				this.cached_docm_doc = new DocumentRef(document_id);
				this.cached_arm_result.docm_document_refs.add(cached_docm_doc);
			} else {
				this.cached_docm_doc = null;
			}
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:Documentation/def:DocumentRef/def:PDFPageRef/".equals(path)) {
			if (this.cached_docm_doc != null) {
				this.cached_docm_doc.document_page_type = attr.getValue("Type");
				this.cached_docm_doc.document_page_reference = attr.getValue("PageRefs");
				this.cached_docm_doc.document_first_page = attr.getValue("FirstPage");
				this.cached_docm_doc.document_last_page = attr.getValue("LastPage");
				this.cached_docm_doc.document_page_title = attr.getValue("Title");
			}
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:ProgrammingCode/".equals(path)) {
			this.cached_arm_result.prog_code_context = attr.getValue("Context");
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:ProgrammingCode/def:DocumentRef/".equals(path)) {
			String document_id = attr.getValue("leafID");
			if (StringUtils.isNotEmpty(document_id)) {
				this.cached_prog_code_doc = new DocumentRef(document_id);
				this.cached_arm_result.prog_code_document_refs.add(this.cached_prog_code_doc);
			} else {
				this.cached_prog_code_doc = null;
			}

		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:ProgrammingCode/def:DocumentRef/def:PDFPageRef/".equals(path)) {
			if (this.cached_prog_code_doc != null) {
				this.cached_prog_code_doc.document_page_type = attr.getValue("Type");
				this.cached_prog_code_doc.document_page_reference = attr.getValue("PageRefs");
				this.cached_prog_code_doc.document_first_page = attr.getValue("FirstPage");
				this.cached_prog_code_doc.document_last_page = attr.getValue("LastPage");
				this.cached_prog_code_doc.document_page_title = attr.getValue("Title");
			}
		// *** ARM Dataset
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:AnalysisDatasets/arm:AnalysisDataset/".equals(path)) {
			DefineARMDatasetPk key = new DefineARMDatasetPk(this.cached_arm_display.display_name, this.cached_arm_result.result_key, attr.getValue("ItemGroupOID"));
			DefineARMDatasetModel arm_dataset = new DefineARMDatasetModel(key);
			arm_dataset.ordinal = cached_arm_dataset_ordinal++;
			define.put(key, arm_dataset);
			this.cached_arm_dataset = arm_dataset;
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:AnalysisDatasets/arm:AnalysisDataset/def:WhereClauseRef/".equals(path)) {
			this.cached_arm_dataset.where_clause_pk = new DefineWCPk(attr.getValue("WhereClauseOID"));
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:AnalysisDatasets/arm:AnalysisDataset/arm:AnalysisVariable/".equals(path)) {
			String analysis_variable_oid = attr.getValue("ItemOID");
			if (StringUtils.isNotEmpty(analysis_variable_oid)) {
				this.cached_arm_dataset.analysis_variable_oids.add(analysis_variable_oid);
			}
		}
	}

	public void characters(char[] ch, int start, int length) {
		String text = new String(ch, start, length).trim();
		if(text != null) {
			//text = StringEscapeUtils.unescapeXml(text);
			bindText(path_stack.getPath(), text);
		}
	}

	public void bindText(String path, String text) {
		if("/ODM/Study/GlobalVariables/StudyName/".equals(path)) {
			DefineStudyModel study = define.getStudy();
			if (study != null) {
				study.study_name += text;
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if("/ODM/Study/GlobalVariables/StudyDescription/".equals(path)) {
			DefineStudyModel study = define.getStudy();
			if (study != null) {
				study.study_description += text;
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if("/ODM/Study/GlobalVariables/ProtocolName/".equals(path)) {
			DefineStudyModel study = define.getStudy();
			if (study != null) {
				study.protocol_name += text;
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if("/ODM/Study/MetaDataVersion/def:leaf/def:title/".equals(path)) {
			DefineDocumentPk key = new DefineDocumentPk(this.cached_document_id);
			DefineDocumentModel document = define.get(key);
			if (document != null) {
				document.document_title += text;
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if("/ODM/Study/MetaDataVersion/ItemGroupDef/Description/TranslatedText/".equals(path)) {
			DefineDatasetPk key = new DefineDatasetPk(this.cached_itemgroup_name);
			DefineDatasetModel dataset = define.get(key);
			if (dataset != null) {
				dataset.description += text;
			}
		} else if("/ODM/Study/MetaDataVersion/ItemGroupDef/def:leaf/def:title/".equals(path)) {
			DefineDatasetPk key = new DefineDatasetPk(this.cached_itemgroup_name);
			DefineDatasetModel dataset = define.get(key);
			if (dataset != null) {
				dataset.leaf_title += text;
			}
		} else if("/ODM/Study/MetaDataVersion/ItemDef/Description/TranslatedText/".equals(path)) {
			List<DefineVariableModel> variables = define.getVariableByOid(this.cached_itemdef_oid);
			if (!variables.isEmpty()) {	//VARIABLE
				for (DefineVariableModel variable : variables) {
					variable.variable_label += text;
				}
			} else {
				DefineValueModel value = define.get(new DefineValuePk(this.cached_itemdef_oid));
				if (value != null) {	//VALUE
					value.value_label += text;
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/ItemDef/def:Origin/Description/TranslatedText/".equals(path)) {
			List<DefineVariableModel> variables = define.getVariableByOid(this.cached_itemdef_oid);
			if (!variables.isEmpty()) {
				for (DefineVariableModel variable : variables) {
					variable.predecessor += text;
				}
			} else {
				DefineValueModel value = define.get(new DefineValuePk(this.cached_itemdef_oid));
				if (value != null) {	//VALUE
					value.predecessor += text;
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/def:WhereClauseDef/RangeCheck/CheckValue/".equals(path)) {
			if (this.cached_wc_condition != null) {
				this.cached_wc_condition.values.add(text);
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if("/ODM/Study/MetaDataVersion/CodeList/CodeListItem/Decode/TranslatedText/".equals(path)) {
			this.cached_codelist.decode += text;
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/Description/TranslatedText/".equals(path)) {
			this.cached_arm_display.display_desc += text;
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/Description/TranslatedText/".equals(path)) {
			this.cached_arm_result.result_desc += text;
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:Documentation/Description/TranslatedText/".equals(path)) {
			this.cached_arm_result.docm_text += text;
		} else if ("/ODM/Study/MetaDataVersion/arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:ProgrammingCode/arm:Code/".equals(path)) {
			this.cached_arm_result.prog_code_text += text;
		} else if("/ODM/Study/MetaDataVersion/MethodDef/Description/TranslatedText/".equals(path)) {
			DefineMethodPk key = new DefineMethodPk(this.cached_method_id);
			DefineMethodModel method = define.get(key);
			if (method != null) {
				method.description += text;
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if("/ODM/Study/MetaDataVersion/MethodDef/FormalExpression/".equals(path)) {
			DefineMethodPk key = new DefineMethodPk(this.cached_method_id);
			DefineMethodModel method = define.get(key);
			if (method != null) {
				method.formal_expression += text;
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		} else if("/ODM/Study/MetaDataVersion/def:CommentDef/Description/TranslatedText/".equals(path)) {
			DefineCommentPk key = new DefineCommentPk(this.cached_comment_id);
			DefineCommentModel comment = define.get(key);
			if (comment != null) {
				comment.comment_text += text;
			} else {
				logger.warn(MESSAGE_ORPHANED);
			}
		}
	}

	public void endElement(String namespaceURI, String localName, String qName) {
//		logger.info("Exiting out of " + path_stack.getPath());
		path_stack.pop();
	}

	public void endDocument()  {
//		this.odm.updateField();
	}

	public void warning(SAXParseException exception) throws SAXException {
	}

	public void error(SAXParseException exception) throws SAXException {
		ErrorInfo error = new ErrorInfo();
	    error.setId(exception.getSystemId());
	    error.setLine(exception.getLineNumber());
	    error.setColumn(exception.getColumnNumber());
	    error.setMessage(exception.getMessage());
	    error.setMessage(exception.getMessage() + "\nnear the line number " + locator.getLineNumber() + ".");
	    errors.add(error);
    }

	public void fatalError(SAXParseException exception) throws SAXException{
		ErrorInfo error = new ErrorInfo();
		error.setId(exception.getSystemId());
		error.setLine(exception.getLineNumber());
		error.setColumn(exception.getColumnNumber());
		error.setMessage(exception.getMessage() + "\nnear the line number " + locator.getLineNumber() + ".");
		errors.add(error);
	}
	
	public DefineModel getDefineModel() {
		return define;
	}

	public List<ErrorInfo> getErrors() {
		return errors;
	}
	
	/* This method can be called only from elements below MetadataVersion */
	private String concat(String current_text, String new_text) {
		if (StringUtils.isEmpty(current_text)) {
			return new_text;
		} else {
			return current_text + config.valueDelimiter + new_text;
		}
	}
}
