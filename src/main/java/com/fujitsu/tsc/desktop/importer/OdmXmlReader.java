/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.fujitsu.tsc.desktop.importer.models.OdmCodelistModel;
import com.fujitsu.tsc.desktop.importer.models.OdmConditionModel;
import com.fujitsu.tsc.desktop.importer.models.OdmEventFormModel;
import com.fujitsu.tsc.desktop.importer.models.OdmEventModel;
import com.fujitsu.tsc.desktop.importer.models.OdmFieldModel;
import com.fujitsu.tsc.desktop.importer.models.OdmFormModel;
import com.fujitsu.tsc.desktop.importer.models.OdmMethodModel;
import com.fujitsu.tsc.desktop.importer.models.OdmModel;
import com.fujitsu.tsc.desktop.importer.models.OdmStudyModel;
import com.fujitsu.tsc.desktop.importer.models.OdmUnitModel;
import com.fujitsu.tsc.desktop.importer.models.OdmCodelistModel.OdmCodelistPk;
import com.fujitsu.tsc.desktop.importer.models.OdmConditionModel.OdmConditionPk;
import com.fujitsu.tsc.desktop.importer.models.OdmEventFormModel.OdmEventFormPk;
import com.fujitsu.tsc.desktop.importer.models.OdmEventModel.OdmEventPk;
import com.fujitsu.tsc.desktop.importer.models.OdmFieldModel.OdmFieldPk;
import com.fujitsu.tsc.desktop.importer.models.OdmFormModel.OdmFormPk;
import com.fujitsu.tsc.desktop.importer.models.OdmMethodModel.OdmMethodPk;
import com.fujitsu.tsc.desktop.importer.models.OdmUnitModel.OdmUnitPk;
import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.ErrorInfo;
import com.fujitsu.tsc.desktop.util.TscStack;

/**
 * This class binds an ODM xml file to the {@link OdmModel} object.
 */
public class OdmXmlReader extends DefaultHandler {
	
	private static Logger logger;
	private static String MESSAGE_ORPHANED = "An orphaned element found.";
	private Config config;
	private Locator locator;
	private List<ErrorInfo> errors = new ArrayList<>();
	private TscStack path_stack = new TscStack();
	private OdmModel odm = new OdmModel();
	private String study_oid = "";
	private String metadata_version_oid = "";
	
	/*
	 *  Variables in this section are used to tell where in the ODM xml file the SAX parser is currently parsing.
	 */
	private String cached_study_oid = "";
	private String cached_metadata_version_oid = "";
	private String cached_unit_id = "";
	private String cached_xml_lang = "";
	private int cached_unit_ordinal = 1;
	private Map<String, OdmUnitModel> cached_units = new HashMap<>();
	private String cached_event_id = "";
	private int cached_event_ordinal = 1;
	private Map<String, OdmEventModel> cached_events = new HashMap<>();
	private String cached_form_id = "";
	private int cached_eventform_ordinal = 1;
	private Map<String, OdmEventFormModel> cached_eventforms = new HashMap<>();
	private int cached_form_ordinal = 1;
	private String cached_itemgroup_id = "";
	private int cached_itemgroup_ordinal = 1;
	private String cached_item_id = "";
	private int cached_item_ordinal = 1;
	private String cached_codelist_id = "";
	private String cached_user_code = "";
	private int cached_codelist_ordinal = 1;
	private Map<String, OdmCodelistModel> cached_codelists = new HashMap<>();
	private String cached_method_id = "";
	private int cached_method_ordinal = 1;
	private String cached_condition_id = "";
	private int cached_condition_ordinal = 1;
 
	/**
	 * Constructor
	 * @param config An OdmModel object to which the ODM xml file is bound. Cannot be null.
	 * @param study_oid When multiple studies are included in the ODM xml file, only metadata of this study will be read. Cannot be null.
	 * @param metadata_version_oid When multiple metadata versions are included in the ODM xml file, only metadata of this version will be read. Cannot be null.
	 */
	public OdmXmlReader(Config config, String study_oid, String metadata_version_oid){
		logger = Logger.getLogger("com.fujitsu.tsc.desktop");
		this.config = config;
		this.study_oid = study_oid;
		this.metadata_version_oid = metadata_version_oid;
	}

