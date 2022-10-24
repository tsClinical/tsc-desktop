/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fujitsu.tsc.desktop.importer.models.OdmCodelistModel.OdmCodelistPk;
import com.fujitsu.tsc.desktop.importer.models.OdmConditionModel.OdmConditionPk;
import com.fujitsu.tsc.desktop.importer.models.OdmEventFormModel.OdmEventFormPk;
import com.fujitsu.tsc.desktop.importer.models.OdmEventModel.OdmEventPk;
import com.fujitsu.tsc.desktop.importer.models.OdmFieldModel.OdmFieldPk;
import com.fujitsu.tsc.desktop.importer.models.OdmFormModel.OdmFormPk;
import com.fujitsu.tsc.desktop.importer.models.OdmMethodModel.OdmMethodPk;
import com.fujitsu.tsc.desktop.importer.models.OdmUnitModel.OdmUnitPk;
import com.fujitsu.tsc.desktop.util.Config;

/**
 * Java object representing an ODM excel file which consists of multiple sheets.
 * Each sheet is represented by a respective model object.
 *  - STUDY table ({@link OdmStudyModel})
 *  - UNIT table ({@link OdmUnitModel})
 *  - EVENT table ({@link OdmEventModel})
 *  - EVENTxFORM table ({@link OdmEventFormModel})
 *  - FORM table ({@link OdmFormModel})
 *  - FIELD table ({@link OdmFieldModel})
 *  - CODELIST table ({@link OdmCodelistModel})
 *  - METHOD table ({@link OdmMethodModel})
 *  - CONDITION table ({@link OdmConditionModel})
 *  
 *  The STUDY table is required for an ODM file, but the other tables could be blank.
 */
public class OdmModel {
	private OdmStudyModel odm_study;	//STUDY
	private Map<OdmUnitPk, OdmUnitModel> map_odm_unit;	//UNIT
	private int unit_max_ordinal;
	private Map<OdmEventPk, OdmEventModel> map_odm_event;	//EVENT
	private Map<OdmEventFormPk, OdmEventFormModel> map_odm_eventform;	//EVENTxFORM
	private Map<OdmFormPk, OdmFormModel> map_odm_form;	//FORM
	private Map<OdmFieldPk, OdmFieldModel> map_odm_field;	//FIELD
	private Map<OdmCodelistPk, OdmCodelistModel> map_odm_codelist;	//CODELIST
	private Map<OdmMethodPk, OdmMethodModel> map_odm_method;	//METHOD
	private Map<OdmConditionPk, OdmConditionModel> map_odm_condition;	//CONDITION
	private EdcKeysModel edc_keys;	//EDC_KEYS
	
	public OdmModel() {
		this.odm_study = new OdmStudyModel();
		this.map_odm_unit = new HashMap<>();
		this.unit_max_ordinal = 0;
		this.map_odm_event = new HashMap<>();
		this.map_odm_eventform = new HashMap<>();
		this.map_odm_form = new HashMap<>();
		this.map_odm_field = new HashMap<>();
		this.map_odm_codelist = new HashMap<>();
		this.map_odm_method = new HashMap<>();
		this.map_odm_condition = new HashMap<>();
		this.edc_keys = new EdcKeysModel();
	}
	
	public void put(OdmStudyModel study) {
		this.odm_study = study;
	}
	
	public OdmStudyModel getStudy() {
		return this.odm_study;
	}
	
