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

import com.fujitsu.tsc.desktop.importer.ExcelWriter2.ExcelColumn;
import com.fujitsu.tsc.desktop.importer.models.DefineDatasetModel.DefineDatasetPk;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorN;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorNull;
import com.fujitsu.tsc.desktop.importer.models.DefineVariableModel.DefineVariablePk;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.DefineWCPk;
import com.fujitsu.tsc.desktop.util.Utils;

/**
 * Java object representing a record (a line) in the VALUE table of {@link DefineModel}.
 * [Primary Keys]
 * 	- value_oid
 *  (Combination of dataset_name, variable_name and value_name are expected to be keys.
 *  However, value_key can be used on behalf of value_key if they do not make the keys.)
 * [Required]
 * 	- dataset_name
 *  - variable_name
 *  - value_name
 *  - mandatory
 *  - data_type
 *  - where_clauses
 */
public class DefineValueModel implements Comparable<DefineValueModel> {

	public static class DefineValuePk implements Comparable<DefineValuePk> {
		public final String value_oid;
		
		public DefineValuePk(String value_oid) {
			this.value_oid = value_oid;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.value_oid.equals(((DefineValuePk)obj).value_oid)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineValuePk key) {
			return (this.value_oid).compareTo(key.value_oid);
		}
		
		@Override
		public String toString() {
			String rtn = "ValueOID: " + this.value_oid;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.value_oid);
	    }
	}
	
	/** ValueOID */
	public final String value_oid;
	/** ValueList OID */
	public String valuelist_oid;
	/** Dataset Name */
	public String dataset_name;
	/** Variable Name */
	public String variable_name;
	/** Value Name */
	public String value_name;
	/** Value Key */
	public String value_key;
	/** レコード順序 */
	public Integer ordinal;
	/** Domain */
	public String domain;
	/** Label */
	public String value_label;
	/** No Data */
	public YorNull has_no_data;
	/** No Data (derived) */
	public YorNull has_no_data_derived;	//Equal to has_no_data, but Yes if parent dataset or variable has no data.
	/** Mandatory */
	public YorN mandatory;
	/** Key Sequence */
	public String key_sequence;
	/** DataType */
	public String data_type;
	/** Length */
	public String length;
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
	/** Predecessor */
	public String predecessor;
	/** Method OID */
	public String method_oid;
	/** Comment OID */
	public String comment_oid;
	/** Alias Context */
	public String alias_context;
	/** Alias Name */
	public String alias_name;
	/** User Note 1 */
	public String user_note1;
	/** User Note 2 */
	public String user_note2;
	/** Where Clauses */
	public List<DefineWCPk> where_clause_pks;
	
	/** QNAM Value - referenced when converting NSVs to Variable */
	public String qnam;

	public DefineValueModel(DefineValuePk key) {
		this.value_oid = key.value_oid;
		this.valuelist_oid = "";
		this.dataset_name = "";
		this.variable_name = "";
		this.value_name = "";
		this.value_key = "";
		this.ordinal = 0;
		this.domain = "";
		this.value_label = "";
		this.has_no_data = null;
		this.has_no_data_derived = null;
		this.mandatory = YorN.No;
		this.key_sequence = "";
		this.data_type = "";
		this.length = "";
		this.significant_digits = "";
		this.sas_field_name = "";
		this.display_format = "";
		this.codelist = "";
		this.origin = "";
		this.source = "";
		this.crf_id = "";
		this.crf_page_type = "";
		this.crf_page_reference = "";
		this.crf_first_page = "";
		this.crf_last_page = "";
		this.crf_page_title = "";
		this.predecessor = "";
		this.method_oid = "";
		this.comment_oid = "";
		this.alias_context = "";
		this.alias_name = "";
		this.user_note1 = "";
		this.user_note2 = "";
		this.where_clause_pks = new ArrayList<>();
		this.qnam = "";
	}
	
	public DefineValuePk getKey() {
		return new DefineValuePk(this.value_oid);
	}
	
	/**
	 * @return "IT." + dataset_name + "." + variable_name + "." + coded value_name_or_key
	 */
	public String toOid() {
		return this.value_oid;
	}
	public static String createOid(String dataset_name, String variable_name, String value_name, String value_key) {
		String value_oid = "";
		if (StringUtils.isEmpty(dataset_name) || StringUtils.isEmpty(variable_name) || StringUtils.isEmpty(value_name)) {
			return value_oid;
		}
		if (StringUtils.isEmpty(value_key)) {
			value_oid = "IT." + dataset_name + "." + variable_name + "." + Utils.codedText(value_name);
		} else {
			value_oid = "IT." + dataset_name + "." + variable_name + "." + Utils.codedText(value_key);
		}
		return value_oid;
	}
	
	/**
	 * Derive has_no_data_derived based on this has_no_data and parent variable and dataset.
	 * @param define This DefineModel
	 * @return
	 */
	public YorNull deriveHasNoData(DefineModel define) {
		DefineVariableModel variable = define.get(new DefineVariablePk(this.dataset_name, DefineVariableModel.createOid(this.dataset_name, this.variable_name)));
		if (variable == null || variable.has_no_data_derived == null) {
			return this.has_no_data;
		} else {
			return YorNull.Yes;
		}
	}
	
	@Override
	public int compareTo(DefineValueModel value) {
		if (this.dataset_name.equals(value.dataset_name)) {
			if (this.variable_name.equals(value.variable_name)) {
				return this.value_name.compareTo(value.value_name);
			} else {
				return this.variable_name.compareTo(value.variable_name);
			}
		} else {
			return this.dataset_name.compareTo(value.dataset_name);
		}
	}
}
