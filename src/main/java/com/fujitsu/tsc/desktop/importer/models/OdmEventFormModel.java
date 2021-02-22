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
 * Java object representing a record (a line) in the EVENTxFORM table of {@link OdmModel}.
 * [Primary Keys]
 * 	- event_id
 * 	- form_id
 * [Required]
 * 	- event_id
 * 	- event_name
 * 	- form_id
 *  - form_name
 *  - mandatory
 */
public class OdmEventFormModel implements Comparable<OdmEventFormModel> {

	public static class OdmEventFormPk implements Comparable<OdmEventFormPk> {
		public final String event_id;
		public final String form_id;
		
		public OdmEventFormPk(String event_id, String form_id) {
			this.event_id = event_id;
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
	        if (this.event_id.equals(((OdmEventFormPk)obj).event_id) && this.form_id.equals(((OdmEventFormPk)obj).form_id)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(OdmEventFormPk key) {
			return (this.event_id + this.form_id).compareTo(key.event_id + key.form_id);
		}
		
		@Override
		public String toString() {
			String rtn = "Event ID: " + this.event_id + System.lineSeparator();
			rtn += "Form ID: " + this.form_id;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.event_id, this.form_id);
	    }
	}
	
	/** イベントID */
	public final String event_id;
	/** Event Name */
	@ExcelColumn( name = "Event Name" , ordinal = 1)
	public String event_name;
	/** フォームID */
	public final String form_id;
	/** Form Name */
	@ExcelColumn( name = "Form Name" , ordinal = 2)
	public String form_name;
	/** レコード順序 */
	public Integer ordinal;
	/** Mandatory */
	@ExcelColumn( name = "Mandatory" , ordinal = 3)
	public String mandatory;
	/** CollectionExceptionCondition */
	@ExcelColumn( name = "CollectionExceptionCondition" , ordinal = 4)
	public String collection_exception_cnd;
	/** User Note 1 */
	@ExcelColumn( name = "User Note 1" , ordinal = 5)
	public String user_note1;
	/** User Note 2 */
	@ExcelColumn( name = "User Note 2" , ordinal = 6)
	public String user_note2;

	public OdmEventFormModel(OdmEventFormPk key) {
		this.event_id = key.event_id;
		this.event_name = "";
		this.form_id = key.form_id;
		this.form_name = "";
		this.ordinal = 0;
		this.mandatory = "";
		this.collection_exception_cnd = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
	
	public void copy(OdmEventFormModel eventform) {
		if (!StringUtils.isEmpty(eventform.event_name))
			this.event_name = eventform.event_name;
		if (!StringUtils.isEmpty(eventform.form_name))
			this.form_name = eventform.form_name;
		if (eventform.ordinal > 0)
			this.ordinal = eventform.ordinal;
		if (!StringUtils.isEmpty(eventform.mandatory))
			this.mandatory = eventform.mandatory;
		if (!StringUtils.isEmpty(eventform.collection_exception_cnd))
			this.collection_exception_cnd = eventform.collection_exception_cnd;
		if (!StringUtils.isEmpty(eventform.user_note1))
			this.user_note1 = eventform.user_note1;
		if (!StringUtils.isEmpty(eventform.user_note2))
			this.user_note2 = eventform.user_note2;
	}
	
	@Override
	public int compareTo(OdmEventFormModel eventform) {
		if (this.event_name.equals(eventform.event_name)) {
			return Integer.compare(this.ordinal, eventform.ordinal);
		} else {
			return this.event_name.compareTo(eventform.event_name);
		}
	}
}