	public List<OdmUnitModel> listUnit() {
		List<OdmUnitModel> rtn = new ArrayList<>();
		rtn = this.map_odm_unit.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<OdmEventModel> listEvent() {
		List<OdmEventModel> rtn = new ArrayList<>();
		rtn = this.map_odm_event.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<OdmEventFormModel> listEventForm() {
		List<OdmEventFormModel> rtn = new ArrayList<>();
		rtn = this.map_odm_eventform.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<OdmFormModel> listForm() {
		List<OdmFormModel> rtn = new ArrayList<>();
		rtn = this.map_odm_form.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<OdmFieldModel> listField() {
		List<OdmFieldModel> rtn = new ArrayList<>();
		rtn = this.map_odm_field.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<OdmCodelistModel> listCodelist() {
		List<OdmCodelistModel> rtn = new ArrayList<>();
		rtn = this.map_odm_codelist.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<OdmMethodModel> listMethod() {
		List<OdmMethodModel> rtn = new ArrayList<>();
		rtn = this.map_odm_method.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public List<OdmConditionModel> listCondition() {
		List<OdmConditionModel> rtn = new ArrayList<>();
		rtn = this.map_odm_condition.values().stream().sorted().collect(Collectors.toList());
		return rtn;
	}
	
	public void put(OdmUnitPk key, OdmUnitModel unit) {
		this.map_odm_unit.put(key, unit);
		if (unit != null && unit.ordinal > this.unit_max_ordinal)
			this.unit_max_ordinal = unit.ordinal;
	}
	
	public OdmUnitModel get(OdmUnitPk key) {
		return this.map_odm_unit.get(key);
	}
	
	public int getUnitMaxOrdinal() {
		return this.unit_max_ordinal;
	}
	
	public void put(OdmEventPk key, OdmEventModel event) {
		this.map_odm_event.put(key, event);
	}
	
	public OdmEventModel get(OdmEventPk key) {
		return this.map_odm_event.get(key);
	}
	
	public void put(OdmEventFormPk key, OdmEventFormModel eventform) {
		this.map_odm_eventform.put(key, eventform);
	}
	
	public OdmEventFormModel get(OdmEventFormPk key) {
		return this.map_odm_eventform.get(key);
	}
	
	public void put(OdmFormPk key, OdmFormModel form) {
		this.map_odm_form.put(key, form);
	}
	
	public OdmFormModel get(OdmFormPk key) {
		return this.map_odm_form.get(key);
	}
	
	public void updateEventForm(String form_id, String form_name) {
		Set<OdmEventFormPk> keys = this.map_odm_eventform.keySet();
		for (OdmEventFormPk key : keys) {
			OdmEventFormModel eventform = this.map_odm_eventform.get(key);
			if (eventform.form_id.equals(form_id)) {
				eventform.form_name = form_name;
				this.map_odm_eventform.put(key, eventform);
			}
		}
	}
	
	public void put(OdmFieldPk key, OdmFieldModel field) {
		this.map_odm_field.put(key, field);
	}
	
	public OdmFieldModel get(OdmFieldPk key) {
		return this.map_odm_field.get(key);
	}
	
	public List<OdmFieldModel> getItemGroupById(String item_group_oid) {
		List<OdmFieldModel> rtn = new ArrayList<>();
		Set<OdmFieldPk> keys = this.map_odm_field.keySet();
		for (OdmFieldPk key : keys) {
			OdmFieldModel itemgroup = this.map_odm_field.get(key);
			if (itemgroup.field_id.equals(item_group_oid) && itemgroup.item_group_oid.equals(item_group_oid)) {
				rtn.add(itemgroup);
			}
		}
		return rtn;
	}
	
	public List<OdmFieldModel> getItemById(String item_oid) {
		List<OdmFieldModel> rtn = new ArrayList<>();
		Set<OdmFieldPk> keys = this.map_odm_field.keySet();
		for (OdmFieldPk key : keys) {
			OdmFieldModel item = this.map_odm_field.get(key);
			if (item.field_id.equals(item_oid)) {
				rtn.add(item);
			}
		}
		return rtn;
	}
	
	/**
	 * Update Form Name based on form_id
	 */
	public void updateFieldFormName() {
		Set<OdmFieldPk> keys = this.map_odm_field.keySet();
		for (OdmFieldPk key : keys) {
			OdmFieldModel field = this.map_odm_field.get(key);
			OdmFormModel form = this.map_odm_form.get(new OdmFormPk(key.form_id));
			if (field != null && form != null) {
				field.form_name = form.name;
			}
		}
	}

	/**
	 * Update field_id
	 * If a field is NOT a common variable, then the field_id must be [form_id].[field_id]
	 * @param delimiter The delimiter of common variables
	 */
	public void updateFieldId(String delimiter) {
		Set<String> common_vars_set = new HashSet<>();
		String[] common_vars_array = StringUtils.split(this.edc_keys.common_vars, delimiter);
		if (common_vars_array == null) {
			return;
		}
		for (int i = 0; i < common_vars_array.length; i++) {
				common_vars_set.add(common_vars_array[i]);
		}
		Set<OdmFieldPk> keys = this.map_odm_field.keySet();
		Map<OdmFieldPk, OdmFieldModel> new_fields_map = new HashMap<>();
		Iterator<OdmFieldPk> iterator = keys.iterator();
		while (iterator.hasNext()) {
			OdmFieldPk key = iterator.next();
			if (!common_vars_set.contains(key.field_id)) {
				String new_field_id = key.form_id + "." + key.field_id;
				OdmFieldPk new_key = new OdmFieldPk(key.form_id, key.item_group_oid, new_field_id);
				OdmFieldModel new_field = OdmFieldModel.clone(this.map_odm_field.get(key), new_key);
				iterator.remove();
				new_fields_map.put(new_key, new_field);
			}
		}
		for (OdmFieldPk key : new_fields_map.keySet()) {
			this.map_odm_field.put(key, new_fields_map.get(key));
		}
	}
	
	public void put(OdmCodelistPk key, OdmCodelistModel codelist) {
		this.map_odm_codelist.put(key, codelist);
	}
	
	public OdmCodelistModel get(OdmCodelistPk key) {
		return this.map_odm_codelist.get(key);
	}
	
	public void updateCodelist(String codelist_id, String alias_context, String alias_name, String DELIMITER) {
		Set<OdmCodelistPk> keys = this.map_odm_codelist.keySet();
		for (OdmCodelistPk key : keys) {
			OdmCodelistModel codelist = this.map_odm_codelist.get(key);
			if (codelist.codelist.equals(codelist_id)) {
				if (StringUtils.isEmpty(alias_context)) {
					codelist.alias_context = alias_context;
				} else {
					codelist.alias_context = codelist.alias_context + DELIMITER + alias_context;
				}
				if (StringUtils.isEmpty(alias_name)) {
					codelist.alias_name = alias_name;
				} else {
					codelist.alias_name = codelist.alias_name + DELIMITER + alias_name;
				}
				if (codelist.alias_context.equals("SDTM"))
					codelist.code = alias_name;
				this.map_odm_codelist.put(key, codelist);
			}
		}
	}
	
	public void removeCodelist(String codelist_id) {
		Set<OdmCodelistPk> keys = this.map_odm_codelist.keySet();
		Iterator<OdmCodelistPk> iterator = keys.iterator();
		while (iterator.hasNext()) {
			OdmCodelistPk key = iterator.next();
			if (StringUtils.equals(codelist_id, key.codelist)) {
				iterator.remove();
			}
		}
	}
	
	public void put(OdmMethodPk key, OdmMethodModel method) {
		this.map_odm_method.put(key, method);
	}
	
	public OdmMethodModel get(OdmMethodPk key) {
		return this.map_odm_method.get(key);
	}
	
	public void put(OdmConditionPk key, OdmConditionModel condition) {
		this.map_odm_condition.put(key, condition);
	}
	
	public OdmConditionModel get(OdmConditionPk key) {
		return this.map_odm_condition.get(key);
	}
	
	public void put(EdcKeysModel edc_keys) {
		this.edc_keys = edc_keys;
	}
	
	public EdcKeysModel getEdcKeys() {
		return this.edc_keys;
	}
	
	public void updateEdcKeys(String delimiter) {
		Set<String> common_vars_set = new HashSet<>();
		String[] edc_keys_array = StringUtils.split(this.edc_keys.common_vars, delimiter);
		if (edc_keys_array == null) {
			return;
		}
		for (int i = 0; i < edc_keys_array.length; i++) {
			common_vars_set.add(edc_keys_array[i]);
		}
		if (StringUtils.equals(this.odm_study.source_system, "Medidata Rave")) {
			if (common_vars_set.contains("Subject")) {
				this.edc_keys.subject_id = "Subject";
			}
			if (common_vars_set.contains("Folder")) {
				this.edc_keys.visit_id = "Folder";
			}
			if (common_vars_set.contains("InstanceRepeatNumber")) {
				this.edc_keys.visit_repeat_key = "InstanceRepeatNumber";
			}
			this.edc_keys.form_id = "<filename>";
			if (common_vars_set.contains("PageRepeatNumber")) {
				this.edc_keys.form_repeat_key = "PageRepeatNumber";
			}
			this.edc_keys.itemgroup_id = "<filename>";
			if (common_vars_set.contains("RecordPosition")) {
				this.edc_keys.itemgroup_repeat_key = "RecordPosition";
			}
		}
	}
}
