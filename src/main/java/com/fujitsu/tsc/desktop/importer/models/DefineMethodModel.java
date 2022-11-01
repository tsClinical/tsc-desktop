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

import com.fujitsu.tsc.desktop.importer.models.DefineModel.DocumentRef;

/**
 * Java object representing a where clause referenced from the DOCUMENT or RESULT2 table of {@link DefineModel}.
 * [Primary Keys]
 *  - oid
 * [Required]
 * 	- oid
 *  - method_name
 *  - method_type
 */
public class DefineMethodModel implements Comparable<DefineMethodModel> {

	public static class DefineMethodPk implements Comparable<DefineMethodPk> {
		public final String oid;
		
		public DefineMethodPk(String oid) {
			this.oid = oid;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.oid.equals(((DefineMethodPk)obj).oid)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineMethodPk key) {
			return (this.oid.compareTo(key.oid));
		}
		
		@Override
		public String toString() {
			String rtn = "Method OID: " + this.oid;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.oid);
	    }
	}
	
	/** Method OID */
	public final String oid;
	/** Method Name */
	public String method_name;
	/** Ordinal */
	public Integer ordinal;
	/** Method Type */
	public String method_type;
	/** Description */
	public String description;
	/** xml:lang */
	public String description_lang;
	/** DocumentRef */
	public List<DocumentRef> document_refs;
	/** Formal Expression Context */
	public String formal_expression_context;
	/** Formal Expression */
	public String formal_expression;
	/** User Note 1 */
	public String user_note1;
	/** User Note 2 */
	public String user_note2;

	public DefineMethodModel(DefineMethodPk key) {
		this.oid = key.oid;
		this.method_name = "";
		this.ordinal = 0;
		this.method_type = "";
		this.description = "";
		this.description_lang = "";
		this.document_refs = new ArrayList<>();
		this.formal_expression_context = "";
		this.formal_expression = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}

	/** Reference copy of given object */
	public void copy(DefineMethodModel method) {
		if (!StringUtils.isEmpty(method.method_name))
			this.method_name = method.method_name;
		if (method.ordinal > 0)
			this.ordinal = method.ordinal;
		if (!StringUtils.isEmpty(method.method_type))
			this.method_type = method.method_type;
		if (!StringUtils.isEmpty(method.description))
			this.description = method.description;
		if (!StringUtils.isEmpty(method.description_lang))
			this.description_lang = method.description_lang;
		if (!document_refs.isEmpty())
			this.document_refs = method.document_refs;
		if (!StringUtils.isEmpty(method.formal_expression_context))
			this.formal_expression_context = method.formal_expression_context;
		if (!StringUtils.isEmpty(method.formal_expression))
			this.formal_expression = method.formal_expression;
		if (!StringUtils.isEmpty(method.user_note1))
			this.user_note1 = method.user_note1;
		if (!StringUtils.isEmpty(method.user_note2))
			this.user_note2 = method.user_note2;
	}
	
	/**
	 * This method creates MethodOID of each Define models.
	 * @param obj
	 * @return
	 */
	public static String createMethodOID(Object obj) {
		final String PREFIX = "MT.";
		if (obj == null) {
			return "";
		}
		if (obj.getClass() == DefineVariableModel.class) {
			return PREFIX + ((DefineVariableModel)obj).toOid();
		} else if (obj.getClass() == DefineValueModel.class) {
			return PREFIX + ((DefineValueModel)obj).toOid(); 
		} else {
			return "";
		}
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

	public String getDocumentPageTitleString(String delimiter) {
		String rtn = "";
		if (this.document_refs.isEmpty()) {
			return rtn;
		}
		/* If all empty, return blank. */
		List<String> document_page_titles = document_refs.stream().map(o -> o.document_page_title).filter(o -> StringUtils.isNotEmpty(o)).collect(Collectors.toList());
		if (document_page_titles.isEmpty()) {
			return rtn;
		}
		for (int i = 0; i < document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += document_refs.get(i).document_page_title;
		}
		return rtn;
	}
	
	@Override
	public int compareTo(DefineMethodModel wc) {
		return this.oid.compareTo(wc.oid);
	}
}
