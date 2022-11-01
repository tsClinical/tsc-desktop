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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.fujitsu.tsc.desktop.exporter.SuppUtil.IsEmpty;
import com.fujitsu.tsc.desktop.exporter.SuppUtil.Origin;
import com.fujitsu.tsc.desktop.exporter.SuppUtil.SuppDataset;
import com.fujitsu.tsc.desktop.exporter.SuppUtil.Variable;
import com.fujitsu.tsc.desktop.exporter.WhereClause.Operator;
import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.InvalidParameterException;
import com.fujitsu.tsc.desktop.util.MetaDataReader;
import com.fujitsu.tsc.desktop.util.Config.DatasetType;

public class DefineXmlWriter {

	private static Logger logger;
	private Config config;
	private MetaDataReader reader;
	private MetaDataReader reader2;
	private OutputStreamWriter sw;
	private BufferedWriter writer;
	private String xmlEncoding;
	private String stylesheetLocation;
	private Config.DatasetType datasetType;
	private int indent;		// Controls text indents in the output define.xml.
	private String strAnnotatedCRF;	// Name of the first Annotated CRF
	private String domainKey;	//"Domain" is the key for SEND/SDTM while "Dataset Name" is the key for ADaM.
	private String wDomainKey;	//"W Domain" is the key for SEND/SDTM while "W Dataset Name" is the key for ADaM.
	private final String DELIMITER;
	private final String DEFAULTLANG = "en";
	private Hashtable<String,String> hashUSUBJID; // A list of USUBJIDs in parent datasets of SUPP--.
	private List<String> emptyDatasets;	// A list of datasets where Is Empty is Yes
	private List<String> emptyCodelists;	// A list of codelists referenced from empty datasets only
	private SuppUtil suppUtil;

	public enum TagType {
		DEFAULT, XMLHEADER, STUDY,
		ITEMGROUPDEF, ITEMDEF, ITEMREF, VALUELISTDEF, VALUELISTREF, WHERECLAUSEDEF, WHERECLAUSEREF,
		CODELIST, CODELISTREF, METHODDEF, COMMENTDEF, LEAF, DOCUMENTREF, RESULTDISPLAY, RESULT
	}

	/* This class will be used to display error hints when a RequiredValueMissingException occurs. */
	protected class ErrorHint {
		protected TagType tagType = TagType.DEFAULT;
		protected String domainKey = "";
		protected String variableName = "";
		protected String valueKey = "";
		protected String param = "";
		protected ErrorHint() { /* An empty constructor. */ };
		protected void setErrorHint(TagType tagType, String domainKey, String variableName, String valueKey, String param) {
			this.tagType = tagType;
			if (domainKey == null) { this.domainKey = "null"; } else { this.domainKey = domainKey; }
			if (variableName == null) { this.variableName = "null"; } else { this.variableName = variableName; }
			if (valueKey == null) { this.valueKey = "null"; } else { this.valueKey = valueKey; }
			if (param == null) { this.param = "null"; } else { this.param = param; }
		}
	}
	private ErrorHint errHint = new ErrorHint();

	public DefineXmlWriter (Config config)
			throws InvalidParameterException, TableNotFoundException, FileNotFoundException, IOException, RequiredValueMissingException, InvalidFormatException {
		logger = Logger.getLogger("com.fujitsu.tsc.desktop");
		this.config = config;
		DELIMITER = config.valueDelimiter;
		this.hashUSUBJID = new Hashtable<String, String>();
		this.emptyDatasets = new ArrayList<String>();
		this.emptyCodelists = new ArrayList<String>();

//		try {
			if (this.config.dataSourceType.name().equals("EXCEL")) {
				reader = new ExcelReader(this.config.e2dDataSourceLocation, this.config.defineDatasetTableName);
				reader2 = new ExcelReader(this.config.e2dDataSourceLocation, this.config.defineVariableTableName);
			} else {
				throw new InvalidParameterException("dataSourceType", this.config.dataSourceType.name());
			}

			this.datasetType = this.config.e2dDatasetType;
			if (this.datasetType.equals(Config.DatasetType.ADaM)) {
				this.domainKey = "Dataset Name";
				this.wDomainKey = "W Dataset Name";
			} else {
				this.domainKey = "Domain";
				this.wDomainKey = "W Domain";
			}
			this.xmlEncoding = this.config.e2dXmlEncoding;
			this.stylesheetLocation = this.config.e2dStylesheetLocation;
			sw = new OutputStreamWriter(new FileOutputStream(this.config.e2dOutputLocation, false), xmlEncoding);
			writer = new BufferedWriter(sw);

			indent = 0;

			/* Identify empty datasets */
			Hashtable<String, String> hash;
			List<String> emptySuppDatasets = new ArrayList<>();
			while ((hash = reader.read()) != null) {
				String dataset_name = hash.get("Dataset Name");
				IsEmpty isEmpty = IsEmpty.parse(hash.get("Is Empty"));
				if (isEmpty == IsEmpty.Yes) {
					this.emptyDatasets.add(dataset_name);
				} else if (isEmpty == IsEmpty.SUPP) {
					emptySuppDatasets.add(dataset_name);
				}
			}
			reader.close();
			/* Identify empty codelists */
			List<String> nonEmptyCodelists = new ArrayList<>();
			while ((hash = reader2.read()) != null) {
				String dataset_name = hash.get("Dataset Name");
				String codelist_id = hash.get("Codelist");
				if (!StringUtils.isEmpty(codelist_id)) {
					if (this.emptyDatasets.contains(dataset_name)) {
						this.emptyCodelists.add(codelist_id);
					} else if (emptySuppDatasets.contains(dataset_name) && "Yes".equals(hash.get("Is SUPP"))) {
						this.emptyCodelists.add(codelist_id);
					} else {
						nonEmptyCodelists.add(codelist_id);
					}
				}
			}
			Iterator<String> codelist_itr = emptyCodelists.iterator();
			while (codelist_itr.hasNext()) {
				String codelist_id = codelist_itr.next();
				if (nonEmptyCodelists.contains(codelist_id)) {
					codelist_itr.remove();	//Remove non-empty codelists from empty codelists
				}
			}
			reader2.close();
			
// Initialize the SuppUtil object for later use - this does not apply to ADaM since ADaM does not have SUPP--.
			if (!this.datasetType.equals(Config.DatasetType.ADaM)) {
				reader.setTable(config.defineDatasetTableName);
				reader2.setTable(config.defineVariableTableName);
				suppUtil = new SuppUtil(reader, reader2);
				reader.close();
				reader2.close();
			}

//		} catch (NullPointerException ex) {
//			throw new RequiredValueMissingException(ex, errHint);
//		}
	}

