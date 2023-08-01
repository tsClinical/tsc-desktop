/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.stream.Stream;

public class Config {

	private static Logger logger = LogManager.getLogger();

	public static String SOFTWARE_NAME = "tsClinical Metadata";
	public static String SOFTWARE_VERSION = "1.1.3 (Open Source Edition)";
	public static String[] DEFINE_VERSIONS = new String[] { "2.0.0", "2.1.n" };
	public static String[] ODM_VERSIONS = new String[] { "1.3.2" };
	public static String[] EDT_TYPE = new String[] {
			"Screening (Denormalized)",
			"KeyCode Table",
			"Exposure (Normalized)",
			"Exposure (Denormalized)",
			"Deviation (Normalized)",
			"Deviation (Denormalized)",
			"Coding (MedDRA)",
			"Coding (WHO DD)",
			"Coding (Other)",
			"Test Results (LAB Normalized)",
			"Test Results (LAB Denormalized)",
			"Test Results (ECG Normalized)",
			"Test Results (ECG Denormalized)",
			"Test Results (Other Normalized)",
			"Test Results (Other Denormalized)",
			"Other"
	};
	public static String[] ENCODING = new String[] { "UTF-8", "ISO-8859-1", "Shift_JIS" };
	public static String DEFAULT_ENCODING = "UTF-8";
	public static String[] DELIMITED_FIXED = new String[] { "Delimited" };
	public static String[] TEXT_QUALIFIER = new String[] { "\"", "'", "(None)" };

	/* Parameters in general and of Generate Define.xml */
	public String e2dDefineVersion = "";
	public DatasetType e2dDatasetType = DatasetType.SDTM;
	public boolean e2dIncludeResultMetadata = false;
	public String e2dXmlEncoding = "UTF-8";
	public String e2dStylesheetLocation = "";
	public String e2dDataSourceLocation = "";
	public String e2dOutputLocation = "";
	public String defineStudyTableName = "STUDY";
	public String defineMethodTableName = "METHOD";
	public String defineCommentTableName = "COMMENT";
	public String defineStandardTableName = "STANDARD";
	public String defineDocumentTableName = "DOCUMENT";
	public String defineDatasetTableName = "DATASET";
	public String defineVariableTableName = "VARIABLE";
	public String defineValueTableName = "VALUE";
	public String defineResult1TableName = "RESULT1";
	public String defineResult2TableName = "RESULT2";
	public String defineDictionaryTableName = "DICTIONARY";
	public String defineCodelistTableName = "CODELIST";
	public String valueDelimiter = ";";
	public String defineOdmVersion = "1.3.2";
	public String defineFileType = "Snapshot";
	public static final String PROPERTIES_PATH = "./properties/main.properties";
	public DataSourceType dataSourceType = DataSourceType.EXCEL;
	public RunMode runMode;
	public OidMode oidMode = OidMode.EXACT;

	/* Parameters of Import Define.xml */
	public String d2eDefineVersion = "2.0.0";
	public String d2eDatasetType = "SDTM";
	public boolean d2eSeparateSheet = false;
	public boolean d2eMergeNSVtoParent = true;
	public String schema1SourceLocation = "./schema/hard/cdisc-define-2.0/define2-0-0.xsd";
	public String schema2SourceLocation = "./schema/soft/cdisc-define-2.0/define2-0-0.xsd";
	public String d2eDataSourceLocation;
	public String d2eOutputLocation;
	
	/* Parameters of Convert from Define-XML to HTML */
	public String x2hXmlLocation;
	public String x2hXslLocation;
	public String x2hOutputLocation;

	/* Parameters of Export ODM-XML */
	public String e2oOdmVersion = "1.3.2";
	public String e2oXmlEncoding = "UTF-8";
	public String e2oStylesheetLocation;
	public String e2oDataSourceLocation;
	public String e2oOutputLocation;
	public String odmStudyTableName = "STUDY";
	public String odmUnitTableName = "UNIT";
	public String odmEventTableName = "EVENT";
	public String odmEventFormTableName = "EVENTxFORM";
	public String odmFormTableName = "FORM";
	public String odmFieldTableName = "FIELD";
	public String odmCodelistTableName = "CODELIST";
	public String odmMethodTableName = "METHOD";
	public String odmConditionTableName = "CONDITION";
	
