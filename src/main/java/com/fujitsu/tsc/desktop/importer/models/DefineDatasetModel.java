/* 
 * Copyright (c) 2022 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer.models;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fujitsu.tsc.desktop.importer.ExcelWriter2.ExcelColumn;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorN;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorNull;
import com.fujitsu.tsc.desktop.util.Utils;

/**
 * Java object representing a record (a line) in the DATASET table of {@link DefineModel}.
 * [Primary Keys]
 * 	- dataset_name
 * [Required]
 * 	- domain
 * 	- dataset_name
 *  - dataset_oid
 *  - description
 *  - repeating
 *  - is_reference_data
 *  - purpose
 *  - structure
 *  - dataset_class
 *  - leaf_href
 *  - leaf_title
 */
public class DefineDatasetModel implements Comparable<DefineDatasetModel> {

	public static class DefineDatasetPk implements Comparable<DefineDatasetPk> {
		public final String dataset_name;
		
		public DefineDatasetPk(String dataset_name) {
			this.dataset_name = dataset_name;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.dataset_name.equals(((DefineDatasetPk)obj).dataset_name)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineDatasetPk key) {
			return (this.dataset_name).compareTo(key.dataset_name);
		}
		
		@Override
		public String toString() {
			String rtn = "Dataset Name: " + this.dataset_name;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.dataset_name);
	    }
	}
	
	/** Domain */
	public String domain;
	/** Dataset Name */
	public final String dataset_name;
	/** Dataset OID */
	public String dataset_oid;
	/** Ordinal */
	public Integer ordinal;
	/** Has SUPP */
	public YorN has_supp;
	/** Description */
	public String description;
	/** No Data */
	public YorNull has_no_data;
	/** SASDatasetName */
	public String sas_dataset_name;
	/** Repeating */
	public YorN repeating;
	/** IsReferenceData */
	public YorN is_reference_data;
	/** Purpose */
	public String purpose;
	/** Standard OID */
	public String standard_oid;
	/** Structure */
	public String structure;
	/** Class */
	public String dataset_class;
	/** Subclass */
	public String dataset_subclass;
	/** Comment OID */
	public String comment_oid;
	/** Alias */
	public String alias_name;
	/** href */
	public String leaf_href;
	/** Title */
	public String leaf_title;
	/** User Note 1 */
	public String user_note1;
	/** User Note 2 */
	public String user_note2;

	public DefineDatasetModel(DefineDatasetPk key) {
		this.dataset_name = key.dataset_name;
		this.dataset_oid = "";
		this.domain = "";
		this.ordinal = 0;
		this.has_supp = YorN.No;
		this.description = "";
		this.has_no_data = null;
		this.sas_dataset_name = "";
		this.repeating = YorN.No;
		this.is_reference_data = YorN.No;
		this.purpose = "";
		this.standard_oid = "";
		this.structure = "";
		this.dataset_class = "";
		this.dataset_subclass = "";
		this.comment_oid = "";
		this.alias_name = "";
		this.leaf_href = "";
		this.leaf_title = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
	
	/**
	 * @return "IG." + dataset_name
	 */
	public String toOid() {
		return createOid(dataset_name);
	}
	public static String createOid(String dataset_name) {
		return "IG." + dataset_name;
	}

	/**
	 * @return "LF." + dataset_name
	 */
	public String getLeafOid() {
		return createLeafOid(this.dataset_name);
	}
	public static String createLeafOid(String dataset_name) {
		return "LF." + dataset_name;
	}

	@Override
	public int compareTo(DefineDatasetModel dataset) {
		return Integer.compare(this.ordinal, dataset.ordinal);
	}
}