	public void writeXMLHeader() throws IOException, RequiredValueMissingException {
		errHint.setErrorHint(TagType.XMLHEADER, "", "", "", "");
		try {
			String str = "<?xml version=\"1.0\" encoding=\""
					+ xmlEncoding
					+ "\"?>";
			writer.write(str);
			writer.newLine();

			str = "<?xml-stylesheet type=\"text/xsl\" href=\""
					+ stylesheetLocation
					+ "\"?>";
			writer.write(str);
			writer.newLine();
		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	public void writeStudySection() throws TableNotFoundException, IOException, RequiredValueMissingException {
		errHint.setErrorHint(TagType.STUDY, "", "", "", "");
		try {
			reader.setTable(config.defineStudyTableName);

			Hashtable<String, String> hash = new Hashtable<String, String>();
			// Transpose the normalized STUDY table and store into a new denormalized Hashtable.
			Hashtable<String, String> hashNormalized;
			while ((hashNormalized = reader.read()) != null) {
				hash.put(hashNormalized.get("Property Name"), hashNormalized.get("Property Value"));
			}

			Calendar cal = Calendar.getInstance();
			String str = insertIndent(indent);
			str += "<ODM xmlns=\"http://www.cdisc.org/ns/odm/v1.3\""
					+ " xmlns:xlink=\"http://www.w3.org/1999/xlink\""
					+ " xmlns:def=\"http://www.cdisc.org/ns/def/v2.0\""
					+ (this.datasetType.equals(Config.DatasetType.ADaM) &&
							this.config.e2dIncludeResultMetadata == true ?
							" xmlns:arm=\"http://www.cdisc.org/ns/arm/v1.0\"" : "")
					+ " ODMVersion=\"" + (hash.get("ODMVersion").equals("") ? config.defineOdmVersion : hash.get("ODMVersion"))
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
					+ "\">";
			writer.write(str);
			writer.newLine();
			indent++;

			str = insertIndent(indent);
			str += "<Study OID=\"" + hash.get("StudyOID") + "\">";
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

			str = insertIndent(indent);
			str += "<MetaDataVersion OID=\"" + hash.get("MetaDataOID")
					+ "\" Name=\"" + XmlGenerator.escapeString(hash.get("MetaDataName"))
					+ "\" Description=\"" + XmlGenerator.escapeString(hash.get("MetaDataDescription"))
					+ "\" def:DefineVersion=\"" + hash.get("DefineVersion")
					+ "\" def:StandardName=\"" + hash.get("StandardName")
					+ "\" def:StandardVersion=\"" + hash.get("StandardVersion")
					+ "\">";
			writer.write(str);
			writer.newLine();
			indent++;

			reader.close();
		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	public void writeDocumentSection(String docType) throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		errHint.setErrorHint(TagType.DOCUMENTREF, "", "", "", "");
		try {
			reader.setTable(
					this.config.defineDocumentTableName,
					new WhereClause[] { new WhereClause("Type", WhereClause.Operator.EQ, docType) });

			Hashtable<String, String> hash = new Hashtable<String, String>();
			String str = null;

			str = insertIndent(indent);
			str += "<def:" + docType + ">";
			writer.write(str);
			writer.newLine();
			indent++;

			while ((hash = reader.read()) != null) {
				str = insertIndent(indent);
				str += "<def:DocumentRef leafID=\"" + createOID(TagType.DOCUMENTREF, "", "", "", "", hash.get("ID")) + "\"/>";
				writer.write(str);
				writer.newLine();
				/* Retain AnnotatedCRF ID for use when generating ItemDef section */
				if (docType.equals("AnnotatedCRF") && strAnnotatedCRF == null) {
					strAnnotatedCRF = hash.get("ID");
				}
			}

			indent--;
			str = insertIndent(indent);
			str += "</def:" + docType + ">";
			writer.write(str);
			writer.newLine();

			reader.close();
		} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	/**
	 * This method creates ItemGroupDef from the DATASET sheet excluding empty datasets.
	 * It calls writeVariableRefs() to create ItemRef (including TS.TSVALn and CO.COVALn) tags from the VARIABLE sheet.
	 * It also calls writeSuppItemGroupDefSection() that creates ItemGroupDef tags including ItemRef tags for SUPP datasets,
	 * if the source spreadsheet is in the Auto-SUPP format (i.e. the DATASET sheet has "Has SUPP" column.)
	 * 
	 * @throws InvalidParameterException
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	public void writeItemGroupDefSection() throws InvalidParameterException, TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {
			reader.setTable(this.config.defineDatasetTableName);

			Hashtable<String, String> hash = new Hashtable<String, String>();
			Hashtable<String, String> hash2 = new Hashtable<String, String>();		//Used to retain a row from variable-level metadata
			String str = null;

			while ((hash = reader.read()) != null) {
				String dataset_name = hash.get("Dataset Name");
				errHint.setErrorHint(TagType.ITEMGROUPDEF, dataset_name, "", "", "");
				/* Skip if the dataset is empty. */
				if (this.emptyDatasets.contains(dataset_name)) {
					continue;
				}
				
				str = insertIndent(indent);
				str += "<ItemGroupDef OID=\"" + createOID(TagType.ITEMGROUPDEF, hash.get("Dataset Name"), "", "", "", "")	//OID should include dataset name, not domain, considering split datasets.
						+ (this.datasetType.equals(Config.DatasetType.ADaM) ?
								"" : "\" Domain=\"" + hash.get("Domain"))
						+ "\" Name=\"" + hash.get("Dataset Name")
						+ "\" Repeating=\"" + hash.get("Repeating")
						+ "\" IsReferenceData=\"" + hash.get("IsReferenceData")
						+ "\" SASDatasetName=\"" + hash.get("Dataset Name")
						+ "\" Purpose=\"" + hash.get("Purpose")
						+ "\" def:Structure=\"" + XmlGenerator.escapeString(hash.get("Structure"))
						+ "\" def:Class=\"" + (hash.get("Class").equals("FINDINGS ABOUT") ? "FINDINGS" : hash.get("Class"))
						+ ((hash.get("Comment").equals("")) ? "" :
							"\" def:CommentOID=\"" + createOID(TagType.COMMENTDEF, hash.get("Dataset Name"), "", "", "", ""))	//OID should include dataset name, not domain, considering split datasets.
						+ "\" def:ArchiveLocationID=\"" + createOID(TagType.LEAF, "", "", "", "", hash.get("Dataset Name"))	//OID should include dataset name, not domain, considering split datasets.
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
				str += "<TranslatedText xml:lang=\"en\">"
						+((hash.containsKey("TranslatedText"))?
						    XmlGenerator.escapeString(hash.get("TranslatedText")):
						  (hash.containsKey("Description"))?
					        XmlGenerator.escapeString(hash.get("Description")):"")
				        + "</TranslatedText>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</Description>";
				writer.write(str);
				writer.newLine();

				reader2.setTable(this.config.defineVariableTableName,
						new WhereClause[] { new WhereClause(domainKey, WhereClause.Operator.EQ, hash.get(domainKey)) });
				int orderNumber = 1;		//Used for the OrderNumber attribute of the ItemRef tag
				while ((hash2 = reader2.read()) != null) {
					writeVariableRefs(hash2, hash.get("Dataset Name"), orderNumber++);
				}

				if (!this.datasetType.equals(Config.DatasetType.ADaM) &&
						hash.get("Alias") != null  && !hash.get("Alias").equals("")) {
					str = insertIndent(indent);
					str += "<Alias Context=\"DomainDescription\" Name=\""
							+ XmlGenerator.escapeString(hash.get("Alias"))
							+ "\"/>";
					writer.write(str);
					writer.newLine();
				}

				str = insertIndent(indent);
				str += "<def:leaf ID=\""
						+ createOID(TagType.LEAF, "", "", "", "", hash.get("Dataset Name"))	//OID should include dataset name, not domain, considering split datasets.
						+ "\" xlink:href=\"" + hash.get("href")
						+ "\">";
				writer.write(str);
				writer.newLine();
				indent++;

				str = insertIndent(indent);
				str += "<def:title>" + XmlGenerator.escapeString(hash.get("Title"))
						+ "</def:title>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</def:leaf>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</ItemGroupDef>";
				writer.write(str);
				writer.newLine();

				reader2.close();

				reader.close();
			}

			if(suppUtil.isAutoSuppActive()){
				writeSuppItemGroupDefSection();
			}

		} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}


	/** 
	 * This method creates ItemGroupDefs and ItemRefs for SUPP-- datasets.
	 * 
	 * @throws InvalidParameterException
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	public void writeSuppItemGroupDefSection() throws InvalidParameterException, TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {
			String str = null;

			for(SuppDataset dataset : this.suppUtil.listSuppDatasets()){
				String name = dataset.dataset_name;
				errHint.setErrorHint(TagType.ITEMGROUPDEF, "SUPP"+name, "", "", "");

				str = insertIndent(indent);
				str += "<ItemGroupDef OID=\"" + createOID(TagType.ITEMGROUPDEF, "SUPP"+name, "", "", "", "")	//OID should include dataset name, not domain, considering split datasets.
						+ (this.datasetType.equals(Config.DatasetType.ADaM) ? "" : "\" Domain=\"" + dataset.domain)
						+ "\" Name=\"" + "SUPP"+name
						+ "\" Repeating=\""+"Yes"
						+ "\" IsReferenceData=\""+"No"
						+ "\" SASDatasetName=\""+ "SUPP"+name
						+ "\" Purpose=\"" + "Tabulation"
						+ "\" def:Structure=\"" + "One record per IDVAR, IDVARVAL, and QNAM value per subject"
						+ "\" def:Class=\"" + "RELATIONSHIP"
						+ "\" def:ArchiveLocationID=\"" + createOID(TagType.LEAF, "", "", "", "", "SUPP"+name)	//OID should include dataset name, not domain, considering split datasets.
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
				str += "<TranslatedText xml:lang=\"en\">"
						+"Supplemental Qualifiers for "+name
						+ "</TranslatedText>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</Description>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str += "<ItemRef ItemOID=\"" + createOID(TagType.ITEMREF, "SUPP"+name, "SUPP"+name, "STUDYID", "", "")
						+ "\" OrderNumber=\""+"1"
						+ "\" Mandatory=\""+"Yes"
						+ "\" KeySequence=\""+"1"
						+ "\"/>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str +="<ItemRef ItemOID=\"" + createOID(TagType.ITEMREF, "SUPP"+name, "SUPP"+name, "RDOMAIN", "", "")
						+ "\" OrderNumber=\""+"2"
						+ "\" Mandatory=\""+"Yes"
						+ "\" KeySequence=\""+"2"
//						+ "\" MethodOID=\"" + createOID(TagType.METHODDEF, "SUPP"+name, "SUPP"+name, "RDOMAIN", "", "")
						+ "\"/>";

				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str +="<ItemRef ItemOID=\"" + createOID(TagType.ITEMREF, "SUPP"+name, "SUPP"+name, "USUBJID", "", "")
						+ "\" OrderNumber=\""+"3"
						+ "\" Mandatory=\""+"Yes"
						+ "\" KeySequence=\""+"3"
	                   // Get MethodOID of USUBJID from its parent domain.
						+(this.hashUSUBJID.containsKey(name)? "\" MethodOID=\"" + hashUSUBJID.get(name):"")
						+ "\"/>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str +="<ItemRef ItemOID=\"" + createOID(TagType.ITEMREF, "SUPP"+name, "SUPP"+name, "IDVAR", "", "")
						+ "\" OrderNumber=\""+"4"
						+ "\" Mandatory=\""+"No"
						+ "\" KeySequence=\""+"4"
						+ "\"/>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str +="<ItemRef ItemOID=\"" + createOID(TagType.ITEMREF, "SUPP"+name, "SUPP"+name, "IDVARVAL", "", "")
						+ "\" OrderNumber=\""+"5"
						+ "\" Mandatory=\""+"No"
						+ "\" KeySequence=\""+"5"
						+ "\"/>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str +="<ItemRef ItemOID=\"" + createOID(TagType.ITEMREF, "SUPP"+name, "SUPP"+name, "QNAM", "", "")
						+ "\" OrderNumber=\""+"6"
						+ "\" Mandatory=\""+"Yes"
						+ "\" KeySequence=\""+"6"
						+ "\"/>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str +="<ItemRef ItemOID=\"" + createOID(TagType.ITEMREF, "SUPP"+name, "SUPP"+name, "QLABEL", "", "")
						+ "\" OrderNumber=\""+"7"
						+ "\" Mandatory=\""+"Yes"
						+ "\"/>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str +="<ItemRef ItemOID=\"" + createOID(TagType.ITEMREF, "SUPP"+name, "SUPP"+name, "QVAL", "", "")
						+ "\" OrderNumber=\""+"8"
						+ "\" Mandatory=\""+"Yes"
						+ "\"/>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str +="<ItemRef ItemOID=\"" + createOID(TagType.ITEMREF, "SUPP"+name, "SUPP"+name, "QORIG", "", "")
						+ "\" OrderNumber=\""+"9"
						+ "\" Mandatory=\""+"Yes"
						+ "\"/>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str +="<ItemRef ItemOID=\"" + createOID(TagType.ITEMREF, "SUPP"+name, "SUPP"+name, "QEVAL", "", "")
						+ "\" OrderNumber=\""+"10"
						+ "\" Mandatory=\""+"No"
						+ "\"/>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str += "<def:leaf ID=\""
						+ createOID(TagType.LEAF, "", "", "", "", "SUPP"+name)	//OID should include dataset name, not domain, considering split datasets.
						+ "\" xlink:href=\"" + "supp"+name.toLowerCase()+".xpt"
						+ "\">";
				writer.write(str);
				writer.newLine();
				indent++;

				str = insertIndent(indent);
				str += "<def:title>" + "supp"+name.toLowerCase()+".xpt"
						+ "</def:title>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</def:leaf>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</ItemGroupDef>";
				writer.write(str);
				writer.newLine();
			}
		} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	/**
	 * This method creates ItemRefs (including TSVALn and COVALn) under ItemGroupDef from the VARIABLE sheet.
	 * Note that TSVALn/COVALn should reference the same MethodOID as that of TSVAL/COVAL.
	 * @param hash
	 * @param dataset
	 * @param orderNumber
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	private void writeVariableRefs(Hashtable<String, String> hash, String dataset, int orderNumber) throws IOException, InvalidOidSyntaxException, RequiredValueMissingException {

		errHint.setErrorHint(TagType.ITEMREF, hash.get("Dataset Name"), hash.get("Variable Name"), "", "");
		try {
			
			/* Read "Repeat N" */
			int N = 0;
			String variable_name = hash.get("Variable Name");
			String strN = hash.get("Repeat N");
			if (!StringUtils.isEmpty(strN)) {
				try {
					N = Integer.parseInt(strN);
					if (N > 99) {	//0 < N < 100
						N = 0;
					}
				} catch (NumberFormatException ex) {
					//Do nothing.
				}
			}
			/* Loop 1 + N times */
			for (int i = 0; i <= N; i++) {
				/* Is SUPP = "No" for TSVAL or COVAL, and "Yes" for other Repeat N variables */
				String is_supp = hash.get("Is SUPP");
				if (!StringUtils.isEmpty(is_supp)) {
					if (N > 0 && !"TSVAL".equals(variable_name) && !"COVAL".equals(variable_name)) {
						is_supp = "Yes";
					}
				}
				/* Variable Name += N */
				if (!StringUtils.isEmpty(variable_name)) {
					if (variable_name.length() >= 8 && 1 <= N && N < 10) {
						variable_name = variable_name.substring(0, 7) + N;
					} else if (variable_name.length() == 7 && 10 <= N && N < 100) {
						variable_name = variable_name.substring(0, 6) + N;
					} else {
						variable_name = variable_name + N;
					}
				}
				/* Write ItemRefs for standard variables only. */
				if (is_supp == null || !is_supp.equals("Yes")) {
	
					String str = insertIndent(indent);
					str += "<ItemRef ItemOID=\"" + createOID(TagType.ITEMREF, hash.get(domainKey), hash.get("Dataset Name"), variable_name, "", "");
					str += "\"" + " OrderNumber=\"" + new Integer(orderNumber).toString() + "\"";
					if (hash.get("Mandatory").equals("Yes")) {
						str += " Mandatory=\"Yes\"";
					} else {
						str += " Mandatory=\"No\"";
					}
					if (hash.get("Key Sequence") != null && !hash.get("Key Sequence").equals("")) {
						str += " KeySequence=\"" + hash.get("Key Sequence") + "\"";
					} else {
						//Do nothing.
					}
					if (hash.get("Origin").equals("Derived")) {
						str += " MethodOID=\"" + createOID(TagType.METHODDEF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), "", "") + "\"";
					//Changed for v1.2.0-add Has SUPP
						if(hash.get("Variable Name").equals("USUBJID")){
							this.hashUSUBJID.put(hash.get("Dataset Name"),createOID(TagType.METHODDEF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), "", ""));
						}
					} else {
						//Do nothing.
					}
					if (hash.get("Role") != null && !hash.get("Role").equals("")){
						str += " Role=\"" + hash.get("Role") + "\"";
						if (hash.get("Role Codelist") != null && !hash.get("Role Codelist").equals("")) {
						str += " RoleCodeListOID=\"" + createOID(TagType.CODELISTREF, "", "", "", "", formatCodeListID(hash.get("Role Codelist"))) + "\"";
						}else if (hash.get("Role codelist") != null && !hash.get("Role codelist").equals("")) {
							str += " RoleCodeListOID=\"" + createOID(TagType.CODELISTREF, "", "", "", "", formatCodeListID(hash.get("Role codelist"))) + "\"";
						}
					} else {
						//Do nothing.
					}
					str += "/>";
	
					writer.write(str);
					writer.newLine();
				}
			}
		} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	/**
	 * This method creates ItemDefs from the VARIABLE and VALUE sheets.
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	public void writeItemDefSection() throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		reader.setTable(
				this.config.defineVariableTableName,
				new WhereClause[] { new WhereClause(domainKey, WhereClause.Operator.NE, "")});

		/*
		 * Generate ItemDef tags for variable-level metadata (including variables where Is SUPP=Yes)
		 */
		writeItemDefSection(reader);

		/*
		 * Generate ItemDef tags for value-level metadata
		 */
		reader.setTable(this.config.defineValueTableName,
				new WhereClause[] { new WhereClause("Value Key", WhereClause.Operator.NE, "")});
		HashSet<String> uniqueKeys = new HashSet<>();
		Collections.addAll(uniqueKeys, "Dataset Name", "Variable Name", "Value Key");
		reader.setUniqueKeys(reader.getTableName(), uniqueKeys);
		writeItemDefSection(reader);

		/*
		 * Generate ItemDef tags for variable-level metadata of supp
		 */
		if (this.suppUtil.isAutoSuppActive()) {
			writeSuppItemDefSection();
		}

		reader.close();
	}

	/**
	 * This method creates ItemDefs from the VARIABLE or VALUE sheets as given by the reader.
	 * This method also creates value-level ItemDef for Is SUPP and Repeat N variables when the VARIABLE sheet is given.
	 * Note that Repeat N for TSVAL/COVAL is considered Is SUPP=No while Repeat N for others are Is SUPP=Yes.
	 * This method skips processing for empty datasets (i.e. where Is Empty = Yes/SUPP).
	 * @param reader
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	private void writeItemDefSection(MetaDataReader reader) throws IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {
			Hashtable<String, String> hash = new Hashtable<String, String>();
			String str = null;

			while ((hash = reader.read()) != null) {
				String dataset_name = hash.get("Dataset Name");
				String variable_name = hash.get("Variable Name");
				errHint.setErrorHint(TagType.ITEMDEF, hash.get(domainKey), variable_name, hash.get("Value Key"), "");
				/* Skip if the dataset is empty (Is Empty=Yes). */
				if (this.emptyDatasets.contains(dataset_name)) {
					continue;
				}
				/* Skip if this is value-level ItemDef for SUPP (Variables in SuppUtil) and the SUPP is empty (not in Datasets in SuppUtil). */
				Set<String> supp_dataset_names = this.suppUtil.getSuppDatasetNames();
				Set<String> supp_variables = this.suppUtil.getSuppVariableNames();
				if (supp_variables.contains(dataset_name + "/" + variable_name) && !supp_dataset_names.contains(dataset_name)) {
					continue;
				}

				/* Read "Repeat N" */
				int N = SuppUtil.parseRepeatN(hash.get("Repeat N"));
				N = (supp_dataset_names.contains(dataset_name) ? N : 0);	//Do not create RACEn when SUPP is empty
				/* Loop 1 + N times */
				for (int i = 0; i <= N; i++) {	
					String r_is_supp = SuppUtil.getRepeatIsSupp(variable_name, hash.get("Is SUPP"), N);
					String r_variable_name = SuppUtil.getRepeatVariableName(variable_name, N);
					String r_variable_label = SuppUtil.getRepeatVariableLabel(hash.get("Label"), N);
					String r_length = SuppUtil.getRepeatLength(hash.get("Length"), N);
					String r_sas_field_name = SuppUtil.getRepeatSasFieldName(r_variable_name, hash.get("SASFieldName"), N);
					
					/* STUDYID and USUBJID of SDTM should be written only once */
					if (!((this.datasetType.equals(Config.DatasetType.SDTM) || this.datasetType.equals(Config.DatasetType.SEND)) &&
						!hash.get("Domain").equals("DM") &&
						(hash.get("Variable Name").equals("STUDYID") || hash.get("Variable Name").equals("USUBJID")))) {

						str = insertIndent(indent);
						str += "<ItemDef OID=\"";
						/* If the ItemDef tag is for a variable */
						if (reader.getTableName().equals(this.config.defineVariableTableName)) {
							/* if the ItemDef tag is for a supp value*/
							if (r_is_supp != null && r_is_supp.equals("Yes")) {
								str += createOID(TagType.ITEMDEF, "SUPP"+hash.get("Dataset Name"), "SUPP"+hash.get("Dataset Name"),"QVAL", variable_name, "")
										+ "\" Name=\"" + variable_name;
							} else {
								str += createOID(TagType.ITEMDEF, hash.get(domainKey), hash.get("Dataset Name"), variable_name, "", "")
										+ "\" Name=\"" + variable_name;
							}
							/* If the ItemDef tag is for a value */
						} else if (reader.getTableName().equals(this.config.defineValueTableName)) {
							str += createOID(TagType.ITEMDEF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), hash.get("Value Key"), "")
									+ "\" Name=\"" + hash.get("Value Name");
						} else {
							//Do nothing.
						}
						str += "\" DataType=\"" + hash.get("DataType")
								+ (r_length == null || r_length.equals("") ?
										"" : "\" Length=\"" + r_length)
										+ (hash.get("SignificantDigits") == null || hash.get("SignificantDigits").equals("") ?
												"" : "\" SignificantDigits=\"" + hash.get("SignificantDigits"));
						if (reader.getTableName().equals(this.config.defineVariableTableName)) {
							str += "\" SASFieldName=\""
									+ (r_sas_field_name == null || r_sas_field_name.equals("") ?
											variable_name : r_sas_field_name);
						} else if (reader.getTableName().equals(this.config.defineValueTableName)) {
							str += "\" SASFieldName=\""
									+ (hash.get("SASFieldName") == null || hash.get("SASFieldName").equals("") ?
											hash.get("Value Name") : hash.get("SASFieldName"));
						} else {
							//Do nothing.
						}
						str	+= (hash.get("DisplayFormat") == null || hash.get("DisplayFormat").equals("") ?
								"" : "\" def:DisplayFormat=\"" + hash.get("DisplayFormat"));
						if (hash.get("Comment") != null && !hash.get("Comment").equals("")) {
							/* If the ItemDef tag is for a variable */
							if (reader.getTableName().equals(this.config.defineVariableTableName)) {
								/* if the ItemDef tag is for a supp value*/
								if (hash.get("Is SUPP") != null && hash.get("Is SUPP").equals("Yes")) {
									str += "\" def:CommentOID=\"" + createOID(TagType.COMMENTDEF, "SUPP"+hash.get("Dataset Name"), "SUPP"+hash.get("Dataset Name"), "QVAL", hash.get("Variable Name"), "");
								} else {
									str += "\" def:CommentOID=\"" + createOID(TagType.COMMENTDEF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), "", "");
								}
									/* If the ItemDef tag is for a value */
							} else if (reader.getTableName().equals(this.config.defineValueTableName)) {
								str += "\" def:CommentOID=\"" + createOID(TagType.COMMENTDEF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), hash.get("Value Key"), "");
							} else {
								//Do nothing.
							}
						}
						str += "\">";
						writer.write(str);
						writer.newLine();
						indent++;

						str = insertIndent(indent);
						str += "<Description>";
						writer.write(str);
						writer.newLine();
						indent++;

						str = insertIndent(indent);
						str += "<TranslatedText xml:lang=\"en\">"
								+ XmlGenerator.escapeString(r_variable_label)
								+ "</TranslatedText>";
						writer.write(str);
						writer.newLine();

						indent--;
						str = insertIndent(indent);
						str += "</Description>";
						writer.write(str);
						writer.newLine();

						if (hash.get("Codelist") != null && !hash.get("Codelist").equals("")) {
							str = insertIndent(indent);
							str += "<CodeListRef CodeListOID=\""
									+ createOID(TagType.CODELISTREF, "", "", "", "", formatCodeListID(hash.get("Codelist")))
									+ "\"/>";
							writer.write(str);
							writer.newLine();
						}

						if (hash.get("Origin") != null && hash.get("Origin").equals("CRF")) {
							str = insertIndent(indent);
							str += "<def:Origin Type=\"CRF\">";
							writer.write(str);
							writer.newLine();
							indent++;

							str = insertIndent(indent);
							str += "<def:DocumentRef leafID=\""
									+ createOID(TagType.LEAF, "", "", "", "",
											hash.get("CRF ID") != null && !hash.get("CRF ID").equals("") ? hash.get("CRF ID") : strAnnotatedCRF)
											+ "\">";
							writer.write(str);
							writer.newLine();
							indent++;

							str = insertIndent(indent);
							str += "<def:PDFPageRef PageRefs=\"" + hash.get("CRF Page Reference")
									+ "\" Type=\"" + hash.get("CRF Page Type") + "\"/>";
							writer.write(str);
							writer.newLine();

							indent--;
							str = insertIndent(indent);
							str += "</def:DocumentRef>";
							writer.write(str);
							writer.newLine();

							indent--;
							str = insertIndent(indent);
							str += "</def:Origin>";
							writer.write(str);
							writer.newLine();

						} else if (hash.get("Origin") != null && hash.get("Origin").equals("Predecessor")) {
							str = insertIndent(indent);
							str += "<def:Origin Type=\"Predecessor\">";
							writer.write(str);
							writer.newLine();
							indent++;

							str = insertIndent(indent);
							str += "<Description>";
							writer.write(str);
							writer.newLine();
							indent++;

							str = insertIndent(indent);
							str += "<TranslatedText xml:lang=\"en\">"
									+ XmlGenerator.escapeString(hash.get("Predecessor/Derivation"))
									+ "</TranslatedText>";
							writer.write(str);
							writer.newLine();

							indent--;
							str = insertIndent(indent);
							str += "</Description>";
							writer.write(str);
							writer.newLine();

							indent--;
							str = insertIndent(indent);
							str += "</def:Origin>";
							writer.write(str);
							writer.newLine();

						} else {
							if (hash.get("Origin") != null && !hash.get("Origin").equals("")) {
								str = insertIndent(indent);
								str += "<def:Origin Type=\"" + hash.get("Origin") + "\"/>";
								writer.write(str);
								writer.newLine();
							}
						}

						/* Write def:ValueListRef tag only for variable-level metadata */
						if (reader.getTableName().equals(this.config.defineVariableTableName)) {
							if (hash.get("Has Value Metadata") != null && (hash.get("Has Value Metadata").equals("Y") || hash.get("Has Value Metadata").equals("Yes"))) {
								str = insertIndent(indent);
								str += "<def:ValueListRef ValueListOID=\""
										+ createOID(TagType.VALUELISTREF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), "", "")
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
				}	//End loop 1 + N times
			}
		} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	/**
	 * This method generates ItemDef tags for variable-level metadata of supp
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	private void writeSuppItemDefSection() throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
//		try {
			String str = null;
			boolean isFirst = true;

			/* For each non-empty SUPP dataset (SUPP datasets in SuppUtil) */
			for (String name : this.suppUtil.getSuppDatasetNames()) {
				errHint.setErrorHint(TagType.ITEMDEF, "SUPP"+name, "SUPP"+name, "", "");

               /*
                * ItemDef for "RDOMAIN" variable - write RDOMAIN only once for the entire SUPPQUAL datasets.
                */
				if (isFirst == true) {
					str = insertIndent(indent);
					str += "<ItemDef OID=\""
							+ createOID(TagType.ITEMDEF, "SUPP"+name, "SUPP"+name, "RDOMAIN", "", "")
							+ "\" Name=\"" + "RDOMAIN"
							+ "\" DataType=\"" + "text"
							+ "\" Length=\"" + "2"
							+ "\" SASFieldName=\"" +  "RDOMAIN"
							+ "\" def:CommentOID=\"" +  createOID(TagType.COMMENTDEF, "SUPP"+name, "SUPP"+name, "RDOMAIN", "", "")
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
					str += "<TranslatedText xml:lang=\"en\">Related Domain Abbreviation</TranslatedText>";
					writer.write(str);
					writer.newLine();

					indent--;
					str = insertIndent(indent);
					str += "</Description>";
					writer.write(str);
					writer.newLine();

					str = insertIndent(indent);
					str += "<def:Origin Type=\"Assigned\"/>";
					writer.write(str);
					writer.newLine();

					indent--;
					str = insertIndent(indent);
					str += "</ItemDef>";
					writer.write(str);
					writer.newLine();

					isFirst = false;
				}

				/*
				 * ItemDef for "IDVAR" variable - Length = 1 for SUPPDM, 5 for the others.
				 */
			    str = insertIndent(indent);
				str += "<ItemDef OID=\""
					+ createOID(TagType.ITEMDEF, "SUPP"+name, "SUPP"+name, "IDVAR", "", "")
					+ "\" Name=\"" + "IDVAR"
					+ "\" DataType=\"" + "text"
				 	+ (name.equals("DM") ? "\" Length=\"1" : "\" Length=\"5")
				 	+ "\" SASFieldName=\"" +  "IDVAR"
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
				str += "<TranslatedText xml:lang=\"en\">Identifying Variable</TranslatedText>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</Description>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str += "<def:Origin Type=\"Assigned\"/>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</ItemDef>";
				writer.write(str);
				writer.newLine();

				/*ItemDef for "IDVARVAL" variable-- SUPPDM:Length=1 , others:get domain-SEQ of the parent dataset */
			    str = insertIndent(indent);
				str += "<ItemDef OID=\""
					+ createOID(TagType.ITEMDEF, "SUPP"+name, "SUPP"+name, "IDVARVAL", "", "")
					+ "\" Name=\"" + "IDVARVAL"
					+ "\" DataType=\"" + "text"
					+ (name.equals("DM") ? "\" Length=\"1" : "\" Length=\""+ suppUtil.getIdVarvalLength(name))
					+ "\" SASFieldName=\"" +  "IDVARVAL"
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
				str += "<TranslatedText xml:lang=\"en\">Identifying Variable Value</TranslatedText>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</Description>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str += "<def:Origin Type=\"Assigned\"/>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</ItemDef>";
				writer.write(str);
				writer.newLine();


				/*ItemDef for "QNAM" variable - length is maxmam length of variable name*/
			    str = insertIndent(indent);
				str += "<ItemDef OID=\""
					+ createOID(TagType.ITEMDEF, "SUPP"+name, "SUPP"+name, "QNAM", "", "")
					+ "\" Name=\"" + "QNAM"
					+ "\" DataType=\"" + "text"
					+ "\" Length=\"" + suppUtil.getQnamLength(name)
					+ "\" SASFieldName=\"" +  "QNAM"
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
				str += "<TranslatedText xml:lang=\"en\">Qualifier Variable Name</TranslatedText>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</Description>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str += "<def:Origin Type=\"Assigned\"/>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</ItemDef>";
				writer.write(str);
				writer.newLine();


				/*ItemDef for "QLABEL" variable - length is maxmam length of label*/
			    str = insertIndent(indent);
				str += "<ItemDef OID=\""
					+ createOID(TagType.ITEMDEF, "SUPP"+name, "SUPP"+name, "QLABEL", "", "")
					+ "\" Name=\"" + "QLABEL"
					+ "\" DataType=\"" + "text"
					+ "\" Length=\"" + suppUtil.getQlabelLength(name)
					+ "\" SASFieldName=\"" +  "QLABEL"
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
				str += "<TranslatedText xml:lang=\"en\">Qualifier Variable Label</TranslatedText>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</Description>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str += "<def:Origin Type=\"Assigned\"/>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</ItemDef>";
				writer.write(str);
				writer.newLine();


				/*ItemDef for "QVAL" variable - length is maxmam value of length
				 *                            - Origin is always blank */
				String qvalDataType = suppUtil.getQvalDataType(name);
			    str = insertIndent(indent);
				str += "<ItemDef OID=\""
					+ createOID(TagType.ITEMDEF, "SUPP"+name, "SUPP"+name, "QVAL", "", "")
					+ "\" Name=\"" + "QVAL"
					+ "\" DataType=\"" + qvalDataType
					+ ("text".equals(qvalDataType) || "integer".equals(qvalDataType) || "float".equals(qvalDataType) ? "\" Length=\"" + suppUtil.getQvalLength(name) : "")
					+ ("float".equals(qvalDataType) ? "\" SignificantDigits=\"" + suppUtil.getQvalSigDigits(name) : "")
					+ "\" SASFieldName=\"" +  "QVAL"
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
				str += "<TranslatedText xml:lang=\"en\">Data Value</TranslatedText>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</Description>";
				writer.write(str);
				writer.newLine();

/* Origin should be always empty - be careful to uncomment the codes below because origin,
 * including CRF Page References and Predecessor/Derivation must be consistent with all VLM.
				Origin origin = suppUtil.getQvalOrigin(name);
				if(origin != null) {
					if ("CRF".equals(origin.origin)) {
						str = insertIndent(indent);
						str += "<def:Origin Type=\"CRF\">";
						writer.write(str);
						writer.newLine();
						indent++;

						str = insertIndent(indent);
						str += "<def:DocumentRef leafID=\""
								+ createOID(TagType.LEAF, "", "", "", "", 
										StringUtils.isEmpty(origin.crf_id) ? origin.crf_id : strAnnotatedCRF)
										+ "\">";
						writer.write(str);
						writer.newLine();
						indent++;

						str = insertIndent(indent);
						str += "<def:PDFPageRef PageRefs=\"" + origin.pagesInString()
								+ "\" Type=\"" + origin.crf_page_type + "\"/>";
						writer.write(str);
						writer.newLine();

						indent--;
						str = insertIndent(indent);
						str += "</def:DocumentRef>";
						writer.write(str);
						writer.newLine();

						indent--;
						str = insertIndent(indent);
						str += "</def:Origin>";
						writer.write(str);
						writer.newLine();
					} else {
						str = insertIndent(indent);
						str += "<def:Origin Type=\"" + origin.origin + "\"/>";	//Origin
						writer.write(str);
						writer.newLine();
					}
				}
*/

				str = insertIndent(indent);
				str += "<def:ValueListRef ValueListOID=\""+createOID(TagType.VALUELISTREF, "SUPP"+name, "SUPP"+name,"QVAL", "", "")
				+ "\"/>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</ItemDef>";
				writer.write(str);
				writer.newLine();

				/*ItemDef for "QORIG" variable*/
			    str = insertIndent(indent);
				str += "<ItemDef OID=\""
					+ createOID(TagType.ITEMDEF, "SUPP"+name, "SUPP"+name, "QORIG", "", "")
					+ "\" Name=\"" + "QORIG"
					+ "\" DataType=\"" + "text"
					+ "\" Length=\"" + suppUtil.getQorigLength(name)
					+ "\" SASFieldName=\"" +  "QORIG"
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
				str += "<TranslatedText xml:lang=\"en\">Origin</TranslatedText>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</Description>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str += "<def:Origin Type=\"Assigned\"/>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</ItemDef>";
				writer.write(str);
				writer.newLine();


				/*ItemDef for "QEVAL" variable*/
			    str = insertIndent(indent);
				str += "<ItemDef OID=\""
					+ createOID(TagType.ITEMDEF, "SUPP"+name, "SUPP"+name, "QEVAL", "", "")
					+ "\" Name=\"" + "QEVAL"
					+ "\" DataType=\"" + "text"
					+ "\" Length=\"" + suppUtil.getQevalLength(name)
					+ "\" SASFieldName=\"" +  "QEVAL"
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
				str += "<TranslatedText xml:lang=\"en\">Evaluator</TranslatedText>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</Description>";
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str += "<def:Origin Type=\"Assigned\"/>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</ItemDef>";
				writer.write(str);
				writer.newLine();

			}

