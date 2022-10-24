/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.exporter;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.fujitsu.tsc.desktop.exporter.DefineXmlWriter.TagType;
import com.fujitsu.tsc.desktop.exporter.WhereClause.Operator;
import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.InvalidParameterException;
import com.fujitsu.tsc.desktop.util.MetaDataReader;
import com.fujitsu.tsc.desktop.util.Config.OidMode;

public class OdmXmlWriter {

	private static Logger logger;
	private Config config;
	private MetaDataReader reader;
	private OutputStreamWriter sw;
	private BufferedWriter writer;
	private String xmlEncoding;
	private String stylesheetLocation;
	private Config.OidMode oidMode;
	private String sourceSystem = "";
	private int indent;		// Controls text indents in the output odm.xml.
	private final String DELIMITER;
	private final String DEFAULTLANG = "en";
	private Hashtable<String, String> formNameMandatoryHash = new Hashtable<String, String>();	//Used to retain Mandatory value of forms; eventually used to generate default itemgroup.
	private Hashtable<String, String> formNameRepeatingHash = new Hashtable<String, String>();	//Used to retain Repeating value of forms; eventually used to generate default itemgroup.

	public enum OdmTagType {
		DEFAULT, XMLHEADER, STUDY, MEASUREMENTUNIT, MEASUREMENTUNITREF, PROTOCOL, STUDYEVENTDEF, STUDYEVENTREF, FORMDEF, FORMREF,
		ITEMGROUPDEF, ITEMGROUPREF, ITEMDEF, ITEMREF, CODELIST, CODELISTREF, METHODDEF, CONDITIONDEF, ARCHIVELAYOUT
	}

	protected class ErrorHint {
		protected OdmTagType odmTagType = OdmTagType.DEFAULT;
		protected String eventName = "";
		protected String formName = "";
		protected String groupName = "";
		protected String fieldName = "";
		protected String param = "";
		protected ErrorHint() { /* An empty constructor. */ };
		protected void setErrorHint(OdmTagType odmTagType, String eventName, String formName, String groupName, String fieldName, String param) {
			this.odmTagType = odmTagType;
			if (eventName == null) { this.eventName = "null"; } else { this.eventName = eventName; }
			if (formName == null) { this.formName = "null"; } else { this.formName = formName; }
			if (groupName == null) { this.groupName = "null"; } else { this.groupName = groupName; }
			if (fieldName == null) { this.fieldName = "null"; } else { this.fieldName = fieldName; }
			if (param == null) { this.param = "null"; } else { this.param = param; }
		}
	}
	private ErrorHint errHint = new ErrorHint();
	
