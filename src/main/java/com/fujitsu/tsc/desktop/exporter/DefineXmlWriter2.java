/* 
 * Copyright (c) 2022 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.exporter;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fujitsu.tsc.desktop.exporter.model.XmlDocument.ProcessingInstruction;
import com.fujitsu.tsc.desktop.exporter.model.XmlDocument.XmlElement;
import com.fujitsu.tsc.desktop.exporter.model.XmlDocument.XmlHeader;
import com.fujitsu.tsc.desktop.exporter.model.XmlDocument;
import com.fujitsu.tsc.desktop.importer.models.DefineARMDatasetModel;
import com.fujitsu.tsc.desktop.importer.models.DefineARMDisplayModel;
import com.fujitsu.tsc.desktop.importer.models.DefineARMResultModel;
import com.fujitsu.tsc.desktop.importer.models.DefineARMResultModel.DefineARMResultPk;
import com.fujitsu.tsc.desktop.importer.models.DefineCodelistModel;
import com.fujitsu.tsc.desktop.importer.models.DefineCommentModel;
import com.fujitsu.tsc.desktop.importer.models.DefineDatasetModel;
import com.fujitsu.tsc.desktop.importer.models.DefineDictionaryModel;
import com.fujitsu.tsc.desktop.importer.models.DefineDocumentModel;
import com.fujitsu.tsc.desktop.importer.models.DefineMethodModel;
import com.fujitsu.tsc.desktop.importer.models.DefineModel;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.DocType;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.DocumentRef;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorN;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorNull;
import com.fujitsu.tsc.desktop.importer.models.DefineStandardModel;
import com.fujitsu.tsc.desktop.importer.models.DefineStudyModel;
import com.fujitsu.tsc.desktop.importer.models.DefineValueModel;
import com.fujitsu.tsc.desktop.importer.models.DefineVariableModel;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.DefineWCPk;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.WCCondition;
import com.fujitsu.tsc.desktop.importer.models.DefineStandardModel.StandardType;
import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.Utils;

public class DefineXmlWriter2 {
	
    private static Logger logger;
	private Config config;
	private OutputStreamWriter sw;
	private BufferedWriter writer;
	private final String DELIMITER;
	private final String DEFAULTLANG = "en";
	private final String DEFINE_VERSION;
	
	public static enum SuppVariable {
		STUDYID("Study Identifier"),
		RDOMAIN("Related Domain Abbreviation"),
		USUBJID("Unique Subject Identifier"),
		IDVAR("Identifying Variable"),
		IDVARVAL("Identifying Variable Value"),
		QNAM("Qualifier Variable Name"),
		QLABEL("Qualifier Variable Label"),
		QVAL("Data Value"),
		QORIG("Origin"),
		QEVAL("Evaluator");
		
		private String label;
		
		private SuppVariable(String label) {
			this.label = label;
		}
		
		private String label() {
			return this.label;
		}
	}

	public DefineXmlWriter2 (Config config) throws UnsupportedEncodingException, FileNotFoundException {
		logger = LogManager.getLogger();
		this.config = config;
		this.DELIMITER = config.valueDelimiter;
		this.DEFINE_VERSION = config.e2dDefineVersion;
	}
	
	public XmlDocument bind(DefineModel define) {
		/* Create new XML Document */
		XmlHeader xml_header = new XmlHeader(this.config.e2dXmlEncoding);
		XmlElement root_element = new XmlElement("ODM");
		XmlDocument xml_document = new XmlDocument(xml_header, root_element);

		/* Add Stylesheet Reference */
		ProcessingInstruction processing_instruction = new ProcessingInstruction("xml-stylesheet");
		processing_instruction.addInstruction("type", "text/xsl");
		processing_instruction.addInstruction("href", this.config.e2dStylesheetLocation);
		xml_document.addProcessingInstruction(processing_instruction);

		/* Update ODM element */
		DefineStudyModel study = define.getStudy();
		root_element.addAttribute("xmlns", "http://www.cdisc.org/ns/odm/v1.3");
		root_element.addAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
		if ("2.0.0".equals(DEFINE_VERSION)) {
			root_element.addAttribute("xmlns:def", "http://www.cdisc.org/ns/def/v2.0");
		} else {
			root_element.addAttribute("xmlns:def", "http://www.cdisc.org/ns/def/v2.1");
		}
		if (this.config.e2dDatasetType == Config.DatasetType.ADaM && this.config.e2dIncludeResultMetadata == true) {
			root_element.addAttribute("xmlns:arm", "http://www.cdisc.org/ns/arm/v1.0");
		}
		root_element.addAttribute("ODMVersion", "1.3.2");
		root_element.addAttribute("FileOID", (StringUtils.isEmpty(study.file_oid) ? UUID.randomUUID().toString() : study.file_oid));
		root_element.addAttribute("FileType", "Snapshot");
		String creation_date_time = calendarToIso8601(Calendar.getInstance());
		root_element.addAttribute("CreationDateTime", creation_date_time);
		if (StringUtils.isNotEmpty(study.as_of_date_time)) {
			root_element.addAttribute("AsOfDateTime", study.as_of_date_time);
		}
		if (StringUtils.isNotEmpty(study.originator)) {
			root_element.addAttribute("Originator", study.originator);
		}
		root_element.addAttribute("SourceSystem", Config.SOFTWARE_NAME);
		root_element.addAttribute("SourceSystemVersion", Config.SOFTWARE_VERSION);
		if (!"2.0.0".equals(DEFINE_VERSION)) {
			root_element.addAttribute("def:Context", study.context);
		}

		/* Add Study element */
		XmlElement study_element = new XmlElement("Study");
		root_element.addElement(study_element);
		study_element.addAttribute("OID", study.toStudyOid());
		/* Add GlobalVariables element */
		XmlElement global_var_elem = new XmlElement("GlobalVariables");
		study_element.addElement(global_var_elem);
		/* Add StudyName element */
		XmlElement study_name_elem = new XmlElement("StudyName");
		global_var_elem.addElement(study_name_elem);
		study_name_elem.addText(study.study_name);
		/* Add StudyDescription element */
		XmlElement study_desc_elem = new XmlElement("StudyDescription");
		global_var_elem.addElement(study_desc_elem);
		study_desc_elem.addText(study.study_description);
		/* Add ProtocolName element */
		XmlElement protocol_name_elem = new XmlElement("ProtocolName");
		global_var_elem.addElement(protocol_name_elem);
		protocol_name_elem.addText(study.protocol_name);
		/*Add MetaDataVersion element*/
		XmlElement md_ver_element = new XmlElement("MetaDataVersion");
		study_element.addElement(md_ver_element);
		md_ver_element.addAttribute("OID", study.toMetaDataOid());
		md_ver_element.addAttribute("Name", study.metadata_name);
		md_ver_element.addAttribute("Description", study.metadata_description);
		if ("2.0.0".equals(DEFINE_VERSION)) {
			md_ver_element.addAttribute("def:DefineVersion", this.config.e2dDefineVersion);
			md_ver_element.addAttribute("def:StandardName", this.config.e2dDatasetType.name() + "-IG");
			String standard_version = "";
			if (StringUtils.isEmpty(study.standard_version)) {
				DefineStandardModel standard = define.listSortedStandard().stream()
						.filter(o -> o.standard_type==StandardType.IG && StringUtils.equals(o.standard_name, this.config.e2dDatasetType.name() + "IG"))
						.findFirst().orElse(null);
				if (standard != null) {
					standard_version = standard.standard_version;
				}
			} else {
				standard_version = study.standard_version;
			}
			md_ver_element.addAttribute("def:StandardVersion", standard_version);
		} else {
			if (StringUtils.isEmpty(study.define_version)) {
				md_ver_element.addAttribute("def:DefineVersion", study.define_version);
			} else {
				md_ver_element.addAttribute("def:DefineVersion", "2.1.0");
			}
			md_ver_element.addAttribute("def:CommentOID", study.comment_oid);
		}
		/* Add def:Standards element */
		if (!"2.0.0".equals(DEFINE_VERSION)) {
			List<DefineStandardModel> standards = define.listSortedStandard();
			if (!standards.isEmpty()) {
				XmlElement standards_element = new XmlElement("def:Standards");
				md_ver_element.addElement(standards_element);
				for (DefineStandardModel standard : standards) {
					XmlElement standard_element = new XmlElement("def:Standard");
					standards_element.addElement(standard_element);
					standard_element.addAttribute("OID", standard.toOid());
					standard_element.addAttribute("Name", standard.standard_name);
					standard_element.addAttribute("Type", standard.standard_type.name());
					standard_element.addAttribute("PublishingSet", standard.publishing_set);
					standard_element.addAttribute("Version", standard.standard_version);
					standard_element.addAttribute("Status", standard.standard_status);
					standard_element.addAttribute("def:CommentOID", standard.comment_oid);
				}
			}
		}
		/* Add def:AnnotatedCRF element */
		List<DefineDocumentModel> acrfs = define.listSortedDocument().stream().filter(o -> o.document_type == DocType.AnnotatedCRF).collect(Collectors.toList());
		if (!acrfs.isEmpty()) {
			XmlElement acrf_element = new XmlElement("def:AnnotatedCRF");
			md_ver_element.addElement(acrf_element);
			for (DefineDocumentModel acrf : acrfs) {
				XmlElement doc_ref_element = new XmlElement("def:DocumentRef");
				doc_ref_element.addAttribute("leafID", acrf.toOid());
				acrf_element.addElement(doc_ref_element);
			}
		}
		/* Add def:SupplementalDoc element */
		List<DefineDocumentModel> supp_docs = define.listSortedDocument().stream().filter(o -> o.document_type == DocType.SupplementalDoc).collect(Collectors.toList());
		if (!supp_docs.isEmpty()) {
			XmlElement supp_doc_element = new XmlElement("def:SupplementalDoc");
			md_ver_element.addElement(supp_doc_element);
			for (DefineDocumentModel supp_doc : supp_docs) {
				XmlElement doc_ref_element = new XmlElement("def:DocumentRef");
				doc_ref_element.addAttribute("leafID", supp_doc.toOid());
				supp_doc_element.addElement(doc_ref_element);
			}
		}
		/* Create def:ValueListDef and def:WhereClauseDef elements later. */
		
		/*
		 * Add ItemGroupDef elements
		 */
		List<DefineDatasetModel> datasets = define.listSortedDataset();
		for (DefineDatasetModel dataset : datasets) {
			/* ItemGroupDef */
			if ("2.0.0".equals(DEFINE_VERSION) && dataset.has_no_data == YorNull.Yes) {
				continue;	//Skip No Data
			}
			XmlElement ig_def_element = new XmlElement("ItemGroupDef");
			md_ver_element.addElement(ig_def_element);
			ig_def_element.addAttribute("OID", dataset.toOid());
			if (!(config.e2dDatasetType == Config.DatasetType.ADaM)) {
				ig_def_element.addAttribute("Domain", dataset.domain);
			}
			ig_def_element.addAttribute("Name", dataset.dataset_name);
			ig_def_element.addAttribute("Repeating", dataset.repeating.name());
			ig_def_element.addAttribute("IsReferenceData", dataset.is_reference_data.name());
			ig_def_element.addAttribute("SASDatasetName", dataset.sas_dataset_name);
			ig_def_element.addAttribute("Purpose", dataset.purpose);
			ig_def_element.addAttribute("def:Structure", dataset.structure);
			if ("2.0.0".equals(DEFINE_VERSION)) {
				ig_def_element.addAttribute("def:Class", dataset.dataset_class);
			}
			ig_def_element.addAttribute("def:ArchiveLocationID", dataset.getLeafOid());
			ig_def_element.addAttribute("def:CommentOID", dataset.comment_oid);
			if (!"2.0.0".equals(DEFINE_VERSION)) {
				if (StringUtils.isEmpty(dataset.standard_oid)) {
					ig_def_element.addAttribute("def:IsNonStandard", YorNull.Yes.name());
				} else {
					ig_def_element.addAttribute("def:StandardOID", dataset.standard_oid);
				}
				if (dataset.has_no_data == YorNull.Yes) {
					ig_def_element.addAttribute("def:HasNoData", YorNull.Yes.name());
				}
			}
			/* Description */
			XmlElement desc_element = new XmlElement("Description");
			ig_def_element.addElement(desc_element);
			XmlElement trans_element = new XmlElement("TranslatedText");
			desc_element.addElement(trans_element);
			trans_element.addAttribute("xml:lang", DEFAULTLANG);
			trans_element.addText(dataset.description);
			/* Add ItemRef elements */
			List<DefineVariableModel> variables = define.listSortedVariable().stream().filter(o -> StringUtils.equals(o.dataset_name, dataset.dataset_name)).collect(Collectors.toList());
			int order_number = 1;
			for (DefineVariableModel variable : variables) {
				if (("2.0.0".equals(DEFINE_VERSION) && (dataset.has_no_data == YorNull.Yes || variable.has_no_data == YorNull.Yes)) || variable.is_supp == YorN.Yes) {
					continue;	//Skip No Data and Is SUPP 
				}
				XmlElement variable_element = new XmlElement("ItemRef");
				ig_def_element.addElement(variable_element);
				variable_element.addAttribute("ItemOID", variable.toOid());
				variable_element.addAttribute("OrderNumber", String.valueOf(order_number++));
				variable_element.addAttribute("Mandatory", variable.mandatory.name());
				variable_element.addAttribute("KeySequence", variable.key_sequence);
				variable_element.addAttribute("MethodOID", variable.method_oid);
				variable_element.addAttribute("Role", variable.role);
				variable_element.addAttribute("RoleCodeListOID", variable.role_codelist);
				if (!"2.0.0".equals(DEFINE_VERSION)) {
					if (variable.is_non_standard == YorNull.Yes) {
						variable_element.addAttribute("def:IsNonStandard", YorNull.Yes.name());
					}
					if (variable.has_no_data == YorNull.Yes) {
						variable_element.addAttribute("def:HasNoData", YorNull.Yes.name());
					}
				}
				/* Create TSVALn and COVALn if they are repeating. */
			}
			/* Alias */
			if (StringUtils.isNotEmpty(dataset.alias_name)) {
				XmlElement alias_element = new XmlElement("Alias");
				ig_def_element.addElement(alias_element);
				alias_element.addAttribute("Context", "DomainDescription");
				alias_element.addAttribute("Name", dataset.alias_name);
			}
			/* def:Class */
			if (!"2.0.0".equals(DEFINE_VERSION)) {
				if (StringUtils.isNotEmpty(dataset.dataset_class)) {
					XmlElement class_element = new XmlElement("def:Class");
					ig_def_element.addElement(class_element);
					class_element.addAttribute("Name", dataset.dataset_class);
					if (StringUtils.isNotEmpty(dataset.dataset_subclass)) {
						XmlElement subclass_element = new XmlElement("def:SubClass");
						class_element.addElement(subclass_element);
						subclass_element.addAttribute("Name", dataset.dataset_subclass);
					}
				}
			}
			/* def:Leaf */
			if (StringUtils.isNotEmpty(dataset.leaf_href) && dataset.has_no_data == null) {
				XmlElement leaf_element = new XmlElement("def:leaf");
				ig_def_element.addElement(leaf_element);
				leaf_element.addAttribute("ID", dataset.getLeafOid());
				leaf_element.addAttribute("xlink:href", dataset.leaf_href);
				XmlElement title_element = new XmlElement("def:title");
				leaf_element.addElement(title_element);
				title_element.addText(dataset.leaf_title);
			}
		}
		
		/*
		 * Add ItemDef and then def:ValueListDef elements
		 */
		List<DefineVariableModel> variables = define.listSortedVariable();
		for (DefineVariableModel variable : variables) {
			/*
			 * Create ItemDef elements
			 */
			DefineDatasetModel dataset = define.listSortedDataset().stream().filter(o -> StringUtils.equals(variable.dataset_name, o.dataset_name)).findFirst().orElse(null);
			if (("2.0.0".equals(DEFINE_VERSION) && ((dataset != null && dataset.has_no_data == YorNull.Yes) || variable.has_no_data == YorNull.Yes)) || variable.is_supp == YorN.Yes) {
				continue;	//Skip No Data and Is SUPP 
			}
			/* ItemDef */
			XmlElement item_def_element = new XmlElement("ItemDef");
			md_ver_element.addElement(item_def_element);
			item_def_element.addAttribute("OID", variable.toOid());
			item_def_element.addAttribute("Name", variable.variable_name);
			item_def_element.addAttribute("DataType", variable.data_type);
			item_def_element.addAttribute("Length", getRepeatLength(variable.length, variable.repeat_n_length));
			item_def_element.addAttribute("SignificantDigits", variable.significant_digits);
			item_def_element.addAttribute("SASFieldName", variable.sas_field_name);
			item_def_element.addAttribute("def:DisplayFormat", variable.display_format);
			item_def_element.addAttribute("def:CommentOID", variable.comment_oid);
			/* Description */
			XmlElement desc_element = new XmlElement("Description");
			item_def_element.addElement(desc_element);
			XmlElement trans_element = new XmlElement("TranslatedText");
			desc_element.addElement(trans_element);
			trans_element.addAttribute("xml:lang", DEFAULTLANG);
			trans_element.addText(variable.variable_label);
			/* CodeListRef */
			if (StringUtils.isNotEmpty(variable.codelist)) {
				XmlElement cl_ref_element = new XmlElement("CodeListRef");
				item_def_element.addElement(cl_ref_element);
				cl_ref_element.addAttribute("CodeListOID", DefineCodelistModel.createCodelistOid(variable.codelist));
			}
			/* def:Origin */
			if (StringUtils.isNotEmpty(variable.origin)) {
				XmlElement origin_element = new XmlElement("def:Origin");
				item_def_element.addElement(origin_element);
				if ("2.0.0".equals(DEFINE_VERSION)) {
					if ("Collected".equals(variable.origin)) {
						if ("Vendor".equals(variable.source)) {
							origin_element.addAttribute("Type", "eDT");
						} else {
							origin_element.addAttribute("Type", "CRF");
						}
					} else {
						origin_element.addAttribute("Type", variable.origin);
					}
				} else {
					if ("CRF".equals(variable.origin) || "eDT".equals(variable.origin)) {
						origin_element.addAttribute("Type", "Collected");
					} else {
						origin_element.addAttribute("Type", variable.origin);
					}
					origin_element.addAttribute("Source", variable.source);
				}
				if (StringUtils.isNotEmpty(variable.predecessor)) {
					XmlElement desc_element2 = new XmlElement("Description");
					origin_element.addElement(desc_element2);
					XmlElement trans_element2 = new XmlElement("TranslatedText");
					desc_element2.addElement(trans_element2);
					trans_element2.addAttribute("xml:lang", DEFAULTLANG);
					trans_element2.addText(variable.predecessor);
				}
				if (StringUtils.isNotEmpty(variable.crf_page_type)) {
					XmlElement doc_ref_element = new XmlElement("def:DocumentRef");
					origin_element.addElement(doc_ref_element);
					doc_ref_element.addAttribute("leafID", DefineDocumentModel.createOid(variable.crf_id));
					XmlElement pdf_page_element = new XmlElement("def:PDFPageRef");
					doc_ref_element.addElement(pdf_page_element);
					pdf_page_element.addAttribute("Type", variable.crf_page_type);
					pdf_page_element.addAttribute("PageRefs", variable.crf_page_reference);
					pdf_page_element.addAttribute("FirstPage", variable.crf_first_page);
					pdf_page_element.addAttribute("LastPage", variable.crf_last_page);
					if (!"2.0.0".equals(DEFINE_VERSION)) {
						pdf_page_element.addAttribute("Title", variable.crf_page_title);
					}
				}
			}

			/*
			 * def:ValueListRef/def:ValueListDef/def:WhereClauseDef/ItemDef from VALUES sheet
			 */
			List<DefineValueModel> values = define.listSortedValue().stream()
					.filter(o -> StringUtils.equals(o.dataset_name, variable.dataset_name) && StringUtils.equals(o.variable_name, variable.variable_name))
					.collect(Collectors.toList());
			if (!values.isEmpty()) {
				/* def:ValueListRef */
				XmlElement val_list_ref_element = new XmlElement("def:ValueListRef");
				item_def_element.addElement(val_list_ref_element);
				val_list_ref_element.addAttribute("ValueListOID", DefineVariableModel.createValueListOid(variable.dataset_name, variable.variable_name));
				/* def:ValueListDef */
				XmlElement val_list_def_element = new XmlElement("def:ValueListDef");
				md_ver_element.addElement(val_list_def_element);
				val_list_def_element.addAttribute("OID", DefineVariableModel.createValueListOid(variable.dataset_name, variable.variable_name));
				/* ItemRef */
				int order_number = 1;
				for (DefineValueModel value : values) {
					if ("2.0.0".equals(DEFINE_VERSION) && ((dataset != null && dataset.has_no_data == YorNull.Yes) || variable.has_no_data == YorNull.Yes || value.has_no_data == YorNull.Yes)) {
						continue;	//Skip No Data
					}
					XmlElement item_ref_element = new XmlElement("ItemRef");
					val_list_def_element.addElement(item_ref_element);
					item_ref_element.addAttribute("ItemOID", value.toOid());
					item_ref_element.addAttribute("OrderNumber", String.valueOf(order_number++));
					item_ref_element.addAttribute("Mandatory", value.mandatory.name());
					item_ref_element.addAttribute("KeySequence", value.key_sequence);
					item_ref_element.addAttribute("MethodOID", value.method_oid);
					if (!"2.0.0".equals(DEFINE_VERSION) && variable.has_no_data == YorNull.Yes) {
						item_ref_element.addAttribute("def:HasNoData", YorNull.Yes.name());
					}
					/* def:WhereClauseRef */
					for (DefineWCPk pk : value.where_clause_pks) {
						DefineWCModel wc = define.get(pk);
						XmlElement wc_ref_element = new XmlElement("def:WhereClauseRef");
						item_ref_element.addElement(wc_ref_element);
						wc_ref_element.addAttribute("WhereClauseOID", wc.toOid());
						/* def:WhereClauseDef */
						XmlElement wc_def_element = new XmlElement("def:WhereClauseDef");
						md_ver_element.addElement(wc_def_element);
						wc_def_element.addAttribute("OID", wc.toOid());
						wc_def_element.addAttribute("def:CommentOID", wc.comment_oid);
						/* RangeCheck */
						for (WCCondition wc_condition : wc.wc_conditions) {
							XmlElement range_check_element = new XmlElement("RangeCheck");
							wc_def_element.addElement(range_check_element);
							range_check_element.addAttribute("Comparator", wc_condition.operator);
							range_check_element.addAttribute("SoftHard", "Soft");
							range_check_element.addAttribute("def:ItemOID", DefineVariableModel.createOid(wc_condition.dataset_name, wc_condition.variable_name));
							for (String check_value : wc_condition.values) {
								XmlElement check_value_element = new XmlElement("CheckValue");
								range_check_element.addElement(check_value_element);
								check_value_element.addText(check_value);
							}
						}
					}
					/*
					 * ItemDef
					 */
					XmlElement val_item_def_element = new XmlElement("ItemDef");
					md_ver_element.addElement(val_item_def_element);
					val_item_def_element.addAttribute("OID", value.toOid());
					val_item_def_element.addAttribute("Name", value.value_name);
					val_item_def_element.addAttribute("DataType", value.data_type);
					val_item_def_element.addAttribute("Length", value.length);
					val_item_def_element.addAttribute("SignificantDigits", value.significant_digits);
					val_item_def_element.addAttribute("SASFieldName", value.sas_field_name);
					val_item_def_element.addAttribute("def:DisplayFormat", value.display_format);
					val_item_def_element.addAttribute("def:CommentOID", value.comment_oid);
					/* Description */
					XmlElement val_desc_element = new XmlElement("Description");
					val_item_def_element.addElement(val_desc_element);
					XmlElement val_trans_element = new XmlElement("TranslatedText");
					val_desc_element.addElement(val_trans_element);
					val_trans_element.addAttribute("xml:lang", DEFAULTLANG);
					val_trans_element.addText(value.value_label);
					/* CodeListRef */
					if (StringUtils.isNotEmpty(value.codelist)) {
						XmlElement cl_ref_element = new XmlElement("CodeListRef");
						val_item_def_element.addElement(cl_ref_element);
						cl_ref_element.addAttribute("CodeListOID", DefineCodelistModel.createCodelistOid(value.codelist));
					}
					/* def:Origin */
					if (StringUtils.isNotEmpty(value.origin)) {
						XmlElement val_origin_element = new XmlElement("def:Origin");
						val_item_def_element.addElement(val_origin_element);
						if ("2.0.0".equals(DEFINE_VERSION)) {
							if ("Collected".equals(value.origin)) {
								if ("Vendor".equals(value.source)) {
									val_origin_element.addAttribute("Type", "eDT");
								} else {
									val_origin_element.addAttribute("Type", "CRF");
								}
							} else {
								val_origin_element.addAttribute("Type", value.origin);
							}
						} else {
							if ("CRF".equals(variable.origin) || "eDT".equals(value.origin)) {
								val_origin_element.addAttribute("Type", "Collected");
							} else {
								val_origin_element.addAttribute("Type", value.origin);
							}
							val_origin_element.addAttribute("Source", value.source);
						}
						if (StringUtils.isNotEmpty(value.predecessor)) {
							XmlElement desc_element2 = new XmlElement("Description");
							val_origin_element.addElement(desc_element2);
							XmlElement trans_element2 = new XmlElement("TranslatedText");
							desc_element2.addElement(trans_element2);
							trans_element2.addAttribute("xml:lang", DEFAULTLANG);
							trans_element2.addText(value.predecessor);
						}
						if (StringUtils.isNotEmpty(value.crf_page_type)) {
							XmlElement doc_ref_element = new XmlElement("def:DocumentRef");
							val_origin_element.addElement(doc_ref_element);
							doc_ref_element.addAttribute("leafID", DefineDocumentModel.createOid(value.crf_id));
							XmlElement pdf_page_element = new XmlElement("def:PDFPageRef");
							doc_ref_element.addElement(pdf_page_element);
							pdf_page_element.addAttribute("Type", value.crf_page_type);
							pdf_page_element.addAttribute("PageRefs", value.crf_page_reference);
							pdf_page_element.addAttribute("FirstPage", value.crf_first_page);
							pdf_page_element.addAttribute("LastPage", value.crf_last_page);
							if (!"2.0.0".equals(DEFINE_VERSION)) {
								pdf_page_element.addAttribute("Title", value.crf_page_title);
							}
						}
					}
					/* Alias */
					if (!"2.0.0".equals(DEFINE_VERSION)) {
						if (StringUtils.isNotEmpty(value.alias_context) && StringUtils.isNotEmpty(value.alias_name)) {
							XmlElement alias_element = new XmlElement("Alias");
							val_item_def_element.addElement(alias_element);
							alias_element.addAttribute("Context", value.alias_context);
							alias_element.addAttribute("Name", value.alias_name);
						}
					}
				}
			}
			/* Alias */
			if (!"2.0.0".equals(DEFINE_VERSION)) {
				if (StringUtils.isNotEmpty(variable.alias_context) && StringUtils.isNotEmpty(variable.alias_name)) {
					XmlElement alias_element = new XmlElement("Alias");
					item_def_element.addElement(alias_element);
					alias_element.addAttribute("Context", variable.alias_context);
					alias_element.addAttribute("Name", variable.alias_name);
				}
			}
		}

		/*
		 * Add ItemGroupDef/ItemDef elements for datasets with is_supp variables containing data.
		 */
		if (!(config.e2dDatasetType == Config.DatasetType.ADaM)) {
			addAutoSuppElements(define, xml_document);
		}

		/*
		 * Add CodeList elements
		 */
		List<DefineCodelistModel> codelists = define.listSortedCodelist();
		Set<String> referenced_cl_ids = new HashSet<>();
		if ("2.0.0".equals(DEFINE_VERSION)) {
			referenced_cl_ids.addAll(define.listSortedVariable().stream().filter(o -> o.has_no_data_derived == null)
					.map(o -> o.codelist).collect(Collectors.toSet()));
			referenced_cl_ids.addAll(define.listSortedVariable().stream().filter(o -> o.has_no_data_derived == null)
					.map(o -> o.role_codelist).collect(Collectors.toSet()));
			referenced_cl_ids.addAll(define.listSortedValue().stream().filter(o -> o.has_no_data_derived == null)
					.map(o -> o.codelist).collect(Collectors.toSet()));
		} else {
			referenced_cl_ids.addAll(define.listSortedVariable().stream().map(o -> o.codelist).collect(Collectors.toSet()));
			referenced_cl_ids.addAll(define.listSortedVariable().stream().map(o -> o.role_codelist).collect(Collectors.toSet()));
			referenced_cl_ids.addAll(define.listSortedValue().stream().map(o -> o.codelist).collect(Collectors.toSet()));
		}
		XmlElement last_cl_element = null;
		String last_codelist_id = "";
		for (DefineCodelistModel codelist : codelists) {
			/* Do not create codelists that made orphaned due to Has No Data.  */
			if (!referenced_cl_ids.contains(codelist.codelist_id)) {
				logger.warn("The codelist " + codelist.codelist_id + " is not referenced and thus skipped.");
				continue;	//Skip
			}
			/* CodeList */
			if (!StringUtils.equals(codelist.codelist_id, last_codelist_id)) {
				XmlElement cl_element = new XmlElement("CodeList");
				md_ver_element.addElement(cl_element);
				cl_element.addAttribute("OID", codelist.getCodelistOid());
				cl_element.addAttribute("Name", codelist.codelist_label);
				cl_element.addAttribute("DataType", codelist.data_type);
				if (!"2.0.0".equals(DEFINE_VERSION)) {
					if (StringUtils.isEmpty(codelist.codelist_code)) {
						cl_element.addAttribute("def:IsNonStandard", YorNull.Yes.name());
					}
					cl_element.addAttribute("def:StandardOID", codelist.standard_oid);
				}
				cl_element.addAttribute("SASFormatName", codelist.sas_format_name);
				if (!"2.0.0".equals(DEFINE_VERSION)) {
					cl_element.addAttribute("def:CommentOID", codelist.comment_oid);
				}
				/* CodeList Alias */
				if (StringUtils.isNotEmpty(codelist.codelist_code)) {
					XmlElement alias_element = new XmlElement("Alias");
					cl_element.addElement(alias_element);
					alias_element.addAttribute("Context", "nci:ExtCodeID");
					alias_element.addAttribute("Name", codelist.codelist_code);
				}
				last_codelist_id = codelist.codelist_id;
				last_cl_element = cl_element;
			}
			if (StringUtils.isEmpty(codelist.decode)) {
				/* EnumeratedItem */
				XmlElement item_element = new XmlElement("EnumeratedItem");
				last_cl_element.addElement(item_element);
				item_element.addAttribute("CodedValue", codelist.submission_value);
				item_element.addAttribute("Rank", codelist.rank);
				item_element.addAttribute("OrderNumber", codelist.order_number);
				if (codelist.extended_value == YorNull.Yes) {
					item_element.addAttribute("def:ExtendedValue", codelist.extended_value.name());
				}
				if (StringUtils.isNotEmpty(codelist.code)) {
					XmlElement alias_element = new XmlElement("Alias");
					item_element.addElement(alias_element);
					alias_element.addAttribute("Context", "nci:ExtCodeID");
					alias_element.addAttribute("Name", codelist.code);
				}
				if (StringUtils.isNotEmpty(codelist.alias_context) && StringUtils.isNotEmpty(codelist.alias_name)) {
					XmlElement alias_element = new XmlElement("Alias");
					item_element.addElement(alias_element);
					alias_element.addAttribute("Context", codelist.alias_context);
					alias_element.addAttribute("Name", codelist.alias_name);
				}
			} else {
				/* CodeListItem */
				XmlElement item_element = new XmlElement("CodeListItem");
				last_cl_element.addElement(item_element);
				item_element.addAttribute("CodedValue", codelist.submission_value);
				item_element.addAttribute("Rank", codelist.rank);
				item_element.addAttribute("OrderNumber", codelist.order_number);
				if (codelist.extended_value == YorNull.Yes) {
					item_element.addAttribute("def:ExtendedValue", codelist.extended_value.name());
				}
				XmlElement decode_element = new XmlElement("Decode");
				item_element.addElement(decode_element);
				XmlElement trans_element = new XmlElement("TranslatedText");
				decode_element.addElement(trans_element);
				trans_element.addAttribute("xml:lang", codelist.xml_lang);
				trans_element.addText(codelist.decode);
				if (StringUtils.isNotEmpty(codelist.code)) {
					XmlElement alias_element = new XmlElement("Alias");
					item_element.addElement(alias_element);
					alias_element.addAttribute("Context", "nci:ExtCodeID");
					alias_element.addAttribute("Name", codelist.code);
				}
				if (StringUtils.isNotEmpty(codelist.alias_context) && StringUtils.isNotEmpty(codelist.alias_name)) {
					XmlElement alias_element = new XmlElement("Alias");
					item_element.addElement(alias_element);
					alias_element.addAttribute("Context", codelist.alias_context);
					alias_element.addAttribute("Name", codelist.alias_name);
				}
			}
			/* CodeList Alias */
			if (!StringUtils.equals(codelist.codelist_id, last_codelist_id)) {
				if (StringUtils.isNotEmpty(codelist.codelist_code)) {
					XmlElement alias_element = new XmlElement("Alias");
					last_cl_element.addElement(alias_element);
					alias_element.addAttribute("Context", "nci:ExtCodeID");
					alias_element.addAttribute("Name", codelist.codelist_code);
				}
			}
		}

		List<DefineDictionaryModel> dictionaries = define.listSortedDictionary();
		for (DefineDictionaryModel dictionary : dictionaries) {
			XmlElement cl_element = new XmlElement("CodeList");
			md_ver_element.addElement(cl_element);
			cl_element.addAttribute("OID", dictionary.toOid());
			cl_element.addAttribute("Name", dictionary.dictionary_name);
			cl_element.addAttribute("DataType", dictionary.data_type);
			if (!"2.0.0".equals(DEFINE_VERSION)) {
				cl_element.addAttribute("def:CommentOID", dictionary.comment_oid);
			}
			XmlElement dictionary_element = new XmlElement("ExternalCodeList");
			cl_element.addElement(dictionary_element);
			dictionary_element.addAttribute("Dictionary", dictionary.dictionary_name);
			dictionary_element.addAttribute("Version", dictionary.dictionary_version);
			dictionary_element.addAttribute("ref", dictionary.dictionary_ref);
			dictionary_element.addAttribute("href", dictionary.dictionary_href);
		}

		/*
		 * Add MethodDef elements
		 */
		List<DefineMethodModel> methods = define.listSortedMethod();
		Set<String> referenced_method_oids = new HashSet<>();
		if ("2.0.0".equals(DEFINE_VERSION)) {
			referenced_method_oids.addAll(define.listSortedVariable().stream().filter(o -> o.has_no_data_derived == null)
					.map(o -> o.method_oid).collect(Collectors.toSet()));
			referenced_method_oids.addAll(define.listSortedValue().stream().filter(o -> o.has_no_data_derived == null)
					.map(o -> o.method_oid).collect(Collectors.toSet()));
		} else {
			referenced_method_oids.addAll(define.listSortedVariable().stream().map(o -> o.method_oid).collect(Collectors.toSet()));
			referenced_method_oids.addAll(define.listSortedValue().stream().map(o -> o.method_oid).collect(Collectors.toSet()));
		}
		for (DefineMethodModel method : methods) {
			if (!referenced_method_oids.contains(method.oid)) {
				logger.warn("The method " + method.oid + " is not referenced and thus skipped.");
				continue;	//Skip No Data
			}
			XmlElement method_element = new XmlElement("MethodDef");
			md_ver_element.addElement(method_element);
			method_element.addAttribute("OID", method.oid);
			method_element.addAttribute("Name", method.method_name);
			method_element.addAttribute("Type", method.method_type);
			XmlElement desc_element = new XmlElement("Description");
			method_element.addElement(desc_element);
			XmlElement trans_element = new XmlElement("TranslatedText");
			desc_element.addElement(trans_element);
			trans_element.addAttribute("xml:lang", method.description_lang);
			trans_element.addText(method.description);
			for (DocumentRef document_ref : method.document_refs) {
				XmlElement doc_ref_element = new XmlElement("def:DocumentRef");
				method_element.addElement(doc_ref_element);
				doc_ref_element.addAttribute("leafID", DefineDocumentModel.createOid(document_ref.document_id));
				if (StringUtils.isNotEmpty(document_ref.document_page_type)) {
					XmlElement pdf_page_element = new XmlElement("def:PDFPageRef");
					doc_ref_element.addElement(pdf_page_element);
					pdf_page_element.addAttribute("Type", document_ref.document_page_type);
					pdf_page_element.addAttribute("PageRefs", document_ref.document_page_reference);
					pdf_page_element.addAttribute("FirstPage", document_ref.document_first_page);
					pdf_page_element.addAttribute("LastPage", document_ref.document_last_page);
					if (!"2.0.0".equals(DEFINE_VERSION)) {
						pdf_page_element.addAttribute("Title", document_ref.document_page_title);
					}
				}
			}
			if (StringUtils.isNotEmpty(method.formal_expression)) {
				XmlElement expression_element = new XmlElement("FormalExpression");
				method_element.addElement(expression_element);
				expression_element.addAttribute("Context", method.formal_expression_context);
				expression_element.addText(method.formal_expression);
			}
		}

		/*
		 * Add def:CommentDef elements
		 */
		List<DefineCommentModel> comments = define.listSortedComment();
		Set<String> referenced_comment_oids = new HashSet<>();
		if ("2.0.0".equals(DEFINE_VERSION)) {
			referenced_comment_oids.addAll(define.listSortedDataset().stream().filter(o -> o.has_no_data == null)
					.map(o -> o.comment_oid).collect(Collectors.toSet()));
			referenced_comment_oids.addAll(define.listSortedVariable().stream().filter(o -> o.has_no_data_derived == null)
					.map(o -> o.comment_oid).collect(Collectors.toSet()));
			List<DefineValueModel> values = define.listSortedValue();
			for (DefineValueModel value : values) {
				if (value.has_no_data_derived != null) {
					continue;
				}
				referenced_comment_oids.add(value.comment_oid);
				for (DefineWCPk wc_pk : value.where_clause_pks) {
					DefineWCModel wc = define.get(wc_pk);
					if (wc != null) {
						referenced_comment_oids.add(wc.comment_oid);
					}
				}
			}
		} else {
			if (define.getStudy() != null) {
				referenced_comment_oids.add(define.getStudy().comment_oid);
			}
			referenced_comment_oids.addAll(define.listSortedStandard().stream().map(o -> o.comment_oid).collect(Collectors.toSet()));
			referenced_comment_oids.addAll(define.listSortedDataset().stream().map(o -> o.comment_oid).collect(Collectors.toSet()));
			referenced_comment_oids.addAll(define.listSortedVariable().stream().map(o -> o.comment_oid).collect(Collectors.toSet()));
			referenced_comment_oids.addAll(define.listSortedDictionary().stream().map(o -> o.comment_oid).collect(Collectors.toSet()));
			referenced_comment_oids.addAll(define.listSortedCodelist().stream().map(o -> o.comment_oid).collect(Collectors.toSet()));
			List<DefineValueModel> values = define.listSortedValue();
			for (DefineValueModel value : values) {
				referenced_comment_oids.add(value.comment_oid);
				for (DefineWCPk wc_pk : value.where_clause_pks) {
					DefineWCModel wc = define.get(wc_pk);
					if (wc != null) {
						referenced_comment_oids.add(wc.comment_oid);
					}
				}
			}
		}
		if (this.config.e2dDatasetType == Config.DatasetType.ADaM && this.config.e2dIncludeResultMetadata == true) {
			referenced_comment_oids.addAll(define.listSortedARMResult().stream().map(o -> o.dataset_comment_oid).collect(Collectors.toSet()));
			Set<DefineWCPk> wc_pks = define.listSortedARMDataset().stream().map(o -> o.where_clause_pk).collect(Collectors.toSet());
			for (DefineWCPk wc_pk : wc_pks) {
				DefineWCModel wc = define.get(wc_pk);
				if (wc != null) {
					referenced_comment_oids.add(wc.comment_oid);
				}
			}
		}
		for (DefineCommentModel comment : comments) {
			if (!referenced_comment_oids.contains(comment.oid)) {
				logger.warn("The comment " + comment.oid + " is not referenced and thus skipped.");
				continue;	//Skip No Data
			}
			XmlElement comment_element = new XmlElement("def:CommentDef");
			md_ver_element.addElement(comment_element);
			comment_element.addAttribute("OID", comment.oid);
			XmlElement desc_element = new XmlElement("Description");
			comment_element.addElement(desc_element);
			XmlElement trans_element = new XmlElement("TranslatedText");
			desc_element.addElement(trans_element);
			trans_element.addAttribute("xml:lang", comment.comment_lang);
			trans_element.addText(comment.comment_text);
			for (DocumentRef document_ref : comment.document_refs) {
				XmlElement doc_ref_element = new XmlElement("def:DocumentRef");
				comment_element.addElement(doc_ref_element);
				doc_ref_element.addAttribute("leafID", DefineDocumentModel.createOid(document_ref.document_id));
				if (StringUtils.isNotEmpty(document_ref.document_page_type)) {
					XmlElement pdf_page_element = new XmlElement("def:PDFPageRef");
					doc_ref_element.addElement(pdf_page_element);
					pdf_page_element.addAttribute("Type", document_ref.document_page_type);
					pdf_page_element.addAttribute("PageRefs", document_ref.document_page_reference);
					pdf_page_element.addAttribute("FirstPage", document_ref.document_first_page);
					pdf_page_element.addAttribute("LastPage", document_ref.document_last_page);
					if (!"2.0.0".equals(DEFINE_VERSION)) {
						pdf_page_element.addAttribute("Title", document_ref.document_page_title);
					}
				}
			}
		}

		/*
		 * Add def:leaf elements
		 */
		List<DefineDocumentModel> documents = define.listSortedDocument();
		for (DefineDocumentModel document : documents) {
			XmlElement leaf_element = new XmlElement("def:leaf");
			md_ver_element.addElement(leaf_element);
			leaf_element.addAttribute("ID", document.toOid());
			leaf_element.addAttribute("xlink:href", document.document_href);
			if (StringUtils.isNotEmpty(document.document_title)) {
				XmlElement title_element = new XmlElement("def:title");
				leaf_element.addElement(title_element);
				title_element.addText(document.document_title);
			}
		}

		/*
		 * Create Analysis Results Metadata
		 */
		if (this.config.e2dDatasetType == Config.DatasetType.ADaM && this.config.e2dIncludeResultMetadata == true) {
			List<DefineARMDisplayModel> displays = define.listSortedARMDisplay();
			List<DefineARMDatasetModel> arm_datasets = define.listSortedARMDataset();
			/* arm:AnalysisResultDisplays */
			XmlElement displays_element = new XmlElement("arm:AnalysisResultDisplays");
			md_ver_element.addElement(displays_element);
			for (DefineARMDisplayModel display : displays) {
				/* arm:ResultDisplay */
				XmlElement display_element = new XmlElement("arm:ResultDisplay");
				displays_element.addElement(display_element);
				display_element.addAttribute("OID", display.toOid());
				display_element.addAttribute("Name", display.display_name);
				/* Description */
				XmlElement display_desc_element = new XmlElement("Description");
				display_element.addElement(display_desc_element);
				XmlElement display_trans_element = new XmlElement("TranslatedText");
				display_desc_element.addElement(display_trans_element);
				display_trans_element.addAttribute("xml:lang", display.display_lang);
				display_trans_element.addText(display.display_desc);
				/* def:DocumentRef */
				for (DocumentRef document_ref : display.document_refs) {
					XmlElement doc_ref_element = new XmlElement("def:DocumentRef");
					display_element.addElement(doc_ref_element);
					doc_ref_element.addAttribute("leafID", DefineDocumentModel.createOid(document_ref.document_id));
					if (StringUtils.isNotEmpty(document_ref.document_page_type)) {
						XmlElement pdf_page_element = new XmlElement("def:PDFPageRef");
						doc_ref_element.addElement(pdf_page_element);
						pdf_page_element.addAttribute("Type", document_ref.document_page_type);
						pdf_page_element.addAttribute("PageRefs", document_ref.document_page_reference);
						pdf_page_element.addAttribute("FirstPage", document_ref.document_first_page);
						pdf_page_element.addAttribute("LastPage", document_ref.document_last_page);
						if (!"2.0.0".equals(DEFINE_VERSION)) {
							pdf_page_element.addAttribute("Title", document_ref.document_page_title);
						}
					}
				}
				for (DefineARMResultPk result_pk : display.arm_result_pks) {
					DefineARMResultModel result = define.get(result_pk);
					/* arm:AnalysisResult */
					XmlElement result_element = new XmlElement("arm:AnalysisResult");
					display_element.addElement(result_element);
					result_element.addAttribute("OID", result.toOid());
					result_element.addAttribute("ParameterOID", result.param_oid);
					result_element.addAttribute("AnalysisReason", result.analysis_reason);
					result_element.addAttribute("AnalysisPurpose", result.analysis_purpose);
					/* Description */
					XmlElement result_desc_element = new XmlElement("Description");
					result_element.addElement(result_desc_element);
					XmlElement result_trans_element = new XmlElement("TranslatedText");
					result_desc_element.addElement(result_trans_element);
					result_trans_element.addAttribute("xml:lang", result.result_lang);
					result_trans_element.addText(result.result_desc);
					/* arm:AnalysisDatasets */
					XmlElement datasets_element = new XmlElement("arm:AnalysisDatasets");
					result_element.addElement(datasets_element);
					datasets_element.addAttribute("def:CommentOID", result.dataset_comment_oid);
					List<DefineARMDatasetModel> filtered_arm_datasets = arm_datasets.stream()
							.filter(o -> StringUtils.equals(o.display_name, result.display_name) && StringUtils.equals(o.result_key, result.result_key))
							.collect(Collectors.toList());
					for (DefineARMDatasetModel arm_dataset : filtered_arm_datasets) {
						/* arm:AnalysisDataset */
						XmlElement dataset_element = new XmlElement("arm:AnalysisDataset");
						datasets_element.addElement(dataset_element);
						dataset_element.addAttribute("ItemGroupOID", arm_dataset.dataset_oid);
						/* def:WhereClauseRef */
						DefineWCModel wc = define.get(arm_dataset.where_clause_pk);
						XmlElement wc_ref_element = new XmlElement("def:WhereClauseRef");
						dataset_element.addElement(wc_ref_element);
						wc_ref_element.addAttribute("WhereClauseOID", wc.toOid());
						/* def:WhereClauseDef */
						XmlElement wc_def_element = new XmlElement("def:WhereClauseDef");
						md_ver_element.addElement(wc_def_element);
						wc_def_element.addAttribute("OID", wc.toOid());
						wc_def_element.addAttribute("def:CommentOID", wc.comment_oid);
						/* RangeCheck */
						for (WCCondition wc_condition : wc.wc_conditions) {
							XmlElement range_check_element = new XmlElement("RangeCheck");
							wc_def_element.addElement(range_check_element);
							range_check_element.addAttribute("Comparator", wc_condition.operator);
							range_check_element.addAttribute("SoftHard", "Soft");
							range_check_element.addAttribute("def:ItemOID", DefineVariableModel.createOid(wc_condition.dataset_name, wc_condition.variable_name));
							for (String check_value : wc_condition.values) {
								XmlElement check_value_element = new XmlElement("CheckValue");
								range_check_element.addElement(check_value_element);
								check_value_element.addText(check_value);
							}
						}
						/* arm:AnalysisVariable */
						if (!arm_dataset.analysis_variable_oids.isEmpty()) {
							for (String analysis_variable_oid : arm_dataset.analysis_variable_oids) {
								XmlElement analysis_var_element = new XmlElement("arm:AnalysisVariable");
								dataset_element.addElement(analysis_var_element);
								analysis_var_element.addAttribute("ItemOID", analysis_variable_oid);
							}
						}
					}
					if (StringUtils.isNotEmpty(result.docm_text)) {
						/* arm:Documentation */
						XmlElement docm_element = new XmlElement("arm:Documentation");
						result_element.addElement(docm_element);
						/* Description */
						XmlElement desc_element = new XmlElement("Description");
						docm_element.addElement(desc_element);
						XmlElement trans_element = new XmlElement("TranslatedText");
						desc_element.addElement(trans_element);
						trans_element.addAttribute("xml:lang", result.docm_lang);
						trans_element.addText(result.docm_text);
						for (DocumentRef document_ref : result.docm_document_refs) {
							/* def:DocumentRef */
							XmlElement doc_ref_element = new XmlElement("def:DocumentRef");
							docm_element.addElement(doc_ref_element);
							doc_ref_element.addAttribute("leafID", DefineDocumentModel.createOid(document_ref.document_id));
							if (StringUtils.isNotEmpty(document_ref.document_page_type)) {
								XmlElement pdf_page_element = new XmlElement("def:PDFPageRef");
								doc_ref_element.addElement(pdf_page_element);
								pdf_page_element.addAttribute("Type", document_ref.document_page_type);
								pdf_page_element.addAttribute("PageRefs", document_ref.document_page_reference);
								pdf_page_element.addAttribute("FirstPage", document_ref.document_first_page);
								pdf_page_element.addAttribute("LastPage", document_ref.document_last_page);
								if (!"2.0.0".equals(DEFINE_VERSION)) {
									pdf_page_element.addAttribute("Title", document_ref.document_page_title);
								}
							}
						}
					}
					if (StringUtils.isNotEmpty(result.prog_code_text) || !result.prog_code_document_refs.isEmpty()) {
						/* arm:ProgrammingCode */
						XmlElement prog_code_element = new XmlElement("arm:ProgrammingCode");
						result_element.addElement(prog_code_element);
						prog_code_element.addAttribute("Context", result.prog_code_context);
						if (StringUtils.isNotEmpty(result.prog_code_text)) {
							/* arm:Code */
							XmlElement code_element = new XmlElement("arm:Code");
							prog_code_element.addElement(code_element);
							code_element.addText(result.prog_code_text);
						}
						for (DocumentRef document_ref : result.docm_document_refs) {
							/* def:DocumentRef */
							XmlElement doc_ref_element = new XmlElement("def:DocumentRef");
							prog_code_element.addElement(doc_ref_element);
							doc_ref_element.addAttribute("leafID", DefineDocumentModel.createOid(document_ref.document_id));
							if (StringUtils.isNotEmpty(document_ref.document_page_type)) {
								XmlElement pdf_page_element = new XmlElement("def:PDFPageRef");
								doc_ref_element.addElement(pdf_page_element);
								pdf_page_element.addAttribute("Type", document_ref.document_page_type);
								pdf_page_element.addAttribute("PageRefs", document_ref.document_page_reference);
								pdf_page_element.addAttribute("FirstPage", document_ref.document_first_page);
								pdf_page_element.addAttribute("LastPage", document_ref.document_last_page);
								if (!"2.0.0".equals(DEFINE_VERSION)) {
									pdf_page_element.addAttribute("Title", document_ref.document_page_title);
								}
							}
						}
					}
				}
			}
		}
		return xml_document;
	}
	
	public void addAutoSuppElements(DefineModel define, XmlDocument xml_document) {
		List<DefineVariableModel> supp_variables = new ArrayList<>();
		if ("2.0.0".equals(DEFINE_VERSION)) {
			supp_variables = define.listSortedVariable().stream().filter(o -> o.is_supp == YorN.Yes && o.has_no_data == null).collect(Collectors.toList());
		} else {
			supp_variables = define.listSortedVariable().stream().filter(o -> o.is_supp == YorN.Yes).collect(Collectors.toList());
		}
		Set<String> str_datasets = supp_variables.stream().map(o -> o.dataset_name).collect(Collectors.toSet());
		List<DefineDatasetModel> datasets_w_supp = define.listSortedDataset().stream().filter(o -> str_datasets.contains(o.dataset_name))
				.sorted((o1, o2) -> StringUtils.compare(o1.dataset_name, o2.dataset_name)).collect(Collectors.toList());
		XmlElement root_element = xml_document.getRootElement();
		XmlElement study_element = root_element.getElementByName("Study");
		XmlElement md_ver_element = study_element.getElementByName("MetaDataVersion");
		for (DefineDatasetModel dataset_w_supp : datasets_w_supp) {
			/* ItemGroupDef */
			String dataset_name = "SUPP" + dataset_w_supp.dataset_name;
			XmlElement ig_def_element = new XmlElement("ItemGroupDef");
			md_ver_element.addElement(ig_def_element);
			ig_def_element.addAttribute("OID", DefineDatasetModel.createOid(dataset_name));
//			ig_def_element.addAttribute("Domain", dataset_w_supp.domain);
			ig_def_element.addAttribute("Domain", "SUPP" + dataset_w_supp.domain);	//To avoid DD0050
			ig_def_element.addAttribute("Name", dataset_name);
			ig_def_element.addAttribute("Repeating", YorN.Yes.name());
			ig_def_element.addAttribute("IsReferenceData", YorN.No.name());
			ig_def_element.addAttribute("SASDatasetName", dataset_name);
			ig_def_element.addAttribute("Purpose", dataset_w_supp.purpose);
			ig_def_element.addAttribute("def:Structure", "One record per IDVAR, IDVARVAL, and QNAM value per subject");
			if ("2.0.0".equals(DEFINE_VERSION)) {
				ig_def_element.addAttribute("def:Class", "RELATIONSHIP");
			}
			ig_def_element.addAttribute("def:ArchiveLocationID", DefineDatasetModel.createLeafOid(dataset_name));
			if (!"2.0.0".equals(DEFINE_VERSION)) {
				if (StringUtils.isEmpty(dataset_w_supp.standard_oid)) {
					ig_def_element.addAttribute("def:IsNonStandard", YorNull.Yes.name());
				} else {
					ig_def_element.addAttribute("def:StandardOID", dataset_w_supp.standard_oid);
				}
				if (dataset_w_supp.has_no_data == YorNull.Yes) {
					ig_def_element.addAttribute("def:HasNoData", YorNull.Yes.name());
				} else {
					List<DefineVariableModel> filtered_supp_variables = supp_variables.stream().filter(o -> StringUtils.equals(dataset_w_supp.dataset_name, o.dataset_name)).collect(Collectors.toList());
					List<DefineVariableModel> variables_w_data = filtered_supp_variables.stream().filter(o -> o.has_no_data == null).collect(Collectors.toList());
					if (variables_w_data.isEmpty()) {
						ig_def_element.addAttribute("def:HasNoData", YorNull.Yes.name());
					}
				}
			}

			/* Description */
			XmlElement desc_element = new XmlElement("Description");
			ig_def_element.addElement(desc_element);
			XmlElement trans_element = new XmlElement("TranslatedText");
			desc_element.addElement(trans_element);
			trans_element.addAttribute("xml:lang", DEFAULTLANG);
			trans_element.addText("Supplemental Qualifiers for " + dataset_w_supp.dataset_name);
			/* Add ItemRef/ItemDef elements */
			List<SuppVariable> enum_supp_variables = Arrays.asList(new SuppVariable[]{SuppVariable.STUDYID, SuppVariable.RDOMAIN, SuppVariable.USUBJID, SuppVariable.IDVAR, SuppVariable.IDVARVAL, SuppVariable.QNAM, SuppVariable.QLABEL, SuppVariable.QVAL, SuppVariable.QORIG, SuppVariable.QEVAL});
			DefineVariableModel studyid = define.listSortedVariable().stream()
					.filter(o -> StringUtils.equals(o.dataset_name, dataset_w_supp.dataset_name) && "STUDYID".equals(o.variable_name))
					.findFirst().orElse(null);
			DefineVariableModel usubjid = define.listSortedVariable().stream()
					.filter(o -> StringUtils.equals(o.dataset_name, dataset_w_supp.dataset_name) && "USUBJID".equals(o.variable_name))
					.findFirst().orElse(null);
			DefineVariableModel seq = define.listSortedVariable().stream()
					.filter(o -> StringUtils.equals(o.dataset_name, dataset_w_supp.dataset_name) && (dataset_w_supp.domain + "SEQ").equals(o.variable_name))
					.findFirst().orElse(null);
			List<DefineVariableModel> filtered_supp_variables = supp_variables.stream().filter(o -> StringUtils.equals(o.dataset_name, dataset_w_supp.dataset_name)).collect(Collectors.toList());
			int order_number = 1;
			for (SuppVariable enum_supp_variable : enum_supp_variables) {
				/* ItemRef */
				XmlElement item_ref_element = new XmlElement("ItemRef");
				ig_def_element.addElement(item_ref_element);
				item_ref_element.addAttribute("ItemOID", DefineVariableModel.createOid(dataset_name, enum_supp_variable.name()));
				item_ref_element.addAttribute("OrderNumber", String.valueOf(order_number++));
				YorN mandatory = YorN.No;
				if (enum_supp_variable == SuppVariable.STUDYID || enum_supp_variable == SuppVariable.RDOMAIN || enum_supp_variable == SuppVariable.USUBJID || enum_supp_variable == SuppVariable.QNAM || enum_supp_variable == SuppVariable.QLABEL || enum_supp_variable == SuppVariable.QVAL || enum_supp_variable == SuppVariable.QORIG) {
					mandatory = YorN.Yes;
				}
				item_ref_element.addAttribute("Mandatory", mandatory.name());
				if (enum_supp_variable == SuppVariable.STUDYID) {
					item_ref_element.addAttribute("KeySequence", "1");
					item_ref_element.addAttribute("MethodOID", studyid.method_oid);
				} else if (enum_supp_variable == SuppVariable.RDOMAIN) {
					item_ref_element.addAttribute("KeySequence", "2");
//					/* Add MethodOID as RDOMAIN is derived. */
//					item_ref_element.addAttribute("MethodOID", DefineVariableModel.createMethodOid(dataset_name, enum_supp_variable.name()));
//					/* Add MethodDef */
//					XmlElement method_element = new XmlElement("MethodDef");
//					md_ver_element.addElement(method_element);
//					method_element.addAttribute("OID", DefineVariableModel.createMethodOid(dataset_name, enum_supp_variable.name()));
//					method_element.addAttribute("Name", "Algorithm to derive RDOMAIN");
//					method_element.addAttribute("Type", "Computation");
//					XmlElement method_desc_element = new XmlElement("Description");
//					method_element.addElement(method_desc_element);
//					XmlElement method_trans_element = new XmlElement("TranslatedText");
//					method_desc_element.addElement(method_trans_element);
//					method_trans_element.addAttribute("xml:lang", "en");
//					method_trans_element.addText("Domain abbreviation from where data originated.");
				} else if (enum_supp_variable == SuppVariable.USUBJID) {
					item_ref_element.addAttribute("KeySequence", "3");
					item_ref_element.addAttribute("MethodOID", usubjid.method_oid);
				} else if (enum_supp_variable == SuppVariable.IDVAR) {
					item_ref_element.addAttribute("KeySequence", "4");
				} else if (enum_supp_variable == SuppVariable.IDVARVAL) {
					item_ref_element.addAttribute("KeySequence", "5");
				} else if (enum_supp_variable == SuppVariable.QNAM) {
					item_ref_element.addAttribute("KeySequence", "6");
				}
				/* ItemDef */
				XmlElement item_def_element = new XmlElement("ItemDef");
				md_ver_element.addElement(item_def_element);
				item_def_element.addAttribute("OID", DefineVariableModel.createOid(dataset_name, enum_supp_variable.name()));
				item_def_element.addAttribute("Name", enum_supp_variable.name());
				item_def_element.addAttribute("DataType", "text");
				if (enum_supp_variable == SuppVariable.STUDYID) {
					if (studyid != null) {
						item_def_element.addAttribute("Length", studyid.length);
					}
				} else if (enum_supp_variable == SuppVariable.RDOMAIN) {
					item_def_element.addAttribute("Length", "2");
				} else if (enum_supp_variable == SuppVariable.USUBJID) {
					if (usubjid != null) {
						item_def_element.addAttribute("Length", usubjid.length);
					}
				} else if (enum_supp_variable == SuppVariable.IDVAR) {
					if ("DM".equals(dataset_w_supp.dataset_name)) {
						item_def_element.addAttribute("Length", "1");
					} else {
						item_def_element.addAttribute("Length", "5");
					}
				} else if (enum_supp_variable == SuppVariable.IDVARVAL) {
					if ("DM".equals(dataset_w_supp.dataset_name)) {
						item_def_element.addAttribute("Length", "1");
					} else {
						if (seq != null) {
							item_def_element.addAttribute("Length", seq.length);
						}
					}
				} else if (enum_supp_variable == SuppVariable.QNAM) {
					int length = 1;
					for (int i = 0; i < filtered_supp_variables.size(); i++) {
						DefineVariableModel filtered_supp_variable = filtered_supp_variables.get(i);
						if (length < filtered_supp_variable.variable_name.length()) {
							length = filtered_supp_variable.variable_name.length();
						}
					}
					item_def_element.addAttribute("Length", String.valueOf(length));
				} else if (enum_supp_variable == SuppVariable.QLABEL) {
					int length = 1;
					for (int i = 0; i < filtered_supp_variables.size(); i++) {
						DefineVariableModel filtered_supp_variable = filtered_supp_variables.get(i);
						if (length < filtered_supp_variable.variable_label.length()) {
							length = filtered_supp_variable.variable_label.length();
						}
					}
					item_def_element.addAttribute("Length", String.valueOf(length));
				} else if (enum_supp_variable == SuppVariable.QVAL) {
					int length = 1;
					for (int i = 0; i < filtered_supp_variables.size(); i++) {
						DefineVariableModel filtered_supp_variable = filtered_supp_variables.get(i);
						int parsed_length = NumberUtils.toInt(getRepeatLength(filtered_supp_variable.length, filtered_supp_variable.repeat_n_length));
						if (length < parsed_length) {
							length = parsed_length;
						}
					}
					item_def_element.addAttribute("Length", String.valueOf(length));
				} else if (enum_supp_variable == SuppVariable.QORIG) {
					int length = 1;
					for (int i = 0; i < filtered_supp_variables.size(); i++) {
						DefineVariableModel filtered_supp_variable = filtered_supp_variables.get(i);
						if (length < filtered_supp_variable.origin.length()) {
							length = filtered_supp_variable.origin.length();
						}
					}
					item_def_element.addAttribute("Length", String.valueOf(length));
				} else if (enum_supp_variable == SuppVariable.QEVAL) {
					int length = 1;
					for (int i = 0; i < filtered_supp_variables.size(); i++) {
						DefineVariableModel filtered_supp_variable = filtered_supp_variables.get(i);
						if (length < filtered_supp_variable.evaluator.length()) {
							length = filtered_supp_variable.evaluator.length();
						}
					}
					item_def_element.addAttribute("Length", String.valueOf(length));
				}
				item_def_element.addAttribute("SASFieldName", enum_supp_variable.name());
				/* Description */
				XmlElement item_desc_element = new XmlElement("Description");
				item_def_element.addElement(item_desc_element);
				XmlElement item_trans_element = new XmlElement("TranslatedText");
				item_desc_element.addElement(item_trans_element);
				item_trans_element.addAttribute("xml:lang", DEFAULTLANG);
				item_trans_element.addText(enum_supp_variable.label());
				/* def:Origin */
				XmlElement origin_element = new XmlElement("def:Origin");
				if (enum_supp_variable == SuppVariable.STUDYID) {
					if (!"2.0.0".equals(DEFINE_VERSION) && ("CRF".equals(studyid.origin) || "eDT".equals(studyid.origin)) ) {
						origin_element.addAttribute("Type", "Collected");
						origin_element.addAttribute("Source", studyid.source);
					} else {
						if ("Collected".equals(studyid.origin)) {
							if ("Vendor".equals(studyid.source)) {
								origin_element.addAttribute("Type", "eDT");
							} else {
								origin_element.addAttribute("Type", "CRF");
							}
						} else {
							origin_element.addAttribute("Type", studyid.origin);
						}
					}
					item_def_element.addElement(origin_element);
				} else if (enum_supp_variable == SuppVariable.RDOMAIN) {
					origin_element.addAttribute("Type", "Assigned");
					item_def_element.addElement(origin_element);
				} else if (enum_supp_variable == SuppVariable.USUBJID) {
					if (!"2.0.0".equals(DEFINE_VERSION) && ("CRF".equals(usubjid.origin) || "eDT".equals(usubjid.origin)) ) {
						origin_element.addAttribute("Type", "Collected");
						origin_element.addAttribute("Source", usubjid.source);
					} else {
						if ("Collected".equals(usubjid.origin)) {
							if ("Vendor".equals(usubjid.source)) {
								origin_element.addAttribute("Type", "eDT");
							} else {
								origin_element.addAttribute("Type", "CRF");
							}
						} else {
							origin_element.addAttribute("Type", usubjid.origin);
						}
					}
					item_def_element.addElement(origin_element);
				} else if (enum_supp_variable == SuppVariable.QVAL) {
					// Empty origin
				} else {
					origin_element.addAttribute("Type", "Assigned");
					item_def_element.addElement(origin_element);
				}
				
				/**
				 * Create Value Level Metadata to QVAL
				 */
				if (enum_supp_variable == SuppVariable.QVAL) {
					/* def:ValueListRef */
					XmlElement val_list_element = new XmlElement("def:ValueListRef");
					item_def_element.addElement(val_list_element);
					String value_list_oid = DefineVariableModel.createValueListOid(dataset_name, enum_supp_variable.name());
					val_list_element.addAttribute("ValueListOID", value_list_oid);
					/* def:ValueListDef */
					XmlElement val_list_def_element = new XmlElement("def:ValueListDef");
					md_ver_element.addElement(val_list_def_element);
					val_list_def_element.addAttribute("OID", value_list_oid);
					int val_order_number = 1;
					for (int i = 0; i < filtered_supp_variables.size(); i++) {
						DefineVariableModel is_supp_variable = filtered_supp_variables.get(i);
//						for (int j = 0; j <= is_supp_variable.repeat_n; j++) {
							/* ItemRef */
							String value_name = is_supp_variable.variable_name;
							XmlElement value_element = new XmlElement("ItemRef");
							val_list_def_element.addElement(value_element);
							String val_item_oid = DefineValueModel.createOid(dataset_name, enum_supp_variable.name(), value_name, "");
							value_element.addAttribute("ItemOID", val_item_oid);
							value_element.addAttribute("OrderNumber", String.valueOf(val_order_number++));
							value_element.addAttribute("Mandatory", is_supp_variable.mandatory.name());
							value_element.addAttribute("MethodOID", is_supp_variable.method_oid);
							if (is_supp_variable.has_no_data == YorNull.Yes) {
								value_element.addAttribute("def:HasNoData", YorNull.Yes.name());
							}
							/* def:WhereClauseRef */
							XmlElement wc_ref_element = new XmlElement("def:WhereClauseRef");
							value_element.addElement(wc_ref_element);
							String wc_oid = DefineWCModel.createOid(dataset_name, enum_supp_variable.name(), value_name, "", "");
							wc_ref_element.addAttribute("WhereClauseOID", wc_oid);
							/* def:WhereClauseDef */
							XmlElement wc_def_element = new XmlElement("def:WhereClauseDef");
							md_ver_element.addElement(wc_def_element);
							wc_def_element.addAttribute("OID", wc_oid);
							XmlElement range_check_element = new XmlElement("RangeCheck");
							wc_def_element.addElement(range_check_element);
							range_check_element.addAttribute("Comparator", "EQ");
							range_check_element.addAttribute("SoftHard", "Soft");
							range_check_element.addAttribute("def:ItemOID", DefineVariableModel.createOid(dataset_name, "QNAM"));
							XmlElement check_value_element = new XmlElement("CheckValue");
							range_check_element.addElement(check_value_element);
							check_value_element.addText(is_supp_variable.variable_name);
							/*
							 * ItemDef
							 */
							XmlElement val_item_def_element = new XmlElement("ItemDef");
							md_ver_element.addElement(val_item_def_element);
							val_item_def_element.addAttribute("OID", val_item_oid);
							val_item_def_element.addAttribute("Name", value_name);
							val_item_def_element.addAttribute("DataType", is_supp_variable.data_type);
							val_item_def_element.addAttribute("Length", getRepeatLength(is_supp_variable.length, is_supp_variable.repeat_n_length));
							val_item_def_element.addAttribute("SignificantDigits", is_supp_variable.significant_digits);
							val_item_def_element.addAttribute("SASFieldName", is_supp_variable.sas_field_name);
							val_item_def_element.addAttribute("def:DisplayFormat", is_supp_variable.display_format);
							val_item_def_element.addAttribute("def:CommentOID", is_supp_variable.comment_oid);
							/* Description */
							XmlElement val_desc_element = new XmlElement("Description");
							val_item_def_element.addElement(val_desc_element);
							XmlElement val_trans_element = new XmlElement("TranslatedText");
							val_desc_element.addElement(val_trans_element);
							val_trans_element.addAttribute("xml:lang", DEFAULTLANG);
							val_trans_element.addText(is_supp_variable.variable_label);
							/* CodeListRef */
							if (StringUtils.isNotEmpty(is_supp_variable.codelist)) {
								XmlElement cl_ref_element = new XmlElement("CodeListRef");
								val_item_def_element.addElement(cl_ref_element);
								cl_ref_element.addAttribute("CodeListOID", DefineCodelistModel.createCodelistOid(is_supp_variable.codelist));
							}
							/* def:Origin */
							if (StringUtils.isNotEmpty(is_supp_variable.origin)) {
								XmlElement val_origin_element = new XmlElement("def:Origin");
								val_item_def_element.addElement(val_origin_element);
								if ("2.0.0".equals(DEFINE_VERSION)) {
									if ("Collected".equals(is_supp_variable.origin)) {
										if ("Vendor".equals(is_supp_variable.source)) {
											val_origin_element.addAttribute("Type", "eDT");
										} else {
											val_origin_element.addAttribute("Type", "CRF");
										}
									} else {
										val_origin_element.addAttribute("Type", is_supp_variable.origin);
									}
								} else {
									if ("CRF".equals(is_supp_variable.origin) || "eDT".equals(is_supp_variable.origin)) {
										val_origin_element.addAttribute("Type", "Collected");
									} else {
										val_origin_element.addAttribute("Type", is_supp_variable.origin);
									}
									val_origin_element.addAttribute("Source", is_supp_variable.source);
								}
								if (StringUtils.isNotEmpty(is_supp_variable.predecessor)) {
									XmlElement desc_element2 = new XmlElement("Description");
									val_origin_element.addElement(desc_element2);
									XmlElement trans_element2 = new XmlElement("TranslatedText");
									desc_element2.addElement(trans_element2);
									trans_element2.addAttribute("xml:lang", DEFAULTLANG);
									trans_element2.addText(is_supp_variable.predecessor);
								}
								if (StringUtils.isNotEmpty(is_supp_variable.crf_page_type)) {
									XmlElement doc_ref_element = new XmlElement("def:DocumentRef");
									val_origin_element.addElement(doc_ref_element);
									doc_ref_element.addAttribute("leafID", DefineDocumentModel.createOid(is_supp_variable.crf_id));
									XmlElement pdf_page_element = new XmlElement("def:PDFPageRef");
									doc_ref_element.addElement(pdf_page_element);
									pdf_page_element.addAttribute("Type", is_supp_variable.crf_page_type);
									pdf_page_element.addAttribute("PageRefs", is_supp_variable.crf_page_reference);
									pdf_page_element.addAttribute("FirstPage", is_supp_variable.crf_first_page);
									pdf_page_element.addAttribute("LastPage", is_supp_variable.crf_last_page);
									if (!"2.0.0".equals(DEFINE_VERSION)) {
										pdf_page_element.addAttribute("Title", is_supp_variable.crf_page_title);
									}
								}
							}
							/* Alias */
							if (!"2.0.0".equals(DEFINE_VERSION)) {
								if (StringUtils.isNotEmpty(is_supp_variable.alias_context) && StringUtils.isNotEmpty(is_supp_variable.alias_name)) {
									XmlElement alias_element = new XmlElement("Alias");
									val_item_def_element.addElement(alias_element);
									alias_element.addAttribute("Context", is_supp_variable.alias_context);
									alias_element.addAttribute("Name", is_supp_variable.alias_name);
								}
							}
//						}
					}
				}
			}
			/* Alias */
			if (!"2.0.0".equals(DEFINE_VERSION) || Utils.isSplitDataset(this.config.e2dDatasetType, dataset_name)) {
				XmlElement alias_element = new XmlElement("Alias");
				ig_def_element.addElement(alias_element);
				alias_element.addAttribute("Context", "DomainDescription");
				alias_element.addAttribute("Name", dataset_w_supp.description);
			}
			/* def:Class */
			if (!"2.0.0".equals(DEFINE_VERSION)) {
				XmlElement class_element = new XmlElement("def:Class");
				ig_def_element.addElement(class_element);
				class_element.addAttribute("Name", "RELATIONSHIP");
			}
			/* def:Leaf */
			List<DefineVariableModel> vars_w_data = filtered_supp_variables.stream().filter(o -> o.has_no_data_derived == null).collect(Collectors.toList());
			if (!vars_w_data.isEmpty()) {
				XmlElement leaf_element = new XmlElement("def:leaf");
				ig_def_element.addElement(leaf_element);
				leaf_element.addAttribute("ID", DefineDatasetModel.createLeafOid(dataset_name));
				leaf_element.addAttribute("xlink:href", dataset_name.toLowerCase() + ".xpt");
				XmlElement title_element = new XmlElement("def:title");
				leaf_element.addElement(title_element);
				title_element.addText(dataset_name.toLowerCase() + ".xpt");
			}
		}
	}
	
	/*
	 * Length = Length - (200 * N)
	 */
	/**
	 * Returns length of repeating variable
	 * @param DefineVariableModel.length
	 * @param DefineVariableModel.repeat_n_length
	 * @return Length of repeating variable or return 1(default)
	 */
	public static String getRepeatLength(String str_length, List<String> str_length_0_n) {
		String default_length = "";
		if (StringUtils.isEmpty(str_length)) {
			return default_length;
		}
		if (str_length_0_n == null || str_length_0_n.size() == 0) {
			return str_length;
		} else {
			if (str_length_0_n.isEmpty()) {
				return default_length;
			} else {
				return str_length_0_n.get(0);
			}
		}
	}
	
	public void writeout(XmlDocument xml_document) throws IOException {
		this.sw = new OutputStreamWriter(new FileOutputStream(this.config.e2dOutputLocation, false), config.e2dXmlEncoding);
		this.writer = new BufferedWriter(sw);
		
		/* Sort elements under MetaDataVersion before writing,
		 * because elements were not added in the order as defined by the Define-XML specification. */
		XmlElement root_element = xml_document.getRootElement();
		XmlElement study_element = root_element.getElementByName("Study");
		XmlElement md_ver_element = study_element.getElementByName("MetaDataVersion");
		final List<String> ordered_child_elements = Arrays.asList(new String[]{"def:AnnotatedCRF", "def:SupplementalDoc", "def:ValueListDef", "def:WhereClauseDef", "ItemGroupDef", "ItemDef", "CodeList", "MethodDef", "def:CommentDef", "def:leaf", "arm:AnalysisResultDisplays"});
		md_ver_element.sortElements(new Comparator<XmlElement>() {

			@Override
			public int compare(XmlElement o1, XmlElement o2) {
				if (StringUtils.equals(o1.getName(), o2.getName())) {
					return Integer.compare(md_ver_element.getElementIndex(o1), md_ver_element.getElementIndex(o2));
				} else {
					return Integer.compare(ordered_child_elements.indexOf(o1.getName()), ordered_child_elements.indexOf(o2.getName()));
				}
			}
		});
		/* Sort elements under CodeList elements before writing to place Alias to the end. */
		List<XmlElement> codelist_elements = md_ver_element.getElementsByName("CodeList");
		for (XmlElement codelist_element : codelist_elements) {
			final List<String> ordered_child_elements2 = Arrays.asList(new String[]{"EnumeratedItem", "CodeListItem", "ExternalCodeList", "Alias"});
			codelist_element.sortElements(new Comparator<XmlElement>() {

				@Override
				public int compare(XmlElement o1, XmlElement o2) {
					if (StringUtils.equals(o1.getName(), o2.getName())) {
						return Integer.compare(md_ver_element.getElementIndex(o1), md_ver_element.getElementIndex(o2));
					} else {
						return Integer.compare(ordered_child_elements2.indexOf(o1.getName()), ordered_child_elements2.indexOf(o2.getName()));
					}
				}
			});
		}
		
		this.writer.write(xml_document.toString());
		this.writer.close();
		this.sw.close();
	}
	
	private static String calendarToIso8601(Calendar cal) {
		return cal.get(Calendar.YEAR) + "-"
				+ twoDigits((cal.get(Calendar.MONTH) + 1)) + "-"
				+ twoDigits(cal.get(Calendar.DAY_OF_MONTH)) + "T"
				+ twoDigits(cal.get(Calendar.HOUR_OF_DAY)) + ":"
				+ twoDigits(cal.get(Calendar.MINUTE)) + ":"
				+ twoDigits(cal.get(Calendar.SECOND));
	}
	
	/*
	 * This method formats a component of date/time value into two digits.
	 */
	private static String twoDigits(int n) {
		if (n >= 0 && n <= 9) {
			return new String("0" + new Integer(n).toString());
		} else {
			return new Integer(n).toString();
		}
	}
}
