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
 * Java object representing the STUDY data of {@link OdmModel}.
 * [Primary Keys]
 * 	- file_oid
 * [Required]
 * 	- file_type
 *  - file_oid
 */
public class OdmStudyModel {
	/** ODMVersion */
	@ExcelColumn( name = "ODMVersion" , ordinal = 1)
	public String odm_version;
	/** FileType */
	@ExcelColumn( name = "FileType" , ordinal = 2)
	public String file_type;
	/** FileOID */
	@ExcelColumn( name = "FileOID" , ordinal = 3)
	public String file_oid;
	/** AsOfDateTime */
	@ExcelColumn( name = "AsOfDateTime" , ordinal = 4)
	public String as_of_date_time;
	/** Originator */
	@ExcelColumn( name = "Originator" , ordinal = 5)
	public String originator;
	/** StudyOID */
	@ExcelColumn( name = "StudyOID" , ordinal = 6)
	public String study_oid;
	/** StudyName */
	@ExcelColumn( name = "StudyName" , ordinal = 7)
	public String study_name;
	/** StudyDescription */
	@ExcelColumn( name = "StudyDescription" , ordinal = 8)
	public String study_description;
	/** ProtocolName */
	@ExcelColumn( name = "ProtocolName" , ordinal = 9)
	public String protocol_name;
	/** MetaDataOID */
	@ExcelColumn( name = "MetaDataOID" , ordinal = 10)
	public String metadata_oid;
	/** MetaDataName */
	@ExcelColumn( name = "MetaDataName" , ordinal = 11)
	public String metadata_name;
	/** MetaDataDescription */
	@ExcelColumn( name = "MetaDataDescription" , ordinal = 12)
	public String metadata_description;
	/** ProtocolDescription */
	@ExcelColumn( name = "ProtocolDescription" , ordinal = 13)
	public String protocol_description;
	/** ProtocolDescription xml:lang */
	@ExcelColumn( name = "ProtocolDescription xml:lang" , ordinal = 14)
	public String protocol_description_lang;
	/** Source System */
	@ExcelColumn( name = "Source System" , ordinal = 15)
	public String source_system;
	/** Dataset Type */
	@ExcelColumn( name = "Dataset Type" , ordinal = 16)
	public String edc_dataset_type;
	/** # of Header Lines */
	@ExcelColumn( name = "# of Header Lines" , ordinal = 17)
	public int header_line;
	/** Dataset Character Encoding */
	@ExcelColumn( name = "Dataset Character Encoding" , ordinal = 18)
	public String encoding;
	/** Dataset Delimiter */
	@ExcelColumn( name = "Dataset Delimiter" , ordinal = 19)
	public String delimiter;
	/** Dataset Text Qualifier */
	@ExcelColumn( name = "Dataset Text Qualifier" , ordinal = 20)
	public String text_qualifier;
	/** Date Format */
	@ExcelColumn( name = "Date Format" , ordinal = 21)
	public String edc_date_format;
	/** Unknown Date/Time Text */
	@ExcelColumn( name = "Unknown Date/Time Text" , ordinal = 22)
	public String edc_unk_date_time_text;
	/** User Note 1 */
	@ExcelColumn( name = "User Note 1" , ordinal = 23)
	public String user_note1;
	/** User Note 2 */
	@ExcelColumn( name = "User Note 2" , ordinal = 24)
	public String user_note2;

	public OdmStudyModel() {
		this.odm_version = "";
		this.file_type = "";
		this.file_oid = "";
		this.as_of_date_time = "";
		this.originator = "";
		this.study_oid = "";
		this.study_name = "";
		this.study_description = "";
		this.protocol_name = "";
		this.metadata_oid = "";
		this.metadata_name = "";
		this.metadata_description = "";
		this.protocol_description = "";
		this.protocol_description_lang = "";
		this.source_system = "";
		this.edc_dataset_type = "";
		this.header_line = 1;
		this.encoding = "";
		this.delimiter = "";
		this.text_qualifier = "";
		this.edc_date_format = "";
		this.edc_unk_date_time_text = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
}
