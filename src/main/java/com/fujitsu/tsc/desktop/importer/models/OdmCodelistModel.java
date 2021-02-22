/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer.models;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fujitsu.tsc.desktop.importer.ExcelWriter2.ExcelColumn;

/**
 * Java object representing a record (a line) in the CODELIST table of {@link OdmModel}.
 * [Primary Keys]
 * 	- codelist
 *  - user_code
 * [Required]
 * 	- codelist
 *  - codelist_label
 *  - data_type
 *  - user_code
 */
public class OdmCodelistModel implements Comparable<OdmCodelistModel> {

	public static class OdmCodelistPk implements Comparable<OdmCodelistPk> {
		public final String codelist;
		public final String user_code;
		
		public OdmCodelistPk(String codelist, String user_code) {
			this.codelist = codelist;
			this.user_code = user_code;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.codelist.equals(((OdmCodelistPk)obj).codelist) && this.user_code.equals(((OdmCodelistPk)obj).user_code)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(OdmCodelistPk key) {
			return (this.codelist + this.user_code).compareTo(key.codelist + key.user_code);
		}
		
		@Override
		public String toString() {
			String rtn = "Codelist: " + this.codelist + System.lineSeparator();
			rtn += "User Code: " + this.user_code;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.codelist, this.user_code);
	    }
	}
	
	/** Codelist ID */
	@ExcelColumn( name = "Codelist ID" , ordinal = 1)
	public final String codelist;
	/** Codelist Code */
	@ExcelColumn( name = "Codelist Code" , ordinal = 2)
	public String codelist_code;
	/** Codelist Label */
	@ExcelColumn( name = "Codelist Label" , ordinal = 3)
	public String codelist_label;
	/** DataType */
	@ExcelColumn( name = "DataType" , ordinal = 4)
	public String data_type;
	/** SASFormatName */
	@ExcelColumn( name = "SASFormatName" , ordinal = 5)
	public String sas_format_name;
	/** Code */
	@ExcelColumn( name = "Code" , ordinal = 6)
	public String code;
	/** User Code */
	@ExcelColumn( name = "User Code" , ordinal = 7)
	public final String user_code;
	/** レコード順序 */
	public Integer ordinal;
	/** Decode */
	@ExcelColumn( name = "Decode" , ordinal = 8)
	public String decode;
	/** xml:lang */
	@ExcelColumn( name = "xml:lang" , ordinal = 9)
	public String xml_lang;
	/** Order Number */
	@ExcelColumn( name = "Order Number" , ordinal = 10)
	public Integer order_number;
	/** Rank */
	@ExcelColumn( name = "Rank" , ordinal = 11)
	public Integer rank;
	/** ExtendedValue */
	@ExcelColumn( name = "ExtendedValue" , ordinal = 12)
	public String extended_value;
	/** Submission Value */
	@ExcelColumn( name = "Submission Value" , ordinal = 13)
	public String submission_value;
	/** Alias Context */
	@ExcelColumn( name = "Alias Context" , ordinal = 14)
	public String alias_context;
	/** Alias Name */
	@ExcelColumn( name = "Alias Name" , ordinal = 15)
	public String alias_name;
	/** User Note 1 */
	@ExcelColumn( name = "User Note 1" , ordinal = 16)
	public String user_note1;
	/** User Note 2 */
	@ExcelColumn( name = "User Note 2" , ordinal = 17)
	public String user_note2;

	public OdmCodelistModel(OdmCodelistPk key) {
		this.codelist = key.codelist;
		this.codelist_code = "";
		this.codelist_label = "";
		this.data_type = "";
		this.sas_format_name = "";
		this.code = "";
		this.user_code = key.user_code;
		this.ordinal = 0;
		this.decode = "";
		this.xml_lang = "";
		this.order_number = -1;
		this.rank = -1;
		this.extended_value = "No";
		this.submission_value = "";
		this.alias_context = "";
		this.alias_name = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
	
	public void copy(OdmCodelistModel codelist) {
		if (!StringUtils.isEmpty(codelist.codelist_code))
			this.codelist_code = codelist.codelist_code;
		if (!StringUtils.isEmpty(codelist.codelist_label))
			this.codelist_label = codelist.codelist_label;
		if (!StringUtils.isEmpty(codelist.data_type))
			this.data_type = codelist.data_type;
		if (!StringUtils.isEmpty(codelist.sas_format_name))
			this.sas_format_name = codelist.sas_format_name;
		if (!StringUtils.isEmpty(codelist.code))
			this.code = codelist.code;
		if (codelist.ordinal > 0)
			this.ordinal = codelist.ordinal;
		if (!StringUtils.isEmpty(codelist.decode))
			this.decode = codelist.decode;
		if (!StringUtils.isEmpty(codelist.xml_lang))
			this.xml_lang = codelist.xml_lang;
		if (codelist.order_number > -1)
			this.order_number = codelist.order_number;
		if (codelist.rank > -1)
			this.rank = codelist.rank;
		if (!StringUtils.isEmpty(codelist.extended_value))
			this.extended_value = codelist.extended_value;
		if (!StringUtils.isEmpty(codelist.submission_value))
			this.submission_value = codelist.submission_value;
		if (!StringUtils.isEmpty(codelist.alias_context))
			this.alias_context = codelist.alias_context;
		if (!StringUtils.isEmpty(codelist.alias_name))
			this.alias_name = codelist.alias_name;
		if (!StringUtils.isEmpty(codelist.user_note1))
			this.user_note1 = codelist.user_note1;
		if (!StringUtils.isEmpty(codelist.user_note2))
			this.user_note2 = codelist.user_note2;
	}
	
	
	@Override
	public int compareTo(OdmCodelistModel codelist) {
		if (this.codelist.equals(codelist.codelist)) {
			return Integer.compare(this.ordinal, codelist.ordinal);
		} else {
			return this.codelist.compareTo(codelist.codelist);
		}
	}
}
