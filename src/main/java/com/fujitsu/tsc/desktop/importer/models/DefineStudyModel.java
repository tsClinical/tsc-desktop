/* 
 * Copyright (c) 2022 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.fujitsu.tsc.desktop.importer.ExcelWriter2.ExcelColumn;
import com.fujitsu.tsc.desktop.util.Utils;

/**
 * Java object representing the STUDY data of {@link DefineModel}.
 * [Primary Keys]
 * 	- file_oid
 * [Required]
 * 	- file_type
 *  - file_oid
 *  - context
 *  - study_oid
 *  - study_name
 *  - study_description
 *  - protocol_name
 *  - metadata_oid
 *  - metadata_name
 *  - metadata_description
 */
public class DefineStudyModel {
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
	/** Context */
	@ExcelColumn( name = "Context" , ordinal = 6)
	public String context = "Submission";
	/** StudyOID */
	@ExcelColumn( name = "StudyOID" , ordinal = 7)
	public String study_oid;
	/** StudyName */
	@ExcelColumn( name = "StudyName" , ordinal = 8)
	public String study_name;
	/** StudyDescription */
	@ExcelColumn( name = "StudyDescription" , ordinal = 9)
	public String study_description;
	/** ProtocolName */
	@ExcelColumn( name = "ProtocolName" , ordinal = 10)
	public String protocol_name;
	/** MetaDataOID */
	@ExcelColumn( name = "MetaDataOID" , ordinal = 11)
	public String metadata_oid;
	/** MetaDataName */
	@ExcelColumn( name = "MetaDataName" , ordinal = 12)
	public String metadata_name;
	/** MetaDataDescription */
	@ExcelColumn( name = "MetaDataDescription" , ordinal = 13)
	public String metadata_description;
	/** DefineVersion */
	@ExcelColumn( name = "DefineVersion" , ordinal = 14)
	public String define_version;
	/** StandardName */
	@ExcelColumn( name = "StandardName" , ordinal = 15)
	public String standard_name;
	/** StandardVersion */
	@ExcelColumn( name = "StandardVersion" , ordinal = 16)
	public String standard_version;
	/** Comment */
	@ExcelColumn( name = "Comment" , ordinal = 17)
	public String comment_oid;
	/** User Note 1 */
	@ExcelColumn( name = "User Note 1" , ordinal = 18)
	public String user_note1;
	/** User Note 2 */
	@ExcelColumn( name = "User Note 2" , ordinal = 19)
	public String user_note2;

	public DefineStudyModel() {
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
		this.metadata_name = "001";	//Default
		this.metadata_description = "";
		this.define_version = "";
		this.standard_name = "";
		this.standard_version = "";
		this.comment_oid = "";
		this.user_note1 = "";
		this.user_note2 = "";
	}
	
	public String toStudyOid() {
		if (StringUtils.isEmpty(this.study_oid)) {
			if (StringUtils.isEmpty(this.originator)) {
				return Utils.codedText(this.study_name);
			} else {
				return Utils.codedText(this.originator + "." + this.study_name);
			}
		} else {
			return this.study_oid;
		}
	}
	/**
	 * @return MetaDataOID if populated, or "MDV." + coded metadata name
	 */
	public String toMetaDataOid() {
		if (StringUtils.isEmpty(this.metadata_oid)) {
			return "MDV." + Utils.codedText(metadata_name);
		} else {
			return this.metadata_oid;
		}
	}
}