//		} catch  (NullPointerException ex) {
//			throw new RequiredValueMissingException(ex, errHint);
//		}
	}

	/**
	 * Create ValueListDef from VALUE sheet excluding empty datasets.
	 * This method also creates ValueListDef for QVAL (by calling writeSuppValueListDefSection()).
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	public void writeValueListDefSection() throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {
			reader.setTable(
					this.config.defineValueTableName,
					new WhereClause[] { new WhereClause("Value Key", WhereClause.Operator.NE, "")});
			HashSet<String> uniqueKeys = new HashSet<>();
			Collections.addAll(uniqueKeys, "Dataset Name", "Variable Name", "Value Key");
			reader.setUniqueKeys(reader.getTableName(), uniqueKeys);

			Hashtable<String, String> hash = new Hashtable<String, String>();
			String str = null;
			String prevDomainKey = null;		//Used to judge whether a new row belongs to the same variable group as the previous row.
			String prevVariableName = null;		//Used to judge whether a new row belongs to the same variable group as the previous row.
			int orderNumber = 1;		//Used for the OrderNumber attribute of ItemRef tag
			boolean isBlank = true; //Used to judge whether value sheet is blank or not.

			while ((hash = reader.read()) != null) {
				errHint.setErrorHint(TagType.VALUELISTDEF, hash.get(domainKey), hash.get("Variable Name"), hash.get("Value Key"), "");
				/* Skip if the dataset is empty. */
				if (this.emptyDatasets.contains(hash.get("Dataset Name"))) {
					continue;
				}

				isBlank = false;
				/*
				 *  If this is a new variable group (e.g. VSORRES, VSORRESU, ...),
				 *  insert def:ValueListDef start and end tags.
				 */
				if (prevVariableName == null || !(prevDomainKey + prevVariableName).equals(hash.get("Dataset Name") + hash.get("Variable Name"))) {

					orderNumber = 1;
					/* If this is not a start of the VARIABLE table, close the def:ValueListDef tag.*/
					if (prevVariableName != null) {
						indent--;
						str = insertIndent(indent);
						str += "</def:ValueListDef>";
						writer.write(str);
						writer.newLine();
					}

					str = insertIndent(indent);
					str += "<def:ValueListDef OID=\""
							+ createOID(TagType.VALUELISTDEF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), "", "")
							+ "\">";
					writer.write(str);
					writer.newLine();
					indent++;
				}

				str = insertIndent(indent);
				str += "<ItemRef ItemOID=\""
						+ createOID(TagType.ITEMREF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), hash.get("Value Key"), "")
						+ "\" OrderNumber=\"" + orderNumber
						+ "\" Mandatory=\"" + (hash.get("Mandatory").equals("Yes") ? "Yes" : "No");
				if (hash.get("Origin").equals("Derived")) {
					str += "\" MethodOID=\"" + createOID(TagType.METHODDEF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), hash.get("Value Key"), "");
				} else {
					//Do nothing.
				}

				if (hash.get("WhereClauseValue") != null && !hash.get("WhereClauseValue").equals("")) {
					str += "\">";
					writer.write(str);
					writer.newLine();
					indent++;
	
					str = insertIndent(indent);
					str += "<def:WhereClauseRef WhereClauseOID=\""
							+ createOID(TagType.WHERECLAUSEREF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), hash.get("Value Key"), "")
							+ "\"/>";
					writer.write(str);
					writer.newLine();
	
					indent--;
					str = insertIndent(indent);
					str += "</ItemRef>";
					writer.write(str);
					writer.newLine();
	
				} else {
					str += "\"/>";
					writer.write(str);
					writer.newLine();
				}


				orderNumber++;
				prevDomainKey = hash.get("Dataset Name");
				prevVariableName = hash.get("Variable Name");
			}

			/* Close the def:ValueListDef tag at the end of while loop */
			if (!isBlank) {
			indent--;
			str = insertIndent(indent);
			str += "</def:ValueListDef>";
			writer.write(str);
			writer.newLine();
			} else {
				//do nothing when value sheet is blank.
			}

			reader.close();
			if (this.suppUtil.isAutoSuppActive()) {
				writeSuppValueListDefSection();
			}

		} catch  (NullPointerException ex) {
			ex.printStackTrace();
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	/**
	 * This method creates ValueListDef for QVAL excluding empty datasets.
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	public void writeSuppValueListDefSection() throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {
			String str = null;
			int orderNumber = 1;		//Used for the OrderNumber attribute of ItemRef tag

			for (String name : this.suppUtil.getSuppDatasetNames()) {	//getSuppDatasetNames() does not contain empty datasets
				errHint.setErrorHint(TagType.VALUELISTDEF, "SUPP"+name, "SUPP"+name, "QVAL", "");

				orderNumber = 1;

				str = insertIndent(indent);
				str += "<def:ValueListDef OID=\""
						+ createOID(TagType.VALUELISTDEF, "SUPP"+name, "SUPP"+name, "QVAL", "", "")
						+ "\">";
				writer.write(str);
				writer.newLine();
				indent++;

				for (Variable var : this.suppUtil.listSuppVariables(name)) {
						str = insertIndent(indent);
						str += "<ItemRef ItemOID=\""
								+ createOID(TagType.ITEMREF, "SUPP"+name, "SUPP"+name, "QVAL", var.variable_name, "")
								+ "\" OrderNumber=\"" + orderNumber
								+ "\" Mandatory=\"" + ("Yes".equals(var.mandatory) ? "Yes" : "No")
						        + (StringUtils.equals("Derived", var.origin.origin)?
							     "\" MethodOID=\"" + createOID(TagType.METHODDEF, "SUPP"+name, "SUPP"+name, "QVAL", var.variable_name, "") : "")
							    + "\" >";
						writer.write(str);
						writer.newLine();

						indent++;
						str = insertIndent(indent);
						str += "<def:WhereClauseRef WhereClauseOID=\""
								+ createOID(TagType.WHERECLAUSEREF, "SUPP"+name, "SUPP"+name, "QNAM", var.variable_name, "")
								+ "\"/>";
						writer.write(str);
						writer.newLine();

						indent--;
						str = insertIndent(indent);
						str += "</ItemRef>";
						writer.write(str);
						writer.newLine();
						orderNumber++;
					}
				indent--;
				str = insertIndent(indent);
				str += "</def:ValueListDef>";
				writer.write(str);
				writer.newLine();
			}
		} catch  (NullPointerException ex) {
			ex.printStackTrace();
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	/**
	 * Create WhereClauseDef from VALUE and RESULT2 sheets.
	 * This method also creates WhereClauseDef for QVAL (by calling writeSuppWhereClauseDefSection()).
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	public void writeWhereClauseDefSection() throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {

		/*
		 * Generate WhereClauseDef for value-level metadata
		 */
		reader.setTable(this.config.defineValueTableName);
		writeWhereClauseDefSection(reader);

		/*
		 * Generate WhereClauseDef for analysis results metadata
		 */
		if (this.datasetType.equals(Config.DatasetType.ADaM) &&
				this.config.e2dIncludeResultMetadata == true) {
			reader.setTable(this.config.defineResult2TableName,
					new WhereClause[] { new WhereClause("WhereClauseVariable", WhereClause.Operator.NE, "") });
			writeWhereClauseDefSection(reader);
		}
		if (this.suppUtil.isAutoSuppActive()) {
			writeSuppWhereClauseDefSection();
		}
	}

	/**
	 * This method creates WhereClauseDef from the VALUE or RESULT2 sheets as given by the reader.
	 * This method skips processing for empty datasets (i.e. where Is Empty = Yes/SUPP).
	 * @param reader
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	public void writeWhereClauseDefSection(MetaDataReader reader) throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {

			Hashtable<String, String> hash = new Hashtable<String, String>();
			String str = null;
			boolean isNewWhereClause = true;	//Default value is true, because the first line is a new WhereClause.
			String prevKey = null;	//Used to judge whether a new row belongs to the same WhereClause group.
			boolean isBlank = true; //Used to judge whether whereclause is nothing or not.
			boolean haveWC;
			while ((hash = reader.read()) != null) {
				/* Skip if the dataset is empty. */
				if (this.emptyDatasets.contains(hash.get("W Dataset Name"))) {
					continue;
				}
				isBlank = false;
				haveWC = false;
				if (reader.getTableName().equals(this.config.defineValueTableName)) {
					if (hash.get("WhereClauseValue") != null && !hash.get("WhereClauseValue").equals("")) {
						haveWC = true;
					}
				} else if (reader.getTableName().equals(this.config.defineResult2TableName)) {
					if (hash.get("W Result Key") != null && !hash.get("W Result Key").equals("") &&
						hash.get("W Display Name") != null && !hash.get("W Display Name").equals("") &&
						hash.get("W Dataset Name") != null && !hash.get("W Dataset Name").equals("")) {
						haveWC = true;
					}
				}

				if (reader.getTableName().equals(this.config.defineValueTableName)) {
					errHint.setErrorHint(TagType.WHERECLAUSEDEF, hash.get(wDomainKey), hash.get("W Variable Name"), hash.get("W Value Key"), "");
				} else if (reader.getTableName().equals(this.config.defineResult2TableName)) {
					errHint.setErrorHint(TagType.WHERECLAUSEDEF, "", "", "", "WhereClauseVariable=" + hash.get("WhereClauseVariable"));
				} else {
					//Do nothing.
				}

				/*
				 *  If this is a new value key group (e.g. HEIGHT.CMETRIC, HEIGHT.CNMETRIC, ...),
				 *  insert def:WhereClauseDef start and end tags.
				 */
				if (reader.getTableName().equals(this.config.defineValueTableName)) {
					if (haveWC) {
						if (prevKey == null || !prevKey.equals(hash.get(wDomainKey) + "." + hash.get("W Variable Name") + "." + hash.get("W Value Key"))) {
							isNewWhereClause = true;
						} else {
							isNewWhereClause = false;
						}
					} else {
						isNewWhereClause = false;
					}
				}
				if (reader.getTableName().equals(this.config.defineResult2TableName)) {
					//Changefor v1.3.0 - not write WhereClauseDef, if WhereClause not exsist in excel.
					if (haveWC) {
						if (prevKey == null || !prevKey.equals(hash.get("W Display Name") + "." + hash.get("W Result Key") + "." + hash.get("W Dataset Name"))) {
							isNewWhereClause = true;
						} else {
							isNewWhereClause = false;
						}
					} else {
						isNewWhereClause = false;
					}
				}


				if (isNewWhereClause == true) {
					/* If this is not a start of the table, close the def:ValueListDef tag.*/
					if (prevKey != null) {
						indent--;
						str = insertIndent(indent);
						str += "</def:WhereClauseDef>";
						writer.write(str);
						writer.newLine();
					}


					str = insertIndent(indent);
					if (reader.getTableName().equals(this.config.defineValueTableName)) {
						if (haveWC) {
							str += "<def:WhereClauseDef OID=\""
									+ createOID(TagType.WHERECLAUSEDEF, hash.get(wDomainKey), hash.get("W Dataset Name"), hash.get("W Variable Name"), hash.get("W Value Key"), "");
							if (hash.get("WhereClause Comment") != null && !hash.get("WhereClause Comment").equals("")) {
								str += "\" def:CommentOID=\""
										+ createOID(TagType.COMMENTDEF, hash.get(wDomainKey), hash.get("W Dataset Name"), hash.get("W Variable Name"), hash.get("W Value Key"), "WC");
							}
						} else {

						}

					} else if (reader.getTableName().equals(this.config.defineResult2TableName)) {
						if (haveWC) {
						str += "<def:WhereClauseDef OID=\""
								+ createOID(TagType.WHERECLAUSEDEF, "", "", "", "", hash.get("W Display Name") + "." + hash.get("W Result Key") + "." + hash.get("W Dataset Name"));
						}
						if (hash.get("WhereClause Comment") != null && !hash.get("WhereClause Comment").equals("")) {
							str += "\" def:CommentOID=\""
									+ createOID(TagType.COMMENTDEF, "", "", "", "", "WC." + hash.get("W Display Name") + "." + hash.get("W Result Key") + "." + hash.get("W Dataset Name"));
						}
					} else {
						//Do nothing.
					}
					str += "\">";
					writer.write(str);
					writer.newLine();
					indent++;
				}

				str = insertIndent(indent);
				if (reader.getTableName().equals(this.config.defineValueTableName)) {
					if (haveWC) {
						str += "<RangeCheck SoftHard=\"Soft\" def:ItemOID=\""
								+ createOID(TagType.ITEMDEF, hash.get(wDomainKey), hash.get("WhereClauseDataset"), hash.get("WhereClauseVariable"), "", "");
					} else {
					}
				} else if (reader.getTableName().equals(this.config.defineResult2TableName)) {
					if (haveWC) {
						str += "<RangeCheck SoftHard=\"Soft\" def:ItemOID=\""
								+ createOID(TagType.ITEMDEF, hash.get(wDomainKey), hash.get("WhereClauseDataset"), hash.get("WhereClauseVariable"), "", "");
					} else {
					}
				} else {
					//Do nothing.
				}

				if (haveWC) {
				str += "\" Comparator=\"" + hash.get("WhereClauseOperator")
						+ "\">";
				writer.write(str);
				writer.newLine();
				indent++;

				/* Split values because WhereClauseValue may contain values split by comma */
				String[] checkValues = hash.get("WhereClauseValue").split(DELIMITER);
				for (int i = 0; i < checkValues.length ; i++) {
					str = insertIndent(indent);
					str += "<CheckValue>" + XmlGenerator.escapeString(checkValues[i].trim()) + "</CheckValue>";
					writer.write(str);
					writer.newLine();
				}

				indent--;
				str = insertIndent(indent);
				str += "</RangeCheck>";
				writer.write(str);
				writer.newLine();
				} else {

				}

				if (reader.getTableName().equals(this.config.defineValueTableName)) {
					if (haveWC) {
					prevKey = hash.get("W Dataset Name") + "." + hash.get("W Variable Name") + "." + hash.get("W Value Key");
					}
				} else if (reader.getTableName().equals(this.config.defineResult2TableName)) {
					if (haveWC) {
					prevKey = hash.get("W Display Name") + "." + hash.get("W Result Key") + "." + hash.get("W Dataset Name");
					}
				} else {
					//Do nothing.
				}
			}


			/* Close the def:WhereClauseDef tag at the end of while loop */
				if (!isBlank ) { //when value sheet is not blank
					indent--;
					str = insertIndent(indent);
					str += "</def:WhereClauseDef>";
					writer.write(str);
					writer.newLine();
				} else {
					//do nothing
				}
				reader.close();

			} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	/**
	 * This method creates WhereClauseDef (where QNAM EQ variable_name) for QVAL excluding empty datasets.
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	public void writeSuppWhereClauseDefSection() throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {

			String str = null;

			for (String name : this.suppUtil.getSuppDatasetNames()) {	//getSuppDatasetNames() does not contain empty datasets
				errHint.setErrorHint(TagType.WHERECLAUSEDEF, "SUPP"+name, "SUPP"+name, "QNAM", "");

				for (Variable var : suppUtil.listSuppVariables(name)) {
					str = insertIndent(indent);
					str += "<def:WhereClauseDef OID=\""
							+ createOID(TagType.WHERECLAUSEDEF, "SUPP"+name, "SUPP"+name, "QNAM", var.variable_name, "");
					str += "\">";

					writer.write(str);
					writer.newLine();
					indent++;

					str = insertIndent(indent);
					str += "<RangeCheck SoftHard=\"Soft\" def:ItemOID=\""
						+ createOID(TagType.ITEMDEF, "SUPP"+name, "SUPP"+name, "QNAM", "", "")
					    + "\" Comparator=\"" +"EQ"
						+ "\">";

					writer.write(str);
					writer.newLine();
					indent++;

					str = insertIndent(indent);
					str += "<CheckValue>" + var.variable_name + "</CheckValue>";
					writer.write(str);
					writer.newLine();
					indent--;
					str = insertIndent(indent);
					str += "</RangeCheck>";
					writer.write(str);
					writer.newLine();

					/* Close the def:WhereClauseDef tag at the end of while loop */
					indent--;
					str = insertIndent(indent);
					str += "</def:WhereClauseDef>";
					writer.write(str);
					writer.newLine();
				}
			}
		} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	/**
	 * This method creates CodeList tags from CODELIST sheet excluding empty codelists
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	public void writeCodelistSection() throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {
			reader.setTable(this.config.defineCodelistTableName);

			Hashtable<String, String> hash = new Hashtable<String, String>();
			String str = null;
			String prevCodelistID = null;		//Used to judge whether a new row belongs to the same codelist group as the previous row.
			String prevCodelistCode = null;		//Used to retain Codelist Code value of the previous loop to create Alias tags.
			boolean isBlank = true; //if codelist is blank
			boolean hasDecodeValue = false; //false for EnumeratedItem; true for CodelistItem;

			while ((hash = reader.read()) != null) {
				errHint.setErrorHint(TagType.CODELIST, "", "", "", hash.get("Codelist ID"));
				/* Skip if the dataset is empty. */
				if (this.emptyCodelists.contains(hash.get("Codelist ID"))) {
					continue;
				}

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
						/*
						 * When it is a NCI codelist
						 */
						if (prevCodelistCode != null && !prevCodelistCode.equals("")) {
							str = insertIndent(indent);
							str += "<Alias Name=\"" + prevCodelistCode
									+ "\" Context=\"nci:ExtCodeID\"/>";
							writer.write(str);
							writer.newLine();
						} else {
							/*
							 * When it is a sponsor-defined codelist, do nothing.
							 */
						}

						indent--;
						str = insertIndent(indent);
						str += "</CodeList>";
						writer.write(str);
						writer.newLine();
					}

					/*
					 *  When it is either a NCI or a sponsor-defined codelist
					 */
					isBlank = false;//codelist is not blank
					str = insertIndent(indent);
					str += "<CodeList OID=\""
							+ createOID(TagType.CODELIST, "", "", "", "", hash.get("Codelist ID"))
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
					/* When it is a NCI codelist */
					if (hash.get("Codelist Code") != null && !hash.get("Codelist Code").equals("")) {
						str = insertIndent(indent);
						str += "<EnumeratedItem CodedValue=\"" + XmlGenerator.escapeString(hash.get("Submission Value"))
								+ (hash.get("Order Number") == null || hash.get("Order Number").equals("") ?
										"" : "\" OrderNumber=\"" + hash.get("Order Number"))
								+ (hash.get("ExtendedValue") != null && hash.get("ExtendedValue").equals("Yes") ?
										"\" def:ExtendedValue=\"" + hash.get("ExtendedValue") : "")
								+ "\">";
						writer.write(str);
						writer.newLine();
						indent++;

						if (hash.get("ExtendedValue") != null && !hash.get("ExtendedValue").equals("Yes")) {
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
					} else {
					/* When it is a sponsor-defined codelist */
						str = insertIndent(indent);
						str += "<EnumeratedItem CodedValue=\"" + XmlGenerator.escapeString(hash.get("Submission Value"))
								+ (hash.get("Rank") == null || hash.get("Rank").equals("") ?
										"" : "\" Rank=\"" + hash.get("Rank"))
								+ (hash.get("Order Number") == null || hash.get("Order Number").equals("") ?
										"" : "\" OrderNumber=\"" + hash.get("Order Number"))
								+ (hash.get("ExtendedValue") != null && hash.get("ExtendedValue").equals("Yes") ?
										"\" def:ExtendedValue=\"" + hash.get("ExtendedValue") : "")
								+ "\"/>";
						writer.write(str);
						writer.newLine();
					}

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
							+ (hash.get("ExtendedValue") != null && hash.get("ExtendedValue").equals("Yes") ?
									"\" def:ExtendedValue=\"" + hash.get("ExtendedValue") : "")
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
					str += "<TranslatedText xml:lang=\""
							+ (hash.get("xml:lang") != null && !hash.get("xml:lang").equals("") ? hash.get("xml:lang") : DEFAULTLANG)
							+ "\">" + (hash.get("Translated Text") == null || hash.get("Translated Text").equals("") ?
									XmlGenerator.escapeString(hash.get("Decode")) : XmlGenerator.escapeString(hash.get("Translated Text"))) 
							+ "</TranslatedText>";
					writer.write(str);
					writer.newLine();

					indent--;
					str = insertIndent(indent);
					str += "</Decode>";
					writer.write(str);
					writer.newLine();

					/* When it is a NCI codelist */
					if (hash.get("Codelist Code") != null && !hash.get("Codelist Code").equals("")
							&& !hash.get("ExtendedValue").equals("Yes")) {
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
			} else {
				/*
				 * When it is a sponsor-defined codelist, do nothing.
				 */
			}

			if (isBlank == false) {
				indent--;
				str = insertIndent(indent);
				str += "</CodeList>";
				writer.write(str);
				writer.newLine();
			}

			/*
			 * Write external dictionary information
			 */
			reader.setTable(this.config.defineDictionaryTableName);
			while ((hash = reader.read()) != null) {
				str = insertIndent(indent);
				str += "<CodeList OID=\""
						+ createOID(TagType.CODELIST, "", "", "", "", hash.get("Dictionary ID"))
						+ "\" Name=\"" + XmlGenerator.escapeString(hash.get("Name"))
						+ "\" DataType=\"" + hash.get("DataType")
						+ "\">";
				indent++;
				writer.write(str);
				writer.newLine();

				str = insertIndent(indent);
				str += "<ExternalCodeList Dictionary=\"" + hash.get("Dictionary ID")
						+ "\" Version=\"" + hash.get("Version")
						+ (hash.get("ref") == null || hash.get("ref").equals("") ?
								"" : "\" ref=\"" + XmlGenerator.escapeString(hash.get("ref")))
						+ (hash.get("href") == null || hash.get("href").equals("") ?
								"" : "\" href=\"" + XmlGenerator.escapeString(hash.get("href")))
						+ "\"/>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</CodeList>";
				writer.write(str);
				writer.newLine();
			}

			reader.close();
		} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	/**
	 * This method creates MethodDefs from VARIABLE and VALUE sheets.
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	public void writeMethodDefSection() throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {

		/*
		 * Generate MethodDefs for variable-level metadata
		 */
		reader.setTable(
				this.config.defineVariableTableName,
				new WhereClause[] { new WhereClause("Origin", WhereClause.Operator.EQ, "Derived") });
		writeMethodDefSection(reader);

		/*
		 * Generate MethodDefs for value-level metadata
		 */
		reader.setTable(
				this.config.defineValueTableName,
				new WhereClause[] { new WhereClause("Origin", WhereClause.Operator.EQ, "Derived") });
		writeMethodDefSection(reader);
		/* if the input table has "Has SUPP" column, write RDOMAIN method once */
