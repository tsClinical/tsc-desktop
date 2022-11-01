/* 
 * Copyright (c) 2022 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fujitsu.tsc.desktop.exporter.InvalidOidSyntaxException;
import com.fujitsu.tsc.desktop.exporter.RequiredValueMissingException;
import com.fujitsu.tsc.desktop.exporter.DefineXmlWriter.TagType;
import com.fujitsu.tsc.desktop.importer.models.DefineARMDatasetModel.DefineARMDatasetPk;
import com.fujitsu.tsc.desktop.importer.models.DefineARMDisplayModel.DefineARMDisplayPk;
import com.fujitsu.tsc.desktop.importer.models.DefineARMResultModel.DefineARMResultPk;
import com.fujitsu.tsc.desktop.importer.models.DefineCodelistModel.DefineCodelistPk;
import com.fujitsu.tsc.desktop.importer.models.DefineCommentModel.DefineCommentPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDatasetModel.DefineDatasetPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDictionaryModel.DefineDictionaryPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDocumentModel.DefineDocumentPk;
import com.fujitsu.tsc.desktop.importer.models.DefineMethodModel.DefineMethodPk;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorN;
import com.fujitsu.tsc.desktop.importer.models.DefineStandardModel.DefineStandardPk;
import com.fujitsu.tsc.desktop.importer.models.DefineValueModel.DefineValuePk;
import com.fujitsu.tsc.desktop.importer.models.DefineVariableModel.DefineVariablePk;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.DefineWCPk;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.WCCondition;
import com.fujitsu.tsc.desktop.importer.models.OdmFieldModel.OdmFieldPk;
import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.Utils;
import com.fujitsu.tsc.desktop.util.Config.DatasetType;

/**
 * Java object representing a Define excel file which consists of multiple sheets.
 * Each sheet is represented by a respective model object.
 *  - STUDY table ({@link DefineStudyModel})
 *  - STANDARD table ({@link DefineStandardModel})
 *  - DOCUMENT table ({@link DefineDocumentModel})
 *  - DATASET table ({@link DefineDatasetModel})
 *  - VARIABLE table ({@link DefineVariableModel})
 *  - VALUE table ({@link DefineValueModel})
 *  - DICTIONARY table ({@link DefineDictionaryModel})
 *  - CODELIST table ({@link DefineCodelistModel})
 *  
 *  The STUDY table is required for a Define file, but the other tables could be blank.
 */
public class DefineModel {
	private DefineStudyModel define_study;	//STUDY
	private Map<DefineStandardPk, DefineStandardModel> map_define_standard;	//STANDARD
	private Map<DefineDocumentPk, DefineDocumentModel> map_define_document;	//DOCUMENT
	private Map<DefineDatasetPk, DefineDatasetModel> map_define_dataset;	//DATASET
	private Map<DefineVariablePk, DefineVariableModel> map_define_variable;	//VARIABLE
	private Map<DefineValuePk, DefineValueModel> map_define_value;	//VALUE
	private Map<DefineWCPk, DefineWCModel> map_define_wc;	//WhereClauses referenced from VALUE or RESULT2
	private Map<DefineDictionaryPk, DefineDictionaryModel> map_define_dictionary;	//DICTIONARY
	private Map<DefineCodelistPk, DefineCodelistModel> map_define_codelist;	//CODELIST
	private Map<DefineARMDisplayPk, DefineARMDisplayModel> map_define_arm_display;	//RESULT1
	private Map<DefineARMResultPk, DefineARMResultModel> map_define_arm_result;	//Analysis Results referenced from RESULT1
	private Map<DefineARMDatasetPk, DefineARMDatasetModel> map_define_arm_dataset;	//RESULT2
	private Map<DefineMethodPk, DefineMethodModel> map_define_method;	//Methods referenced from VARIABLE and VALUE
	private Map<DefineCommentPk, DefineCommentModel> map_define_comment;	//Comments
	
