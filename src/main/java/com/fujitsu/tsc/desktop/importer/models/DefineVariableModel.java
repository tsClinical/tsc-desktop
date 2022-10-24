/* 
 * Copyright (c) 2022 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fujitsu.tsc.desktop.importer.models.DefineDatasetModel.DefineDatasetPk;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorN;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorNull;

/**
 * Java object representing a record (a line) in the DOCUMENT table of {@link DefineModel}.
 * [Primary Keys]
 *  - dataset_name
 * 	- variable_oid
 * [Required]
 * 	- dataset_name
 *  - variable_name
 *  - variable_oid
 *  - is_supp
 *  - mandatory
 *  - data_type
 */
public class DefineVariableModel implements Comparable<DefineVariableModel> {

	public static class DefineVariablePk implements Comparable<DefineVariablePk> {
		public final String dataset_name;
		public final String variable_oid;
		
		public DefineVariablePk(String dataset_name, String variable_oid) {
			this.dataset_name = dataset_name;
			this.variable_oid = variable_oid;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.dataset_name.equals(((DefineVariablePk)obj).dataset_name) && this.variable_oid.equals(((DefineVariablePk)obj).variable_oid)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineVariablePk key) {
			return (this.dataset_name + this.variable_oid).compareTo(key.dataset_name + key.variable_oid);
		}
		
