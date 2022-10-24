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

/**
 * Java object representing a record (a line) in the DOCUMENT table of {@link DefineModel}.
 * [Primary Keys]
 * 	- dictionary_oid
 * [Required]
 * 	- dictionary_oid
 * 	- dictionary_id
 *  - dictionary_name
 *  - data_type
 *  - dictionary_version
 */
public class DefineDictionaryModel implements Comparable<DefineDictionaryModel> {

	public static class DefineDictionaryPk implements Comparable<DefineDictionaryPk> {
		public final String dictionary_oid;
		
		public DefineDictionaryPk(String dictionary_oid) {
			this.dictionary_oid = dictionary_oid;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.dictionary_oid.equals(((DefineDictionaryPk)obj).dictionary_oid)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(DefineDictionaryPk key) {
			return this.dictionary_oid.compareTo(key.dictionary_oid);
		}
		
		@Override
		public String toString() {
			String rtn = "Dictionary OID: " + this.dictionary_oid;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.dictionary_oid);
	    }
	}
	
	/** Dictionary OID */
	public final String dictionary_oid;
	/** Dictionary ID */
	public String dictionary_id;
	/** Ordinal */
	public Integer ordinal;
	/** Name */
	public String dictionary_name;
	/** DataType */
	public String data_type;
	/** Version */
	public String dictionary_version;
	/** ref */
	public String dictionary_ref;
	/** href */
	public String dictionary_href;
	/** Comment OID */
	public String comment_oid;
	/** User Note 1 */
	public String user_note1;
	/** User Note 2 */
	public String user_note2;

	public DefineDictionaryModel(DefineDictionaryPk key) {
		this.dictionary_oid = key.dictionary_oid;
		this.dictionary_id = "";
		this.ordinal = 0;
		this.dictionary_name = "";
		this.data_type = "";
		this.dictionary_version = "";
		this.dictionary_ref = "";
		this.dictionary_href = "";
		this.comment_oid = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
	
	public void copy(DefineDictionaryModel dictionary) {
		if (dictionary.ordinal > 0)
			this.ordinal = dictionary.ordinal;
		if (!StringUtils.isEmpty(dictionary.dictionary_name))
			this.dictionary_name = dictionary.dictionary_name;
		if (!StringUtils.isEmpty(dictionary.data_type))
			this.data_type = dictionary.data_type;
		if (!StringUtils.isEmpty(dictionary.dictionary_version))
			this.dictionary_version = dictionary.dictionary_version;
		if (!StringUtils.isEmpty(dictionary.dictionary_ref))
			this.dictionary_ref = dictionary.dictionary_ref;
		if (!StringUtils.isEmpty(dictionary.dictionary_href))
			this.dictionary_href = dictionary.dictionary_href;
		if (!StringUtils.isEmpty(dictionary.comment_oid))
			this.comment_oid = dictionary.comment_oid;
		if (!StringUtils.isEmpty(dictionary.user_note1))
			this.user_note1 = dictionary.user_note1;
		if (!StringUtils.isEmpty(dictionary.user_note2))
			this.user_note2 = dictionary.user_note2;
	}
	
	/**
	 * @return "CL." + dictionary_id
	 */
	public String toOid() {
		return dictionary_oid;
	}
	public static String createOid(String dictionary_id) {
		return DefineCodelistModel.createCodelistOid(dictionary_id);
	}
	
	@Override
	public int compareTo(DefineDictionaryModel dictionary) {
		return this.dictionary_id.compareTo(dictionary.dictionary_id);
	}
}
