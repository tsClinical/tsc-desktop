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
 * Java object representing a record (a line) in the FIELD table of {@link OdmModel}.
 * [Primary Keys]
 * 	- form_id
 *  - item_group_oid
 * 	- field_id
 * [Required]
 * 	- form_id
 * 	- form_name
 *  - item_group_oid
 *  - field_id
 *  - name
 *  - level
 *  - mandatory
 */
public class OdmFieldModel implements Comparable<OdmFieldModel> {

	public static class OdmFieldPk implements Comparable<OdmFieldPk> {
		public final String form_id;
		public final String item_group_oid;
		public final String field_id;
		
		public OdmFieldPk(String form_id, String item_group_oid, String field_id) {
			this.form_id = form_id;
			this.item_group_oid = item_group_oid;
			this.field_id = field_id;
		}
		
		@Override
		public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        if (this.form_id.equals(((OdmFieldPk)obj).form_id) && this.item_group_oid.equals(((OdmFieldPk)obj).item_group_oid)
	        		&& this.field_id.equals(((OdmFieldPk)obj).field_id)) {
	        	return true;
	        } else {
	        	return false;
	        }
		}
		
		@Override
		public int compareTo(OdmFieldPk key) {
			return (this.form_id + this.item_group_oid + this.field_id).compareTo(key.form_id + key.item_group_oid + key.field_id);
		}
		
		@Override
		public String toString() {
			String rtn = "Form ID: " + this.form_id + System.lineSeparator();
			rtn += "ItemGroup ID: " + this.item_group_oid + System.lineSeparator();
			rtn += "ID: " + this.field_id;
			return rtn;
		}
		