		@Override
		public String toString() {
			String rtn = "Dataset Name: " + this.dataset_name + System.lineSeparator();
			rtn += "Variable OID: " + this.variable_oid;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.dataset_name, this.variable_oid);
	    }
	}
	
	/** Dataset Name */
	public final String dataset_name;
	/** Variable OID */
	public final String variable_oid;
	/** Variable Name */
	public String variable_name;
	/** Dataset Ordinal */
	public Integer dataset_ordinal;	//Ordinal of datasets
	/** Ordinal */
	public Integer ordinal;	//Ordinal within a dataset
	/** Domain */
	public String domain;
	/** Is SUPP */
	public YorN is_supp;
	/** Repeat N */
	public Integer repeat_n;
	/** Label */
	public String variable_label;
	/** No Data */
	public YorNull has_no_data;
	/** No Data (derived) */
	public YorNull has_no_data_derived;	//Equal to has_no_data, but Yes if parent dataset has no data.
	/** Non Standard */
	public YorNull is_non_standard;
	/** Mandatory */
	public YorN mandatory;
	/** Key Sequence */
	public String key_sequence;
	/** Sort Order */
	public String sort_order;
	/** DataType */
	public String data_type;
	/** Length */
	public String length;
	/**
	 * Empty if Length is a single value, or a list of Length for 0 to N if Length is a list for Repeat N.
	 */
	public List<String> repeat_n_length;	// 
	/** SignificantDigits */
	public String significant_digits;
	/** SASFieldName */
	public String sas_field_name;
	/** DisplayFormat */
	public String display_format;
	/** Codelist */
	public String codelist;
	/** Origin */
	public String origin;
	/** Source */
	public String source;
	/** Evaluator */
	public String evaluator;
	/** CRF ID */
	public String crf_id;
	/** CRF Page Type */
	public String crf_page_type;
	/** CRF Page Reference */
	public String crf_page_reference;
	/** CRF First Page */
	public String crf_first_page;
	/** CRF Last Page */
	public String crf_last_page;
	/** CRF Page Title */
	public String crf_page_title;
	/** Has Value Metadata */
	public YorN has_vlm;
	/** Predecessor */
	public String predecessor;
	/** Method OID */
	public String method_oid;
	/** Comment OID */
	public String comment_oid;
	/** Role */
	public String role;
	/** Role codelist */
	public String role_codelist;
	/** Alias Context */
	public String alias_context;
	/** Alias Name */
	public String alias_name;
	/** User Note 1 */
	public String user_note1;
	/** User Note 2 */
	public String user_note2;
	/** ValueList OID */
	public String valuelist_oid;

	public DefineVariableModel(DefineVariablePk key) {
		this.dataset_name = key.dataset_name;
		this.variable_oid = key.variable_oid;
		this.variable_name = "";
		this.dataset_ordinal = 0;
		this.ordinal = 0;
		this.domain = "";
		this.is_supp = YorN.No;
		this.repeat_n = 0;
		this.variable_label = "";
		this.has_no_data = null;
		this.has_no_data_derived = null;
		this.is_non_standard = null;
		this.mandatory = YorN.No;
		this.key_sequence = "";
		this.sort_order = "";
		this.data_type = "";
		this.length = "";
		this.repeat_n_length = new ArrayList<>();
		this.significant_digits = "";
		this.sas_field_name = "";
		this.display_format = "";
		this.codelist = "";
		this.origin = "";
		this.source = "";
		this.evaluator = "";
		this.crf_id = "";
		this.crf_page_type = "";
		this.crf_page_reference = "";
		this.crf_first_page = "";
		this.crf_last_page = "";
		this.crf_page_title = "";
		this.has_vlm = YorN.No;
		this.predecessor = "";
		this.method_oid = "";
		this.comment_oid = "";
		this.role = "";
		this.role_codelist = "";
		this.alias_context = "";
		this.alias_name = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
	
	public void copy(DefineVariableModel variable) {
		if (variable == null) {
			return;
		}
		this.variable_name = variable.variable_name;
		this.dataset_ordinal = variable.dataset_ordinal;
		this.ordinal = 0;
		this.domain = variable.domain;
		this.is_supp = variable.is_supp;
		this.repeat_n = variable.repeat_n;
		this.variable_label = variable.variable_label;
		this.has_no_data = variable.has_no_data;
		this.has_no_data_derived = variable.has_no_data_derived;
		this.is_non_standard = variable.is_non_standard;
		this.mandatory = variable.mandatory;
		this.key_sequence = variable.key_sequence;
		this.sort_order = variable.sort_order;
		this.data_type = variable.data_type;
		this.length = variable.length;
		this.repeat_n_length = variable.repeat_n_length;
		this.significant_digits = variable.significant_digits;
		this.sas_field_name = variable.sas_field_name;
		this.display_format = variable.display_format;
		this.codelist = variable.codelist;
		this.origin = variable.origin;
		this.source = variable.source;
		this.evaluator = variable.evaluator;
		this.crf_id = variable.crf_id;
		this.crf_page_type = variable.crf_page_type;
		this.crf_page_reference = variable.crf_page_reference;
		this.crf_first_page = variable.crf_first_page;
		this.crf_last_page = variable.crf_last_page;
		this.crf_page_title = variable.crf_page_title;
		this.has_vlm = variable.has_vlm;
		this.predecessor = variable.predecessor;
		this.method_oid = variable.method_oid;
		this.comment_oid = variable.comment_oid;
		this.role = variable.role;
		this.role_codelist = variable.role_codelist;
		this.alias_context = variable.alias_context;
		this.alias_name = variable.alias_name;
		this.user_note1 = variable.user_note1;
		this.user_note2 = variable.user_note2;
	}

	public DefineVariablePk getKey() {
		return new DefineVariablePk(this.dataset_name, this.variable_oid);
	}
	
	/**
	 * @return "IT." + dataset_name + "." + variable_name
	 */
	public String toOid() {
		return this.variable_oid;
	}
	public static String createOid(String dataset_name, String variable_name) {
		return "IT." + dataset_name + "." + variable_name;
	}
	/**
	 * @return "VL." + dataset_name + "." + variable_name
	 */
	public static String createValueListOid(String dataset_name, String variable_name) {
		return "VL." + dataset_name + "." + variable_name;
	}
	/**
	 * @return "MT." + dataset_name + "." + variable_name
	 */
	public static String createMethodOid(String dataset_name, String variable_name) {
		return "MT." + DefineVariableModel.createOid(dataset_name, variable_name);
	}
	
	/**
	 * Derive has_no_data_derived based on this has_no_data and parent dataset.
	 * @param define This DefineModel
	 * @return
	 */
	public YorNull deriveHasNoData(DefineModel define) {
		DefineDatasetModel dataset = define.get(new DefineDatasetPk(this.dataset_name));
		if (dataset == null || dataset.has_no_data == null) {
			return this.has_no_data;
		} else {
			return YorNull.Yes;
		}
	}

	@Override
	public int compareTo(DefineVariableModel variable) {
		if (this.dataset_ordinal == variable.dataset_ordinal) {
			return Integer.compare(this.ordinal, variable.ordinal);
		} else {
			return Integer.compare(this.dataset_ordinal, variable.dataset_ordinal);
		}
	}
}
