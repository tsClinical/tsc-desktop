/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer.models;

import org.apache.commons.lang3.math.NumberUtils;

import com.fujitsu.tsc.desktop.importer.ExcelWriter2.ExcelColumn;

/**
 * Java object representing the EDC_KEYS data of {@link OdmModel}.
 * [Primary Keys]
 * 	N/A (single object)
 * [Required]
 * 	- subject_id
 *  - visit_id
 *  - form_id
 */
public class EdcKeysModel {
	/** TSC_SubjectID */
	@ExcelColumn( name = "SubjectKey" , ordinal = 1)
	public String subject_id;
	/** TSC_VisitID */
	@ExcelColumn( name = "StudyEventOID" , ordinal = 2)
	public String visit_id;
	/** TSC_VisitRepeatKey */
	@ExcelColumn( name = "StudyEventRepeatKey" , ordinal = 3)
	public String visit_repeat_key;
	/** TSC_FormID */
	@ExcelColumn( name = "FormOID" , ordinal = 4)
	public String form_id;
	/** TSC_FormRepeatKey */
	@ExcelColumn( name = "FormRepeatKey" , ordinal = 5)
	public String form_repeat_key;
	/** TSC_ItemGroupID */
	@ExcelColumn( name = "ItemGroupOID" , ordinal = 6)
	public String itemgroup_id;
	/** TSC_ItemGroupRepeatKey */
	@ExcelColumn( name = "ItemGroupRepeatKey" , ordinal = 7)
	public String itemgroup_repeat_key;
	/** Unmapped Common Variables */
	@ExcelColumn( name = "Unmapped Common Variables" , ordinal = 8)
	public String common_vars;
	/** TSC_ItemID */
	@ExcelColumn( name = "ItemOID" , ordinal = 9)
	public String item_id;
	/** Value */
	@ExcelColumn( name = "Value" , ordinal = 10)
	public String value;

	public EdcKeysModel() {
		this.subject_id = "";
		this.visit_id = "";
		this.visit_repeat_key = "";
		this.form_id = "";
		this.form_repeat_key = "";
		this.itemgroup_id = "";
		this.itemgroup_repeat_key = "";
		this.common_vars = "";
		this.item_id = "";
		this.value = "";
	}
}
