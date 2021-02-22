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
 * Java object representing a record (a line) in the EVENT table of {@link OdmModel}.
 * [Primary Keys]
 * 	- event_id
 * [Required]
 * 	- event_id
 *  - event_name
 *  - mandatory
 *  - repeating
 *  - event_type
 */
public class OdmEventModel implements Comparable<OdmEventModel> {

	public static class OdmEventPk implements Comparable<OdmEventPk> {
		public final String event_id;
		
		public OdmEventPk(String event_id) {
			this.event_id = event_id;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.event_id.equals(((OdmEventPk)obj).event_id)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(OdmEventPk key) {
			return (this.event_id).compareTo(key.event_id);
		}
		
		@Override
		public String toString() {
			String rtn = "ID: " + this.event_id;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.event_id);
	    }
	}
	
	/** ID */
	@ExcelColumn( name = "ID" , ordinal = 1)
	public final String event_id;
	/** Name */
	@ExcelColumn( name = "Name" , ordinal = 2)
	public String event_name;
	/** レコード順序 */
	public Integer ordinal;
	/** Mandatory */
	@ExcelColumn( name = "Mandatory" , ordinal = 3)
	public String mandatory;
	/** Repeating */
	@ExcelColumn( name = "Repeating" , ordinal = 4)
	public String repeating;
	/** Type */
	@ExcelColumn( name = "Type" , ordinal = 5)
	public String event_type;
	/** Category */
	@ExcelColumn( name = "Category" , ordinal = 6)
	public String category;
	/** Description */
	@ExcelColumn( name = "Description" , ordinal = 7)
	public String description;
	/** xml:lang */
	@ExcelColumn( name = "xml:lang" , ordinal = 8)
	public String xml_lang;
	/** Alias Context */
	@ExcelColumn( name = "Alias Context" , ordinal = 9)
	public String alias_context;
	/** Alias Name */
	@ExcelColumn( name = "Alias Name" , ordinal = 10)
	public String alias_name;
	/** CollectionExceptionCondition */
	@ExcelColumn( name = "CollectionExceptionCondition" , ordinal = 11)
	public String collection_exception_cnd;
	/** User Note 1 */
	@ExcelColumn( name = "User Note 1" , ordinal = 12)
	public String user_note1;
	/** User Note 2 */
	@ExcelColumn( name = "User Note 2" , ordinal = 13)
	public String user_note2;

	public OdmEventModel(OdmEventPk key) {
		this.event_id = key.event_id;
		this.event_name = "";
		this.ordinal = 0;
		this.mandatory = "";
		this.repeating = "";
		this.event_type = "";
		this.category = "";
		this.description = "";
		this.xml_lang = "";
		this.alias_context = "";
		this.alias_name = "";
		this.collection_exception_cnd = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
	
	@Override
	public int compareTo(OdmEventModel event) {
		return Integer.compare(this.ordinal, event.ordinal);
	}
}