	public DefineModel() {
		this.define_study = new DefineStudyModel();
		this.map_define_standard = new HashMap<>();
		this.map_define_document = new HashMap<>();
		this.map_define_dataset = new HashMap<>();
		this.map_define_variable = new HashMap<>();
		this.map_define_value = new HashMap<>();
		this.map_define_wc = new HashMap<>();
		this.map_define_dictionary = new HashMap<>();
		this.map_define_codelist = new HashMap<>();
		this.map_define_arm_display = new HashMap<>();
		this.map_define_arm_result = new HashMap<>();
		this.map_define_arm_dataset = new HashMap<>();
		this.map_define_method = new HashMap<>();
		this.map_define_comment = new HashMap<>();
	}
	
	public static enum YorN {
		Yes, No;
		
		public static YorN parse(String yOrN) {
			if ("Yes".equals(yOrN)) {
				return YorN.Yes;
			} else if ("Y".equals(yOrN)) {
				return YorN.Yes;
			} else {
				return YorN.No;
			}
		}
	}
	
	public static enum YorNull {
		Yes;
		
		public static YorNull parse(String yOrNull) {
			if ("Yes".equals(yOrNull)) {
				return YorNull.Yes;
			} else if ("Y".equals(yOrNull)) {
				return YorNull.Yes;
			} else {
				return null;
			}
		}
	}
	
	public static enum DocType {
		AnnotatedCRF, SupplementalDoc, Other;
		
		public static DocType parse(String docType) {
			if ("AnnotatedCRF".equals(docType)) {
				return DocType.AnnotatedCRF;
			} else if ("SupplementalDoc".equals(docType)) {
				return DocType.SupplementalDoc;
			} else {
				return DocType.Other;
			}
		}
	}
	
	public static class DocumentRef {
		/** DocumentID */
		public String document_id;
		/** Document Page Type */
		public String document_page_type;
		/** Document Page Reference */
		public String document_page_reference;
		/** Document First Page */
		public String document_first_page;
		/** Document Last Page */
		public String document_last_page;
		/** Document Page Title */
		public String document_page_title;
		
		public DocumentRef(String document_id) {
			this.document_id = document_id;
			this.document_page_type = "";
			this.document_page_reference = "";
			this.document_first_page = "";
			this.document_last_page = "";
			this.document_page_title = "";
		}
	}

	
	public void put(DefineStudyModel study) {
		this.define_study = study;
	}
	
	public DefineStudyModel getStudy() {
		return this.define_study;
	}
	