	public OdmXmlWriter (Config config) throws InvalidParameterException, TableNotFoundException, InvalidParameterException, RequiredValueMissingException, InvalidFormatException, IOException {
		
		logger = Logger.getLogger("com.fujitsu.tsc.desktop");
		this.config = config;
		DELIMITER = config.valueDelimiter;
		
		try {
			if (this.config.dataSourceType.name().equals("EXCEL")) {
				reader = new ExcelReader(this.config.e2oDataSourceLocation, this.config.odmFormTableName);
			} else {
				throw new InvalidParameterException("dataSourceType", this.config.dataSourceType.name());
			}
			
			this.xmlEncoding = this.config.e2oXmlEncoding;
			this.stylesheetLocation = this.config.e2oStylesheetLocation;
			this.oidMode = this.config.oidMode;
			sw = new OutputStreamWriter(new FileOutputStream
						(this.config.e2oOutputLocation, false), xmlEncoding);
			writer = new BufferedWriter(sw);
			indent = 0;
		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}
	
	public void writeXMLHeader() throws IOException, RequiredValueMissingException {
		errHint.setErrorHint(OdmTagType.XMLHEADER, "", "", "", "", "");
		try {
			String str = "<?xml version=\"1.0\" encoding=\""
					+ xmlEncoding
					+ "\"?>";
			writer.write(str);
			writer.newLine();

			if (stylesheetLocation != null && !stylesheetLocation.equals("")) {
				str = "<?xml-stylesheet type=\"text/xsl\" href=\""
						+ stylesheetLocation
						+ "\"?>";
				writer.write(str);
				writer.newLine();
			}
		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	public void writeStudySection() throws TableNotFoundException, IOException, RequiredValueMissingException {
		errHint.setErrorHint(OdmTagType.STUDY, "", "", "", "", "");
		try {
			reader.setTable(config.odmStudyTableName);

			Hashtable<String, String> hash = new Hashtable<String, String>();
			// Transpose the normalized STUDY table and store into a new denormalized Hashtable.
			Hashtable<String, String> hashNormalized;
			while ((hashNormalized = reader.read()) != null) {
				hash.put(hashNormalized.get("Property Name"), hashNormalized.get("Property Value"));
			}
			this.sourceSystem = hash.get("Source System");

			Calendar cal = Calendar.getInstance();
			String str = insertIndent(indent);
			str += "<ODM xmlns=\"http://www.cdisc.org/ns/odm/v1.3\""
					+ " xmlns:xlink=\"http://www.w3.org/1999/xlink\""
					+ (StringUtils.isNotEmpty(this.sourceSystem) ? " xmlns:ddedcp=\"http://www.fujitsu.com/ddedcp/odm\"" : "")
					+ " ODMVersion=\"" + (hash.get("ODMVersion").equals("") ? config.e2oOdmVersion : hash.get("ODMVersion"))
					+ "\" FileOID=\"" + (hash.get("FileOID").equals("") ? UUID.randomUUID().toString() : hash.get("FileOID"))
					+ "\" FileType=\"" + (hash.get("FileType").equals("") ? config.defineFileType : hash.get("FileType"))
					+ "\" CreationDateTime=\""
						+ cal.get(Calendar.YEAR) + "-"
						+ twoDigits((cal.get(Calendar.MONTH) + 1)) + "-"
						+ twoDigits(cal.get(Calendar.DAY_OF_MONTH)) + "T"
						+ twoDigits(cal.get(Calendar.HOUR_OF_DAY)) + ":"
						+ twoDigits(cal.get(Calendar.MINUTE)) + ":"
						+ twoDigits(cal.get(Calendar.SECOND))
					+ (hash.get("AsOfDateTime").equals("") ? "" : "\" AsOfDateTime=\"" + hash.get("AsOfDateTime"))
					+ (hash.get("Originator").equals("") ? "" : "\" Originator=\"" + hash.get("Originator"))
					+ "\" SourceSystem=\"" + Config.SOFTWARE_NAME
					+ "\" SourceSystemVersion=\"" + Config.SOFTWARE_VERSION
					+ (StringUtils.isNotEmpty(this.sourceSystem) ? "\" ddedcp:SourceDataFrom=\"" + this.sourceSystem : "")
					+ "\">";
			writer.write(str);
			writer.newLine();
			indent++;

			str = insertIndent(indent);
			str += "<Study OID=\"" + (hash.get("StudyOID").equals("") ? UUID.randomUUID().toString() : hash.get("StudyOID")) + "\">";
			writer.write(str);
			writer.newLine();
			indent++;

			str = insertIndent(indent);
			str += "<GlobalVariables>";
			writer.write(str);
			writer.newLine();
			indent++;

			str = insertIndent(indent);
			str += "<StudyName>" + hash.get("StudyName") + "</StudyName>";
			writer.write(str);
			writer.newLine();

			str = insertIndent(indent);
			str += "<StudyDescription>" + XmlGenerator.escapeString(hash.get("StudyDescription")) + "</StudyDescription>";
			writer.write(str);
			writer.newLine();

			str = insertIndent(indent);
			str += "<ProtocolName>" + hash.get("ProtocolName") + "</ProtocolName>";
			writer.write(str);
			writer.newLine();

			indent--;
			str = insertIndent(indent);
			str += "</GlobalVariables>";
			writer.write(str);
			writer.newLine();

		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	public void writeUnitSection() throws TableNotFoundException, IOException, RequiredValueMissingException, InvalidOidSyntaxException {
		errHint.setErrorHint(OdmTagType.MEASUREMENTUNIT, "", "", "", "", "");
		try {
			reader.setTable(config.odmUnitTableName);

			Hashtable<String, String> hash = new Hashtable<String, String>();
			boolean isSheetStart = true; // Indicates the line is beginning of the unit sheet
			boolean isUnitStart = true;	// Indicates the line is beginning of a unit
			String prevUnitName = null;	//Used to judge whether a new row belongs to the same unit group as the previous row.
			int unitNumber = 0;
			String str = null;

			while ((hash = reader.read()) != null) {
				if (prevUnitName != null) {
					if (prevUnitName.equals(hash.get("Name"))) {
						isUnitStart = false;
					} else {
						isUnitStart = true;
					}
				} 
				prevUnitName = hash.get("Name");

				errHint.setErrorHint(OdmTagType.MEASUREMENTUNIT, "", "", "", "", hash.get("Name"));
				if (isSheetStart) {
					unitNumber++;
					str = insertIndent(indent);
					str += "<BasicDefinitions>";
					writer.write(str);
					writer.newLine();
					indent++;
					
					str = insertIndent(indent);
					str += "<MeasurementUnit OID=\"" + createOID(OdmTagType.MEASUREMENTUNIT, "", "", "", "", hash.get("ID"))
							+ "\" Name=\"" + hash.get("Name")
							+ "\">";
					writer.write(str);
					writer.newLine();
					indent++;
					
					str = insertIndent(indent);
					str += "<Symbol>";
					writer.write(str);
					writer.newLine();
					indent++;
				}
				
				if (isUnitStart && !isSheetStart) {
					unitNumber++;
					indent--;
					str = insertIndent(indent);
					str += "</Symbol>";
					writer.write(str);
					writer.newLine();
					
					indent--;
					str = insertIndent(indent);
					str += "</MeasurementUnit>";
					writer.write(str);
					writer.newLine();
					
					str = insertIndent(indent);
					str += "<MeasurementUnit OID=\"" + createOID(OdmTagType.MEASUREMENTUNIT, "", "", "", "", hash.get("ID"))
							+ "\" Name=\"" + hash.get("Name")
							+ "\">";
					writer.write(str);
					writer.newLine();
					indent++;
					
					str = insertIndent(indent);
					str += "<Symbol>";
					writer.write(str);
					writer.newLine();
					indent++;
				}
				
				str = insertIndent(indent);
				str += "<TranslatedText"
						+ (StringUtils.isEmpty(hash.get("xml:lang")) ? "" : " xml:lang=\"" + hash.get("xml:lang") + "\"")
						+ ">"
						+ XmlGenerator.escapeString(hash.get("Symbol"))
						+ "</TranslatedText>";
				writer.write(str);
				writer.newLine();
				
				if (isSheetStart) isSheetStart = false;
			}
			
			//Finally, end BasicDefinitions
			if (unitNumber > 0) {
				indent--;
				str = insertIndent(indent);
				str += "</Symbol>";
				writer.write(str);
				writer.newLine();
				
				indent--;
				str = insertIndent(indent);
				str += "</MeasurementUnit>";
				writer.write(str);
				writer.newLine();
				
				indent--;
				str = insertIndent(indent);
				str += "</BasicDefinitions>";
				writer.write(str);
				writer.newLine();
			}
			
		} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	public void writeProtocolSection() throws TableNotFoundException, IOException, RequiredValueMissingException, InvalidOidSyntaxException {
		try {
			errHint.setErrorHint(OdmTagType.PROTOCOL, "", "", "", "", "");
			reader.setTable(config.odmStudyTableName);

			Hashtable<String, String> hash = new Hashtable<String, String>();
			// Transpose the normalized STUDY table and store into a new denormalized Hashtable.
			Hashtable<String, String> hashNormalized;
			while ((hashNormalized = reader.read()) != null) {
				hash.put(hashNormalized.get("Property Name"), hashNormalized.get("Property Value"));
			}
			
			String str = insertIndent(indent);
			str += "<MetaDataVersion OID=\"" + (hash.get("MetaDataOID").equals("") ? UUID.randomUUID().toString() : hash.get("MetaDataOID"))
					+ "\" Name=\"" + hash.get("MetaDataName")
					+ "\" Description=\"" + hash.get("MetaDataDescription")
					+ "\">";
			writer.write(str);
			writer.newLine();
			indent++;
			
			String protocolDescription = XmlGenerator.escapeString(hash.get("ProtocolDescription"));
			String protocolDescriptionLang = hash.get("ProtocolDescription xml:lang");
			
			if (protocolDescription != null && !protocolDescription.equals("")) {
				str = insertIndent(indent);
				str += "<Protocol>";
				writer.write(str);
				writer.newLine();
				indent++;
				
				str = insertIndent(indent);
				str += "<Description>";
				writer.write(str);
				writer.newLine();
				indent++;
				
				str = insertIndent(indent);
				str += "<TranslatedText"
						+ (StringUtils.isEmpty(protocolDescriptionLang) ? "" : " xml:lang=\"" + protocolDescriptionLang + "\"")
						+ ">"
						+ XmlGenerator.escapeString(protocolDescription) + "</TranslatedText>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</Description>";
				writer.write(str);
				writer.newLine();
			}

			reader.setTable(config.odmEventTableName);

			int i = 1;
			while ((hash = reader.read()) != null) {
				errHint.setErrorHint(OdmTagType.STUDYEVENTREF, hash.get("Name"), "", "", "", "");
				
				// If the Protocol tag is not generated in the Study Section2, then;
				if (i == 1 && (protocolDescription == null || protocolDescription.equals(""))) {
					str = insertIndent(indent);
					str += "<Protocol>";
					writer.write(str);
					writer.newLine();
					indent++;
				}

				str = insertIndent(indent);
				str += "<StudyEventRef StudyEventOID=\"" + createOID(OdmTagType.STUDYEVENTREF, hash.get("ID"), "", "", "", "")
						+ "\" OrderNumber=\"" + i++
						+ "\" Mandatory=\"" + hash.get("Mandatory")
						+ (hash.get("CollectionExceptionCondition").equals("") ? "" : "\" CollectionExceptionConditionOID=\"" + createOID(OdmTagType.CONDITIONDEF, "", "", "", "", hash.get("CollectionExceptionCondition")))
						+ "\"/>";
				writer.write(str);
				writer.newLine();
			}

			// If the Protocol tag is generated, then
			if (!(i == 1 && (protocolDescription == null || protocolDescription.equals("")))) {
				indent--;
				str = insertIndent(indent);
				str += "</Protocol>";
				writer.write(str);
				writer.newLine();
			}
			
		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	public void writeEventDefSection() throws TableNotFoundException, IOException, RequiredValueMissingException, InvalidOidSyntaxException {
		errHint.setErrorHint(OdmTagType.STUDYEVENTDEF, "", "", "", "", "");
		try {
			Hashtable<String, String> hash = new Hashtable<String, String>();
			Hashtable<String, String> hash2 = new Hashtable<String, String>();
			Hashtable<String, String> formNameIdHash = new Hashtable<String, String>(); //Pairs of Form ID and Name
			String str = new String();
			int order = 1;
			
			//Get pairs of Form ID and Name for later use.
			reader.setTable(config.odmFormTableName);
			while ((hash = reader.read(config.odmFormTableName)) != null) {
				errHint.setErrorHint(OdmTagType.FORMDEF, "", hash.get("Name"), "", "", "");
				formNameIdHash.put(hash.get("Name"), hash.get("ID"));
			}
			
			reader.setTable(config.odmEventTableName);
			while ((hash = reader.read(config.odmEventTableName)) != null) {
				errHint.setErrorHint(OdmTagType.STUDYEVENTDEF, hash.get("Name"), "", "", "", "");
				str = insertIndent(indent);
				str += "<StudyEventDef OID=\"" + createOID(OdmTagType.STUDYEVENTDEF, hash.get("ID"), "", "", "", "")
						+ "\" Name=\"" + hash.get("Name")
						+ "\" Repeating=\"" + hash.get("Repeating")
						+ "\" Type=\"" + hash.get("Type")
						+ (hash.get("Category").equals("") ? "" : "\" Category=\"" + hash.get("Category"))
						+ "\">";
				writer.write(str);
				writer.newLine();
				indent++;
				
				if (hash.get("Description") != null && !hash.get("Description").equals("")) {
					str = insertIndent(indent);
					str += "<Description>";
					writer.write(str);
					writer.newLine();
					indent++;
					
					str = insertIndent(indent);
					str += "<TranslatedText"
							+ (StringUtils.isEmpty(hash.get("xml:lang")) ? "" : " xml:lang=\"" + hash.get("xml:lang") + "\"")
							+ ">"
							+ XmlGenerator.escapeString(hash.get("Description")) + "</TranslatedText>";
					writer.write(str);
					writer.newLine();
	
					indent--;
					str = insertIndent(indent);
					str += "</Description>";
					writer.write(str);
					writer.newLine();
				}
				
				reader.setTable(config.odmEventFormTableName,
						new WhereClause[] { new WhereClause("Event Name", WhereClause.Operator.EQ, hash.get("Name")) });
				
				while ((hash2 = reader.read(config.odmEventFormTableName)) != null) {
					errHint.setErrorHint(OdmTagType.FORMREF, hash2.get("Event Name"), hash2.get("Form Name"), "", "",  "");

					str = insertIndent(indent);
					str += "<FormRef FormOID=\"" + createOID(OdmTagType.FORMREF, "", formNameIdHash.get(hash2.get("Form Name")), "", "", "")
							+ "\" OrderNumber=\"" + order++
							+ "\" Mandatory=\"" + hash2.get("Mandatory")
							+ (hash2.get("CollectionExceptionCondition").equals("") ? "" : "\" CollectionExceptionConditionOID=\"" + createOID(OdmTagType.CONDITIONDEF, "", "", "", "", hash2.get("CollectionExceptionCondition")))
							+ "\"/>";
					writer.write(str);
					writer.newLine();
					
					formNameMandatoryHash.put(hash2.get("Form Name"), hash2.get("Mandatory"));	//Used to retain Mandatory value of forms; eventually used to generate default itemgroup.
				}
				
				if (hash.get("Alias Name") != null && !hash.get("Alias Name").equals("")) {
					String strAliasName[] = hash.get("Alias Name").split(DELIMITER);
					String strAliasContext[] = hash.get("Alias Context").split(DELIMITER);
					for (int i2 = 0; i2 < strAliasName.length; i2++) {
						str = insertIndent(indent);
						str += "<Alias Context=\"" + strAliasContext[i2].trim()
								+ "\" Name=\"" + strAliasName[i2].trim()
								+ "\"/>";
						writer.write(str);
						writer.newLine();
					}
				}
				
				indent--;
				str = insertIndent(indent);
				str += "</StudyEventDef>";
				writer.write(str);
				writer.newLine();
			}
			
		} catch (ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
			throw new RequiredValueMissingException(ex, errHint);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	public void writeFormDefSection() throws TableNotFoundException, IOException, RequiredValueMissingException, InvalidOidSyntaxException {
		try {
			errHint.setErrorHint(OdmTagType.FORMDEF, "", "", "", "", "");
			Hashtable<String, String> hash = new Hashtable<String, String>();
			Hashtable<String, String> hash2 = new Hashtable<String, String>();
			String str = new String();
			
			reader.setTable(config.odmFormTableName);
			while ((hash = reader.read(config.odmFormTableName)) != null) {
				errHint.setErrorHint(OdmTagType.FORMDEF, "", hash.get("Name"), "", "", "");
				String isRepeating = hash.get("Repeating");
				if (StringUtils.isEmpty(isRepeating)) {
					isRepeating = "No";
				}
				formNameRepeatingHash.put(hash.get("Name"), isRepeating);	//Used to retain Repeating value of forms; eventually used to generate default itemgroup.

				str = insertIndent(indent);
				str += "<FormDef OID=\"" + createOID(OdmTagType.FORMDEF, "", hash.get("ID"), "", "", "")
						+ "\" Name=\"" + XmlGenerator.escapeString(hash.get("Name"))
						+ "\" Repeating=\"" + isRepeating
						+ "\">";
				writer.write(str);
				writer.newLine();
				indent++;
				
				if (hash.get("Description") != null && !hash.get("Description").equals("")) {
					str = insertIndent(indent);
					str += "<Description>";
					writer.write(str);
					writer.newLine();
					indent++;
					
					str = insertIndent(indent);
					str += "<TranslatedText"
							+ (StringUtils.isEmpty(hash.get("xml:lang")) ? "" : " xml:lang=\"" + hash.get("xml:lang") + "\"")
							+ ">"
							+ XmlGenerator.escapeString(hash.get("Description")) + "</TranslatedText>";
					writer.write(str);
					writer.newLine();

					indent--;
					str = insertIndent(indent);
					str += "</Description>";
					writer.write(str);
					writer.newLine();
				}
				reader.setTable(config.odmFieldTableName,
						new WhereClause[] { new WhereClause("Form Name", WhereClause.Operator.EQ, hash.get("Name"))});
				
				int i = 1;
				int itemGroupNumber = 0;	// Number of itemGroups in a form
				while ((hash2 = reader.read(config.odmFieldTableName)) != null) {
					errHint.setErrorHint(OdmTagType.ITEMGROUPREF, "", hash2.get("Form Name"), hash2.get("Item Name"), "",  "");

					if (hash2.get("Level").equals("1")) {
						String conditionId = hash2.get("Condition ID");
						if (StringUtils.isEmpty(conditionId)) {
							conditionId = hash2.get("CollectionExceptionCondition");
						}
						itemGroupNumber++;
						str = insertIndent(indent);
						str += "<ItemGroupRef ItemGroupOID=\"" + createOID(OdmTagType.ITEMGROUPREF, "", "", hash2.get("ID"), "", "")
								+ "\" OrderNumber=\"" + i++
								+ "\" Mandatory=\"" + hash2.get("Mandatory")
								+ (StringUtils.isEmpty(conditionId) ? "" : "\" CollectionExceptionConditionOID=\"" + createOID(OdmTagType.CONDITIONDEF, "", "", "", "", conditionId))
								+ "\"/>";
						writer.write(str);
						writer.newLine();
					} else {
						if (itemGroupNumber == 0) {
							String mandatory = formNameMandatoryHash.get(hash2.get("Form Name"));
							if (StringUtils.isEmpty(mandatory)) {
								mandatory = "No";
							}
							//Begin default ItemGroupDef
							itemGroupNumber++;
							str = insertIndent(indent);
							str += "<ItemGroupRef ItemGroupOID=\"" + createOID(OdmTagType.ITEMGROUPDEF, "", "", createOID(OdmTagType.FORMDEF, "", hash.get("ID"), "", "", ""), "", "")
									+ "\" OrderNumber=\"" + i++
									+ "\" Mandatory=\"" + mandatory	//Assign Mandatory value of event-form
									+ "\"/>";
							writer.write(str);
							writer.newLine();
						}
					}
				}
				
				if (hash.get("PdfFileName") != null && !hash.get("PdfFileName").equals("")) {
					str = insertIndent(indent);
					str += "<ArchiveLayout OID=\"" + createOID(OdmTagType.ARCHIVELAYOUT, "", "", "", "", hash.get("PdfFileName"))
							+ "\" PdfFileName=\"" + hash.get("PdfFileName")
							+ "\"/>";
					writer.write(str);
					writer.newLine();
				}
				
				if (hash.get("Alias Name") != null && !hash.get("Alias Name").equals("")) {
					String strAliasName[] = hash.get("Alias Name").split(DELIMITER);
					String strAliasContext[] = hash.get("Alias Context").split(DELIMITER);
					for (int i2 = 0; i2 < strAliasName.length; i2++) {
						str = insertIndent(indent);
						str += "<Alias Context=\"" + strAliasContext[i2].trim()
								+ "\" Name=\"" + strAliasName[i2].trim()
								+ "\"/>";
						writer.write(str);
						writer.newLine();
					}
				}
				
				indent--;
				str = insertIndent(indent);
				str += "</FormDef>";
				writer.write(str);
				writer.newLine();
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	public void writeItemGroupDefSection() throws TableNotFoundException, IOException, RequiredValueMissingException, InvalidOidSyntaxException {

		/*
		 * *** Algorithm Description ***
		 * (1) If the "level" attribute is 1, then;
		 *     (1-1) If it is the start of the SHEET, then;
		 *         - Begin ItemGroupDef
		 *     (1-2) Else, if it is NOT the start of the SHEET, then;
		 *         - End ItemGroupDef
		 *         - Begin ItemGroupDef
		 * (2) If the "level" attribute is 0, then;
		 * 	   (2-1) If it is the start of the SHEET, then;
		 *          - Begin default ItemGroupDef
		 *          - Create ItemRef
		 *     (2-2) If it is NOT the start of the SHEET, then;
		 *         (2-2-1) If it is the start of the FORM, then;
		 *             - End ItemGroupDef
		 *             - Begin default ItemGroupDef
		 *             - Create ItemRef
		 *         (2-2-2) If it is NOT the start of the FORM, then;
		 *             - Create ItemRef
		 * Finally, end ItemGroupDef
		 */
		try {
			Hashtable<String, String> hash = new Hashtable<String, String>();
			Hashtable<String, String> formNameIdHash = new Hashtable<String, String>();
			String str = new String();
			
			boolean isSheetStart = true; // Indicates the line is beginning of the field sheet
			boolean isFormStart = true;	// Indicates the line is beginning of a form
			String prevFormName = null;
			int itemGroupNumber = 0;	// Number of itemGroups in a form
			int orderNumber = 1;
			String strAliasName[] = null;
			String strAliasContext[] = null;
			
			reader.setTable(config.odmFormTableName);
			while ((hash = reader.read(config.odmFormTableName)) != null) {
				formNameIdHash.put(hash.get("Name"), hash.get("ID"));
			}
			
			reader.setTable(config.odmFieldTableName);
			while ((hash = reader.read(config.odmFieldTableName)) != null) {
				if (prevFormName != null) {
					if (prevFormName.equals(hash.get("Form Name"))) {
						isFormStart = false;
					} else {
						isFormStart = true;
						itemGroupNumber = 0;	//Reset itemGroupNumber for a new form. 
					}
				} 
				prevFormName = hash.get("Form Name");
				
				if (hash.get("Level").equals("1")) {
					errHint.setErrorHint(OdmTagType.ITEMGROUPDEF, "", "", hash.get("Item Name"), "", "");
					itemGroupNumber++;

					//End ItemGroupDef
					if (!isSheetStart) {
						if (strAliasName[0] != null && !strAliasName[0].equals("")) {
							for (int i2 = 0; i2 < strAliasName.length; i2++) {
								str = insertIndent(indent);
								str += "<Alias Context=\"" + strAliasContext[i2].trim()
										+ "\" Name=\"" + strAliasName[i2].trim()
										+ "\"/>";
								writer.write(str);
								writer.newLine();
							}
						}
						indent--;
						str = insertIndent(indent);
						str += "</ItemGroupDef>";
						writer.write(str);
						writer.newLine();
						strAliasName = null;
						strAliasContext = null;
					} else {
						isSheetStart = false;
					}
					
					//Begin ItemGroupDef
					orderNumber = 1;	//Reset order number
					strAliasContext = hash.get("Alias Context").split(DELIMITER);	//Retain alias value to close ItemGroupDef later
					strAliasName = hash.get("Alias Name").split(DELIMITER);	//Retain alias value to close ItemGroupDef later
					str = insertIndent(indent);
					str += "<ItemGroupDef OID=\"" + createOID(OdmTagType.ITEMGROUPDEF, "", "", hash.get("ID"), "", "")
							+ "\" Name=\"" + hash.get("Item Name")
							+ "\" Repeating=\"" + hash.get("Repeating")
							+ (hash.get("IsReferenceData").equals("") ? "" : "\" IsReferenceData=\"" + hash.get("IsReferenceData"))
							+ (hash.get("SAS Name").equals("") ? "" : "\" SASDatasetName=\"" + hash.get("SAS Name"))
							+ "\">";
					writer.write(str);
					writer.newLine();
					indent++;
					
					if (hash.get("Description") != null && !hash.get("Description").equals("")) {
						str = insertIndent(indent);
						str += "<Description>";
						writer.write(str);
						writer.newLine();
						indent++;
						
						str = insertIndent(indent);
						str += "<TranslatedText"
								+ (StringUtils.isEmpty(hash.get("Description xml:lang")) ? "" : " xml:lang=\"" + hash.get("Description xml:lang") + "\"")
								+ ">"
								+ XmlGenerator.escapeString(hash.get("Description")) + "</TranslatedText>";
						writer.write(str);
						writer.newLine();

						indent--;
						str = insertIndent(indent);
						str += "</Description>";
						writer.write(str);
						writer.newLine();
					}
				} else {	//hash.get("Level").equals("0")
					errHint.setErrorHint(OdmTagType.ITEMREF, "", "", "", hash.get("Item Name"), "");
					if (isSheetStart) {
						//Begin default ItemGroupDef
						isSheetStart = false;
						itemGroupNumber++;
						strAliasContext = hash.get("Alias Context").split(DELIMITER);	//Retain alias value to close ItemGroupDef later
						strAliasName = hash.get("Alias Name").split(DELIMITER);	//Retain alias value to close ItemGroupDef later
						str = insertIndent(indent);
						str += "<ItemGroupDef OID=\"" + createOID(OdmTagType.ITEMGROUPDEF, "", "", createOID(OdmTagType.FORMDEF, "", formNameIdHash.get(hash.get("Form Name")), "", "", ""), "", "")
								+ "\" Name=\"DEFAULT_" + itemGroupNumber
								+ "\" Repeating=\"" + formNameRepeatingHash.get(hash.get("Form Name"))
								+ "\">";
						writer.write(str);
						writer.newLine();
						indent++;
					} else {
						if (isFormStart) {
							//End ItemGroupDef
							isFormStart = false;
							if (strAliasName[0] != null && !strAliasName[0].equals("")) {
								for (int i2 = 0; i2 < strAliasName.length; i2++) {
									str = insertIndent(indent);
									str += "<Alias Context=\"" + strAliasContext[i2].trim()
											+ "\" Name=\"" + strAliasName[i2].trim()
											+ "\"/>";
									writer.write(str);
									writer.newLine();
								}
							}
							indent--;
							str = insertIndent(indent);
							str += "</ItemGroupDef>";
							writer.write(str);
							writer.newLine();
							strAliasName = null;
							strAliasContext = null;
							
							//Begin default ItemGroupDef
							orderNumber = 1;	//Reset order number
							itemGroupNumber++;
							strAliasContext = hash.get("Alias Context").split(DELIMITER);	//Retain alias value to close ItemGroupDef later
							strAliasName = hash.get("Alias Name").split(DELIMITER);	//Retain alias value to close ItemGroupDef later
							str = insertIndent(indent);
							str += "<ItemGroupDef OID=\"" + createOID(OdmTagType.ITEMGROUPDEF, "", "", createOID(OdmTagType.FORMDEF, "", formNameIdHash.get(hash.get("Form Name")), "", "", ""), "", "")
									+ "\" Name=\"DEFAULT_" + itemGroupNumber
									+ "\" Repeating=\"" + formNameRepeatingHash.get(hash.get("Form Name"))
									+ "\">";
							writer.write(str);
							writer.newLine();
							indent++;
						}
					}

					//Create ItemRef
					String methodId = hash.get("Method ID");
					if (StringUtils.isEmpty(methodId)) {
						methodId = hash.get("Derivation");
					}
					String conditionId = hash.get("Condition ID");
					if (StringUtils.isEmpty(conditionId)) {
						conditionId = hash.get("CollectionExceptionCondition");
					}
					str = insertIndent(indent);
					str += "<ItemRef ItemOID=\"" + createOID(OdmTagType.ITEMREF, "", "", "", hash.get("ID"), "")
							+ "\" OrderNumber=\"" + orderNumber++
							+ "\" Mandatory=\"" + hash.get("Mandatory")
							+ (hash.get("Key Sequence").equals("") ? "" : "\" KeySequence=\"" + hash.get("Key Sequence"))
							+ (StringUtils.isEmpty(methodId) ? ""
									: "\" MethodOID=\"" + createOID(OdmTagType.METHODDEF, "", "", "", "", methodId))
							+ (StringUtils.isEmpty(conditionId) ? ""
									: "\" CollectionExceptionConditionOID=\"" + createOID(OdmTagType.CONDITIONDEF, "", "", "", "", conditionId))
							+ "\"/>";
					writer.write(str);
					writer.newLine();
				}
			}
			
			//Finally, end ItemGroupDef
			if (itemGroupNumber > 0) {
				if (strAliasName[0] != null && !strAliasName[0].equals("")) {
					for (int i2 = 0; i2 < strAliasName.length; i2++) {
						str = insertIndent(indent);
						str += "<Alias Context=\"" + strAliasContext[i2].trim()
								+ "\" Name=\"" + strAliasName[i2].trim()
								+ "\"/>";
						writer.write(str);
						writer.newLine();
					}
				}
				indent--;
				str = insertIndent(indent);
				str += "</ItemGroupDef>";
				writer.write(str);
				writer.newLine();
				strAliasName = null;
				strAliasContext = null;
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
			throw new RequiredValueMissingException(ex, errHint);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			throw new RequiredValueMissingException(ex, errHint);
		}
	}
	
	public void writeItemDefSection() throws TableNotFoundException, IOException, RequiredValueMissingException, InvalidOidSyntaxException {
		try {
			errHint.setErrorHint(OdmTagType.ITEMDEF, "", "", "", "", "");
			Hashtable<String, String> hash = new Hashtable<String, String>();
			Hashtable<String, String> unitNameIdHash = new Hashtable<String, String>();
			String str = new String();

			//Get pairs of Unit ID and Name for later use.
			reader.setTable(config.odmUnitTableName);
			while ((hash = reader.read(config.odmUnitTableName)) != null) {
				errHint.setErrorHint(OdmTagType.MEASUREMENTUNIT, "", hash.get("Name"), "", "", "");
				unitNameIdHash.put(hash.get("Name"), hash.get("ID"));
			}

			reader.setTable(config.odmFieldTableName,
					new WhereClause[] { new WhereClause("Level", WhereClause.Operator.EQ, "0")});

			Set<String> itemOids = new HashSet<>();	//All ItemOids to check uniqueness
			while ((hash = reader.read(config.odmFieldTableName)) != null) {
				errHint.setErrorHint(OdmTagType.ITEMDEF, "", "", "", hash.get("Item Name"), "");
				String itemOid = createOID(OdmTagType.ITEMDEF, "", "", "", hash.get("ID"), "");
				if (itemOids.contains(itemOid)) {
					continue;	//Skip because the itemOid already exists.
				}
				itemOids.add(itemOid);
				str = insertIndent(indent);
				str += "<ItemDef OID=\"" + itemOid
						+ "\" Name=\"" + XmlGenerator.escapeString(hash.get("Item Name"))
						+ "\" DataType=\"" + hash.get("DataType")
						+ (hash.get("Length").equals("") ? "" : "\" Length=\"" + hash.get("Length"))
						+ (hash.get("SignificantDigits").equals("") ? "" : "\" SignificantDigits=\"" + hash.get("SignificantDigits"))
						+ (hash.get("SAS Name").equals("") ? "" : "\" SASFieldName=\"" + hash.get("SAS Name"))
						+ (StringUtils.isEmpty(hash.get("ControlType")) ? "" : "\" ddedcp:InputFormatTyp=\"" + hash.get("ControlType"))
						+ (StringUtils.isEmpty(hash.get("Section Label")) ? "" : "\" ddedcp:SectionLabelStyle=\"2")
						+ (StringUtils.isEmpty(hash.get("Section Label")) ? "" : "\" ddedcp:SectionLabel=\"" + XmlGenerator.escapeString(hash.get("Section Label")))
						+ "\">";
				writer.write(str);
				writer.newLine();
				indent++;
				
				if (hash.get("Description") != null && !hash.get("Description").equals("")) {
					str = insertIndent(indent);
					str += "<Description>";
					writer.write(str);
					writer.newLine();
					indent++;
					
					str = insertIndent(indent);
					str += "<TranslatedText"
							+ (StringUtils.isEmpty(hash.get("Description xml:lang")) ? "" : " xml:lang=\"" + hash.get("Description xml:lang") + "\"")
							+ ">"
							+ XmlGenerator.escapeString(hash.get("Description")) + "</TranslatedText>";
					writer.write(str);
					writer.newLine();

					indent--;
					str = insertIndent(indent);
					str += "</Description>";
					writer.write(str);
					writer.newLine();
				}

				if (hash.get("Question") != null && !hash.get("Question").equals("")) {
					str = insertIndent(indent);
					str += "<Question>";
					writer.write(str);
					writer.newLine();
					indent++;
					
					str = insertIndent(indent);
					str += "<TranslatedText"
							+ (StringUtils.isEmpty(hash.get("Question xml:lang")) ? "" : " xml:lang=\"" + hash.get("Question xml:lang") + "\"")
							+ ">"
							+ XmlGenerator.escapeString(hash.get("Question")) + "</TranslatedText>";
					writer.write(str);
					writer.newLine();

					indent--;
					str = insertIndent(indent);
					str += "</Question>";
					writer.write(str);
					writer.newLine();
				}

				if (hash.get("Unit Name") != null && !hash.get("Unit Name").equals("")) {
					String strUnitName[] = hash.get("Unit Name").split(DELIMITER);
					for (int i2 = 0; i2 < strUnitName.length; i2++) {
						errHint.setErrorHint(OdmTagType.ITEMDEF, "", "", "", hash.get("Item Name"), strUnitName[i2].trim());
						str = insertIndent(indent);
						str += "<MeasurementUnitRef MeasurementUnitOID=\""
								+ createOID(OdmTagType.MEASUREMENTUNIT, "", "", "", "", unitNameIdHash.get(strUnitName[i2].trim()))
								+ "\"/>";
						writer.write(str);
						writer.newLine();
					}
				}
				errHint.setErrorHint(OdmTagType.ITEMDEF, "", "", "", hash.get("Item Name"), "");

				if ((hash.get("RangeCheck") != null && !hash.get("RangeCheck").equals(""))
						|| (hash.get("Formal Expression") != null && !hash.get("Formal Expression").equals(""))) {
					if (hash.get("RangeCheck") != null && !hash.get("RangeCheck").equals("")) {
						String strRangeCheck[] = hash.get("RangeCheck").split(" ", 2);
						String strCheckValue[] = strRangeCheck[1].split(DELIMITER);
						
						str = insertIndent(indent);
						str += "<RangeCheck Comparator=\"" + strRangeCheck[0].trim()
								+ "\" SoftHard=\"" + hash.get("SoftHard")
								+ "\">";
						writer.write(str);
						writer.newLine();
						indent++;
						
						for (int i2 = 0; i2 < strCheckValue.length; i2++) {
							str = insertIndent(indent);
							str += "<CheckValue>"
									+ strCheckValue[i2].trim()
									+ "</CheckValue>";
							writer.write(str);
							writer.newLine();
						}
					} else {
						str = insertIndent(indent);
						str += "<RangeCheck SoftHard=\"" + hash.get("SoftHard")
								+ "\">";
						writer.write(str);
						writer.newLine();
						indent++;
						
						str = insertIndent(indent);
						str += "<FormalExpression Context=\"" + hash.get("Formal Expression Context") + "\">"
								+ XmlGenerator.escapeString(hash.get("Formal Expression")) + "</FormalExpression>";
						writer.write(str);
						writer.newLine();
					}
					
					if (hash.get("Unit Name") != null && !hash.get("Unit Name").equals("")) {
						String strUnitName[] = hash.get("Unit Name").split(DELIMITER);
						for (int i2 = 0; i2 < strUnitName.length; i2++) {
							errHint.setErrorHint(OdmTagType.ITEMDEF, "", "", "", hash.get("Item Name"), strUnitName[i2].trim());
							str = insertIndent(indent);
							str += "<MeasurementUnitRef MeasurementUnitOID=\""
									+ createOID(OdmTagType.MEASUREMENTUNIT, "", "", "", "", unitNameIdHash.get(strUnitName[i2].trim()))
									+ "\"/>";
							writer.write(str);
							writer.newLine();
						}
					}
					errHint.setErrorHint(OdmTagType.ITEMDEF, "", "", "", hash.get("Item Name"), "");
					
					if (hash.get("RangeCheck Error Message") != null && !hash.get("RangeCheck Error Message").equals("")) {
						str = insertIndent(indent);
						str += "<ErrorMessage>";
						writer.write(str);
						writer.newLine();
						indent++;
						
						str = insertIndent(indent);
						str += "<TranslatedText"
								+ (StringUtils.isEmpty(hash.get("Question xml:lang")) ? "" : " xml:lang=\"" + hash.get("Question xml:lang") + "\"")
								+ ">"
								+ XmlGenerator.escapeString(hash.get("RangeCheck Error Message")) + "</TranslatedText>";
						writer.write(str);
						writer.newLine();

						indent--;
						str = insertIndent(indent);
						str += "</ErrorMessage>";
						writer.write(str);
						writer.newLine();
					}
					
					indent--;
					str = insertIndent(indent);
					str += "</RangeCheck>";
					writer.write(str);
					writer.newLine();
				}
				
				if (hash.get("Codelist") != null && !hash.get("Codelist").equals("")) {
					str = insertIndent(indent);
					str += "<CodeListRef CodeListOID=\""
							+ createOID(OdmTagType.CODELISTREF, "", "", "", "", hash.get("Codelist"))  
							+ "\"/>";
					writer.write(str);
					writer.newLine();
				}
				
				if (hash.get("Alias Name") != null && !hash.get("Alias Name").equals("")) {
					String strAliasName[] = hash.get("Alias Name").split(DELIMITER);
					String strAliasContext[] = hash.get("Alias Context").split(DELIMITER);
					for (int i2 = 0; i2 < strAliasName.length; i2++) {
						str = insertIndent(indent);
						str += "<Alias Context=\"" + strAliasContext[i2].trim()
								+ "\" Name=\"" + strAliasName[i2].trim()
								+ "\"/>";
						writer.write(str);
						writer.newLine();
					}
				}
				
				indent--;
				str = insertIndent(indent);
				str += "</ItemDef>";
				writer.write(str);
				writer.newLine();
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	public void writeCodelistSection() throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {
			errHint.setErrorHint(OdmTagType.CODELIST, "", "", "", "", "");
			reader.setTable(config.odmCodelistTableName);

			Hashtable<String, String> hash = new Hashtable<String, String>();
			String str = null;
			String prevCodelistID = null;		//Used to judge whether a new row belongs to the same codelist group as the previous row.
			String prevCodelistCode = null;		//Used to retain Codelist Code value of the previous loop to create Alias tags.
			boolean hasDecodeValue = false; //false for EnumeratedItem; true for CodelistItem;

			while ((hash = reader.read(config.odmCodelistTableName)) != null) {
				errHint.setErrorHint(OdmTagType.CODELIST, "", "", "", "", hash.get("Codelist ID"));

				/* If this is a new codelist group (e.g. NY, AGEU, SEX ...) */
				if (prevCodelistID == null || !prevCodelistID.equals(hash.get("Codelist ID"))) {

					/* Judge whether the codelist is enumearation or code/decode list using the first record of the codelist */
					if ((hash.get("Translated Text") == null || hash.get("Translated Text").equals(""))
							&& (hash.get("Decode") == null || hash.get("Decode").equals(""))) {
						hasDecodeValue = false;
					} else {
						hasDecodeValue = true;
					}

					/* If this is not a start of the CODELIST table, close the Codelist tag.*/
					if (prevCodelistID != null) {
						if (prevCodelistCode != null && !prevCodelistCode.equals("")) {
							str = insertIndent(indent);
							str += "<Alias Name=\"" + prevCodelistCode
									+ "\" Context=\"nci:ExtCodeID\"/>";
							writer.write(str);
							writer.newLine();
						}
						indent--;
						str = insertIndent(indent);
						str += "</CodeList>";
						writer.write(str);
						writer.newLine();
					}

					str = insertIndent(indent);
					str += "<CodeList OID=\""
							+ createOID(OdmTagType.CODELIST, "", "", "", "", hash.get("Codelist ID"))
							+ "\" Name=\"" + XmlGenerator.escapeString(hash.get("Codelist Label"))
							+ "\" DataType=\"" + hash.get("DataType")
							+ (hash.get("SASFormatName") == null || hash.get("SASFormatName").equals("") ?
									"" : "\" SASFormatName=\"" + hash.get("SASFormatName"))
							+ "\">";
					writer.write(str);
					writer.newLine();
					indent++;

				} else {
					// If this is another entry of the previous codelist group, simply skip the steps above.
				}
				
				/*
				 * When a decode value is not provided
				 */
				if (hasDecodeValue == false) {
					str = insertIndent(indent);
					str += "<EnumeratedItem CodedValue=\"" + XmlGenerator.escapeString(hash.get("Submission Value"))
							+ (hash.get("Rank") == null || hash.get("Rank").equals("") ?
									"" : "\" Rank=\"" + hash.get("Rank"))
							+ (hash.get("Order Number") == null || hash.get("Order Number").equals("") ?
									"" : "\" OrderNumber=\"" + hash.get("Order Number"))
							+ "\">";
					writer.write(str);
					writer.newLine();
					indent++;
					if (hash.get("Alias Name") != null && !hash.get("Alias Name").equals("")) {
						String strAliasName[] = hash.get("Alias Name").split(DELIMITER);
						String strAliasContext[] = hash.get("Alias Context").split(DELIMITER);
						for (int i2 = 0; i2 < strAliasName.length; i2++) {
							str = insertIndent(indent);
							str += "<Alias Context=\"" + strAliasContext[i2].trim()
									+ "\" Name=\"" + strAliasName[i2].trim()
									+ "\"/>";
							writer.write(str);
							writer.newLine();
						}
					}
					if (hash.get("Code") != null && !hash.get("Code").equals("")) {
						str = insertIndent(indent);
						str += "<Alias Name=\"" + hash.get("Code")
								+ "\" Context=\"nci:ExtCodeID\"/>";
						writer.write(str);
						writer.newLine();
					}

					indent--;
					str = insertIndent(indent);
					str += "</EnumeratedItem>";
					writer.write(str);
					writer.newLine();

				/*
				 * When a decode value is provided
				 */
				} else {
					str = insertIndent(indent);
					str += "<CodeListItem CodedValue=\"" + XmlGenerator.escapeString(hash.get("Submission Value"))
							+ (hash.get("Rank") == null || hash.get("Rank").equals("") ?
									"" : "\" Rank=\"" + hash.get("Rank"))
							+ (hash.get("Order Number") == null || hash.get("Order Number").equals("") ?
									"" : "\" OrderNumber=\"" + hash.get("Order Number"))
							+ "\">";
					writer.write(str);
					writer.newLine();
					indent++;

					str = insertIndent(indent);
					str += "<Decode>";
					writer.write(str);
					writer.newLine();
					indent++;

					str = insertIndent(indent);
					str += "<TranslatedText"
							+ (StringUtils.isEmpty(hash.get("xml:lang")) ? "" : " xml:lang=\"" + hash.get("xml:lang") + "\"")
							+ ">"
							+ (hash.get("Translated Text") == null || hash.get("Translated Text").equals("") ?
									XmlGenerator.escapeString(hash.get("Decode")) : XmlGenerator.escapeString(hash.get("Translated Text"))) 
							+ "</TranslatedText>";
					writer.write(str);
					writer.newLine();

					indent--;
					str = insertIndent(indent);
					str += "</Decode>";
					writer.write(str);
					writer.newLine();

					if (hash.get("Alias Name") != null && !hash.get("Alias Name").equals("")) {
						String strAliasName[] = hash.get("Alias Name").split(DELIMITER);
						String strAliasContext[] = hash.get("Alias Context").split(DELIMITER);
						for (int i2 = 0; i2 < strAliasName.length; i2++) {
							str = insertIndent(indent);
							str += "<Alias Context=\"" + strAliasContext[i2].trim()
									+ "\" Name=\"" + strAliasName[i2].trim()
									+ "\"/>";
							writer.write(str);
							writer.newLine();
						}
					}
					if (hash.get("Code") != null && !hash.get("Code").equals("")) {
						str = insertIndent(indent);
						str += "<Alias Name=\"" + hash.get("Code")
								+ "\" Context=\"nci:ExtCodeID\"/>";
						writer.write(str);
						writer.newLine();
					}
					
					indent--;
					str = insertIndent(indent);
					str += "</CodeListItem>";
					writer.write(str);
					writer.newLine();
				}

				prevCodelistID = hash.get("Codelist ID");
				prevCodelistCode = hash.get("Codelist Code");
			}		//End of while loop

			/* Close the CodeList tag at the end of while loop */
			/*
			 * When it is a NCI codelist
			 */
			if (prevCodelistCode != null && !prevCodelistCode.equals("")) {
				str = insertIndent(indent);
				str += "<Alias Name=\"" + prevCodelistCode
						+ "\" Context=\"nci:ExtCodeID\"/>";
				writer.write(str);
				writer.newLine();
			}
			if (prevCodelistID != null && !prevCodelistID.equals("")) {
				indent--;
				str = insertIndent(indent);
				str += "</CodeList>";
				writer.write(str);
				writer.newLine();
			}

		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}
	
	
	public void writeConditionDefSection() throws TableNotFoundException, IOException, RequiredValueMissingException, InvalidOidSyntaxException {
		errHint.setErrorHint(OdmTagType.CONDITIONDEF, "", "", "", "", "");
		try {
			reader.setTable(config.odmConditionTableName);

			Hashtable<String, String> hash = new Hashtable<String, String>();
			String prevConditionName = null;	//FormalExpression can be represented using multiple rows
			int conditionNumber = 0;
			String str = null;
			String strAliasName[] = null;
			String strAliasContext[] = null;

			while ((hash = reader.read(config.odmConditionTableName)) != null) {
				errHint.setErrorHint(OdmTagType.CONDITIONDEF, "", "", "", "", "");
				String conditionId = hash.get("ID");
				if (StringUtils.isEmpty(conditionId)) {
					conditionId = hash.get("Condition ID");
				}
				String conditionName = hash.get("Name");
				if (StringUtils.isEmpty(conditionName)) {
					conditionName = hash.get("Condition Name");
				}
				if (prevConditionName == null || !prevConditionName.equals(conditionName)) {
					conditionNumber++;
					if (prevConditionName != null) {
						if (strAliasName[0] != null && !strAliasName[0].equals("")) {
							for (int i2 = 0; i2 < strAliasName.length; i2++) {
								str = insertIndent(indent);
								str += "<Alias Context=\"" + strAliasContext[i2].trim()
										+ "\" Name=\"" + strAliasName[i2].trim()
										+ "\"/>";
								writer.write(str);
								writer.newLine();
							}
						}
						strAliasName = null;
						strAliasContext = null;
						
						indent--;
						str = insertIndent(indent);
						str += "</ConditionDef>";
						writer.write(str);
						writer.newLine();
					}
					str = insertIndent(indent);
					str += "<ConditionDef OID=\"" + createOID(OdmTagType.CONDITIONDEF, "", "", "", "", conditionId)
							+ "\" Name=\"" + conditionName
							+ "\">";
					writer.write(str);
					writer.newLine();
					indent++;
					
					str = insertIndent(indent);
					str += "<Description>";
					writer.write(str);
					writer.newLine();
					indent++;
						
					str = insertIndent(indent);
					str += "<TranslatedText"
							+ (StringUtils.isEmpty(hash.get("xml:lang")) ? "" : " xml:lang=\"" + hash.get("xml:lang") + "\"")
							+ ">"
							+ XmlGenerator.escapeString(hash.get("Description")) + "</TranslatedText>";
					writer.write(str);
					writer.newLine();

					indent--;
					str = insertIndent(indent);
					str += "</Description>";
					writer.write(str);
					writer.newLine();
					
					strAliasName = hash.get("Alias Name").split(DELIMITER);
					strAliasContext = hash.get("Alias Context").split(DELIMITER);
				}
				
				str = insertIndent(indent);
				str += "<FormalExpression Context=\"" + hash.get("Formal Expression Context") + "\">"
						+ XmlGenerator.escapeString(hash.get("Formal Expression")) + "</FormalExpression>";
				writer.write(str);
				writer.newLine();
				
				prevConditionName = conditionName;
			}

			if (conditionNumber > 0) {
				if (strAliasName[0] != null && !strAliasName[0].equals("")) {
					for (int i2 = 0; i2 < strAliasName.length; i2++) {
						str = insertIndent(indent);
						str += "<Alias Context=\"" + strAliasContext[i2].trim()
								+ "\" Name=\"" + strAliasName[i2].trim()
								+ "\"/>";
						writer.write(str);
						writer.newLine();
					}
				}
				strAliasName = null;
				strAliasContext = null;
				
				indent--;
				str = insertIndent(indent);
				str += "</ConditionDef>";
				writer.write(str);
				writer.newLine();
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		} catch  (TableNotFoundException ex) {
			logger.info("No ConditionDef section found. Skipping...");
		}
	}
	
	public void writeMethodDefSection() throws TableNotFoundException, IOException, RequiredValueMissingException, InvalidOidSyntaxException {
		errHint.setErrorHint(OdmTagType.METHODDEF, "", "", "", "", "");
		try {
			reader.setTable(config.odmMethodTableName);

			Hashtable<String, String> hash = new Hashtable<String, String>();
			String prevMethodName = null;	//FormalExpression can be represented using multiple rows
			int methodNumber = 0;
			String str = null;
			String strAliasName[] = null;
			String strAliasContext[] = null;

			while ((hash = reader.read(config.odmMethodTableName)) != null) {
				errHint.setErrorHint(OdmTagType.METHODDEF, "", "", "", "", "");
				String methodId = hash.get("ID");
				if (StringUtils.isEmpty(methodId)) {
					methodId = hash.get("Method ID");
				}
				String methodName = hash.get("Name");
				if (StringUtils.isEmpty(methodName)) {
					methodName = hash.get("Method Name");
				}
				String methodType = hash.get("Type");
				if (StringUtils.isEmpty(methodType)) {
					methodType = hash.get("Method Type");
				}
				if (prevMethodName == null || !prevMethodName.equals(methodName)) {
					methodNumber++;
					if (prevMethodName != null) {
						if (strAliasName[0] != null && !strAliasName[0].equals("")) {
							for (int i2 = 0; i2 < strAliasName.length; i2++) {
								str = insertIndent(indent);
								str += "<Alias Context=\"" + strAliasContext[i2].trim()
										+ "\" Name=\"" + strAliasName[i2].trim()
										+ "\"/>";
								writer.write(str);
								writer.newLine();
							}
						}
						strAliasName = null;
						strAliasContext = null;
						
						indent--;
						str = insertIndent(indent);
						str += "</MethodDef>";
						writer.write(str);
						writer.newLine();
					}
					str = insertIndent(indent);
					str += "<MethodDef OID=\"" + createOID(OdmTagType.METHODDEF, "", "", "", "", methodId)
							+ "\" Name=\"" + methodName
							+ "\" Type=\"" + methodType
							+ "\">";
					writer.write(str);
					writer.newLine();
					indent++;
					
					str = insertIndent(indent);
					str += "<Description>";
					writer.write(str);
					writer.newLine();
					indent++;
						
					str = insertIndent(indent);
					str += "<TranslatedText"
							+ (StringUtils.isEmpty(hash.get("xml:lang")) ? "" : " xml:lang=\"" + hash.get("xml:lang") + "\"")
							+ ">"
							+ XmlGenerator.escapeString(hash.get("Description")) + "</TranslatedText>";
					writer.write(str);
					writer.newLine();

					indent--;
					str = insertIndent(indent);
					str += "</Description>";
					writer.write(str);
					writer.newLine();
					
					strAliasName = hash.get("Alias Name").split(DELIMITER);
					strAliasContext = hash.get("Alias Context").split(DELIMITER);
				}
				
				str = insertIndent(indent);
				str += "<FormalExpression Context=\"" + hash.get("Formal Expression Context") + "\">"
						+ XmlGenerator.escapeString(hash.get("Formal Expression")) + "</FormalExpression>";
				writer.write(str);
				writer.newLine();
				
				prevMethodName = methodName;
			}

			if (methodNumber > 0) {
				if (strAliasName[0] != null && !strAliasName[0].equals("")) {
					for (int i2 = 0; i2 < strAliasName.length; i2++) {
						str = insertIndent(indent);
						str += "<Alias Context=\"" + strAliasContext[i2].trim()
								+ "\" Name=\"" + strAliasName[i2].trim()
								+ "\"/>";
						writer.write(str);
						writer.newLine();
					}
				}
				strAliasName = null;
				strAliasContext = null;

				indent--;
				str = insertIndent(indent);
				str += "</MethodDef>";
				writer.write(str);
				writer.newLine();
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		} catch  (TableNotFoundException ex) {
			logger.info("No MethodDef section found. Skipping...");
		}
	}
	
	public void writeEndTag(String tag) throws IOException {
		indent--;
		String str = insertIndent(indent);
		str += "</" + tag + ">";
		writer.write(str);
		writer.newLine();
	}

	public void writeXmlComment(String comment) throws IOException {
		String str = insertIndent(indent);
		str += "<!-- " + comment + " -->";
		writer.write(str);
		writer.newLine();
	}

	public void close() throws IOException {
		reader.close();
		writer.close();
		sw.close();
	}

	private String createOID(OdmTagType odmTagType, String eventId, String formId, String groupId, String fieldId, String param) throws InvalidOidSyntaxException, RequiredValueMissingException {
		try {
			String str = null;
			if (this.oidMode.equals(Config.OidMode.EXACT)) {
				switch (odmTagType) {
				case MEASUREMENTUNIT: case MEASUREMENTUNITREF:
					if (!param.equals("")) {
						str = param;
					} else {
						throw new InvalidOidSyntaxException("MeasurementUnit/MeasurementUnitRef");
					}
					break;
				case STUDYEVENTDEF: case STUDYEVENTREF:
					if (!eventId.equals("")) {
						str = eventId;
					} else {
						throw new InvalidOidSyntaxException("StudyEventDef/StudyEventRef");
					}
					break;
				case FORMDEF: case FORMREF:
					if (!formId.equals("")) {
						str = formId;
					} else {
						throw new InvalidOidSyntaxException("FormDef/FormRef");
					}
					break;
				case ITEMGROUPDEF: case ITEMGROUPREF:
					if (!groupId.equals("")) {
						str = groupId;
					} else {
						throw new InvalidOidSyntaxException("ItemGroupDef/ItemGroupRef");
					}
					break;
				case ITEMDEF: case ITEMREF:
					if (!fieldId.equals("")) {
						str = fieldId;
					} else {
						throw new InvalidOidSyntaxException("ItemDef/ItemRef");
					}
					break;
				case CODELIST: case CODELISTREF:
					if (!param.equals("")) {
						str = param;
					} else {
						throw new InvalidOidSyntaxException("Codelist/CodelistRef");
					}
					break;
				case METHODDEF:
					if (!param.equals("")) {
						str = param;
					} else {
						throw new InvalidOidSyntaxException("MethodDef/MethodRef");
					}
					break;
				case CONDITIONDEF:
					if (!param.equals("")) {
						str = param;
					} else {
						throw new InvalidOidSyntaxException("ConditionDef/ConditionRef");
					}
					break;
				case ARCHIVELAYOUT:
					if (!param.equals("")) {
						str = param;
					} else {
						throw new InvalidOidSyntaxException("ArchiveLayout");
					}
					break;
				default:
					throw new InvalidOidSyntaxException("Unknown");
				}
			} else {
				switch (odmTagType) {
				case MEASUREMENTUNIT: case MEASUREMENTUNITREF:
					if (!param.equals("")) {
						str = "MU." + param;
					} else {
						throw new InvalidOidSyntaxException("MeasurementUnit/MeasurementUnitRef");
					}
					break;
				case STUDYEVENTDEF: case STUDYEVENTREF:
					if (!eventId.equals("")) {
						str = "EV." + eventId;
					} else {
						throw new InvalidOidSyntaxException("StudyEventDef/StudyEventRef");
					}
					break;
				case FORMDEF: case FORMREF:
					if (!formId.equals("")) {
						str = "FR." + formId;
					} else {
						throw new InvalidOidSyntaxException("FormDef/FormRef");
					}
					break;
				case ITEMGROUPDEF: case ITEMGROUPREF:
					if (!groupId.equals("")) {
						str = "IG." + groupId;
					} else {
						throw new InvalidOidSyntaxException("ItemGroupDef/ItemGroupRef");
					}
					break;
				case ITEMDEF: case ITEMREF:
					if (!fieldId.equals("")) {
						str = "IT." + fieldId;
					} else {
						throw new InvalidOidSyntaxException("ItemDef/ItemRef");
					}
					break;
				case CODELIST: case CODELISTREF:
					if (!param.equals("")) {
						str = "CL." + param;
					} else {
						throw new InvalidOidSyntaxException("Codelist/CodelistRef");
					}
					break;
				case METHODDEF:
					if (!param.equals("")) {
						str = "MT." + param;
					} else {
						throw new InvalidOidSyntaxException("MethodDef/MethodRef");
					}
					break;
				case CONDITIONDEF:
					if (!param.equals("")) {
						str = "CD." + param;
					} else {
						throw new InvalidOidSyntaxException("ConditionDef/ConditionRef");
					}
					break;
				case ARCHIVELAYOUT:
					if (!param.equals("")) {
						str = "LF." + param;
					} else {
						throw new InvalidOidSyntaxException("ArchiveLayout");
					}
					break;
				default:
					throw new InvalidOidSyntaxException("Unknown");
				}
			}
			return str;
		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	/*
	 * This method returns spaces based on the current indent.
	 */
	private static String insertIndent(int indent) {
		String str = "";
		for (int i = 0; i < indent; i++) {
			str += "  ";
		}
		return str;
	}

	/*
	 * This method formats a component of date/time value into two digits.
	 */
	private static String twoDigits(int n) {
		if (n >= 0 && n <= 9) {
			return new String("0" + new Integer(n).toString());
		} else {
			return new Integer(n).toString();
		}
	}
}
