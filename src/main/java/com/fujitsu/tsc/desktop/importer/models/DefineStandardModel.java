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

import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorN;
import com.fujitsu.tsc.desktop.util.Utils;

/**
 * Java object representing a record (a line) in the DOCUMENT table of {@link DefineModel}.
 * [Primary Keys]
 * 	- standard_name
 *  - standard_version
 *  - publishing_set
 * [Required]
 * 	- standard_name
 *  - standard_type
 *  - standard_version
 *  - publishing_set
 */
public class DefineStandardModel implements Comparable<DefineStandardModel> {

	public static class DefineStandardPk implements Comparable<DefineStandardPk> {
		public final String standard_oid;
		
		public DefineStandardPk(String standard_oid) {
			this.standard_oid = standard_oid;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.standard_oid.equals(((DefineStandardPk)obj).standard_oid)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineStandardPk key) {
			return this.standard_oid.compareTo(key.standard_oid);
		}
		
		@Override
		public String toString() {
			String rtn = "Standard OID: " + this.standard_oid;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.standard_oid);
	    }
	}
	
	/** Standard OID */
	public final String standard_oid;
	/** Standard Name */
	public String standard_name;
	/** Standard Version */
	public String standard_version;
	/** Publishing Set */
	public String publishing_set;
	/** Ordinal */
	public Integer ordinal;
	/** Standard Type */
	public StandardType standard_type;
	/** Standard Status */
	public String standard_status;
	/** Comment OID */
	public String comment_oid;
	/** User Note 1 */
	public String user_note1;
	/** User Note 2 */
	public String user_note2;
	
	public enum StandardType {
		CT, IG;	//Defined in the Define-XML Terminology
		public static StandardType parse(String standard_type) {
			if ("CT".equals(standard_type)) {
				return StandardType.CT;
			} else {
				return StandardType.IG;
			}
		}
	}
	
	public DefineStandardModel(DefineStandardPk key) {
		this.standard_oid = key.standard_oid;
		this.standard_name = "";
		this.standard_version = "";
		this.publishing_set = "";
		this.ordinal = 0;
		this.standard_type = StandardType.IG;
		this.standard_status = "";
		this.comment_oid = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}

	/**
	 * @return "STD." + coded standard name + "." + coded_standard_version (+ "." + coded_publishing_set)
	 */
	public String toOid() {
		return createOid(this.standard_name, this.standard_version, this.publishing_set);
	}
	public static String createOid(String standard_name, String standard_version, String publishing_set) {
		String coded_standard_name = Utils.codedText(standard_name);
		String coded_standard_version = Utils.codedText(standard_version);
		String coded_publishing_set = "";
		if (StringUtils.isNotEmpty(publishing_set)) {
			coded_publishing_set = Utils.codedText(publishing_set);
		}
		String rtn = "STD." + coded_standard_name + "_" + coded_standard_version;
		if (StringUtils.isNotEmpty(coded_publishing_set)) {
			rtn += "_" + coded_publishing_set;
		}
		return rtn;
	}
	
	public String name(StandardType std_type) {
		if (std_type == StandardType.CT) {
			return this.publishing_set + " " + this.standard_version;
		} else {
			return this.standard_name + " " + this.standard_version;
		}
	}

	@Override
	public int compareTo(DefineStandardModel standard) {
		if (this.standard_name == standard.standard_name) {
			if (this.standard_version == standard.standard_version) {
				return this.publishing_set.compareTo(standard.publishing_set);
			} else {
				return this.standard_version.compareTo(standard.standard_version);
			}
		} else {
			return this.standard_name.compareTo(standard.standard_name);
		}
	}
}