	public List<DefineStandardModel> listSortedStandard() {
		List<DefineStandardModel> rtn = new ArrayList<>();
		rtn = this.map_define_standard.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<DefineDocumentModel> listSortedDocument() {
		List<DefineDocumentModel> rtn = new ArrayList<>();
		rtn = this.map_define_document.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<DefineDatasetModel> listSortedDataset() {
		List<DefineDatasetModel> rtn = new ArrayList<>();
		rtn = this.map_define_dataset.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<DefineVariableModel> listSortedVariable() {
		List<DefineVariableModel> rtn = new ArrayList<>();
		rtn = this.map_define_variable.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<DefineValueModel> listSortedValue() {
		List<DefineValueModel> rtn = new ArrayList<>();
		rtn = this.map_define_value.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<DefineDictionaryModel> listSortedDictionary() {
		List<DefineDictionaryModel> rtn = new ArrayList<>();
		rtn = this.map_define_dictionary.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<DefineCodelistModel> listSortedCodelist() {
		List<DefineCodelistModel> rtn = new ArrayList<>();
		rtn = this.map_define_codelist.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<DefineARMDisplayModel> listSortedARMDisplay() {
		List<DefineARMDisplayModel> rtn = new ArrayList<>();
		rtn = this.map_define_arm_display.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}

	public List<DefineARMResultModel> listSortedARMResult() {
		List<DefineARMResultModel> rtn = new ArrayList<>();
		rtn = this.map_define_arm_result.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<DefineARMDatasetModel> listSortedARMDataset() {
		List<DefineARMDatasetModel> rtn = new ArrayList<>();
		rtn = this.map_define_arm_dataset.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<DefineMethodModel> listSortedMethod() {
		List<DefineMethodModel> rtn = new ArrayList<>();
		rtn = this.map_define_method.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<DefineCommentModel> listSortedComment() {
		List<DefineCommentModel> rtn = new ArrayList<>();
		rtn = this.map_define_comment.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public DefineDocumentModel get(DefineDocumentPk key) {
		return this.map_define_document.get(key);
	}
	
	public void put(DefineStandardPk key, DefineStandardModel standard) {
		this.map_define_standard.put(key, standard);
	}
	
	public DefineStandardModel get(DefineStandardPk key) {
		return this.map_define_standard.get(key);
	}
	
	public void put(DefineDocumentPk key, DefineDocumentModel document) {
		this.map_define_document.put(key, document);
	}
	
	public DefineDatasetModel get(DefineDatasetPk key) {
		return this.map_define_dataset.get(key);
	}
	
	public void put(DefineDatasetPk key, DefineDatasetModel dataset) {
		this.map_define_dataset.put(key, dataset);
	}
	
	public DefineVariableModel get(DefineVariablePk key) {
		return this.map_define_variable.get(key);
	}
	
	public void put(DefineVariablePk key, DefineVariableModel variable) {
		this.map_define_variable.put(key, variable);
	}
	
	public List<DefineVariableModel> getVariableByOid(String item_oid) {
		List<DefineVariableModel> rtn = new ArrayList<>();
		Set<Entry<DefineVariablePk, DefineVariableModel>> entries = this.map_define_variable.entrySet();
		for (Entry<DefineVariablePk, DefineVariableModel> entry : entries) {
			DefineVariableModel variable = entry.getValue();
			if (StringUtils.equals(variable.variable_oid, item_oid)) {
				rtn.add(variable);
			}
		}
		return rtn;
	}

	public DefineValueModel get(DefineValuePk key) {
		return this.map_define_value.get(key);
	}
	
	public void put(DefineValuePk key, DefineValueModel value) {
		this.map_define_value.put(key, value);
	}
	
	public DefineWCModel get(DefineWCPk key) {
		return this.map_define_wc.get(key);
	}
	
	public void put(DefineWCPk key, DefineWCModel wc) {
		this.map_define_wc.put(key, wc);
	}
	
	public DefineDictionaryModel get(DefineDictionaryPk key) {
		return this.map_define_dictionary.get(key);
	}
	
	public void put(DefineDictionaryPk key, DefineDictionaryModel dictionary) {
		this.map_define_dictionary.put(key, dictionary);
	}
	
	public DefineCodelistModel get(DefineCodelistPk key) {
		return this.map_define_codelist.get(key);
	}
	
	public void put(DefineCodelistPk key, DefineCodelistModel codelist) {
		this.map_define_codelist.put(key, codelist);
	}
	
	public List<DefineCodelistModel> getCodelistByCodelistId(String codelist_oid) {
		List<DefineCodelistModel> rtn = new ArrayList<>();
		Set<Entry<DefineCodelistPk, DefineCodelistModel>> entries = this.map_define_codelist.entrySet();
		for (Entry<DefineCodelistPk, DefineCodelistModel> entry : entries) {
			DefineCodelistModel codelist = entry.getValue();
			if (StringUtils.equals(codelist.codelist_id, codelist_oid)) {
				rtn.add(codelist);
			}
		}
		return rtn;
	}
	
	public DefineARMDisplayModel get(DefineARMDisplayPk key) {
		return this.map_define_arm_display.get(key);
	}
	
	public void put(DefineARMDisplayPk key, DefineARMDisplayModel arm) {
		this.map_define_arm_display.put(key, arm);
	}
	
	public DefineARMResultModel get(DefineARMResultPk key) {
		return this.map_define_arm_result.get(key);
	}
	
	public void put(DefineARMResultPk key, DefineARMResultModel result) {
		this.map_define_arm_result.put(key, result);
	}
	
	public DefineARMDatasetModel get(DefineARMDatasetPk key) {
		return this.map_define_arm_dataset.get(key);
	}
	
	public void put(DefineARMDatasetPk key, DefineARMDatasetModel arm) {
		this.map_define_arm_dataset.put(key, arm);
	}
	
	public DefineMethodModel get(DefineMethodPk key) {
		return this.map_define_method.get(key);
	}
	
	public void put(DefineMethodPk key, DefineMethodModel method) {
		this.map_define_method.put(key, method);
	}
	
	public DefineCommentModel get(DefineCommentPk key) {
		return this.map_define_comment.get(key);
	}
	
	public void put(DefineCommentPk key, DefineCommentModel comment) {
		this.map_define_comment.put(key, comment);
	}
	
	/**
	 * (1) Find Variable metadata of QVALs in SUPP datasets
	 * (2) Find VLMs of (1)
	 * (3) Find QNAM values from WCs of (2)
	 * (4) Create Variable metadata from (3)
	 * (5) Remove Dataset, Variable and VLM of SUPP datasets
	 */
	public void convertToAutoSupp() {
		/* (1) Find Variable metadata of QVALs in SUPP datasets */
		List<DefineVariableModel> qvals = new ArrayList<>();
		Set<Entry<DefineVariablePk, DefineVariableModel>> variable_entries = this.map_define_variable.entrySet();
		for (Entry<DefineVariablePk, DefineVariableModel> entry : variable_entries) {
			DefineVariableModel variable = entry.getValue();
			if (StringUtils.startsWith(variable.dataset_name, "SUPP") && "QVAL".equals(variable.variable_name)) {
				qvals.add(variable);
			}
		}
		/* (2) Find VLMs of (1) */
		List<DefineValueModel> qval_values = new ArrayList<>();
		for (DefineVariableModel qval : qvals) {
			List<DefineValueModel> values = this.listSortedValue();
			for (DefineValueModel value : values) {
				if (StringUtils.equals(qval.valuelist_oid, value.valuelist_oid)) {
					qval_values.add(value);
				}
			}
		}
		/* (3) Find QNAM values from WCs of (2) */
		List<DefineValueModel> nsvs = new ArrayList<>();	//Pair of QNAM and qval_value
		for (DefineValueModel qval_value : qval_values) {
			for (DefineWCPk where_caluse_pk : qval_value.where_clause_pks) {
				DefineWCModel wc = this.map_define_wc.get(where_caluse_pk);
				if (wc != null && !wc.wc_conditions.isEmpty()) {
					for (WCCondition wc_condition : wc.wc_conditions) {
						if ("QNAM".equals(wc_condition.variable_name)) {
							if (!wc_condition.values.isEmpty()) {
								qval_value.qnam = wc_condition.values.get(0);
								nsvs.add(qval_value);
							}
							break;
						}
					}
				}
				if (!StringUtils.isEmpty(qval_value.qnam)) {
					break;
				}
			}
		}
		/* (4) Create Variable metadata from (3) */
		for (DefineValueModel nsv : nsvs) {
			String dataset_name = StringUtils.substring(nsv.dataset_name, 4);
			DefineVariablePk key = new DefineVariablePk(dataset_name, nsv.qnam);
			DefineVariableModel variable = new DefineVariableModel(key);
			variable.variable_name = nsv.qnam;
			List<DefineVariableModel> filtered_variables = this.map_define_variable.values().stream().filter(o -> StringUtils.equals(dataset_name, o.dataset_name)).collect(Collectors.toList());
			variable.ordinal = filtered_variables.size() + 1;
			variable.domain = nsv.domain;
			variable.is_supp = YorN.Yes;
			variable.variable_label = nsv.value_label;
			if (nsv.has_no_data == YorNull.Yes) {
				variable.has_no_data = nsv.has_no_data;
			} else {
				DefineDatasetModel dataset = this.map_define_dataset.values().stream().filter(o -> StringUtils.equals(nsv.dataset_name, o.dataset_name)).findFirst().orElse(null);
				if (dataset != null) {
					variable.has_no_data = dataset.has_no_data;
				}
			}
			variable.mandatory = nsv.mandatory;
			variable.key_sequence = nsv.key_sequence;
			variable.data_type = nsv.data_type;
			variable.length = nsv.length;
			variable.significant_digits = nsv.significant_digits;
			variable.sas_field_name = nsv.sas_field_name;
			variable.display_format = nsv.display_format;
			variable.codelist = nsv.codelist;
			variable.origin = nsv.origin;
			variable.source = nsv.source;
			variable.crf_id = nsv.crf_id;
			variable.crf_page_type = nsv.crf_page_type;
			variable.crf_page_reference = nsv.crf_page_reference;
			variable.crf_first_page = nsv.crf_first_page;
			variable.crf_last_page = nsv.crf_last_page;
			variable.crf_page_title = nsv.crf_page_title;
			variable.predecessor = nsv.predecessor;
			variable.method_oid = nsv.method_oid;
			variable.comment_oid = nsv.comment_oid;
			variable.user_note1 = nsv.user_note1;
			variable.user_note2 = nsv.user_note2;
			this.map_define_variable.put(key, variable);
		}
		/* (5) Remove Dataset, Variable and VLM of SUPP datasets */
		/* Remove Values and WCs */
		Iterator<DefineValuePk> value_iterator = this.map_define_value.keySet().iterator();
		while (value_iterator.hasNext()) {
			DefineValuePk key = value_iterator.next();
			DefineValueModel value = this.map_define_value.get(key);
			if (StringUtils.startsWith(value.dataset_name, "SUPP")) {
				for (DefineWCPk where_clause_pk : value.where_clause_pks) {
					this.map_define_wc.remove(where_clause_pk);
				}
				value_iterator.remove();
			}
		}
		/* Remove Variables */
		Iterator<DefineVariablePk> variable_iterator = this.map_define_variable.keySet().iterator();
		while (variable_iterator.hasNext()) {
			DefineVariablePk key = variable_iterator.next();
			if (StringUtils.startsWith(key.dataset_name, "SUPP")) {
				variable_iterator.remove();
			}
		}
		/* Remove Datasets */
		Iterator<DefineDatasetPk> dataset_iterator = this.map_define_dataset.keySet().iterator();
		while (dataset_iterator.hasNext()) {
			DefineDatasetPk key = dataset_iterator.next();
			if (StringUtils.startsWith(key.dataset_name, "SUPP")) {
				dataset_iterator.remove();
			}
		}
	}
	
	public void updateHasSupp() {
		List<DefineVariableModel> variables = listSortedVariable();
		for (DefineVariableModel variable : variables) {
			if (variable.is_supp == YorN.Yes) {
				DefineDatasetModel dataset = get(new DefineDatasetPk(variable.dataset_name));
				dataset.has_supp = YorN.Yes;
			}
		}
	}
	
	public void updateVariableOrdinal() {
		Set<Entry<DefineVariablePk, DefineVariableModel>> entries = this.map_define_variable.entrySet();
		for (Entry<DefineVariablePk, DefineVariableModel> entry : entries) {
			DefineVariableModel variable = entry.getValue();
			DefineDatasetModel dataset = this.map_define_dataset.get(new DefineDatasetPk(variable.dataset_name));
			if (dataset != null) {
				variable.dataset_ordinal = dataset.ordinal;
			}
		}
	}
	
	/* Update has_vlm of Variable, group_id, dataset_name, variable_name of Value and WC */
	public void updateVLM() {
		List<DefineVariableModel> variables = listSortedVariable();
		Set<Entry<DefineValuePk, DefineValueModel>> entries = this.map_define_value.entrySet();
		for (Entry<DefineValuePk, DefineValueModel> entry : entries) {
			DefineValueModel value = entry.getValue();
			DefineVariableModel variable = variables.stream().filter(o -> StringUtils.equals(value.valuelist_oid, o.valuelist_oid)).findFirst().orElse(null);
			if (variable != null) {
				variable.has_vlm = YorN.Yes;
				value.dataset_name = variable.dataset_name;
				value.variable_name = variable.variable_name;
			}
			for (int i = 0; i < value.where_clause_pks.size(); i++) {
				DefineWCPk where_clause_pk = value.where_clause_pks.get(i);
				DefineWCModel wc = this.map_define_wc.get(where_clause_pk);
				if (value.where_clause_pks.size() > 1) {
					wc.group_id = "WC" + (i + 1);
				}
				if (wc != null && !wc.wc_conditions.isEmpty()) {
					for (WCCondition wc_condition : wc.wc_conditions) {
						DefineVariableModel wc_variable = variables.stream().filter(o -> StringUtils.equals(wc_condition.variable_oid, o.variable_oid)).findFirst().orElse(null);
						if (wc_variable != null) {
							wc_condition.dataset_name = wc_variable.dataset_name;
							wc_condition.variable_name = wc_variable.variable_name;
						}
					}
				}
			}
		}
	}
	
	/* Update param_dataset */
	public void updateARMDisplay() {
		List<DefineVariableModel> variables = listSortedVariable();
		List<DefineARMDisplayModel> displays = listSortedARMDisplay();
		for (DefineARMDisplayModel display : displays) {
			for (DefineARMResultPk result_pk : display.arm_result_pks) {
				DefineARMResultModel result = this.map_define_arm_result.get(result_pk);
				DefineVariableModel variable = variables.stream().filter(o -> StringUtils.equals(result.param_oid, o.variable_oid)).findFirst().orElse(null);
				if (variable != null) {
					result.param_dataset = variable.dataset_name;
				}
			}
		}
	}
	
	/* Update dataset_name and variable_name of ARMDataset and WC */
	public void updateARMDataset() {
		List<DefineDatasetModel> datasets = listSortedDataset();
		List<DefineVariableModel> variables = listSortedVariable();
		List<DefineARMDatasetModel> arm_datasets = listSortedARMDataset();
		for (DefineARMDatasetModel arm_dataset : arm_datasets) {
			DefineDatasetModel dataset = datasets.stream().filter(o -> StringUtils.equals(arm_dataset.dataset_oid, o.dataset_oid)).findFirst().orElse(null);
			List<DefineVariableModel> filtered_variables = variables.stream().filter(o -> arm_dataset.analysis_variable_oids.contains(o.variable_oid)).collect(Collectors.toList());
			if (dataset != null) {
				arm_dataset.dataset_name = dataset.dataset_name;
			}
			if (!filtered_variables.isEmpty()) {
				for (DefineVariableModel filtered_variable : filtered_variables) {
					arm_dataset.analysis_variables.add(filtered_variable.variable_name);
				}
			}
			if (arm_dataset.where_clause_pk != null) {
				DefineWCModel wc = this.map_define_wc.get(arm_dataset.where_clause_pk);
				if (wc != null && !wc.wc_conditions.isEmpty()) {
					for (WCCondition wc_condition : wc.wc_conditions) {
						DefineVariableModel wc_variable = variables.stream().filter(o -> StringUtils.equals(wc_condition.variable_oid, o.variable_oid)).findFirst().orElse(null);
						if (wc_variable != null) {
							wc_condition.dataset_name = wc_variable.dataset_name;
							wc_condition.variable_name = wc_variable.variable_name;
						}
					}
				}
			}
		}
	}
	
	/**
	 * This method add repeating variables to the define model by processing the variable Repeat N property.
	 * This method is applicable to SDTM only.
	 * For SDTM TSVALn/COVALn, this method adds them to the parent dataset.
	 * For the other SDTM variables, this method adds them to the SUPP-- dataset.
	 * @param type DatasetType that indicates if the Define-XML is for SDTM or ADaM
	 */
	public void processRepeatN(Config.DatasetType type) {
		if (type == null || type == DatasetType.ADaM) {
			return;
		}
		List<DefineVariableModel> variables = this.listSortedVariable().stream().filter(o -> o.repeat_n > 0).collect(Collectors.toList());
		/* Check if the SDTM spec is in the AutoSupp format.  */
		boolean is_auto_supp = false;
		List<DefineDatasetModel> supp_datasets = this.listSortedDataset().stream().filter(o -> StringUtils.startsWith(o.dataset_name, "SUPP") && !"SUPPQUAL".equals(o.dataset_name)).collect(Collectors.toList());
		if (supp_datasets.isEmpty()) {
			is_auto_supp = true;
		}
		for (DefineVariableModel variable : variables) {
			for (int i = 1; i <= variable.repeat_n; i++) {
				DefineDatasetModel supp_dataset = supp_datasets.stream().filter(o -> ("SUPP" + variable.dataset_name).equals(o.dataset_name)).findFirst().orElse(null);
				String variable_name = getRepeatVariableName(variable.variable_name, i);
				if (is_auto_supp || supp_dataset == null) {
					/* Add a variable with its Is SUPP Yes unless the variable is TSVAL or COVAL */
					DefineVariablePk new_var_pk = new DefineVariablePk(variable.dataset_name, DefineVariableModel.createOid(variable.dataset_name, variable_name));
					DefineVariableModel new_var = new DefineVariableModel(new_var_pk);
					new_var.copy(variable);
					new_var.variable_name = variable_name;
					new_var.ordinal = variable.ordinal + i;
					if ("TSVAL".equals(variable.variable_name) || "COVAL".equals(variable.variable_name)) {
						new_var.is_supp = YorN.No;
					} else {
						new_var.is_supp = YorN.Yes;
					}
					new_var.repeat_n = 0;
					new_var.variable_label = getRepeatVariableLabel(variable.variable_label, i);
					new_var.length = (variable.repeat_n_length.size() > i ? variable.repeat_n_length.get(i) : variable.length);
					new_var.repeat_n_length = new ArrayList<>();
					new_var.sas_field_name = new_var.variable_name;
					this.put(new_var_pk, new_var);
					this.shiftOrdinal(new_var);
				} else {
					/* Add a VLM to SUPP--.QVAL */
					DefineValuePk new_value_pk = new DefineValuePk(DefineValueModel.createOid(supp_dataset.dataset_name, "QVAL", variable_name, ""));
					DefineValueModel new_value = new DefineValueModel(new_value_pk);
					new_value.dataset_name = supp_dataset.dataset_name;
					new_value.variable_name = "QVAL";
					new_value.value_name = variable_name;
					new_value.ordinal = i;
					new_value.value_label = getRepeatVariableLabel(variable.variable_label, i);
					new_value.mandatory = variable.mandatory;
					new_value.data_type = variable.data_type;
					new_value.length = (variable.repeat_n_length.size() > i ? variable.repeat_n_length.get(i) : variable.length);
					new_value.significant_digits = variable.significant_digits;
					new_value.display_format = variable.display_format;
					new_value.sas_field_name = variable_name;
					new_value.codelist = variable.codelist;
					new_value.origin = variable.origin;
					new_value.source = variable.source;
					new_value.crf_id = variable.crf_id;
					new_value.crf_page_type = variable.crf_page_type;
					new_value.crf_page_reference = variable.crf_page_reference;
					new_value.crf_first_page = variable.crf_first_page;
					new_value.crf_last_page = variable.crf_last_page;
					new_value.crf_page_title = variable.crf_page_title;
					new_value.predecessor = variable.predecessor;
					new_value.method_oid = variable.method_oid;
					new_value.comment_oid = variable.comment_oid;
					new_value.alias_context = variable.alias_context;
					new_value.alias_name = variable.alias_name;
					new_value.user_note1 = variable.user_note1;
					new_value.user_note2 = variable.user_note2;
					String wc_oid = DefineWCModel.createOid(supp_dataset.dataset_name, variable_name, "QVAL", "", "");
					DefineWCPk wc_pk = new DefineWCPk(wc_oid);
					DefineWCModel wc = new DefineWCModel(wc_pk);
					WCCondition wc_condition = new WCCondition();
					wc_condition.dataset_name = supp_dataset.dataset_name;
					wc_condition.variable_name = "QVAL";
					wc_condition.operator = "EQ";
					wc_condition.values.add(variable_name);
					wc.wc_conditions.add(wc_condition);
					this.put(wc_pk, wc);
					new_value.where_clause_pks.add(wc_pk);
					this.put(new_value_pk, new_value);
					this.shiftOrdinal(new_value);
					/* Update ValueList */
					DefineVariableModel qval = this.get(new DefineVariablePk(supp_dataset.dataset_name, DefineVariableModel.createOid(supp_dataset.dataset_name, "QVAL")));
					qval.valuelist_oid = DefineVariableModel.createValueListOid(supp_dataset.dataset_name, "QVAL");
				}
			}
		}
	}
	
	/**
	 * Shift ordinal of the other variables in the dataset.
	 * Call this method when you insert a new variable to a variables list.
	 */
	public void shiftOrdinal(DefineVariableModel var_to_insert) {
		List<DefineVariableModel> variables = this.listSortedVariable().stream()
				.filter(o -> StringUtils.equals(var_to_insert.dataset_name, o.dataset_name))
				.collect(Collectors.toList());
		for (DefineVariableModel variable : variables) {
			if (StringUtils.equals(variable.variable_oid, var_to_insert.variable_oid)) {
				continue;
			}
			if (variable.ordinal >= var_to_insert.ordinal) {
				variable.ordinal = variable.ordinal + 1;
			}
		}
	}
	
	public void shiftOrdinal(DefineValueModel val_to_insert) {
		List<DefineValueModel> values = this.listSortedValue().stream()
				.filter(o -> StringUtils.equals(val_to_insert.dataset_name, o.dataset_name) && StringUtils.equals(val_to_insert.variable_name, o.variable_name))
				.collect(Collectors.toList());
		for (DefineValueModel value : values) {
			if (StringUtils.equals(value.value_oid, val_to_insert.value_oid)) {
				continue;
			}
			if (value.ordinal >= val_to_insert.ordinal) {
				value.ordinal = value.ordinal + 1;
			}
		}
	}
	
	/* Variable Name += N */
	public static String getRepeatVariableName(String variable_name, int N) {
		if (StringUtils.isEmpty(variable_name)) {
			return variable_name;
		}
		if (N == 0) {
			return variable_name;
		}
		if ("CMCLASCD".equals(variable_name)) {
			if (N < 10) {
				variable_name = "CMCLSCD" + N;
			} else {
				variable_name = "CMCLSC" + N;
			}
		} else {
			if (variable_name.length() >= 8 && 1 <= N && N < 10) {
				variable_name = variable_name.substring(0, 7) + N;
			} else if (variable_name.length() == 7 && 10 <= N && N < 100) {
				variable_name = variable_name.substring(0, 6) + N;
			} else {
				variable_name = variable_name + N;
			}
		}
		return variable_name;
	}
	
	/* Variable Label += " " + N */
	public static String getRepeatVariableLabel(String variable_label, int N) {
		if (StringUtils.isEmpty(variable_label)) {
			return variable_label;
		}
		if (N == 0) {
			return variable_label;
		}
		if (variable_label.length() >= 39 && 1 <= N && N < 10) {
			variable_label = variable_label.substring(0, 38) + " " + N;
		} else if (variable_label.length() == 38 && 10 <= N && N < 100) {
			variable_label = variable_label.substring(0, 37) + " " +  N;
		} else {
			variable_label = variable_label + " " + N;
		}
		return variable_label;
	}
}
