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
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.DefineWCPk;
import com.fujitsu.tsc.desktop.util.Utils;

/**
 * Java object representing a where clause referenced from the DOCUMENT or RESULT2 table of {@link DefineModel}.
 * [Primary Keys]
 *  - display_name
 *  - result_key
 *  - dataset_oid
 * [Required]
 *  - display_desc
 * 	- analysis_results
 */
public class DefineARMDatasetModel implements Comparable<DefineARMDatasetModel> {

	public static class DefineARMDatasetPk implements Comparable<DefineARMDatasetPk> {
		public final String display_name;
		public final String result_key;
		public final String dataset_oid;
		
		public DefineARMDatasetPk(String display_name, String result_key, String dataset_oid) {
			this.display_name = display_name;
			this.result_key = result_key;
			this.dataset_oid = dataset_oid;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.display_name.equals(((DefineARMDatasetPk)obj).display_name)
	        		&& this.result_key.equals(((DefineARMDatasetPk)obj).result_key)
	        		&& this.dataset_oid.equals(((DefineARMDatasetPk)obj).dataset_oid)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineARMDatasetPk key) {
			return ((this.display_name + this.result_key + this.dataset_oid).compareTo(key.display_name + this.result_key + this.dataset_oid));
		}
		
		@Override
		public String toString() {
			String rtn = "Display Name: " + this.display_name + System.lineSeparator();
			rtn += "Result Key: " + this.result_key + System.lineSeparator();
			rtn += "ItemGroup OID: " + this.dataset_oid;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.display_name, this.result_key, this.dataset_oid);
	    }
	}
	
	/** Display Name */
	public final String display_name;
	/** Result Key */
	public final String result_key;
	/** Dataset OID */
	public String dataset_oid;
	/** Dataset Name */
	public String dataset_name;
	/** Ordinal */
	public Integer ordinal;
	/** Analysis Variable OID */
	public List<String> analysis_variable_oids;
	/** Analysis Variable */
	public List<String> analysis_variables;
	/** User Note 1 */
	public String user_note1;
	/** User Note 2 */
	public String user_note2;
	/** Where Clause */
	public DefineWCPk where_clause_pk;
	
	public DefineARMDatasetModel(DefineARMDatasetPk key) {
		this.display_name = key.display_name;
		this.result_key = key.result_key;
		this.dataset_oid = key.dataset_oid;
		this.dataset_name = "";
		this.ordinal = 0;
		this.analysis_variable_oids = new ArrayList<>();
		this.analysis_variables = new ArrayList<>();
		this.user_note1 = "";
		this.user_note2 = "";
		this.where_clause_pk = null;
	}

	/** Reference copy of given object */
	public void copy(DefineARMDatasetModel arm_dataset) {
		if (arm_dataset.ordinal > 0)
			this.ordinal = arm_dataset.ordinal;
		if (!arm_dataset.analysis_variable_oids.isEmpty())
			this.analysis_variable_oids = arm_dataset.analysis_variable_oids;
		if (!arm_dataset.analysis_variables.isEmpty())
			this.analysis_variables = arm_dataset.analysis_variables;
		if (!StringUtils.isEmpty(arm_dataset.user_note1))
			this.user_note1 = arm_dataset.user_note1;
		if (!StringUtils.isEmpty(arm_dataset.user_note2))
			this.user_note2 = arm_dataset.user_note2;
		if (arm_dataset.where_clause_pk != null)
			this.where_clause_pk = arm_dataset.where_clause_pk;
	}
	
	@Override
	public int compareTo(DefineARMDatasetModel arm_dataset) {
		if (this.display_name.equals(arm_dataset.display_name)) {
			if (this.result_key.equals(arm_dataset.result_key)) {
				return this.dataset_oid.compareTo(arm_dataset.dataset_oid);
			} else {
				return this.result_key.compareTo(arm_dataset.result_key);
			}
		} else {
			return this.display_name.compareTo(arm_dataset.display_name);
		}
	}
}