	/* Parameters of Import ODM-XML */
	public String o2eOdmVersion = "1.3.2";
	public String o2eOdmLocation;
	public String o2eOutputLocation;

	/* Parameters of Create CRF Spec from Datasets */
    public String crfArchitectLocation;
    public String[] crfSourceFiles;
    public String crfHeaderCnt;
    public String crfHeaderRow;
    public String crfEncoding = "UTF-8";
    public String crfDelimiter;
    public String crfTextQualifier = "\"";
    public String crfOutputLocation;

	/* Parameters of CreateEdtSpec */
    public String edtType = "Test Results (LAB Normalized)";
    public String edtHeaderCnt;
    public String edtHeaderRow;
    public String edtEncoding = "UTF-8";
    public String edtDelimitedOrFixed = "Delimited";
    public String edtDelimiter;
    public String edtTextQualifier = "\"";
    public String edtDataSourceLocation;
    public String edtOutputLocation;
    public String edtGeneralTableName = "GENERAL";
    public String edtColumnTableName = "COLUMN";
    public String edtCodelistTableName = "CODELIST";
	
	/* Parameters of Validate against XML Schema */
	public String validateXmlLocation;
	public String validateSchemaLocation;

	public static enum DatasetType {
		SDTM, ADaM, SEND;
		
		public static String[] stringValues() {
			return Stream.of(DatasetType.values()).map(DatasetType::name).toArray(String[]::new);
		}
	}

	public enum DataSourceType {
		EXCEL
	}

	public enum RunMode {
		CLI, GUI, API
	}

	public enum OidMode {
		NATIVE, EXACT
	}
	
	public Config() {
	}

	public Config(Properties prop) {
		try {
			initConfig(prop);
		} catch (IllegalArgumentException ex) {
			logger.error(ex.getMessage());
			logger.error("Exiting the program...");
			System.exit(-1);
		}
	}

