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
 * Java object representing a record (a line) in the UNIT table of {@link OdmModel}.
 * [Primary Keys]
 * 	- unit_id
 * 	- xml_lang (can be blank)
 * [Required]
 * 	- unit_id
 *  - unit_name
 *  - symbol
 */
public class OdmUnitModel implements Comparable<OdmUnitModel> {

	public static class OdmUnitPk implements Comparable<OdmUnitPk> {
		public final String unit_id;
		public final String xml_lang;
		
		public OdmUnitPk(String unit_id, String xml_lang) {
			this.unit_id = unit_id;
			this.xml_lang = xml_lang;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.unit_id.equals(((OdmUnitPk)obj).unit_id) && this.xml_lang.equals(((OdmUnitPk)obj).xml_lang)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(OdmUnitPk key) {
			return (this.unit_id + this.xml_lang).compareTo(key.unit_id + key.xml_lang);
		}
		
		@Override
		public String toString() {
			String rtn = "ID: " + this.unit_id;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.unit_id, this.xml_lang);
	    }
	}
	
	/** ID */
	@ExcelColumn( name = "ID" , ordinal = 1)
	public final String unit_id;
	/** Name */
	@ExcelColumn( name = "Name" , ordinal = 2)
	public String unit_name;
	/** Symbol */
	@ExcelColumn( name = "Symbol" , ordinal = 3)
	public String symbol;
	/** xml:lang */
	@ExcelColumn( name = "xml:lang" , ordinal = 4)
	public final String xml_lang;
	/** レコード順序 */
	public Integer ordinal;
	/** Alias Context */
	@ExcelColumn( name = "Alias Context" , ordinal = 5)
	public String alias_context;
	/** Alias Name */
	@ExcelColumn( name = "Alias Name" , ordinal = 6)
	public String alias_name;
	/** User Note 1 */
	@ExcelColumn( name = "User Note 1" , ordinal = 7)
	public String user_note1;
	/** User Note 1 */
	@ExcelColumn( name = "User Note 2" , ordinal = 8)
	public String user_note2;

	public OdmUnitModel(OdmUnitPk key) {
		this.unit_id = key.unit_id;
		this.unit_name = "";
		this.symbol = "";
		this.xml_lang = key.xml_lang;
		this.ordinal = 0;
		this.alias_context = "";
		this.alias_name = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
	
	public void copy(OdmUnitModel unit) {
		if (!StringUtils.isEmpty(unit.unit_name))
			this.unit_name = unit.unit_name;
		if (!StringUtils.isEmpty(unit.symbol))
			this.unit_name = unit.symbol;
		if (unit.ordinal > 0)
			this.ordinal = unit.ordinal;
		if (!StringUtils.isEmpty(unit.alias_context))
			this.alias_context = unit.alias_context;
		if (!StringUtils.isEmpty(unit.alias_name))
			this.alias_name = unit.alias_name;
		if (!StringUtils.isEmpty(unit.user_note1))
			this.user_note1 = unit.user_note1;
		if (!StringUtils.isEmpty(unit.user_note2))
			this.user_note2 = unit.user_note2;
	}
	
	@Override
	public int compareTo(OdmUnitModel unit) {
		return Integer.compare(this.ordinal, unit.ordinal);
	}
}
