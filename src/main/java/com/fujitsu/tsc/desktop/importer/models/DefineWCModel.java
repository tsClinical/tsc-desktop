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

import com.fujitsu.tsc.desktop.util.Utils;

/**
 * Java object representing a where clause referenced from the DOCUMENT or RESULT2 table of {@link DefineModel}.
 * [Primary Keys]
 *  - oid
 * [Required]
 * 	- wc_conditions
 */
public class DefineWCModel implements Comparable<DefineWCModel> {

	public static class DefineWCPk implements Comparable<DefineWCPk> {
		public final String oid;
		
		public DefineWCPk(String oid) {
			this.oid = oid;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.oid.equals(((DefineWCPk)obj).oid)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineWCPk key) {
			return (this.oid.compareTo(key.oid));
		}
		
		@Override
		public String toString() {
			String rtn = "WhereClause OID: " + this.oid;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.oid);
	    }
	}
	
	public static class WCCondition {
		public String variable_oid = "";
		public String dataset_name = "";
		public String variable_name = "";
		public String operator = "";
		public List<String> values = new ArrayList<>();
	}
	
	/** WhereClause OID */
	public final String oid;
	/** WhereClause Group ID */
	public String group_id;
	/** WhereClause Conditions */
	public List<WCCondition> wc_conditions;
	/** Comment OID */
	public String comment_oid;
	
	public DefineWCModel(DefineWCPk key) {
		this.oid = key.oid;
		this.group_id = "";
		this.wc_conditions = new ArrayList<>();
		this.comment_oid = "";
	}

	/** Reference copy of given object */
	public void copy(DefineWCModel wc) {
		if (wc.wc_conditions != null && !wc.wc_conditions.isEmpty())
			this.wc_conditions = wc.wc_conditions;
		if (!StringUtils.isEmpty(wc.comment_oid))
			this.comment_oid = wc.comment_oid;
	}

	/**
	 * @return "WC." + dataset_name + "." + variable_name + "." + coded value_name_or_key (+ "." + coded wc_group_id)
	 */
	public String toOid() {
		return this.oid;
	}
	public static String createOid(String dataset_name, String variable_name, String value_name, String value_key, String wc_group_id) {
		String wc_oid = "";
		String coded_wc_group_id = Utils.codedText(wc_group_id);
		if (StringUtils.isEmpty(value_key)) {
			wc_oid = "WC." + dataset_name + "." + variable_name + "." + Utils.codedText(value_name) + (StringUtils.isEmpty(coded_wc_group_id) ? "" : "." + coded_wc_group_id);
		} else {
			wc_oid = "WC." + dataset_name + "." + variable_name + "." + Utils.codedText(value_key) + (StringUtils.isEmpty(coded_wc_group_id) ? "" : "." + coded_wc_group_id);
		}
		return wc_oid;
	}
	public static String createOid(DefineARMDatasetModel arm_dataset) {
		if (arm_dataset == null) {
			return "";
		} else {
			return "WC." + Utils.codedText(arm_dataset.display_name) + "." + Utils.codedText(arm_dataset.result_key) + "." + arm_dataset.dataset_name;
		}
	}

	@Override
	public int compareTo(DefineWCModel wc) {
		return this.oid.compareTo(wc.oid);
	}
}
