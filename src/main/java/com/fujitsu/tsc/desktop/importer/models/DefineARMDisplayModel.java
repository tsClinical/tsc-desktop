/* 
 * Copyright (c) 2022 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fujitsu.tsc.desktop.importer.models.DefineARMDatasetModel.DefineARMDatasetPk;
import com.fujitsu.tsc.desktop.importer.models.DefineARMResultModel.DefineARMResultPk;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.DocumentRef;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.DefineWCPk;
import com.fujitsu.tsc.desktop.util.Utils;

/**
 * Java object representing a record (a line) in the RESULT1 table of {@link DefineModel}.
 * [Primary Keys]
 *  - display_name
 * [Required]
 *  - display_desc
 * 	- analysis_results
 */
public class DefineARMDisplayModel implements Comparable<DefineARMDisplayModel> {

	public static class DefineARMDisplayPk implements Comparable<DefineARMDisplayPk> {
		public final String display_name;
		
		public DefineARMDisplayPk(String display_name) {
			this.display_name = display_name;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.display_name.equals(((DefineARMDisplayPk)obj).display_name)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineARMDisplayPk key) {
			return (this.display_name.compareTo(key.display_name));
		}
		
		@Override
		public String toString() {
			String rtn = "Display Name: " + this.display_name;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.display_name);
	    }
	}
	
	/** Display Name */
	public final String display_name;
	/** Ordinal */
	public Integer ordinal;
	/** Display Description */
	public String display_desc;
	/** Display xml:lang */
	public String display_lang;
	/** Leaf DocumentRef */
	public List<DocumentRef> document_refs;
	/** User Note 1 */
	public String user_note1;
	/** User Note 2 */
	public String user_note2;
	/** ARM Result */
	public List<DefineARMResultPk> arm_result_pks;
	
	public DefineARMDisplayModel(DefineARMDisplayPk key) {
		this.display_name = key.display_name;
		this.ordinal = 0;
		this.display_desc = "";
		this.display_lang = "";
		this.document_refs = new ArrayList<>();
		this.user_note1 = "";
		this.user_note2 = "";
		this.arm_result_pks = new ArrayList<>();
	}

	/** Reference copy of given object */
	public void copy(DefineARMDisplayModel arm) {
		if (arm.ordinal > 0)
			this.ordinal = arm.ordinal;
		if (!StringUtils.isEmpty(arm.display_desc))
			this.display_desc = arm.display_desc;
		if (!StringUtils.isEmpty(arm.display_lang))
			this.display_lang = arm.display_lang;
		if (!arm.document_refs.isEmpty())
			this.document_refs = arm.document_refs;
		if (arm.arm_result_pks != null && !arm.arm_result_pks.isEmpty())
			this.arm_result_pks = arm.arm_result_pks;
	}
	
	/**
	 * @return "RD." + coded display_name
	 */
	public String toOid() {
		return "RD." + Utils.codedText(this.display_name);
	}
	
	public String getDocumentIdString(String delimiter) {
		String rtn = "";
		for (int i = 0; i < document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += document_refs.get(i).document_id;
		}
		return rtn;
	}
	
	public String getDocumentPageTypeString(String delimiter) {
		String rtn = "";
		for (int i = 0; i < document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += document_refs.get(i).document_page_type;
		}
		return rtn;
	}
	
	public String getDocumentPageRefString(String delimiter) {
		String rtn = "";
		if (this.document_refs.isEmpty()) {
			return rtn;
		}
		/* If all empty, return blank. */
		List<String> document_page_references = document_refs.stream().map(o -> o.document_page_reference).filter(o -> StringUtils.isNotEmpty(o)).collect(Collectors.toList());
		if (document_page_references.isEmpty()) {
			return rtn;
		}
		for (int i = 0; i < document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += document_refs.get(i).document_page_reference;
		}
		return rtn;
	}
	
	public String getDocumentFirstPageString(String delimiter) {
		String rtn = "";
		if (this.document_refs.isEmpty()) {
			return rtn;
		}
		/* If all empty, return blank. */
		List<String> document_first_pages = document_refs.stream().map(o -> o.document_first_page).filter(o -> StringUtils.isNotEmpty(o)).collect(Collectors.toList());
		if (document_first_pages.isEmpty()) {
			return rtn;
		}
		for (int i = 0; i < document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += document_refs.get(i).document_first_page;
		}
		return rtn;
	}
	
	public String getDocumentLastPageString(String delimiter) {
		String rtn = "";
		if (this.document_refs.isEmpty()) {
			return rtn;
		}
		/* If all empty, return blank. */
		List<String> document_last_pages = document_refs.stream().map(o -> o.document_last_page).filter(o -> StringUtils.isNotEmpty(o)).collect(Collectors.toList());
		if (document_last_pages.isEmpty()) {
			return rtn;
		}
		for (int i = 0; i < document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += document_refs.get(i).document_last_page;
		}
		return rtn;
	}
	
	@Override
	public int compareTo(DefineARMDisplayModel arm) {
		return Integer.compare(this.ordinal, arm.ordinal);
	}
}