	private void initConfig(Properties prop) throws IllegalArgumentException {
		
		// Generate Define.xml
		if (!prop.getProperty("e2dDefineVersion", "").equals(""))
			e2dDefineVersion = prop.getProperty("e2dDefineVersion");
		if (!prop.getProperty("e2dDatasetType", "").equals(""))
			e2dDatasetType = DatasetType.valueOf(prop.getProperty("e2dDatasetType"));
		if (!prop.getProperty("e2dXmlEncoding", "").equals(""))
			e2dXmlEncoding = prop.getProperty("e2dXmlEncoding");
		if (!prop.getProperty("e2dStylesheetLocation", "").equals(""))
			e2dStylesheetLocation = prop.getProperty("e2dStylesheetLocation");
		if (!prop.getProperty("e2dDataSourceLocation", "").equals(""))
			e2dDataSourceLocation = prop.getProperty("e2dDataSourceLocation");
		if (!prop.getProperty("e2dOutputLocation", "").equals(""))
			e2dOutputLocation = prop.getProperty("e2dOutputLocation");
		if (prop.getProperty("e2dIncludeResultMetadata", "").toUpperCase().equals("TRUE"))
			e2dIncludeResultMetadata = true;
		if (!prop.getProperty("defineStudyTableName", "").equals(""))
			defineStudyTableName = prop.getProperty("defineStudyTableName");
		if (!prop.getProperty("defineMethodTableName", "").equals(""))
			defineMethodTableName = prop.getProperty("defineMethodTableName");
		if (!prop.getProperty("defineCommentTableName", "").equals(""))
			defineCommentTableName = prop.getProperty("defineCommentTableName");
		if (!prop.getProperty("defineStandardTableName", "").equals(""))
			defineStandardTableName = prop.getProperty("defineStandardTableName");
		if (!prop.getProperty("defineDocumentTableName", "").equals(""))
			defineDocumentTableName = prop.getProperty("defineDocumentTableName");
		if (!prop.getProperty("defineDatasetTableName", "").equals(""))
			defineDatasetTableName = prop.getProperty("defineDatasetTableName");
		if (!prop.getProperty("defineVariableTableName", "").equals(""))
			defineVariableTableName = prop.getProperty("defineVariableTableName");
		if (!prop.getProperty("defineValueTableName", "").equals(""))
			defineValueTableName = prop.getProperty("defineValueTableName");
		if (!prop.getProperty("defineResult1TableName", "").equals(""))
			defineResult1TableName = prop.getProperty("defineResult1TableName");
		if (!prop.getProperty("defineResult2TableName", "").equals(""))
			defineResult2TableName = prop.getProperty("defineResult2TableName");
		if (!prop.getProperty("defineDictionaryTableName", "").equals(""))
			defineDictionaryTableName = prop.getProperty("defineDictionaryTableName");
		if (!prop.getProperty("defineCodelistTableName", "").equals(""))
			defineCodelistTableName = prop.getProperty("defineCodelistTableName");
		if (!prop.getProperty("valueDelimiter", "").equals(""))
			valueDelimiter = prop.getProperty("valueDelimiter");

		//Import Define-XML
		if (!prop.getProperty("d2eDefineVersion", "").equals(""))
			d2eDefineVersion = prop.getProperty("d2eDefineVersion");
		if (!prop.getProperty("d2eDatasetType", "").equals(""))
			d2eDatasetType = prop.getProperty("d2eDatasetType");
		if (prop.getProperty("d2eSeparateSheet", "").toUpperCase().equals("TRUE")) {
			d2eSeparateSheet = true;
		} else {
			d2eSeparateSheet = false;
		}
		if (prop.getProperty("d2eMergeNSVtoParent", "").toUpperCase().equals("TRUE")) {
			d2eMergeNSVtoParent = true;
		} else {
			d2eMergeNSVtoParent = false;
		}
		if (!prop.getProperty("d2eDataSourceLocation", "").equals(""))
			d2eDataSourceLocation = prop.getProperty("d2eDataSourceLocation");
		if (!prop.getProperty("d2eOutputLocation", "").equals(""))
			d2eOutputLocation = prop.getProperty("d2eOutputLocation");

		//Convert from XML to HTML
		if (!prop.getProperty("x2hXmlLocation", "").equals(""))
			x2hXmlLocation = prop.getProperty("x2hXmlLocation");
		if (!prop.getProperty("x2hXslLocation", "").equals(""))
			x2hXslLocation = prop.getProperty("x2hXslLocation");
		if (!prop.getProperty("x2hOutputLocation", "").equals(""))
			x2hOutputLocation = prop.getProperty("x2hOutputLocation");

		// Export ODM-XML
		if (!prop.getProperty("e2oOdmVersion", "").equals(""))
			e2oOdmVersion = prop.getProperty("e2oOdmVersion");
		if (!prop.getProperty("e2oXmlEncoding", "").equals(""))
			e2oXmlEncoding = prop.getProperty("e2oXmlEncoding");
		if (!prop.getProperty("e2oStylesheetLocation", "").equals(""))
			e2oStylesheetLocation = prop.getProperty("e2oStylesheetLocation");
		if (!prop.getProperty("e2oDataSourceLocation", "").equals(""))
			e2oDataSourceLocation = prop.getProperty("e2oDataSourceLocation");
		if (!prop.getProperty("e2oOutputLocation", "").equals(""))
			e2oOutputLocation = prop.getProperty("e2oOutputLocation");
		if (!prop.getProperty("odmStudyTableName", "").equals(""))
			odmStudyTableName = prop.getProperty("odmStudyTableName");
		if (!prop.getProperty("odmUnitTableName", "").equals(""))
			odmUnitTableName = prop.getProperty("odmUnitTableName");
		if (!prop.getProperty("odmEventTableName", "").equals(""))
			odmEventTableName = prop.getProperty("odmEventTableName");
		if (!prop.getProperty("odmEventFormTableName", "").equals(""))
			odmEventFormTableName = prop.getProperty("odmEventFormTableName");
		if (!prop.getProperty("odmFormTableName", "").equals(""))
			odmFormTableName = prop.getProperty("odmFormTableName");
		if (!prop.getProperty("odmFieldTableName", "").equals(""))
			odmFieldTableName = prop.getProperty("odmFieldTableName");
		if (!prop.getProperty("odmCodelistTableName", "").equals(""))
			odmCodelistTableName = prop.getProperty("odmCodelistTableName");
		if (!prop.getProperty("odmMethodTableName", "").equals(""))
			odmMethodTableName = prop.getProperty("odmMethodTableName");
		if (!prop.getProperty("oidMode", "").equals(""))
			oidMode = OidMode.valueOf(prop.getProperty("oidMode"));
		
		// Import ODM-XML
		if (!prop.getProperty("o2eOdmVersion", "").equals(""))
			o2eOdmVersion = prop.getProperty("o2eOdmVersion");
		if (!prop.getProperty("o2eOdmLocation", "").equals(""))
			o2eOdmLocation = prop.getProperty("o2eOdmLocation");
		if (!prop.getProperty("o2eOutputLocation", "").equals(""))
			o2eOutputLocation = prop.getProperty("o2eOutputLocation");

		// Create CRF Spec from Datasets
		if (!prop.getProperty("crfArchitectLocation", "").equals(""))
			crfArchitectLocation = prop.getProperty("crfArchitectLocation");
		if (!prop.getProperty("crfSourceFiles", "").equals("")) {
			String tmpCrfSourceFiles = prop.getProperty("crfSourceFiles");
			//crfSourceFiles = tmpCrfSourceFiles.split("[|]");
			crfSourceFiles = tmpCrfSourceFiles.split("\\|");
		}
		if (!prop.getProperty("crfHeaderCnt", "").equals(""))
			crfHeaderCnt = prop.getProperty("crfHeaderCnt");
		if (!prop.getProperty("crfHeaderRow", "").equals(""))
			crfHeaderRow = prop.getProperty("crfHeaderRow");
		if (!prop.getProperty("crfEncoding", "").equals(""))
			crfEncoding = prop.getProperty("crfEncoding");
		if (!prop.getProperty("crfDelimiter", "").equals(""))
			crfDelimiter = prop.getProperty("crfDelimiter");
		if (!prop.getProperty("crfTextQualifier", "").equals(""))
			crfTextQualifier = prop.getProperty("crfTextQualifier");
		if (!prop.getProperty("crfOutputLocation", "").equals(""))
			crfOutputLocation = prop.getProperty("crfOutputLocation");

		// Create eDT Spec
		if (!prop.getProperty("edtType", "").equals(""))
			edtType = prop.getProperty("edtType");
		if (!prop.getProperty("edtHeaderCnt", "").equals(""))
			edtHeaderCnt = prop.getProperty("edtHeaderCnt");
		if (!prop.getProperty("edtHeaderRow", "").equals(""))
			edtHeaderRow = prop.getProperty("edtHeaderRow");
		if (!prop.getProperty("edtEncoding", "").equals(""))
			edtEncoding = prop.getProperty("edtEncoding");
		if (!prop.getProperty("edtDelimitedOrFixed", "").equals(""))
			edtDelimitedOrFixed = prop.getProperty("edtDelimitedOrFixed");
		if (!prop.getProperty("edtDelimiter", "").equals(""))
			edtDelimiter = prop.getProperty("edtDelimiter");
		if (!prop.getProperty("edtTextQualifier", "").equals(""))
			edtTextQualifier = prop.getProperty("edtTextQualifier");
		if (!prop.getProperty("edtDataSourceLocation", "").equals(""))
			edtDataSourceLocation = prop.getProperty("edtDataSourceLocation");
		if (!prop.getProperty("edtOutputLocation", "").equals(""))
			edtOutputLocation = prop.getProperty("edtOutputLocation");
		if (!prop.getProperty("edtGeneralTableName", "").equals(""))
			edtGeneralTableName = prop.getProperty("edtGeneralTableName");
		if (!prop.getProperty("edtColumnTableName", "").equals(""))
			edtColumnTableName = prop.getProperty("edtColumnTableName");
		if (!prop.getProperty("edtCodelistTableName", "").equals(""))
			edtCodelistTableName = prop.getProperty("edtCodelistTableName");
		
		// Validate against XML Schema
		if (!prop.getProperty("validateXmlLocation", "").equals(""))
			validateXmlLocation = prop.getProperty("validateXmlLocation");
		if (!prop.getProperty("validateSchemaLocation", "").equals(""))
			validateSchemaLocation = prop.getProperty("validateSchemaLocation");
	}
}
