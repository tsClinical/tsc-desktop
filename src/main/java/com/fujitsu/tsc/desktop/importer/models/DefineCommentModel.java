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
public class DefineCommentModel implements Comparable<DefineCommentModel> {

	public static class DefineCommentPk implements Comparable<DefineCommentPk> {
		public final String oid;
		
		public DefineCommentPk(String oid) {
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
	        if (this.oid.equals(((DefineCommentPk)obj).oid)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineCommentPk key) {
			return (this.oid.compareTo(key.oid));
		}
		
		@Override
		public String toString() {
			String rtn = "Comment OID: " + this.oid;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.oid);
	    }
	}
	
	/** Comment OID */
	public final String oid;
	/** Ordinal */
	public Integer ordinal;
	/** Comment Text */
	public String comment_text;
	/** xml:lang */
	public String comment_lang;
	/** DocumentRef */
	public List<DocumentRef> document_refs;
	/** User Note 1 */
	public String user_note1;
	/** User Note 2 */
	public String user_note2;

	public DefineCommentModel(DefineCommentPk key) {
		this.oid = key.oid;
		this.ordinal = 0;
		this.comment_text = "";
		this.comment_lang = "";
		this.document_refs = new ArrayList<>();
	}

	/** Reference copy of given object */
	public void copy(DefineCommentModel comment) {
		if (comment.ordinal > 0)
			this.ordinal = comment.ordinal;
		if (!StringUtils.isEmpty(comment.comment_text))
			this.comment_text = comment.comment_text;
		if (!StringUtils.isEmpty(comment.comment_lang))
			this.comment_lang = comment.comment_lang;
		if (!comment.document_refs.isEmpty()) {
			this.document_refs = comment.document_refs;
		}
	}
	
	/**
	 * This method creates CommentOID of each Define models.
	 * @param obj
	 * @return
	 */
	public static String createCommentOID(Object obj) {
		final String PREFIX = "COM.";
		if (obj == null) {
			return "";
		}
		if (obj.getClass() == DefineStudyModel.class) {
			return PREFIX + ((DefineStudyModel)obj).toMetaDataOid();
		} else if (obj.getClass() == DefineStandardModel.class) {
			return PREFIX + ((DefineStandardModel)obj).toOid(); 
		} else if (obj.getClass() == DefineDatasetModel.class) {
			return PREFIX + ((DefineDatasetModel)obj).toOid();
		} else if (obj.getClass() == DefineVariableModel.class) {
			return PREFIX + ((DefineVariableModel)obj).toOid();
		} else if (obj.getClass() == DefineValueModel.class) {
			return PREFIX + ((DefineValueModel)obj).toOid();
		} else if (obj.getClass() == DefineWCModel.class) {
			return PREFIX + ((DefineWCModel)obj).toOid();
		} else if (obj.getClass() == DefineDictionaryModel.class) {
			return PREFIX + ((DefineDictionaryModel)obj).toOid();
		} else if (obj.getClass() == DefineCodelistModel.class) {
			return PREFIX + ((DefineCodelistModel)obj).getCodelistOid();
		} else if (obj.getClass() == DefineARMResultModel.class) {
			return PREFIX + ((DefineARMResultModel)obj).toOid();
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
	public int compareTo(DefineCommentModel comment) {
		return this.oid.compareTo(comment.oid);
	}
}