	    @Override
	    public int hashCode() {
	        return Objects.hash(this.form_id, this.item_group_oid, this.field_id);
	    }
	}
	
	/** フォームID */
	public final String form_id;
	/** Form Name */
	@ExcelColumn( name = "Form Name" , ordinal = 1)
	public String form_name;
	/** アイテムグループキー */
	public final String item_group_oid;
	/** ID */
	@ExcelColumn( name = "ID" , ordinal = 2)
	public final String field_id;
	/** Item Name */
	@ExcelColumn( name = "Item Name" , ordinal = 3)
	public String name;
	/** Level */
	@ExcelColumn( name = "Level" , ordinal = 4)
	public Integer level;
	/** レコード順序 */
	public Integer ordinal;
	/** Mandatory */
	@ExcelColumn( name = "Mandatory" , ordinal = 5)
	public String mandatory;
	/** Key Sequence */
	@ExcelColumn( name = "Key Sequence" , ordinal = 6)
	public String key_sequence;
	/** Repeating */
	@ExcelColumn( name = "Repeating" , ordinal = 7)
	public String repeating;
	/** IsReferenceData */
	@ExcelColumn( name = "IsReferenceData" , ordinal = 8)
	public String is_reference_data;
	/** Question */
	@ExcelColumn( name = "Question" , ordinal = 9)
	public String question;
	/** Question xml:lang */
	@ExcelColumn( name = "Question xml:lang" , ordinal = 10)
	public String question_xml_lang;
	/** ControlType */
	@ExcelColumn( name = "ControlType" , ordinal = 11)
	public String control_type;
	/** IsLog */
	@ExcelColumn( name = "IsLog" , ordinal = 12)
	public String is_log;
	/** Derived From */
	@ExcelColumn( name = "Derived From" , ordinal = 13)
	public String derived_from;
	/** Section Label */
	@ExcelColumn( name = "Section Label" , ordinal = 14)
	public String section_label;
	/** DataType */
	@ExcelColumn( name = "DataType" , ordinal = 15)
	public String data_type;
	/** Length */
	@ExcelColumn( name = "Length" , ordinal = 16)
	public Integer length;
	/** SignificantDigits */
	@ExcelColumn( name = "SignificantDigits" , ordinal = 17)
	public Integer significant_digits;
	/** SAS Name */
	@ExcelColumn( name = "SAS Name" , ordinal = 18)
	public String sas_name;
	/** Description */
	@ExcelColumn( name = "Description" , ordinal = 19)
	public String description;
	/** Description xml:lang */
	@ExcelColumn( name = "Description xml:lang" , ordinal = 20)
	public String description_xml_lang;
	/** Unit Name */
	@ExcelColumn( name = "Unit Name" , ordinal = 21)
	public String crf_unit;
	/** Codelist */
	@ExcelColumn( name = "Codelist" , ordinal = 22)
	public String crf_codelist;
	/** RangeCheck */
	@ExcelColumn( name = "RangeCheck" , ordinal = 23)
	public String range_check;
	/** SoftHard */
	@ExcelColumn( name = "SoftHard" , ordinal = 24)
	public String soft_hard;
	/** RangeCheck Error Message */
	@ExcelColumn( name = "RangeCheck Error Message" , ordinal = 25)
	public String range_check_error;
	/** Formal Expression Context */
	@ExcelColumn( name = "Formal Expression Context" , ordinal = 26)
	public String formal_expression_context;
	/** Formal Expression */
	@ExcelColumn( name = "Formal Expression" , ordinal = 27)
	public String formal_expression;
	/** Method ID */
	@ExcelColumn( name = "Method ID" , ordinal = 28)
	public String method_id;
	/** Derivation */
	@ExcelColumn( name = "Derivation" , ordinal = 29)
	public String derivation;
	/** Condition ID */
	@ExcelColumn( name = "Condition ID" , ordinal = 30)
	public String condition_id;
	/** CollectionExceptionCondition */
	@ExcelColumn( name = "CollectionExceptionCondition" , ordinal = 31)
	public String collection_exception_cnd;
	/** Alias Context */
	@ExcelColumn( name = "Alias Context" , ordinal = 32)
	public String alias_context;
	/** Alias Name */
	@ExcelColumn( name = "Alias Name" , ordinal = 33)
	public String alias_name;
	/** User Note 1 */
	@ExcelColumn( name = "User Note 1" , ordinal = 34)
	public String user_note1;
	/** User Note 2 */
	@ExcelColumn( name = "User Note 2" , ordinal = 35)
	public String user_note2;

	public OdmFieldModel(OdmFieldPk key) {
		this.form_id = key.form_id;
		this.form_name = "";
		this.item_group_oid = key.item_group_oid;
		this.field_id = key.field_id;
		this.name = "";
		this.level = 0;
		this.ordinal = -1;
		this.mandatory = "";
		this.key_sequence = "";
		this.repeating = "";
		this.is_reference_data = "";
		this.question = "";
		this.question_xml_lang = "";
		this.control_type = "";
		this.is_log = "FALSE";
		this.derived_from = "";
		this.section_label = "";
		this.data_type = "";
		this.length = -1;
		this.significant_digits = -1;
		this.sas_name = "";
		this.description = "";
		this.description_xml_lang = "";
		this.crf_unit = "";
		this.crf_codelist = "";
		this.range_check = "";
		this.soft_hard = "";
		this.range_check_error = "";
		this.formal_expression_context = "";
		this.formal_expression = "";
		this.method_id = "";
		this.derivation = "";
		this.condition_id = "";
		this.collection_exception_cnd = "";
		this.alias_context = "";
		this.alias_name = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
	
	public OdmFieldPk getKey() {
		return new OdmFieldPk(this.form_id, this.item_group_oid, this.field_id);
	}
	
	/**
	 * Create a new field with new_key by copying a field
	 * @param field OdmFieldModel object to clone
	 * @param new_key Key of the new OdmFieldModel object
	 * @return A new OdmFieldModel object
	 */
	public static OdmFieldModel clone(OdmFieldModel field, OdmFieldPk new_key) {
		OdmFieldModel new_field = new OdmFieldModel(new_key);
		new_field.form_name = field.form_name;
		new_field.name = field.name;
		new_field.level = field.level;
		new_field.ordinal = field.ordinal;
		new_field.mandatory = field.mandatory;
		new_field.key_sequence = field.key_sequence;
		new_field.repeating = field.repeating;
		new_field.is_reference_data = field.is_reference_data;
		new_field.question = field.question;
		new_field.question_xml_lang = field.question_xml_lang;
		new_field.control_type = field.control_type;
		new_field.is_log = field.is_log;
		new_field.derived_from = field.derived_from;
		new_field.section_label = field.section_label;
		new_field.data_type = field.data_type;
		new_field.length = field.length;
		new_field.significant_digits = field.significant_digits;
		new_field.sas_name = field.sas_name;
		new_field.description = field.description;
		new_field.description_xml_lang = field.description_xml_lang;
		new_field.crf_unit = field.crf_unit;
		new_field.crf_codelist = field.crf_codelist;
		new_field.range_check = field.range_check;
		new_field.soft_hard = field.soft_hard;
		new_field.range_check_error = field.range_check_error;
		new_field.formal_expression_context = field.formal_expression_context;
		new_field.formal_expression = field.formal_expression;
		new_field.method_id = field.method_id;
		new_field.derivation = field.derivation;
		new_field.condition_id = field.condition_id;
		new_field.collection_exception_cnd = field.collection_exception_cnd;
		new_field.alias_context = field.alias_context;
		new_field.alias_name = field.alias_name;
		new_field.user_note1 = field.user_note1;
		new_field.user_note2 = field.user_note2;
		return new_field;
	}
	
	@Override
	public int compareTo(OdmFieldModel field) {
		if (this.form_id.equals(field.form_id)) {
			if (this.item_group_oid.equals(field.item_group_oid)) {
				if (this.level == field.level) {
					return Integer.compare(this.ordinal, field.ordinal);
				} else {
					return Integer.compare(field.level, this.level);
				}
			} else {
				return this.item_group_oid.compareTo(field.item_group_oid);
			}
		} else {
			return this.form_id.compareTo(field.form_id);
		}
	}
}
