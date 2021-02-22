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
 * Java object representing a record (a line) in the FORM table of {@link OdmModel}.
 * [Primary Keys]
 * 	- form_id
 * [Required]
 * 	- short_name
 *  - name
 *  - repeating
 */
public class OdmFormModel implements Comparable<OdmFormModel> {

	public static class OdmFormPk implements Comparable<OdmFormPk> {
		public final String form_id;
		
		public OdmFormPk(String form_id) {
			this.form_id = form_id;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.form_id.equals(((OdmFormPk)obj).form_id)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(OdmFormPk key) {
			return (this.form_id).compareTo(key.form_id);
		}
		
		@Override
		public String toString() {
			String rtn = "ID: " + this.form_id;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.form_id);
	    }
	}
	
	/** ID */
	@ExcelColumn( name = "ID" , ordinal = 1)
	public final String form_id;
	/** Name */
	@ExcelColumn( name = "Name" , ordinal = 2)
	public String name;
	/** レコード順序 */
	public Integer ordinal;
	/** Repeating */
	@ExcelColumn( name = "Repeating" , ordinal = 3)
	public String repeating;
	/** Description */
	@ExcelColumn( name = "Description" , ordinal = 4)
	public String description;
	/** xml:lang */
	@ExcelColumn( name = "xml:lang" , ordinal = 5)
	public String xml_lang;
	/** PdfFileName */
	@ExcelColumn( name = "PdfFileName" , ordinal = 6)
	public String pdf_file;
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

	public OdmFormModel(OdmFormPk key) {
		this.form_id = key.form_id;
		this.name = "";
		this.ordinal = 0;
		this.repeating = "";
		this.description = "";
		this.xml_lang = "";
		this.pdf_file = "";
		this.alias_context = "";
		this.alias_name = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
	
	@Override
	public int compareTo(OdmFormModel form) {
		return Integer.compare(this.ordinal, form.ordinal);
	}
}
