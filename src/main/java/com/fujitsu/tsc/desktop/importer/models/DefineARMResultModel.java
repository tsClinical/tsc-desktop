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
import com.fujitsu.tsc.desktop.util.Utils;

/**
 * Java object representing an analysis result referenced in the RESULT1 table of {@link DefineModel}.
 * [Primary Keys]
 *  - display_name
 *  - result_key
 * [Required]
 *  - result_desc
 * 	- analysis_reason
 *  - analysis_purpose
 */
public class DefineARMResultModel implements Comparable<DefineARMResultModel> {

	public static class DefineARMResultPk implements Comparable<DefineARMResultPk> {
		public final String display_name;
		public final String result_key;
		
		public DefineARMResultPk(String display_name, String result_key) {
			this.display_name = display_name;
			this.result_key = result_key;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.display_name.equals(((DefineARMResultPk)obj).display_name) && this.result_key.equals(((DefineARMResultPk)obj).result_key)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineARMResultPk key) {
			return (this.display_name + this.result_key).compareTo(key.display_name + key.result_key);
		}
		
		@Override
		public String toString() {
			String rtn = "Display Name: " + this.display_name + System.lineSeparator();
			rtn += "Result Key: " + this.result_key;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.display_name, this.result_key);
	    }
	}
	
	/** Display Name */
	public final String display_name;
	/** Result Key */
	public final String result_key;
	/** Ordinal */
	public Integer ordinal;	//Ordinal within a display
	/** Result Description */
	public String result_desc = "";
	/** Result xml:lang */
	public String result_lang = "";
	/** ParameterOID */
	public String param_oid = "";
	/** ParameterOID Dataset */
	public String param_dataset = "";
	/** Analysis Reason */
	public String analysis_reason = "";
	/** Analysis Purpose */
	public String analysis_purpose = "";
	/** Documentation Text */
	public String docm_text = "";
	/** Documentation xml:lang */
	public String docm_lang = "";
	/** Documentation DocumentRef */
	public List<DocumentRef> docm_document_refs = new ArrayList<>();
	/** Programming Code Context */
	public String prog_code_context = "";
	/** Programming Code Text */
	public String prog_code_text = "";
	/** Programming Code DocumentRef */
	public List<DocumentRef> prog_code_document_refs = new ArrayList<>();
	/** Datasets Comment OID */
	public String dataset_comment_oid = "";
	
	public DefineARMResultModel(DefineARMResultPk key) {
		this.display_name = key.display_name;
		this.result_key = key.result_key;
		this.result_desc = "";
		this.result_lang = "";
		this.param_oid = "";
		this.param_dataset = "";
		this.analysis_reason = "";
		this.analysis_purpose = "";
		this.docm_text = "";
		this.docm_lang = "";
		this.docm_document_refs = new ArrayList<>();
		this.prog_code_context = "";
		this.prog_code_text = "";
		this.prog_code_document_refs = new ArrayList<>();
		this.dataset_comment_oid = "";
	}
	
	/**
	 * @return "RD." + coded display_name
	 */
	public String toOid() {
		return createOid(this.display_name, this.result_key);
	}
	public static String createOid(String display_name, String result_key) {
		return "AR." + Utils.codedText(display_name) + "." + Utils.codedText(result_key);
	}
	
	public String getDocumentationIdString(String delimiter) {
		String rtn = "";
		for (int i = 0; i < docm_document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += docm_document_refs.get(i).document_id;
		}
		return rtn;
	}
	
	public String getDocumentationPageTypeString(String delimiter) {
		String rtn = "";
		for (int i = 0; i < docm_document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += docm_document_refs.get(i).document_page_type;
		}
		return rtn;
	}
	
	public String getDocumentationPageRefString(String delimiter) {
		String rtn = "";
		if (this.docm_document_refs.isEmpty()) {
			return rtn;
		}
		/* If all empty, return blank. */
		List<String> document_page_references = docm_document_refs.stream().map(o -> o.document_page_reference).filter(o -> StringUtils.isNotEmpty(o)).collect(Collectors.toList());
		if (document_page_references.isEmpty()) {
			return rtn;
		}
		for (int i = 0; i < docm_document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += docm_document_refs.get(i).document_page_reference;
		}
		return rtn;
	}
	
	public String getDocumentationFirstPageString(String delimiter) {
		String rtn = "";
		if (this.docm_document_refs.isEmpty()) {
			return rtn;
		}
		/* If all empty, return blank. */
		List<String> document_first_pages = docm_document_refs.stream().map(o -> o.document_first_page).filter(o -> StringUtils.isNotEmpty(o)).collect(Collectors.toList());
		if (document_first_pages.isEmpty()) {
			return rtn;
		}
		for (int i = 0; i < docm_document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += docm_document_refs.get(i).document_first_page;
		}
		return rtn;
	}
	
	public String getDocumentationLastPageString(String delimiter) {
		String rtn = "";
		if (this.docm_document_refs.isEmpty()) {
			return rtn;
		}
		/* If all empty, return blank. */
		List<String> document_last_pages = docm_document_refs.stream().map(o -> o.document_last_page).filter(o -> StringUtils.isNotEmpty(o)).collect(Collectors.toList());
		if (document_last_pages.isEmpty()) {
			return rtn;
		}
		for (int i = 0; i < docm_document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += docm_document_refs.get(i).document_last_page;
		}
		return rtn;
	}
	
	public String getCodeIdString(String delimiter) {
		String rtn = "";
		for (int i = 0; i < docm_document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += docm_document_refs.get(i).document_id;
		}
		return rtn;
	}
	
	public String getCodePageTypeString(String delimiter) {
		String rtn = "";
		for (int i = 0; i < docm_document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += docm_document_refs.get(i).document_page_type;
		}
		return rtn;
	}
	
	public String getCodePageRefString(String delimiter) {
		String rtn = "";
		if (this.docm_document_refs.isEmpty()) {
			return rtn;
		}
		/* If all empty, return blank. */
		List<String> document_page_references = docm_document_refs.stream().map(o -> o.document_page_reference).filter(o -> StringUtils.isNotEmpty(o)).collect(Collectors.toList());
		if (document_page_references.isEmpty()) {
			return rtn;
		}
		for (int i = 0; i < docm_document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += docm_document_refs.get(i).document_page_reference;
		}
		return rtn;
	}
	
	public String getCodeFirstPageString(String delimiter) {
		String rtn = "";
		if (this.docm_document_refs.isEmpty()) {
			return rtn;
		}
		/* If all empty, return blank. */
		List<String> document_first_pages = docm_document_refs.stream().map(o -> o.document_first_page).filter(o -> StringUtils.isNotEmpty(o)).collect(Collectors.toList());
		if (document_first_pages.isEmpty()) {
			return rtn;
		}
		for (int i = 0; i < docm_document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += docm_document_refs.get(i).document_first_page;
		}
		return rtn;
	}
	
	public String getCodeLastPageString(String delimiter) {
		String rtn = "";
		if (this.docm_document_refs.isEmpty()) {
			return rtn;
		}
		/* If all empty, return blank. */
		List<String> document_last_pages = docm_document_refs.stream().map(o -> o.document_last_page).filter(o -> StringUtils.isNotEmpty(o)).collect(Collectors.toList());
		if (document_last_pages.isEmpty()) {
			return rtn;
		}
		for (int i = 0; i < docm_document_refs.size(); i++) {
			if (i > 0) {
				rtn += delimiter;
			}
			rtn += docm_document_refs.get(i).document_last_page;
		}
		return rtn;
	}

	@Override
	public int compareTo(DefineARMResultModel result) {
		if (StringUtils.equals(this.display_name, result.display_name)) {
			return this.result_key.compareTo(result.result_key);
		} else {
			return this.display_name.compareTo(result.display_name);
		}
	}
}
