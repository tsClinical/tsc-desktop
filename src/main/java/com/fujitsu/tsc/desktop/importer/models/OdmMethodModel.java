/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer.models;

import java.util.Objects;

import com.fujitsu.tsc.desktop.importer.ExcelWriter2.ExcelColumn;

/**
 * Java object representing a record (a line) in the METHOD table of {@link OdmModel}.
 * [Primary Keys]
 * 	- method_id
 * [Required]
 * 	- method_id
 *  - method_name
 *  - method_type
 *  - description
 */
public class OdmMethodModel implements Comparable<OdmMethodModel> {

	public static class OdmMethodPk implements Comparable<OdmMethodPk> {
		public final String method_id;
		
		public OdmMethodPk(String method_id) {
			this.method_id = method_id;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.method_id.equals(((OdmMethodPk)obj).method_id)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(OdmMethodPk key) {
			return (this.method_id).compareTo(key.method_id);
		}
		
		@Override
		public String toString() {
			String rtn = "ID: " + this.method_id;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.method_id);
	    }
	}
	
	/** Method ID */
	@ExcelColumn( name = "Method ID" , ordinal = 1)
	public final String method_id;
	/** Method Name */
	@ExcelColumn( name = "Method Name" , ordinal = 2)
	public String method_name;
	/** Sort Order */
	public Integer ordinal;
	/** Method Type */
	@ExcelColumn( name = "Method Type" , ordinal = 3)
	public String method_type;
	/** Description */
	@ExcelColumn( name = "Description" , ordinal = 4)
	public String description;
	/** xml:lang */
	@ExcelColumn( name = "xml:lang" , ordinal = 5)
	public String xml_lang;
	/** Formal Expression Context */
	@ExcelColumn( name = "Formal Expression Context" , ordinal = 6)
	public String formal_expression_context;
	/** Formal Expression */
	@ExcelColumn( name = "Formal Expression" , ordinal = 7)
	public String formal_expression;
	/** Alias Context */
	@ExcelColumn( name = "Alias Context" , ordinal = 8)
	public String alias_context;
	/** Alias Name */
	@ExcelColumn( name = "Alias Name" , ordinal = 9)
	public String alias_name;
	/** User Note 1 */
	@ExcelColumn( name = "User Note 1" , ordinal = 10)
	public String user_note1;
	/** User Note 2 */
	@ExcelColumn( name = "User Note 2" , ordinal = 11)
	public String user_note2;

	public OdmMethodModel(OdmMethodPk key) {
		this.method_id = key.method_id;
		this.method_name = "";
		this.ordinal = 0;
		this.method_type = "";
		this.description = "";
		this.xml_lang = "";
		this.formal_expression_context = "";
		this.formal_expression = "";
		this.alias_context = "";
		this.alias_name = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}

	@Override
	public int compareTo(OdmMethodModel method) {
		return Integer.compare(this.ordinal, method.ordinal);
	}
}
