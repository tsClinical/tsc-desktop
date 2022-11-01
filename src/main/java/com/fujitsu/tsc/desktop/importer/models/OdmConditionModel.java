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
 * Java object representing a record (a line) in the CONDITION table of {@link OdmModel}.
 * [Primary Keys]
 * 	- condition_id
 * [Required]
 * 	- condition_id
 *  - condition_name
 *  - description
 */
public class OdmConditionModel implements Comparable<OdmConditionModel> {

	public static class OdmConditionPk implements Comparable<OdmConditionPk> {
		public final String condition_id;
		
		public OdmConditionPk(String condition_id) {
			this.condition_id = condition_id;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.condition_id.equals(((OdmConditionPk)obj).condition_id)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(OdmConditionPk key) {
			return (this.condition_id).compareTo(key.condition_id);
		}
		
		@Override
		public String toString() {
			String rtn = "ID: " + this.condition_id;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.condition_id);
	    }
	}
	
	/** Condition ID */
	@ExcelColumn( name = "Condition ID" , ordinal = 1)
	public final String condition_id;
	/** Condition Name */
	@ExcelColumn( name = "Condition Name" , ordinal = 2)
	public String condition_name;
	/** Sort Order */
	public Integer ordinal;
	/** Description */
	@ExcelColumn( name = "Description" , ordinal = 3)
	public String description;
	/** xml:lang */
	@ExcelColumn( name = "xml:lang" , ordinal = 4)
	public String xml_lang;
	/** Formal Expression Context */
	@ExcelColumn( name = "Formal Expression Context" , ordinal = 5)
	public String formal_expression_context;
	/** Formal Expression */
	@ExcelColumn( name = "Formal Expression" , ordinal = 6)
	public String formal_expression;
	/** Alias Context */
	@ExcelColumn( name = "Alias Context" , ordinal = 7)
	public String alias_context;
	/** Alias Name */
	@ExcelColumn( name = "Alias Name" , ordinal = 8)
	public String alias_name;
	/** User Note 1 */
	@ExcelColumn( name = "User Note 1" , ordinal = 9)
	public String user_note1;
	/** User Note 2 */
	@ExcelColumn( name = "User Note 2" , ordinal = 10)
	public String user_note2;

	public OdmConditionModel(OdmConditionPk key) {
		this.condition_id = key.condition_id;
		this.condition_name = "";
		this.ordinal = 0;
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
	public int compareTo(OdmConditionModel condition) {
		return Integer.compare(this.ordinal, condition.ordinal);
	}
}