/*		if (hasSuppFlag) {
			String name = suppList.get(0);
			String str = null;
			str = insertIndent(indent);
			str += "<MethodDef OID=\"";
			str += createOID(TagType.METHODDEF, "SUPP"+name, "SUPP"+name, "RDOMAIN","", "")
				+ "\" Name=\"Algorithm to derive " + "SUPP"+ "." + "RDOMAIN";
			str += "\" Type=\"Computation\""
				+ ">";
			writer.write(str);
			writer.newLine();
			indent++;

			str = insertIndent(indent);
			str += "<Description>";
			writer.write(str);
			writer.newLine();
			indent++;

			str = insertIndent(indent);
			str += "<TranslatedText xml:lang=\"en\">"
					+ "Domain abbreviation from where data originated."
					+ "</TranslatedText>";
			writer.write(str);
			writer.newLine();

			indent--;
			str = insertIndent(indent);
			str += "</Description>";
			writer.write(str);
			writer.newLine();

			indent--;
			str = insertIndent(indent);
			str += "</MethodDef>";
			writer.write(str);
			writer.newLine();
		}
*/
		reader.close();
	}

	/**
	 * This method creates MethodDefs from VARIABLE or VALUE sheets as given by the reader.
	 * This method skips processing for empty datasets (i.e. where Is Empty = Yes/SUPP).
	 * This method does not create MethodDef for Repeat N variables because they reference the same method as the parent.
	 * @param reader
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	private void writeMethodDefSection(MetaDataReader reader) throws IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {
			Hashtable<String, String> hash = new Hashtable<String, String>();
			String str = null;

			while ((hash = reader.read()) != null) {
				errHint.setErrorHint(TagType.METHODDEF, hash.get(domainKey), hash.get("Variable Name"), hash.get("Value Key"), "");
				String dataset_name = hash.get("Dataset Name");
				String is_supp = hash.get("Is SUPP");
				/* Skip if the dataset is empty. */
				if (this.emptyDatasets.contains(dataset_name)) {
					continue;
				}
				/* Skip if the SUPP dataset is empty. */
				Set<String> supp_dataset_names = this.suppUtil.getSuppDatasetNames();	//Excluding empty SUPP datasets
				if ("Yes".equals(is_supp) && !supp_dataset_names.contains(dataset_name)) {
					continue;
				}

				/*
				 * STUDYID and USUBJID of SDTM should be written only once.
				 * Skip the loop if:
				 * 	- DatasetType == "SDTM"	&&
				 * 	- Domain != "DM"	&&
				 * 	- Variable Name == "STUDYID" or "USUBJID"
				 */
				if (hash.get("Variable Name") != null &&
						(hash.get("Predecessor/Derivation") != null || !hash.get("Predecessor/Derivation").equals("")) &&
						!((this.datasetType.equals(Config.DatasetType.SDTM) || this.datasetType.equals(Config.DatasetType.SEND)) &&
						!hash.get("Domain").equals("DM") &&
						(hash.get("Variable Name").equals("STUDYID") || hash.get("Variable Name").equals("USUBJID")))
						) {
					str = insertIndent(indent);
					str += "<MethodDef OID=\"";
					/* If the MethodDef tag is for a variable */
					if (reader.getTableName().equals(this.config.defineVariableTableName) && (is_supp == null || !is_supp.equals("Yes"))) {
						str += createOID(TagType.METHODDEF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), "", "")
								+ "\" Name=\"Algorithm to derive " + hash.get("Variable Name");
					/* If the MethodDef tag is for a value */
					} else if (reader.getTableName().equals(this.config.defineValueTableName)) {
								str += createOID(TagType.METHODDEF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), hash.get("Value Key"), "")
								+ "\" Name=\"Algorithm to derive " + hash.get("Variable Name") + "." + hash.get("Value Key");
					/* If the Method tag is for a value of suppqual*/
					} else if (reader.getTableName().equals(this.config.defineVariableTableName) && is_supp != null && is_supp.equals("Yes")) {
						str += createOID(TagType.METHODDEF, "SUPP"+hash.get(domainKey), "SUPP"+hash.get("Dataset Name"), "QVAL", hash.get("Variable Name"), "")
						+ "\" Name=\"Algorithm to derive " + "SUPP"+hash.get("Dataset Name") + "." + hash.get("Variable Name");
					} else {
						//Do nothing.
					}
					str += "\" Type=\"" + hash.get("Derivation Type")
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
					str += "<TranslatedText xml:lang=\""
							+ (hash.get("xml:lang") != null && !hash.get("xml:lang").equals("") ? hash.get("xml:lang") : DEFAULTLANG)
							+ "\">" + XmlGenerator.escapeString(hash.get("Predecessor/Derivation"))
							+ "</TranslatedText>";
					writer.write(str);
					writer.newLine();

					indent--;
					str = insertIndent(indent);
					str += "</Description>";
					writer.write(str);
					writer.newLine();

					if (hash.get("FormalExpression Text") != null && !hash.get("FormalExpression Text").equals("")) {
						str = insertIndent(indent);
						str += "<FormalExpression Context=\""
								+ XmlGenerator.escapeString(hash.get("FormalExpression Context"))
								+ "\">"
								+ XmlGenerator.escapeString(hash.get("FormalExpression Text"))
						    + "</FormalExpression>";
						writer.write(str);
						writer.newLine();
					}else if (hash.get("Formal expression") != null && !hash.get("Formal expression").equals("")) {
						str = insertIndent(indent);
						str += "<FormalExpression Context=\""
								+ XmlGenerator.escapeString(hash.get("Formal expression context"))
								+ "\">"
								+ XmlGenerator.escapeString(hash.get("Formal expression"))
						    + "</FormalExpression>";
						writer.write(str);
						writer.newLine();
					}
					/*
					 * Since DocumentID can be applied to both CommentDef and MethodDef,
					 * DocumentID is applied to MethodDef only when CommentDef is blank.
					 * (i.e. CommentDef precedes MethodDef.)
					 */
					if ((hash.get("Comment") == null || hash.get("Comment").equals("")) &&
							hash.get("DocumentID") != null && !hash.get("DocumentID").equals("")) {

						String[] checkValues = hash.get("DocumentID").split(DELIMITER);
						for (int i = 0; i < checkValues.length ; i++) {
							if (hash.get("Document Page Type") != null && hash.get("Document Page Reference") != null &&
									i < Arrays.asList(hash.get("Document Page Type").split(DELIMITER)).size() &&
									i < Arrays.asList(hash.get("Document Page Reference").split(DELIMITER)).size() &&
									!Arrays.asList(hash.get("Document Page Type").split(DELIMITER)).get(i).trim().equals("")) {
								str = insertIndent(indent);
								str += "<def:DocumentRef leafID=\""
										+ createOID(TagType.LEAF, "", "", "", "", checkValues[i].trim())
										+ "\">";
								writer.write(str);
								writer.newLine();
								indent++;

								str = insertIndent(indent);
								str += "<def:PDFPageRef PageRefs=\"" + Arrays.asList(hash.get("Document Page Reference").split(DELIMITER)).get(i).trim()
										+ "\" Type=\"" + Arrays.asList(hash.get("Document Page Type").split(DELIMITER)).get(i).trim() + "\"/>";
								writer.write(str);
								writer.newLine();

								indent--;
								str = insertIndent(indent);
								str += "</def:DocumentRef>";
								writer.write(str);
								writer.newLine();
							} else {
								str = insertIndent(indent);
								str += "<def:DocumentRef leafID=\""
										+ createOID(TagType.LEAF, "", "", "", "", checkValues[i].trim())
										+ "\"/>";
								writer.write(str);
								writer.newLine();
							}
						}
					}

					indent--;
					str = insertIndent(indent);
					str += "</MethodDef>";
					writer.write(str);
					writer.newLine();
				}
			}
		} catch  (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	/**
	 * This method creates CommentDefs from DATASET, VARIABLE, VALUE (Value and WC), RESULT1 and RESULT2 sheets.
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	public void writeCommentDefSection() throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {

		/*
		 * Generate CommentDefs for domain-level metadata
		 */
		reader.setTable(
				this.config.defineDatasetTableName,
				new WhereClause[] {new WhereClause("Comment", WhereClause.Operator.NE, "") });
		writeCommentDefSection(reader);

		/*
		 * Generate CommentDefs for variable-level metadata
		 */
		reader.setTable(this.config.defineVariableTableName,
				new WhereClause[] { new WhereClause("Comment", WhereClause.Operator.NE, "") });
		writeCommentDefSection(reader);

		/*
		 * Generate CommentDefs for RDOMAIN of SUPP-- datasets
		 */
		if (this.suppUtil.isAutoSuppActive()) {
			for (String name : this.suppUtil.getSuppDatasetNames()) {
				String str = null;
				str = insertIndent(indent);
				str += "<def:CommentDef OID=\"";
	
				str += createOID(TagType.COMMENTDEF, "SUPP"+name, "SUPP"+name, "RDOMAIN","", "");
				str += "\">";
				writer.write(str);
				writer.newLine();
				indent++;
	
				str = insertIndent(indent);
				str += "<Description>";
				writer.write(str);
				writer.newLine();
				indent++;
			
				str = insertIndent(indent);
				str += "<TranslatedText xml:lang=\"en\">"
						+ "Domain abbreviation from where data originated."
						+ "</TranslatedText>";
				writer.write(str);
				writer.newLine();
			
				indent--;
				str = insertIndent(indent);
				str += "</Description>";
				writer.write(str);
				writer.newLine();
			
				indent--;
				str = insertIndent(indent);
				str += "</def:CommentDef>";
				writer.write(str);
				writer.newLine();
			}
		}

		/*
		 * Generate CommentDefs for value-level metadata
		 */
		reader.setTable(this.config.defineValueTableName,
				new WhereClause[] { new WhereClause("Comment", WhereClause.Operator.NE, "") });
		HashSet<String> uniqueKeys = new HashSet<>();
		Collections.addAll(uniqueKeys, "Dataset Name", "Variable Name", "Value Key");
		reader.setUniqueKeys(reader.getTableName(), uniqueKeys);
		writeCommentDefSection(reader);

		/*
		 * Generate CommentDefs for a where-clause in value-level metadata
		 */
		reader.setTable(this.config.defineValueTableName,
				new WhereClause[] { new WhereClause("WhereClause Comment", WhereClause.Operator.NE, ""),
									new WhereClause("Value Key", WhereClause.Operator.NE, "")});
		writeCommentDefSection(reader);

		/*
		 * Generate CommentDefs for analysis results metadata
		 */
		if (this.datasetType.equals(Config.DatasetType.ADaM) &&
				this.config.e2dIncludeResultMetadata == true) {
			reader.setTable(this.config.defineResult1TableName,
					new WhereClause[] { new WhereClause("Datasets Comment", WhereClause.Operator.NE, "") });
			writeCommentDefSection(reader);
		}

		/*
		 * Generate CommentDefs for a where-clause in value-level metadata
		 */
		if (this.datasetType.equals(Config.DatasetType.ADaM) &&
				this.config.e2dIncludeResultMetadata == true) {
			reader.setTable(this.config.defineResult2TableName,
					new WhereClause[] { new WhereClause("WhereClause Comment", WhereClause.Operator.NE, "") });
			writeCommentDefSection(reader);
		}
		reader.close();
	}

	/**
	 * This method creates CommentDefs from DATASET, VARIABLE, VALUE (Value and WC), RESULT1 or RESULT2 sheets as provided in the reader.
	 * @param reader
	 * @throws IOException
	 * @throws InvalidOidSyntaxException
	 * @throws RequiredValueMissingException
	 */
	private void writeCommentDefSection(MetaDataReader reader) throws IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {
			Hashtable<String, String> hash = new Hashtable<String, String>();
			String str = null;

			while ((hash = reader.read()) != null) {
				if (reader.getTableName().equals(this.config.defineValueTableName)) {
					errHint.setErrorHint(TagType.COMMENTDEF, hash.get(wDomainKey), hash.get("W Variable Name"), hash.get("W Value Key"), "");
				} else if (reader.getTableName().equals(this.config.defineResult2TableName)) {
					errHint.setErrorHint(TagType.COMMENTDEF, "", "", "", "Result Key=" + hash.get("Result Key"));
				} else if (reader.getTableName().equals(this.config.defineResult2TableName)) {
					errHint.setErrorHint(TagType.COMMENTDEF, "", "", "", "WhereClauseVariable=" + hash.get("WhereClauseVariable"));
				}else if(reader.getTableName().equals(this.config.defineVariableTableName) && hash.get("Is SUPP") != null && hash.get("Is SUPP").equals("Yes")) {
					errHint.setErrorHint(TagType.COMMENTDEF, "SUPP"+hash.get("Dataset Name"), "SUPP"+hash.get("Dataset Name"), hash.get("Variable Name"), "");
					// End here
				} else {
					errHint.setErrorHint(TagType.COMMENTDEF, hash.get(domainKey), hash.get("Variable Name"), hash.get("Value Key"), "");
				}
				/* Skip if the dataset is empty. */
				if (this.emptyDatasets.contains(hash.get("Dataset Name")) || this.emptyDatasets.contains(hash.get("W Dataset Name"))) {
					continue;
				}
				/* Skip if the SUPP dataset is empty. */
				Set<String> supp_dataset_names = this.suppUtil.getSuppDatasetNames();	//Excluding empty SUPP datasets
				if ("Yes".equals(hash.get("Is SUPP")) && !supp_dataset_names.contains(hash.get("Dataset Name"))) {
					continue;
				}
				/*
				 * STUDYID and USUBJID of SDTM should be written only once - generate the CommentDef tag if:
				 * 	(1) This is a domain-level comment.	OR
				 * 	(2) This is a variable- or value-level comment, and not for STUDYID or USUBJID of SDTM.	OR
				 * 	(3) This is a variable-level comment for STUDYID of the DM domain.	OR
				 * 	(4) This is a variable-level comment for USUBJID of the DM domain.
				 *  (5) This is a comment for analysis results metadata.
				 */
				if (reader.getTableName().equals(this.config.defineDatasetTableName) ||
						reader.getTableName().equals(this.config.defineResult1TableName) ||
						reader.getTableName().equals(this.config.defineResult2TableName) ||
						(hash.get("Variable Name") != null &&
							!((this.datasetType.equals(Config.DatasetType.SDTM) || this.datasetType.equals(Config.DatasetType.SEND)) &&
							!hash.get("Domain").equals("DM") &&
							(hash.get("Variable Name").equals("STUDYID") || hash.get("Variable Name").equals("USUBJID"))))
							) {

					str = insertIndent(indent);
					str += "<def:CommentDef OID=\"";
					/* If the CommentDef tag is for a domain */
					if (reader.getTableName().equals(this.config.defineDatasetTableName)) {
						str += createOID(TagType.COMMENTDEF, hash.get("Dataset Name"), "", "", "", "");	//OID should include dataset name, not domain, considering split datasets.
						/* If the CommentDef tag is for a variable */
					} else if (reader.getTableName().equals(this.config.defineVariableTableName) && (hash.get("Is SUPP") == null || !hash.get("Is SUPP").equals("Yes"))) {
						str += createOID(TagType.COMMENTDEF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), "", "");
						/* If the CommentDef tag is for a value of supp */
					} else if (reader.getTableName().equals(this.config.defineVariableTableName) && hash.get("Is SUPP") != null && hash.get("Is SUPP").equals("Yes")) {
						str += createOID(TagType.COMMENTDEF, "SUPP"+hash.get("Dataset Name"), "SUPP"+hash.get("Dataset Name"), "QVAL", hash.get("Variable Name"), "");
						/* If the CommentDef tag is for a value */
					} else if (reader.getTableName().equals(this.config.defineValueTableName) &&
							reader.getWhereClause()[0].getVariable().equals("Comment")) {
						str += createOID(TagType.COMMENTDEF, hash.get(domainKey), hash.get("Dataset Name"), hash.get("Variable Name"), hash.get("Value Key"), "");
					/* If the CommentDef tag is for a where-clause */
					} else if (reader.getTableName().equals(this.config.defineValueTableName) &&
							reader.getWhereClause()[0].getVariable().equals("WhereClause Comment")) {
						str += createOID(TagType.COMMENTDEF, hash.get(wDomainKey), hash.get("W Dataset Name"), hash.get("W Variable Name"), hash.get("W Value Key"), "WC");
					/* If the CommentDef tag is for analysis results metadata */
					} else if (reader.getTableName().equals(this.config.defineResult1TableName)) {
						str += createOID(TagType.COMMENTDEF, "", "", "", "", "Datasets." + hash.get("W Display Name") + "." + hash.get("Result Key"));
					/* If the CommentDef tag is for a where-clause in value-level metadata */
					} else if (reader.getTableName().equals(this.config.defineResult2TableName)) {
						str += createOID(TagType.COMMENTDEF, "", "", "", "", "WC." + hash.get("W Display Name") + "." + hash.get("W Result Key") + "." + hash.get("W Dataset Name"));
					}

					str += "\">";
					writer.write(str);
					writer.newLine();
					indent++;

					str = insertIndent(indent);
					str += "<Description>";
					writer.write(str);
					writer.newLine();
					indent++;

					str = insertIndent(indent);
					str += "<TranslatedText xml:lang=\"";
					if (reader.getTableName().equals(this.config.defineValueTableName) &&
							reader.getWhereClause()[0].getVariable().equals("WhereClause Comment")) {
						str += (hash.get("W xml:lang") != null && !hash.get("W xml:lang").equals("") ? hash.get("W xml:lang") : DEFAULTLANG)
								+ "\">" + XmlGenerator.escapeString(hash.get("WhereClause Comment"));
					} else if (reader.getTableName().equals(this.config.defineResult1TableName)) {
						str += (hash.get("Datasets xml:lang") != null && !hash.get("Datasets xml:lang").equals("") ? hash.get("Datasets xml:lang") : DEFAULTLANG)
								+ "\">" + XmlGenerator.escapeString(hash.get("Datasets Comment"));
					} else if (reader.getTableName().equals(this.config.defineResult2TableName)) {
						str += (hash.get("W xml:lang") != null && !hash.get("W xml:lang").equals("") ? hash.get("W xml:lang") : DEFAULTLANG)
								+ "\">" + XmlGenerator.escapeString(hash.get("WhereClause Comment"));
					} else {
						str += (hash.get("xml:lang") != null && !hash.get("xml:lang").equals("") ? hash.get("xml:lang") : DEFAULTLANG)
								+ "\">" + XmlGenerator.escapeString(hash.get("Comment"));
					}
					str += "</TranslatedText>";
					writer.write(str);
					writer.newLine();

					indent--;
					str = insertIndent(indent);
					str += "</Description>";
					writer.write(str);
					writer.newLine();

					if (reader.getTableName().equals(this.config.defineValueTableName) &&
							reader.getWhereClause()[0].getVariable().equals("WhereClause Comment")) {
						//Do nothing - DocumentRef for WhereClause is not supported by this version of Define.xml Generator.
					} else {
						if (hash.get("DocumentID") != null && !hash.get("DocumentID").equals("")) {
							String[] checkValues = hash.get("DocumentID").split(DELIMITER);
							for (int i = 0; i < checkValues.length ; i++) {
								if (hash.get("Document Page Type") != null && hash.get("Document Page Reference") != null &&
										i < Arrays.asList(hash.get("Document Page Type").split(DELIMITER)).size() &&
										i < Arrays.asList(hash.get("Document Page Reference").split(DELIMITER)).size() &&
										!Arrays.asList(hash.get("Document Page Type").split(DELIMITER)).get(i).trim().equals("")) {
									str = insertIndent(indent);
									str += "<def:DocumentRef leafID=\""
											+ createOID(TagType.LEAF, "", "", "", "", checkValues[i].trim())
											+ "\">";
									writer.write(str);
									writer.newLine();
									indent++;

									str = insertIndent(indent);
									str += "<def:PDFPageRef PageRefs=\"" + Arrays.asList(hash.get("Document Page Reference").split(DELIMITER)).get(i).trim()
											+ "\" Type=\"" + Arrays.asList(hash.get("Document Page Type").split(DELIMITER)).get(i).trim() + "\"/>";
									writer.write(str);
									writer.newLine();

									indent--;
									str = insertIndent(indent);
									str += "</def:DocumentRef>";
									writer.write(str);
									writer.newLine();
								} else {
									str = insertIndent(indent);
									str += "<def:DocumentRef leafID=\""
											+ createOID(TagType.LEAF, "", "", "", "", checkValues[i].trim())
											+ "\"/>";
									writer.write(str);
									writer.newLine();
								}
							}
						}
					}

					indent--;
					str = insertIndent(indent);
					str += "</def:CommentDef>";
					writer.write(str);
					writer.newLine();
				}
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	public void writeLeafSection() throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {
			reader.setTable(this.config.defineDocumentTableName);

			Hashtable<String, String> hash = new Hashtable<String, String>();
			String str = null;

			while ((hash = reader.read()) != null) {
				errHint.setErrorHint(TagType.LEAF, "", "", "", hash.get("ID"));

				str = insertIndent(indent);
				str += "<def:leaf ID=\""
						+ createOID(TagType.LEAF, "", "", "", "", hash.get("ID"))
						+ "\" xlink:href=\"" + hash.get("href")
						+ "\">";
				writer.write(str);
				writer.newLine();
				indent++;

				str = insertIndent(indent);
				str += "<def:title>" + hash.get("Title")
						+ "</def:title>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</def:leaf>";
				writer.write(str);
				writer.newLine();
			}

			reader.close();
		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
	}

	public void writeAnalysisResultSection() throws TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException {
		try {
			reader.setTable(this.config.defineResult1TableName);

			Hashtable<String, String> hash = null;		//Used to retain a row from RESULT_1 table
			Hashtable<String, String> hash2 = null;		//Used to retain a row from RESULT_2 table
			String str = null;
			String[] checkValues = null;	//Used to contain split values (when a variable is split by delimiters)

			str = insertIndent(indent);
			str += "<arm:AnalysisResultDisplays>";
			writer.write(str);
			writer.newLine();
			indent++;

			do {

				/*
				 * If this is the first time to read the RESULT1 sheet, then read a line.
				 */
				if (hash == null) {
					hash = reader.read();
				}

				errHint.setErrorHint(TagType.RESULTDISPLAY, "", "", "", "Display Name=" + hash.get("Display Name"));

				str = insertIndent(indent);
				str += "<arm:ResultDisplay OID=\"" + createOID(TagType.RESULTDISPLAY, "", "", "", "", hash.get("Display Name"))
						+ "\" Name=\"" + hash.get("Display Name")
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
				str += "<TranslatedText xml:lang=\""
						+ (hash.get("Display xml:lang") != null && !hash.get("Display xml:lang").equals("") ? hash.get("Display xml:lang") : DEFAULTLANG)
						+ "\">"
						+ XmlGenerator.escapeString(hash.get("Display Description"))
						+ "</TranslatedText>";
				writer.write(str);
				writer.newLine();

				indent--;
				str = insertIndent(indent);
				str += "</Description>";
				writer.write(str);
				writer.newLine();

				if (hash.get("Leaf ID") != null && !hash.get("Leaf ID").equals("")) {
					checkValues = hash.get("Leaf ID").split(DELIMITER);
					for (int i = 0; i < checkValues.length ; i++) {
						if (hash.get("Leaf Page Type") != null && hash.get("Leaf Page Reference") != null &&
								i < Arrays.asList(hash.get("Leaf Page Type").split(DELIMITER)).size() &&
								i < Arrays.asList(hash.get("Leaf Page Reference").split(DELIMITER)).size() &&
								!Arrays.asList(hash.get("Leaf Page Type").split(DELIMITER)).get(i).trim().equals("")) {
							str = insertIndent(indent);
							str += "<def:DocumentRef leafID=\""
									+ createOID(TagType.DOCUMENTREF, "", "", "", "", checkValues[i].trim())
									+ "\">";
							writer.write(str);
							writer.newLine();
							indent++;

							str = insertIndent(indent);
							str += "<def:PDFPageRef PageRefs=\"" + hash.get("Leaf Page Reference")
									+ "\" Type=\"" + hash.get("Leaf Page Type")
									+ "\"/>";
							writer.write(str);
							writer.newLine();

							indent--;
							str = insertIndent(indent);
							str += "</def:DocumentRef>";
							writer.write(str);
							writer.newLine();
						} else {
							str = insertIndent(indent);
							str += "<def:DocumentRef leafID=\""
									+ createOID(TagType.DOCUMENTREF, "", "", "", "", checkValues[i].trim())
									+ "\"/>";
							writer.write(str);
							writer.newLine();
						}
					}
				}

				do {
					str = insertIndent(indent);
					str += "<arm:AnalysisResult OID=\""
							+ createOID(TagType.RESULT, "", "", "", "", hash.get("W Display Name") + "." + hash.get("Result Key"))
							+ ((hash.get("ParameterOID Dataset") != null && !hash.get("ParameterOID Dataset").equals("")) ?
									"\" ParameterOID=\"" + createOID(TagType.ITEMDEF, hash.get("ParameterOID Dataset"), hash.get("ParameterOID Dataset"), "PARAMCD", "", "") : "")
							+ "\" AnalysisReason=\"" + hash.get("Analysis Reason")
							+ "\" AnalysisPurpose=\"" + hash.get("Analysis Purpose")
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
					str += "<TranslatedText xml:lang=\""
							+ (hash.get("Result xml:lang") != null && !hash.get("Result xml:lang").equals("") ? hash.get("Result xml:lang") : DEFAULTLANG)
							+ "\">"
							+ XmlGenerator.escapeString(hash.get("Result Description"))
							+ "</TranslatedText>";
					writer.write(str);
					writer.newLine();

					indent--;
					str = insertIndent(indent);
					str += "</Description>";
					writer.write(str);
					writer.newLine();

					str = insertIndent(indent);
					str += "<arm:AnalysisDatasets"
							+ ((hash.get("Datasets Comment") != null && !hash.get("Datasets Comment").equals("")) ?
									" def:CommentOID=\"" + createOID(TagType.COMMENTDEF, "", "", "", "", "Datasets." + hash.get("W Display Name") + "." + hash.get("Result Key")) + "\"" : "")
							+ ">";
					writer.write(str);
					writer.newLine();
					indent++;

					reader2.setTable(this.config.defineResult2TableName,
							new WhereClause[] { new WhereClause("Display Name", WhereClause.Operator.EQ, hash.get("W Display Name")),
								new WhereClause("Result Key", WhereClause.Operator.EQ, hash.get("Result Key")) });
					while ((hash2 = reader2.read()) != null) {
						str = insertIndent(indent);
						str += "<arm:AnalysisDataset ItemGroupOID=\"" + createOID(TagType.ITEMGROUPDEF, hash2.get("Dataset Name"), hash2.get("Dataset Name"), "", "", "") + "\">";
						writer.write(str);
						writer.newLine();
						indent++;

						if (hash2.get("WhereClauseVariable") != null && hash2.get("WhereClauseVariable") != "") {
							str = insertIndent(indent);
							str += "<def:WhereClauseRef WhereClauseOID=\""
									+ createOID(TagType.WHERECLAUSEREF, "", "", "", "", hash2.get("Display Name") + "." + hash.get("Result Key") + "." + hash2.get("Dataset Name"))
									+ "\"/>";
							writer.write(str);
							writer.newLine();
						}

						if (hash2.get("Analysis Variable") != null && hash2.get("Analysis Variable") != "") {
							checkValues = hash2.get("Analysis Variable").split(DELIMITER);
							for (int i = 0; i < checkValues.length ; i++) {
								str = insertIndent(indent);
								str += "<arm:AnalysisVariable ItemOID=\""
										+ createOID(TagType.ITEMDEF, hash2.get("Dataset Name"), hash2.get("Dataset Name"), checkValues[i].trim(), "", "")
										+ "\"/>";
								writer.write(str);
								writer.newLine();
							}
						}

						indent--;
						str = insertIndent(indent);
						str += "</arm:AnalysisDataset>";
						writer.write(str);
						writer.newLine();
					}

					indent--;
					str = insertIndent(indent);
					str += "</arm:AnalysisDatasets>";
					writer.write(str);
					writer.newLine();

					if ((hash.get("Documentation ID") != null && !hash.get("Documentation ID").equals(""))
							|| (hash.get("Documentation Text") != null && !hash.get("Documentation Text").equals(""))) {
						str = insertIndent(indent);
						str += "<arm:Documentation>";
						writer.write(str);
						writer.newLine();
						indent++;

						str = insertIndent(indent);
						str += "<Description>";
						writer.write(str);
						writer.newLine();
						indent++;

						str = insertIndent(indent);
						str += "<TranslatedText xml:lang=\""
								+ (hash.get("Documentation xml:lang") != null && !hash.get("Documentation xml:lang").equals("") ? hash.get("Documentation xml:lang") : DEFAULTLANG)
								+ "\">"
								+ XmlGenerator.escapeString(hash.get("Documentation Text"))
								+ "</TranslatedText>";
						writer.write(str);
						writer.newLine();

						indent--;
						str = insertIndent(indent);
						str += "</Description>";
						writer.write(str);
						writer.newLine();

						if (hash.get("Documentation ID") != null && !hash.get("Documentation ID").equals("")) {
							checkValues = hash.get("Documentation ID").split(DELIMITER);
							for (int i = 0; i < checkValues.length ; i++) {
								if (hash.get("Documentation Page Type") != null && hash.get("Documentation Page Reference") != null &&
										i < Arrays.asList(hash.get("Documentation Page Type").split(DELIMITER)).size() &&
										i < Arrays.asList(hash.get("Documentation Page Reference").split(DELIMITER)).size() &&
										!Arrays.asList(hash.get("Documentation Page Type").split(DELIMITER)).get(i).trim().equals("")) {
									str = insertIndent(indent);
									str += "<def:DocumentRef leafID=\""
											+ createOID(TagType.DOCUMENTREF, "", "", "", "", checkValues[i].trim())
											+ "\">";
									writer.write(str);
									writer.newLine();
									indent++;

									str = insertIndent(indent);
									str += "<def:PDFPageRef PageRefs=\"" + hash.get("Documentation Page Reference")
											+ "\" Type=\"" + hash.get("Documentation Page Type")
											+ "\"/>";
									writer.write(str);
									writer.newLine();

									indent--;
									str = insertIndent(indent);
									str += "</def:DocumentRef>";
									writer.write(str);
									writer.newLine();

								} else {
									str = insertIndent(indent);
									str += "<def:DocumentRef leafID=\""
											+ createOID(TagType.DOCUMENTREF, "", "", "", "", checkValues[i].trim())
											+ "\"/>";
									writer.write(str);
									writer.newLine();
								}
							}
						}

						indent--;
						str = insertIndent(indent);
						str += "</arm:Documentation>";
						writer.write(str);
						writer.newLine();
					}

					if ((hash.get("Programming Code Text") != null && !hash.get("Programming Code Text").equals(""))
							|| (hash.get("Programming Code Document ID") != null && !hash.get("Programming Code Document ID").equals(""))) {
						str = insertIndent(indent);
						str += "<arm:ProgrammingCode"
								+ ((hash.get("Programming Code Context") != null && !hash.get("Programming Code Context").equals("")) ?
										" Context=\"" + hash.get("Programming Code Context") + "\"" : "")
								+ ">";
						writer.write(str);
						writer.newLine();
						indent++;

						if (hash.get("Programming Code Text") != null && !hash.get("Programming Code Text").equals("")) {
							str = insertIndent(indent);
							str += "<arm:Code>";
							writer.write(str);
							writer.newLine();

							str = XmlGenerator.escapeString(hash.get("Programming Code Text"));
							writer.write(str);
							writer.newLine();

							str = insertIndent(indent);
							str += "</arm:Code>";
							writer.write(str);
							writer.newLine();
						}

						if (hash.get("Programming Code Document ID") != null && !hash.get("Programming Code Document ID").equals("")) {
							checkValues = hash.get("Programming Code Document ID").split(DELIMITER);
							for (int i = 0; i < checkValues.length ; i++) {
								if (hash.get("Programming Code Document Page Type") != null && hash.get("Programming Code Document Page Reference") != null &&
										i < Arrays.asList(hash.get("Programming Code Document Page Type").split(DELIMITER)).size() &&
										i < Arrays.asList(hash.get("Programming Code Document Page Reference").split(DELIMITER)).size() &&
										!Arrays.asList(hash.get("Programming Code Document Page Type").split(DELIMITER)).get(i).trim().equals("")) {
									str = insertIndent(indent);
									str += "<def:DocumentRef leafID=\""
											+ createOID(TagType.DOCUMENTREF, "", "", "", "", checkValues[i].trim())
											+ "\">";
									writer.write(str);
									writer.newLine();
									indent++;

									str = insertIndent(indent);
									str += "<def:PDFPageRef PageRefs=\"" + hash.get("Programming Code Document Page Reference")
											+ "\" Type=\"" + hash.get("Programming Code Document Page Type")
											+ "\"/>";
									writer.write(str);
									writer.newLine();

									indent--;
									str = insertIndent(indent);
									str += "</def:DocumentRef>";
									writer.write(str);
									writer.newLine();
								} else {
									str = insertIndent(indent);
									str += "<def:DocumentRef leafID=\""
											+ createOID(TagType.DOCUMENTREF, "", "", "", "", checkValues[i].trim())
											+ "\"/>";
									writer.write(str);
									writer.newLine();
								}
							}
						}

						indent--;
						str = insertIndent(indent);
						str += "</arm:ProgrammingCode>";
						writer.write(str);
						writer.newLine();
					}

					indent--;
					str = insertIndent(indent);
					str += "</arm:AnalysisResult>";
					writer.write(str);
					writer.newLine();

					hash = reader.read();

				/*
				 * If the next line belongs to the same Analysis Display, then continue.
				 * If this is the end of an Analysis Display or the end of the spreadsheet, then finish.
				 */
				} while (hash != null && (hash.get("Display Name") == null || hash.get("Display Name").equals("")));

				indent--;
				str = insertIndent(indent);
				str += "</arm:ResultDisplay>";
				writer.write(str);
				writer.newLine();

			} while (hash != null);

			indent--;
			str = insertIndent(indent);
			str += "</arm:AnalysisResultDisplays>";
			writer.write(str);
			writer.newLine();
			reader.close();
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
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
		writer.close();
		sw.close();
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
	 *
	 */
	private String createOID(TagType tagType, String domainKey, String datasetName, String variableName, String valueKey, String param) throws InvalidOidSyntaxException, RequiredValueMissingException {
		//errHint should not be set here because it is already set by the caller of this method.
		try {
			String str = null;
			String formattedDatasetName = null;
			if (!datasetName.equals("")) formattedDatasetName = datasetName.replace(" ", "").replace(DELIMITER, "-");
			switch (tagType) {
			case ITEMGROUPDEF:
				if (!domainKey.equals("")) {
					str = "IG." + domainKey;
				} else {
					throw new InvalidOidSyntaxException("ItemGroupDef");
				}
				break;
			case ITEMDEF: case ITEMREF:
				if (!domainKey.equals("") && !variableName.equals("")) {
					/* If the ItemDef tag is for value-level metadata */
					if (!valueKey.equals("")) {
						if (this.datasetType.equals(Config.DatasetType.ADaM)) {
							str = "IT." + domainKey + "." + variableName + "." + valueKey;
						} else {
							/* Create an OID using the dataset name (i.e. datasetName), keeping split datasets in mind */
							str = "IT." + formattedDatasetName + "." + variableName + "." + valueKey;
						}
					/* If the ItemDef tag is for variable-level metadata */
					} else {
						if ((this.datasetType.equals(Config.DatasetType.SDTM) || this.datasetType.equals(Config.DatasetType.SEND))
								&& (variableName.equals("STUDYID") || variableName.equals("USUBJID"))) {
							str = "IT." + variableName;
						} else {
							if (this.datasetType.equals(Config.DatasetType.ADaM)) {
								str = "IT." + domainKey + "." + variableName;
							} else {
								if (this.suppUtil.isAutoSuppActive() == true && variableName.equals("RDOMAIN")) {
									if (domainKey.startsWith("SUPP") && !domainKey.equals("SUPPQUAL")) {
										str = "IT.SUPP." + variableName;
									} else {
										str = "IT." + domainKey + "." + variableName;
									}
								} else {
									/* Create an OID using the dataset name (i.e. datasetName), keeping split datasets in mind */
									str = "IT." + formattedDatasetName + "." + variableName;
								}
							}
						}
					}
				} else {
					throw new InvalidOidSyntaxException("ItemDef/ItemRef");
				}
				break;
			case VALUELISTDEF: case VALUELISTREF:
				if (!domainKey.equals("") && !variableName.equals("")) {
					if (this.datasetType.equals(Config.DatasetType.ADaM)) {
						str = "VL." + domainKey + "." + variableName;
					} else {
						/* Create an OID using the dataset name (i.e. datasetName), keeping split datasets in mind */
						str = "VL." + domainKey + "." + formattedDatasetName + "." + variableName;
					}
				} else {
					throw new InvalidOidSyntaxException("ValueListDef/ValueListRef");
				}
				break;
			case WHERECLAUSEDEF: case WHERECLAUSEREF:
				if (!domainKey.equals("") && !variableName.equals("")) {
					if (this.datasetType.equals(Config.DatasetType.ADaM)) {
						str = "WC." + domainKey + "." + variableName + "." + valueKey;
					} else {
						/* Create an OID using the dataset name (i.e. datasetName), keeping split datasets in mind */
						str = "WC." + domainKey + "." + formattedDatasetName + "." + variableName + "." + valueKey;
					}
				} else {
					if (!param.equals("")) {
						str = "WC." + param;
					} else {
						throw new InvalidOidSyntaxException("WhereClauseDef/WhereClauseRef");
					}
				}
				break;
			case CODELIST: case CODELISTREF:
				if (!param.equals("")) {
					str = "CL." + param;
				} else {
					throw new InvalidOidSyntaxException("CodeListDef/CodeListRef");
				}
				break;
			case METHODDEF:
				if (!domainKey.equals("") && !variableName.equals("")) {
					/* If the MethodDef tag is for value-level metadata */
					if (!valueKey.equals("")) {
						if (!variableName.equals("STUDYID") && !variableName.equals("USUBJID")) {
							if (this.datasetType.equals(Config.DatasetType.ADaM)) {
								str = "MT." + domainKey + "." + variableName + "." + valueKey;
							} else {
								/* Create an OID using the dataset name (i.e. datasetName), keeping split datasets in mind */
								str = "MT." + domainKey + "." + formattedDatasetName + "." + variableName + "." + valueKey;
							}
						} else {
							str = "MT." + variableName + "." + valueKey;
						}
						/* If the MethodDef tag is for variable-level metadata */
					} else {
						if (!variableName.equals("STUDYID") && !variableName.equals("USUBJID")
								&& !(this.suppUtil.isAutoSuppActive() == true && variableName.equals("RDOMAIN"))) {
							if (this.datasetType.equals(Config.DatasetType.ADaM)) {
								str = "MT." + domainKey + "." + variableName;
							} else {
								/* Create an OID using the dataset name (i.e. datasetName), keeping split datasets in mind */
								str = "MT." + domainKey + "." + formattedDatasetName + "." + variableName;
							}
						/* If the MethodDef tag is for RDOMAIN in the Auto-SUPP mode */
						} else if (this.suppUtil.isAutoSuppActive() == true && variableName.equals("RDOMAIN")) {
							if (domainKey.startsWith("SUPP") && !domainKey.equals("SUPPQUAL")) {
								str = "MT.SUPP." + variableName;
							} else {
								str = "MT." + domainKey + "." + variableName;
							}
						/* If the MethodDef tag is for STUDYID or USUBJID */
						} else {
							str = "MT." + variableName;
						}
					}
				} else if (!param.equals("")) {
					/* The method is for analysis result metadata. */
					str = "MT." + param;
				} else {
					throw new InvalidOidSyntaxException("MethodDef/MethodRef");
				}
				break;
			case COMMENTDEF:
				if (!domainKey.equals("")) {
					if (!variableName.equals("")) {
						if (!valueKey.equals("")) {
							if (!param.equals("")) {
								/* If this is a comment for a where-clause */
								if (this.datasetType.equals(Config.DatasetType.ADaM)) {
									str = "COM." + domainKey + "." + variableName + "." + valueKey + "." + param;
								} else {
									/* Create an OID using the dataset name (i.e. datasetName), keeping split datasets in mind */
									str = "COM." + domainKey + "." + formattedDatasetName + "." + variableName + "." + valueKey + "." + param;
								}
							} else {
								/* If this is a comment for a value */
								if (this.datasetType.equals(Config.DatasetType.ADaM)) {
									str = "COM." + domainKey + "." + variableName + "." + valueKey;
								} else {
									/* Create an OID using the dataset name (i.e. datasetName), keeping split datasets in mind */
									str = "COM." + domainKey + "." + formattedDatasetName + "." + variableName + "." + valueKey;
								}
							}
						} else {
							/* If this is a comment for a variable */
							if (variableName.equals("STUDYID") || variableName.equals("USUBJID")) {
								if (this.datasetType.equals(Config.DatasetType.ADaM)) {
									str = "COM." + domainKey + "." + variableName;
								} else {
									str = "COM." + variableName;
								}
							} else {
								if (this.datasetType.equals(Config.DatasetType.ADaM)) {
									str = "COM." + domainKey + "." + variableName;
								} else {
									/* Create an OID using the dataset name (i.e. datasetName), keeping split datasets in mind */
									str = "COM." + domainKey + "." + formattedDatasetName + "." + variableName;
								}
							}
						}
					} else {
						/* If this is a comment for a domain */
						str = "COM." + domainKey;
					}
				} else {
					if (!param.equals("")) {
						str = "COM." + param;
					} else {
						throw new InvalidOidSyntaxException("CommentDef/CommentRef");
					}
				}
				break;
			case LEAF: case DOCUMENTREF:
				if (!param.equals("")) {
					str = "LF." + param;
				} else {
					throw new InvalidOidSyntaxException("Leaf/DocumentRef");
				}
				break;
			case RESULTDISPLAY:
				if (!param.equals("")) {
					str = "RD." + param;
				} else {
					throw new InvalidOidSyntaxException("AnalysisResultDisplay");
				}
				break;
			case RESULT:
				if (!param.equals("")) {
					str = "AR." + param;
				} else {
					throw new InvalidOidSyntaxException("AnalysisResult");
				}
				break;
			default:
				throw new InvalidOidSyntaxException("Unknown");
			}

			return str;
		} catch (NullPointerException ex) {
			throw new RequiredValueMissingException(ex, errHint);
		}
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

	/*
	 * This method formats a codelist id provided into correct format.
	 * e.g. (AGEU) -> AGEU
	 */
	private static String formatCodeListID (String strId) {
//		if (strId.startsWith("(") && strId.endsWith(")")) {
//			return strId.substring(1, strId.length() - 1);
//		} else {
			return strId;
//		}
	}

	/*
	 * This method creates SASFormatName attribute from a given name.
	 * The method is incomplete in this version of Define.xml Generator and should not be used.
	 */
	private static String createSASFormatName (String strId) {
		if (strId.length() > 8) {
			return strId.substring(0, 7);
		} else {
			return strId;
		}
	}
}