	public void startDocument() {
	}
	
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) {
		path_stack.push(qName);
//		logger.info("Entering into " + path_stack.getPath());
		bindAttributes(path_stack.getPath(), attr);
	}

	private void bindAttributes(String path, Attributes attr) {
		/* *** OdmStudyModel *** */
		if ("/ODM/".equals(path)) {
			OdmStudyModel study = new OdmStudyModel();
			study.file_oid = attr.getValue("FileOID");
			study.odm_version = attr.getValue("ODMVersion");
			study.file_type = attr.getValue("FileType");
			study.as_of_date_time = attr.getValue("AsOfDateTime");
			study.originator = attr.getValue("Originator");
			study.source_system = attr.getValue("ddedcp:SourceDataFrom");
			if (StringUtils.isEmpty(study.source_system)) {
				study.source_system = attr.getValue("SourceSystem");
			}
			odm.put(study);
		} else if ("/ODM/Study/".equals(path)) {
			this.cached_study_oid = attr.getValue("OID");
			if (StringUtils.isEmpty(this.study_oid)) {
				this.study_oid = this.cached_study_oid;
			}
			if (this.study_oid.equals(this.cached_study_oid)) {
				OdmStudyModel study = odm.getStudy();
				if (study != null) {
					study.study_oid = attr.getValue("OID");
					odm.put(study);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/".equals(path)) {
			this.cached_metadata_version_oid = attr.getValue("OID");
			if (this.metadata_version_oid != null && this.metadata_version_oid.equals("")) {
				this.metadata_version_oid = this.cached_metadata_version_oid;
			}
			if (this.study_oid.equals(this.cached_study_oid) && this.metadata_version_oid.equals(this.cached_metadata_version_oid)) {
				OdmStudyModel study = odm.getStudy();
				if (study != null) {
					study.metadata_oid = attr.getValue("OID");
					study.metadata_name = attr.getValue("Name");
					study.metadata_description = attr.getValue("Description");
					odm.put(study);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/Protocol/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmStudyModel study = odm.getStudy();
				if (study != null) {
					study.protocol_description_lang = attr.getValue("xml:lang");
					odm.put(study);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		}
		/* *** OdmUnitModel *** */
		else if ("/ODM/Study/BasicDefinitions/MeasurementUnit/".equals(path)) {
			if (this.study_oid.equals(this.cached_study_oid)) {
				this.cached_unit_id = attr.getValue("OID");
				OdmUnitPk key = new OdmUnitPk(this.cached_unit_id, "");
				OdmUnitModel unit = new OdmUnitModel(key);
				unit.unit_name = attr.getValue("Name");
				unit.ordinal = this.cached_unit_ordinal++;
				this.cached_units.put(this.cached_unit_id, unit);
			}
		} else if ("/ODM/Study/BasicDefinitions/MeasurementUnit/Symbol/TranslatedText/".equals(path)) {
			if (this.study_oid.equals(this.cached_study_oid)) {
				OdmUnitModel cached_unit = this.cached_units.get(this.cached_unit_id);
				this.cached_xml_lang = (StringUtils.isEmpty(attr.getValue("xml:lang")) ? "" : attr.getValue("xml:lang"));
				OdmUnitPk key = new OdmUnitPk(this.cached_unit_id, this.cached_xml_lang);
				OdmUnitModel unit = new OdmUnitModel(key);
				unit.copy(cached_unit);
				odm.put(key, unit);
			}
		}
		/* *** OdmEventModel *** */
		else if("/ODM/Study/MetaDataVersion/Protocol/StudyEventRef/".equals(path)) {
			if (isTarget()) {
				OdmEventPk key = new OdmEventPk(attr.getValue("StudyEventOID"));
				OdmEventModel cached_event = new OdmEventModel(key);
				try {
					cached_event.ordinal = Integer.parseInt(attr.getValue("OrderNumber"));
				} catch (Exception ex) {
					cached_event.ordinal = this.cached_event_ordinal++;
				}
				cached_event.mandatory = attr.getValue("Mandatory");
				cached_event.collection_exception_cnd = attr.getValue("CollectionExceptionConditionOID");
				this.cached_events.put(cached_event.event_id, cached_event);
			}
		}
		/* *** OdmEventModel, OdmEventFormModel *** */
		else if ("/ODM/Study/MetaDataVersion/StudyEventDef/".equals(path)) {
			if (isTarget()) {
				this.cached_event_id = attr.getValue("OID");
				OdmEventModel event = this.cached_events.get(this.cached_event_id);
				event.event_name = attr.getValue("Name");
				event.repeating = attr.getValue("Repeating");
				event.event_type = attr.getValue("Type");
				event.category = attr.getValue("Category");
				odm.put(new OdmEventPk(event.event_id), event);
				
				OdmEventFormPk key = new OdmEventFormPk(this.cached_event_id, "");
				OdmEventFormModel cached_eventform = new OdmEventFormModel(key);
				cached_eventform.event_name = attr.getValue("Name");
				this.cached_eventforms.put(cached_eventform.event_id, cached_eventform);
			}
		} else if ("/ODM/Study/MetaDataVersion/StudyEventDef/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmEventPk key = new OdmEventPk(this.cached_event_id);
				OdmEventModel event = odm.get(key);
				if (event != null) {
					event.xml_lang = attr.getValue("xml:lang");
					odm.put(key, event);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/StudyEventDef/Alias/".equals(path)) {
			if (isTarget()) {
				OdmEventPk key = new OdmEventPk(this.cached_event_id);
				OdmEventModel event = odm.get(key);
				if (event != null) {
					event.alias_context = concat(event.alias_context, attr.getValue("Context"));
					event.alias_name = concat(event.alias_name, attr.getValue("Name"));
					odm.put(key, event);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/StudyEventDef/FormRef/".equals(path)) {
			if (isTarget()) {
				OdmEventFormModel cached_eventform = this.cached_eventforms.get(this.cached_event_id);
				OdmEventFormPk key = new OdmEventFormPk(this.cached_event_id, attr.getValue("FormOID"));
				OdmEventFormModel eventform = new OdmEventFormModel(key);
				eventform.copy(cached_eventform);
				try {
					eventform.ordinal = Integer.parseInt(attr.getValue("OrderNumber"));
				} catch (Exception ex) {
					eventform.ordinal = this.cached_eventform_ordinal++;
				}
				eventform.mandatory = attr.getValue("Mandatory");
				eventform.collection_exception_cnd = attr.getValue("CollectionExceptionConditionOID");
				odm.put(key, eventform);
			}
		}
		// *** OdmEventFormModel, OdmFormModel
		else if ("/ODM/Study/MetaDataVersion/FormDef/".equals(path)) {
			if (isTarget()) {
				this.cached_form_id = attr.getValue("OID");
				OdmFormPk key = new OdmFormPk(this.cached_form_id);
				OdmFormModel form = new OdmFormModel(key);
				form.name = attr.getValue("Name");
				form.ordinal = this.cached_form_ordinal++;
				form.repeating = attr.getValue("Repeating");
				odm.put(new OdmFormPk(form.form_id), form);
				/* Update EventForm with form name */
				odm.updateEventForm(form.form_id, form.name);
			}
		} else if ("/ODM/Study/MetaDataVersion/FormDef/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmFormPk key = new OdmFormPk(this.cached_form_id);
				OdmFormModel form = odm.get(key);
				if (form != null) {
					form.xml_lang = attr.getValue("xml:lang");
					odm.put(key, form);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/FormDef/ArchiveLayout/".equals(path)) {
			if (isTarget()) {
				OdmFormPk key = new OdmFormPk(this.cached_form_id);
				OdmFormModel form = odm.get(key);
				if (form != null) {
					form.pdf_file = attr.getValue("PdfFileName");
					odm.put(key, form);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/FormDef/Alias/".equals(path)) {
			if (isTarget()) {
				OdmFormPk key = new OdmFormPk(this.cached_form_id);
				OdmFormModel form = odm.get(key);
				if (form != null) {
					form.alias_context = concat(form.alias_context, attr.getValue("Context"));
					form.alias_name = concat(form.alias_name, attr.getValue("Name"));
					odm.put(key, form);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		}
		// *** OdmFieldModel (ItemGroup, Item)
		else if ("/ODM/Study/MetaDataVersion/FormDef/ItemGroupRef/".equals(path)) {
			if (isTarget()) {
				OdmFieldPk key = new OdmFieldPk(this.cached_form_id, attr.getValue("ItemGroupOID"), attr.getValue("ItemGroupOID"));
				OdmFieldModel itemgroup = new OdmFieldModel(key);
				try {
					itemgroup.ordinal = Integer.parseInt(attr.getValue("OrderNumber"));
				} catch (Exception ex) {
					itemgroup.ordinal = this.cached_itemgroup_ordinal++;
				}
				itemgroup.level = 1;
				itemgroup.mandatory = attr.getValue("Mandatory");
				itemgroup.condition_id = attr.getValue("CollectionExceptionConditionOID");
				odm.put(key, itemgroup);
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemGroupDef/".equals(path)) {
			if (isTarget()) {
				this.cached_itemgroup_id = attr.getValue("OID");
				List<OdmFieldModel> itemgroups = odm.getItemGroupById(this.cached_itemgroup_id);
				if (itemgroups != null) {
					this.cached_item_ordinal = 1; //reset
					for (OdmFieldModel itemgroup : itemgroups) {
						itemgroup.name = attr.getValue("Name");
						itemgroup.repeating = attr.getValue("Repeating");
						itemgroup.is_reference_data = attr.getValue("IsReferenceData");
						itemgroup.sas_name = attr.getValue("SASDatasetName");
						odm.put(itemgroup.getKey(), itemgroup);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemGroupDef/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> itemgroups = odm.getItemGroupById(this.cached_itemgroup_id);
				if (itemgroups != null) {
					for (OdmFieldModel itemgroup : itemgroups) {
						itemgroup.description_xml_lang = attr.getValue("xml:lang");
						odm.put(itemgroup.getKey(), itemgroup);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemGroupDef/ItemRef/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> itemgroups = odm.getItemGroupById(this.cached_itemgroup_id);
				if (itemgroups != null) {
					for (OdmFieldModel itemgroup : itemgroups) {
						OdmFieldPk key = new OdmFieldPk(itemgroup.form_id, itemgroup.item_group_oid, attr.getValue("ItemOID"));
						OdmFieldModel item = new OdmFieldModel(key);
						try {
							item.ordinal = Integer.parseInt(attr.getValue("OrderNumber"));
						} catch (Exception ex) {
							item.ordinal = this.cached_item_ordinal++;
						}
						item.level = 0;
						item.mandatory = attr.getValue("Mandatory");
						item.key_sequence = attr.getValue("KeySequence");
						item.method_id = attr.getValue("MethodOID");
						item.condition_id = attr.getValue("CollectionExceptionConditionOID");
						odm.put(key, item);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemGroupDef/Alias/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> itemgroups = odm.getItemGroupById(this.cached_itemgroup_id);
				if (itemgroups != null) {
					for (OdmFieldModel itemgroup : itemgroups) {
						itemgroup.alias_context = concat(itemgroup.alias_context, attr.getValue("Context"));
						itemgroup.alias_name = concat(itemgroup.alias_name, attr.getValue("Name"));
						odm.put(itemgroup.getKey(), itemgroup);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/".equals(path)) {
			if (isTarget()) {
				this.cached_item_id = attr.getValue("OID");
				List<OdmFieldModel> items = odm.getItemById(this.cached_item_id);
				if (items != null) {
					for (OdmFieldModel item : items) {
						item.name = attr.getValue("Name");
						item.control_type = attr.getValue("ddedcp:InputFormatTyp");
						item.section_label = attr.getValue("ddedcp:SectionLabel");
						item.data_type = attr.getValue("DataType");
						try {
							if (!StringUtils.isEmpty(attr.getValue("Length")))
								item.length = Integer.parseInt(attr.getValue("Length"));
							if (!StringUtils.isEmpty(attr.getValue("SignificantDigits")))
								item.significant_digits = Integer.parseInt(attr.getValue("SignificantDigits"));
						} catch (Exception ex) {
							OdmImportError error = new OdmImportError(new OdmFieldPk(item.form_id, item.item_group_oid, item.field_id));
							error.putMessage("Error parsing ItemDef Length/SignificantDigits attributes.");
							logger.warn(error.toString());
						}
						item.sas_name = attr.getValue("SASFieldName");
						odm.put(item.getKey(), item);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> items = odm.getItemById(this.cached_item_id);
				if (items != null) {
					for (OdmFieldModel item : items) {
						item.description_xml_lang = attr.getValue("xml:lang");
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/Question/TranslatedText/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> items = odm.getItemById(this.cached_item_id);
				if (items != null) {
					for (OdmFieldModel item : items) {
						item.question_xml_lang = attr.getValue("xml:lang");
						odm.put(item.getKey(), item);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/MeasurementUnitRef/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> items = odm.getItemById(this.cached_item_id);
				if (items != null) {
					for (OdmFieldModel item : items) {
						item.crf_unit = concat(item.crf_unit, attr.getValue("MeasurementUnitOID"));
						odm.put(item.getKey(), item);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/RangeCheck/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> items = odm.getItemById(this.cached_item_id);
				if (items != null) {
					for (OdmFieldModel item : items) {
						item.range_check = attr.getValue("Comparator");
						item.soft_hard = attr.getValue("SoftHard");
						odm.put(item.getKey(), item);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/RangeCheck/FormalExpression/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> items = odm.getItemById(this.cached_item_id);
				if (items != null) {
					for (OdmFieldModel item : items) {
						item.formal_expression_context = attr.getValue("Context");
						odm.put(item.getKey(), item);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/CodeListRef/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> items = odm.getItemById(this.cached_item_id);
				if (items != null) {
					for (OdmFieldModel item : items) {
						item.crf_codelist = attr.getValue("CodeListOID");
						odm.put(item.getKey(), item);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ItemDef/Alias/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> items = odm.getItemById(this.cached_item_id);
				if (items != null) {
					for (OdmFieldModel item : items) {
						item.alias_context = concat(item.alias_context, attr.getValue("Context"));
						item.alias_name = concat(item.alias_name, attr.getValue("Name"));
						odm.put(item.getKey(), item);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		}
		// *** CodeList
		else if ("/ODM/Study/MetaDataVersion/CodeList/".equals(path)) {
			if (isTarget()) {
				this.cached_codelist_id = attr.getValue("OID");
				this.cached_codelist_ordinal = 1; //reset
				OdmCodelistPk key = new OdmCodelistPk(this.cached_codelist_id, "");
				OdmCodelistModel codelist = new OdmCodelistModel(key);
				codelist.codelist_label = attr.getValue("Name");
				codelist.data_type = attr.getValue("DataType");
				codelist.sas_format_name = attr.getValue("SASFormatName");
				this.cached_codelists.put(this.cached_codelist_id, codelist);
			}
		} else if("/ODM/Study/MetaDataVersion/CodeList/Description/TranslatedText".equals(path)) {
			if (isTarget()) {
				OdmCodelistModel codelist = this.cached_codelists.get(this.cached_codelist_id);
				codelist.xml_lang = attr.getValue("xml:lang");
				this.cached_codelists.put(this.cached_codelist_id, codelist);
			}
		} else if("/ODM/Study/MetaDataVersion/CodeList/CodeListItem/".equals(path)) {
			if (isTarget()) {
				OdmCodelistModel cached_codelist = this.cached_codelists.get(this.cached_codelist_id);
				this.cached_user_code = attr.getValue("CodedValue");
				OdmCodelistPk key = new OdmCodelistPk(this.cached_codelist_id, this.cached_user_code);
				OdmCodelistModel codelist = new OdmCodelistModel(key);
				codelist.copy(cached_codelist);
				codelist.submission_value = codelist.user_code;
				try {
					if (!StringUtils.isEmpty(attr.getValue("Rank")))
						codelist.rank = Integer.parseInt(attr.getValue("Rank"));
					if (!StringUtils.isEmpty(attr.getValue("OrderNumber"))) {
						codelist.order_number = Integer.parseInt(attr.getValue("OrderNumber"));
						codelist.ordinal = codelist.order_number; 
					}
				} catch (Exception ex) {
					codelist.ordinal = this.cached_codelist_ordinal++;
					OdmImportError error = new OdmImportError(new OdmCodelistPk(codelist.codelist, codelist.user_code));
					error.putMessage("Error parsing Codelist Rank/OrderNumber attributes.");
					logger.warn(error.toString());
				}
				odm.put(key, codelist);
			}
		} else if("/ODM/Study/MetaDataVersion/CodeList/CodeListItem/Decode/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmCodelistPk key = new OdmCodelistPk(this.cached_codelist_id, this.cached_user_code);
				OdmCodelistModel codelist = odm.get(key);
				if (codelist != null) {
					codelist.xml_lang = attr.getValue("xml:lang");
					odm.put(key, codelist);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/CodeList/CodeListItem/Alias/".equals(path)) {
			if (isTarget()) {
				OdmCodelistPk key = new OdmCodelistPk(this.cached_codelist_id, this.cached_user_code);
				OdmCodelistModel codelist = odm.get(key);
				if (codelist != null) {
					codelist.alias_context = concat(codelist.alias_context, attr.getValue("Context"));
					codelist.alias_name = concat(codelist.alias_name, attr.getValue("Name"));
					if (codelist.alias_context.equals("SDTM"))
						codelist.code = attr.getValue("Name");
					odm.put(key, codelist);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/CodeList/EnumeratedItem/".equals(path)) {
			if (isTarget()) {
				OdmCodelistModel cached_codelist = this.cached_codelists.get(this.cached_codelist_id);
				this.cached_user_code = attr.getValue("CodedValue");
				OdmCodelistPk key = new OdmCodelistPk(this.cached_codelist_id, this.cached_user_code);
				OdmCodelistModel codelist = new OdmCodelistModel(key);
				codelist.copy(cached_codelist);
				try {
					if (!StringUtils.isEmpty(attr.getValue("Rank")))
						codelist.rank = Integer.parseInt(attr.getValue("Rank"));
					if (!StringUtils.isEmpty(attr.getValue("OrderNumber")))
						codelist.order_number = Integer.parseInt(attr.getValue("OrderNumber"));
						codelist.ordinal = codelist.order_number;  
				} catch (Exception ex) {
					codelist.ordinal = this.cached_codelist_ordinal++;
					OdmImportError error = new OdmImportError(new OdmCodelistPk(codelist.codelist, codelist.user_code));
					error.putMessage("Error parsing Codelist Rank/OrderNumber attributes.");
					logger.warn(error.toString());
				}
				odm.put(key, codelist);
			}
		} else if("/ODM/Study/MetaDataVersion/CodeList/EnumeratedItem/Alias/".equals(path)) {
			if (isTarget()) {
				OdmCodelistPk key = new OdmCodelistPk(this.cached_codelist_id, this.cached_user_code);
				OdmCodelistModel codelist = odm.get(key);
				if (codelist != null) {
					codelist.alias_context = concat(codelist.alias_context, attr.getValue("Context"));
					codelist.alias_name = concat(codelist.alias_name, attr.getValue("Name"));
					if (codelist.alias_context.equals("SDTM"))
						codelist.code = attr.getValue("Name");
					odm.put(key, codelist);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/CodeList/Alias/".equals(path)) {
			if (isTarget()) {
				odm.updateCodelist(this.cached_codelist_id, attr.getValue("Context"), attr.getValue("Name"), config.valueDelimiter);
			}
		}
		// *** Method
		else if ("/ODM/Study/MetaDataVersion/MethodDef/".equals(path)) {
			if (isTarget()) {
				this.cached_method_id = attr.getValue("OID");
				OdmMethodPk key = new OdmMethodPk(this.cached_method_id);
				OdmMethodModel method = new OdmMethodModel(key);
				method.method_name = attr.getValue("Name");
				method.method_type = attr.getValue("Type");
				method.ordinal = this.cached_method_ordinal++;
				odm.put(key, method);
			}
		} else if ("/ODM/Study/MetaDataVersion/MethodDef/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmMethodPk key = new OdmMethodPk(this.cached_method_id);
				OdmMethodModel method = odm.get(key);
				if (method != null) {
					method.xml_lang = attr.getValue("xml:lang");
					odm.put(key, method);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/MethodDef/FormalExpression/".equals(path)) {
			if (isTarget()) {
				OdmMethodPk key = new OdmMethodPk(this.cached_method_id);
				OdmMethodModel method = odm.get(key);
				if (method != null) {
					method.formal_expression_context = attr.getValue("Context");
					odm.put(key, method);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/MethodDef/Alias/".equals(path)) {
			if (isTarget()) {
				OdmMethodPk key = new OdmMethodPk(this.cached_method_id);
				OdmMethodModel method = odm.get(key);
				if (method != null) {
					method.alias_context = concat(method.alias_context, attr.getValue("Context"));
					method.alias_name = concat(method.alias_name, attr.getValue("Name"));
					odm.put(key, method);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		}
		// *** Condition
		else if ("/ODM/Study/MetaDataVersion/ConditionDef/".equals(path)) {
			if (isTarget()) {
				this.cached_condition_id = attr.getValue("OID");
				OdmConditionPk key = new OdmConditionPk(this.cached_condition_id);
				OdmConditionModel condition = new OdmConditionModel(key);
				condition.condition_name = attr.getValue("Name");
				condition.ordinal = this.cached_condition_ordinal++;
				odm.put(key, condition);
			}
		} else if ("/ODM/Study/MetaDataVersion/ConditionDef/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmConditionPk key = new OdmConditionPk(this.cached_condition_id);
				OdmConditionModel condition = odm.get(key);
				if (condition != null) {
					condition.xml_lang = attr.getValue("xml:lang");
					odm.put(key, condition);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ConditionDef/FormalExpression/".equals(path)) {
			if (isTarget()) {
				OdmConditionPk key = new OdmConditionPk(this.cached_condition_id);
				OdmConditionModel condition = odm.get(key);
				if (condition != null) {
					condition.formal_expression_context = attr.getValue("Context");
					odm.put(key, condition);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if ("/ODM/Study/MetaDataVersion/ConditionDef/Alias/".equals(path)) {
			if (isTarget()) {
				OdmConditionPk key = new OdmConditionPk(this.cached_condition_id);
				OdmConditionModel condition = odm.get(key);
				if (condition != null) {
					condition.alias_context = concat(condition.alias_context, attr.getValue("Context"));
					condition.alias_name = concat(condition.alias_name, attr.getValue("Name"));
					odm.put(key, condition);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		}
	}

	public void characters(char[] ch, int start, int length) {
		String text = new String(ch, start, length).trim();
		if(text != null) {
			//text = StringEscapeUtils.unescapeXml(text);
			bindText(path_stack.getPath(), text);
		}
	}

	public void bindText(String path, String text) {
		if("/ODM/Study/GlobalVariables/StudyName/".equals(path)) {
			if (this.study_oid.equals(this.cached_study_oid)) {
				OdmStudyModel study = odm.getStudy();
				if (study != null) {
					study.study_name += text;
					odm.put(study);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/GlobalVariables/StudyDescription/".equals(path)) {
			if (this.study_oid.equals(this.cached_study_oid)) {
				OdmStudyModel study = odm.getStudy();
				if (study != null) {
					study.study_description += text;
					odm.put(study);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/GlobalVariables/ProtocolName/".equals(path)) {
			if (this.study_oid.equals(this.cached_study_oid)) {
				OdmStudyModel study = odm.getStudy();
				if (study != null) {
					study.protocol_name += text;
					odm.put(study);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/Protocol/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmStudyModel study = odm.getStudy();
				if (study != null) {
					study.protocol_description += text;
					odm.put(study);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/BasicDefinitions/MeasurementUnit/Symbol/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmUnitPk key = new OdmUnitPk(this.cached_unit_id, this.cached_xml_lang);
				OdmUnitModel unit = odm.get(key);
				if (unit != null) {
					unit.symbol += text;
					odm.put(key, unit);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/StudyEventDef/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmEventPk key = new OdmEventPk(this.cached_event_id);
				OdmEventModel event = odm.get(key);
				if (event != null) {
					event.description += text;
					odm.put(key, event);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/FormDef/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmFormPk key = new OdmFormPk(this.cached_form_id);
				OdmFormModel form = odm.get(key);
				if (form != null) {
					form.description += text;
					odm.put(key, form);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/ItemGroupDef/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> itemgroups = odm.getItemGroupById(this.cached_itemgroup_id);
				if (itemgroups != null) {
					for (OdmFieldModel itemgroup : itemgroups) {
						OdmFieldPk key = itemgroup.getKey();
						itemgroup.description += text;
						odm.put(key, itemgroup);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/ItemDef/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> items = odm.getItemById(this.cached_item_id);
				if (items != null) {
					for (OdmFieldModel item : items) {
						OdmFieldPk key = item.getKey();
						item.description += text;
						odm.put(key, item);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/ItemDef/Question/TranslatedText/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> items = odm.getItemById(this.cached_item_id);
				if (items != null) {
					for (OdmFieldModel item : items) {
						OdmFieldPk key = item.getKey();
						item.question += text;
						odm.put(key, item);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/ItemDef/RangeCheck/ErrorMessage/TranslatedText/".equals(path)) {
			if (isTarget()) {
				List<OdmFieldModel> items = odm.getItemById(this.cached_item_id);
				if (items != null) {
					for (OdmFieldModel item : items) {
						OdmFieldPk key = item.getKey();
						item.range_check_error += text;
						odm.put(key, item);
					}
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/CodeList/CodeListItem/Decode/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmCodelistPk key = new OdmCodelistPk(this.cached_codelist_id, this.cached_user_code);
				OdmCodelistModel codelist = odm.get(key);
				if (codelist != null) {
					codelist.decode += text;
					odm.put(key, codelist);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/MethodDef/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmMethodPk key = new OdmMethodPk(this.cached_method_id);
				OdmMethodModel method = odm.get(key);
				if (method != null) {
					method.description += text;
					odm.put(key, method);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/MethodDef/FormalExpression/".equals(path)) {
			if (isTarget()) {
				OdmMethodPk key = new OdmMethodPk(this.cached_method_id);
				OdmMethodModel method = odm.get(key);
				if (method != null) {
					method.formal_expression += text;
					odm.put(key, method);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/ConditionDef/Description/TranslatedText/".equals(path)) {
			if (isTarget()) {
				OdmConditionPk key = new OdmConditionPk(this.cached_condition_id);
				OdmConditionModel condition = odm.get(key);
				if (condition != null) {
					condition.description += text;
					odm.put(key, condition);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		} else if("/ODM/Study/MetaDataVersion/ConditionDef/FormalExpression/".equals(path)) {
			if (isTarget()) {
				OdmConditionPk key = new OdmConditionPk(this.cached_condition_id);
				OdmConditionModel condition = odm.get(key);
				if (condition != null) {
					condition.formal_expression += text;
					odm.put(key, condition);
				} else {
					logger.warn(MESSAGE_ORPHANED);
				}
			}
		}
	}

	public void endElement(String namespaceURI, String localName, String qName) {
//		logger.info("Exiting out of " + path_stack.getPath());
		path_stack.pop();
	}

	public void endDocument()  {
//		this.odm.updateField();
	}

	public void warning(SAXParseException exception) throws SAXException {
	}

	public void error(SAXParseException exception) throws SAXException {
		ErrorInfo error = new ErrorInfo();
	    error.setId(exception.getSystemId());
	    error.setLine(exception.getLineNumber());
	    error.setColumn(exception.getColumnNumber());
	    error.setMessage(exception.getMessage());
	    error.setMessage(exception.getMessage() + "\nnear the line number " + locator.getLineNumber() + ".");
	    errors.add(error);
    }

	public void fatalError(SAXParseException exception) throws SAXException{
		ErrorInfo error = new ErrorInfo();
		error.setId(exception.getSystemId());
		error.setLine(exception.getLineNumber());
		error.setColumn(exception.getColumnNumber());
		error.setMessage(exception.getMessage() + "\nnear the line number " + locator.getLineNumber() + ".");
		errors.add(error);
	}
	
	public OdmModel getOdmModel() {
		return odm;
	}

	public List<ErrorInfo> getErrors() {
		return errors;
	}

	/*
	 *  This method checks if each element/text is under the target study and the metadata version.
	 *  This method can be called only from elements below MetadataVersion
	 */
	private boolean isTarget() {
		if (this.study_oid.equals(this.cached_study_oid)
				&& this.metadata_version_oid.equals(this.cached_metadata_version_oid)) {
			return true;
		} else {
			return false;
		}
	}
	
	/* This method can be called only from elements below MetadataVersion */
	private String concat(String current_text, String new_text) {
		if (StringUtils.isEmpty(current_text)) {
			return new_text;
		} else {
			return current_text + config.valueDelimiter + new_text;
		}
	}
}
