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

import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorNull;
import com.fujitsu.tsc.desktop.importer.models.DefineVariableModel.DefineVariablePk;

/**
 * Java object representing a record (a line) in the CODELIST table of {@link DefineModel}.
 * [Primary Keys]
 * 	- codelist_id
 *  - submission_value
 * [Required]
 * 	- codelist_id
 *  - codelist_label
 *  - data_type
 *  - submission_value
 */
public class DefineCodelistModel implements Comparable<DefineCodelistModel> {

	public static class DefineCodelistPk implements Comparable<DefineCodelistPk> {
		public final String codelist_id;
		public final String submission_value;
		
		public DefineCodelistPk(String codelist_id, String submission_value) {
			this.codelist_id = codelist_id;
			this.submission_value = submission_value;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.codelist_id.equals(((DefineCodelistPk)obj).codelist_id) && this.submission_value.equals(((DefineCodelistPk)obj).submission_value)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineCodelistPk key) {
			return (this.codelist_id + this.submission_value).compareTo(key.codelist_id + key.submission_value);
		}
		
		@Override
		public String toString() {
			String rtn = "Codelist ID: " + this.codelist_id + System.lineSeparator();
			rtn += "Submission Value: " + this.submission_value;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.codelist_id, this.submission_value);
	    }
	}
	
	/** Codelist ID */
	public final String codelist_id;
	/** Codelist Code */
	public String codelist_code;
	/** Codelist Label */
	public String codelist_label;
	/** DataType */
	public String data_type;
	/** SASFormatName */
	public String sas_format_name;
	/** Standard OID */
	public String standard_oid;
	/** Comment OID */
	public String comment_oid;
	/** Code */
	public String code;
	/** Ordinal */
	public Integer ordinal;
	/** Order Number */
	public Integer order_number;
	/** Rank */
	public Integer rank;
	/** ExtendedValue */
	public YorNull extended_value;
	/** Submission Value */
	public final String submission_value;
	/** Decode */
	public String decode;
	/** xml:lang */
	public String xml_lang;
	/** Alias Context */
	public String alias_context;
	/** Alias Name */
	public String alias_name;
	/** User Note 1 */
	public String user_note1;
	/** User Note 2 */
	public String user_note2;

	public DefineCodelistModel(DefineCodelistPk key) {
		this.codelist_id = key.codelist_id;
		this.codelist_code = "";
		this.codelist_label = "";
		this.data_type = "";
		this.sas_format_name = "";
		this.standard_oid = "";
		this.comment_oid = "";
		this.code = "";
		this.ordinal = 0;
		this.order_number = -1;
		this.rank = -1;
		this.extended_value = null;
		this.submission_value = key.submission_value;
		this.decode = "";
		this.xml_lang = "";
		this.alias_context = "";
		this.alias_name = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
	
	public DefineCodelistPk getKey() {
		return new DefineCodelistPk(this.codelist_id, this.submission_value);
	}

	public void copy(DefineCodelistModel codelist) {
		if (!StringUtils.isEmpty(codelist.codelist_code))
			this.codelist_code = codelist.codelist_code;
		if (!StringUtils.isEmpty(codelist.codelist_label))
			this.codelist_label = codelist.codelist_label;
		if (!StringUtils.isEmpty(codelist.data_type))
			this.data_type = codelist.data_type;
		if (!StringUtils.isEmpty(codelist.sas_format_name))
			this.sas_format_name = codelist.sas_format_name;
		if (!StringUtils.isEmpty(codelist.standard_oid))
			this.standard_oid = codelist.standard_oid;
		if (!StringUtils.isEmpty(codelist.comment_oid))
			this.comment_oid = codelist.comment_oid;
		if (!StringUtils.isEmpty(codelist.code))
			this.code = codelist.code;
		if (codelist.ordinal > 0)
			this.ordinal = codelist.ordinal;
		if (codelist.order_number > -1)
			this.order_number = codelist.order_number;
		if (codelist.rank > -1)
			this.rank = codelist.rank;
		if (codelist.extended_value != null)
			this.extended_value = codelist.extended_value;
		if (!StringUtils.isEmpty(codelist.decode))
			this.decode = codelist.decode;
		if (!StringUtils.isEmpty(codelist.xml_lang))
			this.xml_lang = codelist.xml_lang;
		if (!StringUtils.isEmpty(codelist.alias_context))
			this.alias_context = codelist.alias_context;
		if (!StringUtils.isEmpty(codelist.alias_name))
			this.alias_name = codelist.alias_name;
		if (!StringUtils.isEmpty(codelist.user_note1))
			this.user_note1 = codelist.user_note1;
		if (!StringUtils.isEmpty(codelist.user_note2))
			this.user_note2 = codelist.user_note2;
	}

	/**
	 * @return "CL." + codelist_id
	 */
	public String getCodelistOid() {
		return createCodelistOid(this.codelist_id);
	}
	public static String createCodelistOid(String codelist_id) {
		return "CL." + codelist_id;
	}

	@Override
	public int compareTo(DefineCodelistModel codelist) {
		if (this.codelist_id.equals(codelist.codelist_id)) {
			return Integer.compare(this.ordinal, codelist.ordinal);
		} else {
			return this.codelist_id.compareTo(codelist.codelist_id);
		}
	}
}
