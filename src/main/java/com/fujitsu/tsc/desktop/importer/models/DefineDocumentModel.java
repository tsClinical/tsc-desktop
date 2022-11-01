/* 
 * Copyright (c) 2022 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer.models;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fujitsu.tsc.desktop.importer.ExcelWriter2.ExcelColumn;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.DocType;
import com.fujitsu.tsc.desktop.util.Utils;

/**
 * Java object representing a record (a line) in the DOCUMENT table of {@link DefineModel}.
 * [Primary Keys]
 * 	- document_id
 * [Required]
 * 	- document_id
 *  - document_type
 *  - document_href
 *  - document_title
 */
public class DefineDocumentModel implements Comparable<DefineDocumentModel> {

	public static class DefineDocumentPk implements Comparable<DefineDocumentPk> {
		public final String document_id;
		
		public DefineDocumentPk(String document_id) {
			this.document_id = document_id;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.document_id.equals(((DefineDocumentPk)obj).document_id)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineDocumentPk key) {
			return this.document_id.compareTo(key.document_id);
		}
		
		@Override
		public String toString() {
			String rtn = "Document ID: " + this.document_id;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.document_id);
	    }
	}
	
	/** Document ID */
	public final String document_id;
	/** Ordinal */
	public Integer ordinal;
	/** Document Type */
	public DocType document_type;
	/** Document Href */
	public String document_href;
	/** Document Title */
	public String document_title;
	/** User Note 1 */
	public String user_note1;
	/** User Note 2 */
	public String user_note2;

	public DefineDocumentModel(DefineDocumentPk key) {
		this.document_id = key.document_id;
		this.ordinal = 0;
		this.document_type = DocType.Other;
		this.document_href = "";
		this.document_title = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
	
	public String toOid() {
		return createOid(this.document_id);
	}
	public static String createOid(String document_id) {
		return "LF." + Utils.codedText(document_id);
	}
	
	@Override
	public int compareTo(DefineDocumentModel document) {
		if (this.document_type == document.document_type) {
			return this.document_id.compareTo(document.document_id);
		} else {
			return this.document_type.ordinal() - document.document_type.ordinal();
		}
	}
}
