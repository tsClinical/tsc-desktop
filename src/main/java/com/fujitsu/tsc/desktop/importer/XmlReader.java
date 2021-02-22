/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.poi.ss.usermodel.Row;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.fujitsu.tsc.desktop.util.ErrorInfo;
import com.fujitsu.tsc.desktop.validator.ValidationResult;

public class XmlReader extends DefaultHandler {

//	private static ArrayList<SAXParseException> result;
//
//	private static Logger logger;

	private String oid;
	private String domain;
	private String datasetName;
	private String codeListName;
	private String dataType;
	private String sasFormatName;

	private String displayName;
	private String displayDescription;
	private String displayXmlLang;
	private String displayLeafID;
	private String displayPageType;
	private String displayPageRefs;
	private String analysisOID;

	private String whereClauseCommentOID;


//	private static Hashtable<String, String> studyDef;
	private Hashtable<String, String> documentDef;
	private Hashtable<String, String> itemGroupDef;
	private Hashtable<String, String> variableItemRef;
	private Hashtable<String, String> valueItemRef;
	private Hashtable<String, String> whereClauseDef;
	private Hashtable<String, String> itemDef;
	private Hashtable<String, String> commentDef;
	private Hashtable<String, String> methodDef;
	private Hashtable<String, String> codeListDef;
	private Hashtable<String, String> codeListItemDef;
//	private Hashtable<String, String> enumeratedItemDef;
	private Hashtable<String, String> dictionaryDef;
	private Hashtable<String, String> leafDef;
	private Hashtable<String, String> analysisResult;
	private Hashtable<String, String> analysisDataset;

	private boolean isItemGroupDefFlag;
	private boolean isCommentDefFlag;
	private boolean isItemDefFlag;
	private boolean isMethodDefFlag;
	private boolean isCodeListDefFlag;
	private boolean isValueListDefFlag;
	private boolean isWhereClauseDefFlag;
	private boolean isCodeListItemFlag;
	private boolean isEnumeratedItemFlag;
	private boolean isAnnotatedCRFFlag;
	private boolean isSupplementalDocFlag;
	private boolean isLeafDefFlag;
	private boolean isResultDesplayFlag;
	private boolean isAnalysisResultFlag;
	private boolean isDocumentationFlag;
	private boolean isProgrammingCodeFlag;
	private boolean isNotOneDocumentFlag;
	private boolean isItemDefOriginFlag;

	 // Elementの格納場所
	 private StringBuffer leaf = null;

	 private ValidationResult vResult;
	 private XmlReadContainer container;
	 private ArrayList<ErrorInfo> errors;
//	 private boolean result;

	public XmlReader(){
		vResult = new ValidationResult();
		container = new XmlReadContainer();
		errors = new ArrayList<ErrorInfo>();

		isItemGroupDefFlag = false;
		isCommentDefFlag = false;
		isItemDefFlag = false;
		isMethodDefFlag = false;
		isCodeListDefFlag = false;
		isValueListDefFlag = false;
		isWhereClauseDefFlag = false;
		isCodeListItemFlag = false;
		isEnumeratedItemFlag = false;
		isAnnotatedCRFFlag = false;
		isSupplementalDocFlag = false;
		isLeafDefFlag = false;
		isResultDesplayFlag = false;
		isAnalysisResultFlag = false;
		isDocumentationFlag = false;
		isProgrammingCodeFlag = false;
		isNotOneDocumentFlag = false;
		isItemDefOriginFlag = false;

	}

	public void startDocument() {
//		System.out.println("startDocument");
	}
	/*
	 * operating StartTag. -get attribute
	 */
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {

		   leaf = new StringBuffer();

		/*ODM*/
		if (qName.equals("ODM")) {
			container.studyDef = new Hashtable();
			if (atts.getValue("ODMVersion") != null) {
				container.studyDef.put("ODMVersion", atts.getValue("ODMVersion"));
			}
			if (atts.getValue("FileType") != null) {
				container.studyDef.put("FileType", atts.getValue("FileType"));
			}
			if (atts.getValue("FileOID") != null) {
				container.studyDef.put("FileOID", atts.getValue("FileOID"));
			}
			if (atts.getValue("CreationDateTime") != null) {
				container.studyDef.put("CreationDateTime", atts.getValue("CreationDateTime"));
			}
			if (atts.getValue("AsOfDateTime") != null) {
				container.studyDef.put("AsOfDateTime", atts.getValue("AsOfDateTime"));
			}
			if (atts.getValue("Originator") != null) {
				container.studyDef.put("Originator", atts.getValue("Originator"));
			}
			if (atts.getValue("SourceSystem") != null) {
				container.studyDef.put("SourceSystem", atts.getValue("SourceSystem"));
			}
			if (atts.getValue("SourceSystemVersion") != null) {
				container.studyDef.put("SourceSystemVersion", atts.getValue("SourceSystemVersion"));
			}

			/*ODM/Study*/
		} else if (qName.equals("Study")) {
			if (atts.getValue("OID") != null) {
				container.studyDef.put("StudyOID", atts.getValue("OID"));
			}

			/*ODM/Study/MetaDataVersion*/
		} else if (qName.equals("MetaDataVersion")) {
			if (atts.getValue("OID") != null) {
				container.studyDef.put("MetaDataOID", atts.getValue("OID"));
			}
			if (atts.getValue("Name") != null) {
				container.studyDef.put("MetaDataName", atts.getValue("Name"));
			}
			if (atts.getValue("Description") != null) {
				container.studyDef.put("MetaDataDescription", atts.getValue("Description"));
			}
			if (atts.getValue("def:DefineVersion") != null) {
				container.studyDef.put("DefineVersion", atts.getValue("def:DefineVersion"));
			}
			if (atts.getValue("def:StandardName") != null) {
				container.studyDef.put("StandardName", atts.getValue("def:StandardName"));
			}
			if (atts.getValue("def:StandardVersion") != null) {
				container.studyDef.put("StandardVersion", atts.getValue("def:StandardVersion"));
			}

			/*def:AnnptatedCRF*/
		} else if (qName.equals("def:AnnotatedCRF")) {
			isAnnotatedCRFFlag = true;

			/*def:AnnotatedCRF/def:DocumentRef*/
		} else if (isAnnotatedCRFFlag && qName.equals("def:DocumentRef")) {
			documentDef = new Hashtable<String, String>();
			documentDef.put("Type", "AnnotatedCRF");
			if (atts.getValue("leafID") != null) {
			documentDef.put("leafID", atts.getValue("leafID"));
			}
			container.documentDefList.put(atts.getValue("leafID"), documentDef);

			/*def:def:SupplementalDoc*/
		} else if (qName.equals("def:SupplementalDoc")) {
			isSupplementalDocFlag = true;

			/*def:SupplementalDoc/def:DocumentRef*/
		} else if (isSupplementalDocFlag && qName.equals("def:DocumentRef")) {
			documentDef = new Hashtable<String, String>();
			documentDef.put("Type", "SupplementalDoc");
			if (atts.getValue("leafID") != null) {
			documentDef.put("leafID", atts.getValue("leafID"));
			}
			container.documentDefList.put(atts.getValue("leafID"),documentDef);

			/*def:ValueListDef*/
		} else if (qName.equals("def:ValueListDef")) {
			isValueListDefFlag = true;
			container.valueItemRefList = new ArrayList<Hashtable<String, String>>();
			oid = atts.getValue("OID");

			/*def:ValueListDef/ItemRef*/
		} else if (isValueListDefFlag && qName.equals("ItemRef")) {
			valueItemRef = new Hashtable<String, String>();
			if (oid != null) {
				valueItemRef.put("ValueListOID", oid);
			}
			if (atts.getValue("ItemOID") != null) {
				valueItemRef.put("ItemOID", atts.getValue("ItemOID"));
			}
			if (atts.getValue("Mandatory") != null) {
			valueItemRef.put("Mandatory", atts.getValue("Mandatory"));
			}
			if(atts.getValue("KeySequence") != null) {
				valueItemRef.put("KeySequence", atts.getValue("KeySequence"));
			}
			if(atts.getValue("MethodOID") != null) {
				valueItemRef.put("MethodOID", atts.getValue("MethodOID"));
			}
			if(atts.getValue("Role") != null){
				valueItemRef.put("Role", atts.getValue("Role"));
			}
			if(atts.getValue("RoleCodeListOID") != null){
				valueItemRef.put("RoleCodelistOID", atts.getValue("RoleCodeListOID"));
			}

			/*def:ValueListDef/ItemRef/def:WhereClauseRef*/
		} else if (isValueListDefFlag && qName.equals("def:WhereClauseRef")) {
			if (atts.getValue("WhereClauseOID") != null) {
				valueItemRef.put("WhereClauseOID", atts.getValue("WhereClauseOID"));
			}

			/*def:WhereClauseDef*/
		} else if (qName.equals("def:WhereClauseDef")) {
//			whereClauseDef = new Hashtable<String, String>();
			container.rankCheckList = new ArrayList<Hashtable<String,String>>();
			isWhereClauseDefFlag = true;
			oid = atts.getValue("OID");
			if (atts.getValue("def:CommentOID") != null) {
				whereClauseCommentOID =atts.getValue("def:CommentOID");
			}

			/*def:WhereClauseDef/RangeCheck*/
		} else if (isWhereClauseDefFlag && qName.equals("RangeCheck")) {
			whereClauseDef = new Hashtable<String, String>();
			if (whereClauseCommentOID != null) {
				whereClauseDef.put("def:CommentOID", whereClauseCommentOID);
			}
			if (atts.getValue("SoftHard") != null) {
				whereClauseDef.put("SoftHard", atts.getValue("SoftHard"));
			}
			if (atts.getValue("def:ItemOID") != null) {
				whereClauseDef.put("def:ItemOID", atts.getValue("def:ItemOID"));
			}
			if (atts.getValue("Comparator") != null) {
				whereClauseDef.put("Comparator", atts.getValue("Comparator"));
			}

			/*ItemGroupDef*/
		} else if (qName.equals("ItemGroupDef")) {
			isItemGroupDefFlag = true;
			itemGroupDef = new Hashtable();
			if (atts.getValue("OID") != null) {
				itemGroupDef.put("OID", atts.getValue("OID"));
				domain = atts.getValue("Domain");
			}
			if (atts.getValue("Domain") != null) {
				itemGroupDef.put("Domain", atts.getValue("Domain"));
				domain = atts.getValue("Domain");
			}
			if (atts.getValue("Name") != null) {
				itemGroupDef.put("DatasetName", atts.getValue("Name"));
				datasetName = atts.getValue("Name");
			}
			if (atts.getValue("Repeating") != null) {
				itemGroupDef.put("Repeating", atts.getValue("Repeating"));
			}
			if (atts.getValue("IsReferenceData") != null) {
				itemGroupDef.put("IsReferenceData", atts.getValue("IsReferenceData"));
			}
			if (atts.getValue("SASDatasetName") != null) {
				itemGroupDef.put("SASDatasetName", atts.getValue("SASDatasetName"));
			}
			if (atts.getValue("Purpose") != null) {
				itemGroupDef.put("Purpose", atts.getValue("Purpose"));
			}
			if (atts.getValue("def:Structure") != null) {
				itemGroupDef.put("Structure",atts.getValue("def:Structure"));
			}
			if (atts.getValue("def:Class") != null) {
				itemGroupDef.put("Class", atts.getValue("def:Class"));
			}
			if(atts.getValue("def:CommentOID") != null){
				itemGroupDef.put("CommentOID", atts.getValue("def:CommentOID"));
			}
			if (atts.getValue("def:ArchiveLocationID") != null) {
				itemGroupDef.put("ArchiveLocationID", atts.getValue("def:ArchiveLocationID"));
			}

			/*ItemGroupDef/Description/TransratedText*/
		} else if (isItemGroupDefFlag && qName.equals("TranslatedText")) {
			if (atts.getValue("xml:lang") != null) {
				itemGroupDef.put("xml:lang", atts.getValue("xml:lang"));
			}

			/*ItemGroupDef/Alias*/
		} else if (isItemGroupDefFlag && qName.equals("Alias")) {
			//only getting first one
			if (!itemGroupDef.containsKey("Alias")){
				if (atts.getValue("Context") != null) {
					itemGroupDef.put("Context", atts.getValue("Context"));
				}
				if (atts.getValue("Name") != null) {
					itemGroupDef.put("Alias", atts.getValue("Name"));
				}
			}

			/*ItemGroupDef/def:leaf*/
		} else if (isItemGroupDefFlag && qName.equals("def:leaf")) {
			if (atts.getValue("xlink:href") != null) {
				itemGroupDef.put("href", atts.getValue("xlink:href"));
			}

			/*ItemGroupDef/ItemRef*/
		}else if (isItemGroupDefFlag && qName.equals("ItemRef")) {
			variableItemRef = new Hashtable<String,String>();
			if (domain != null) {
				//from domain of ItemGrouDef attibute
				variableItemRef.put("Domain", domain);
			}
			if (datasetName != null) {
				//from datasetName of ItemGroupDef attribute
				variableItemRef.put("DatasetName", datasetName);
			}
			if (atts.getValue("ItemOID") != null) {
				variableItemRef.put("ItemOID", atts.getValue("ItemOID"));
			}
			if (atts.getValue("Mandatory") != null) {
				variableItemRef.put("Mandatory", atts.getValue("Mandatory"));
			}
			if(atts.getValue("KeySequence") != null){
				variableItemRef.put("KeySequence", atts.getValue("KeySequence"));
			}
			if(atts.getValue("Role") != null){
				variableItemRef.put("Role", atts.getValue("Role"));
			}
			if(atts.getValue("RoleCodeListOID") != null){
				variableItemRef.put("RoleCodelistOID", atts.getValue("RoleCodeListOID"));
			}
			if(atts.getValue("MethodOID") != null){
				variableItemRef.put("MethodOID", atts.getValue("MethodOID"));
			}
			container.variableItemRefList.add(variableItemRef);
			if (datasetName != null && atts.getValue("ItemOID") != null) {
				container.itemDefOIDandDatasetNameList.put(atts.getValue("ItemOID"), datasetName);
			}


			/*def:CommentDef*/
		} else if (qName.equals("def:CommentDef")) {
			isCommentDefFlag = true;
			commentDef = new Hashtable<String, String>();
			oid = atts.getValue("OID");

			/*def:CommentDef/Description/TransratedText*/
		} else if (isCommentDefFlag && qName.equals("TranslatedText")) {
			if (atts.getValue("xml:lang") != null) {
				commentDef.put("xml:lang", atts.getValue("xml:lang"));
			}

			/*def:CommentDef/def:DocumentRef*/
		} else if (isCommentDefFlag && qName.equals("def:DocumentRef")) {
			if (!commentDef.containsKey("leafID")) {
				if (atts.getValue("leafID") != null) {
					commentDef.put("leafID", atts.getValue("leafID"));
				}
			} else if (commentDef.containsKey("leafID")) {
				commentDef.put("leafID", commentDef.get("leafID")+","+atts.getValue("leafID"));
				isNotOneDocumentFlag = true;
			}

			/*def:CommentDef/Description/TransratedText/def:PDFPageRef*/
		} else if (isCommentDefFlag && qName.equals("def:PDFPageRef")) {
			if (!isNotOneDocumentFlag) {
				if (atts.getValue("PageRefs") != null) {
					commentDef.put("PageRefs", atts.getValue("PageRefs"));
				}
				if (atts.getValue("Type") != null) {
					commentDef.put("PageType", atts.getValue("Type"));
				}
			} else if (isNotOneDocumentFlag) {
				if(commentDef.containsKey("PageRefs")) {
					commentDef.put("PageRefs", commentDef.get("PageRefs")+",");
					if (atts.getValue("PageRefs") != null) {
						commentDef.put("PageRefs", commentDef.get("PageRefs")+atts.getValue("PageRefs"));
					}
				} else if (!commentDef.containsKey("PageRefs")) {
					commentDef.put("PageRefs", ",");
					if (atts.getValue("PageRefs") != null) {
						commentDef.put("PageRefs", commentDef.get("PageRefs")+atts.getValue("PageRefs"));
					}
				}
				if(commentDef.containsKey("PageType")) {
					commentDef.put("PageType", commentDef.get("PageType")+",");
					if (atts.getValue("Type") != null) {
						commentDef.put("PageType", commentDef.get("PageType")+atts.getValue("Type"));
					}
				} else if (!commentDef.containsKey("PageType")) {
					commentDef.put("PageType", ",");
					if (atts.getValue("Type") != null) {
						commentDef.put("PageType", commentDef.get("PageType")+atts.getValue("Type"));
					}
				}
			}

			/*ItemDef*/
		} else if (qName.equals("ItemDef")) {
			isItemDefFlag = true;
			itemDef = new Hashtable<String, String>();
			oid = atts.getValue("OID");
			if (atts.getValue("Name") != null) {
				itemDef.put("Name", atts.getValue("Name"));
			}
			if (atts.getValue("DataType") != null) {
			itemDef.put("DataType", atts.getValue("DataType"));
			}
			if (atts.getValue("Length") != null) {
				itemDef.put("Length", atts.getValue("Length"));
			}
			if (atts.getValue("SignificantDigits") != null) {
				itemDef.put("SignificantDigits", atts.getValue("SignificantDigits"));
			}
			if (atts.getValue("SASFieldName") != null) {
				itemDef.put("SASFieldName", atts.getValue("SASFieldName"));
			}
			if (atts.getValue("def:DisplayFormat") != null) {
				itemDef.put("DisplayFormat", atts.getValue("def:DisplayFormat"));
			}
			if (atts.getValue("def:CommentOID") != null) {
				itemDef.put("CommentOID", atts.getValue("def:CommentOID"));
			}

			/*ItemDef/Description/TranslatedText*/
		} else if (isItemDefFlag && !isItemDefOriginFlag && qName.equals("TranslatedText")) {
			if (atts.getValue("xml:lang") != null) {
				itemDef.put("xml:lang", atts.getValue("xml:lang"));
			}

			/*ItemDef/CodeListRef*/
		} else if (isItemDefFlag && qName.equals("CodeListRef")) {
			if (atts.getValue("CodeListOID") != null) {
				itemDef.put("CodeListOID", atts.getValue("CodeListOID"));
			}

			/*ItemDef/def:Origin*/
		} else if (isItemDefFlag && qName.equals("def:Origin")) {
			isItemDefOriginFlag = true;
			if (atts.getValue("Type") != null ) {
				itemDef.put("Type", atts.getValue("Type"));
			}

			/*ItemDef/def:Origin/Description/TranslatedText*/
		} else if (isItemDefFlag && isItemDefOriginFlag && qName.equals("TranslatedText")) {
			if (atts.getValue("xml:lang") != null) {
				itemDef.put("Origin xml:lang", atts.getValue("xml:lang"));
			}

			/*ItemDef/def:Origin/def:DocumentRef*/
		} else if (isItemDefFlag && qName.equals("def:DocumentRef")) {
			if (!itemDef.containsKey("leafID")) {
				if (atts.getValue("leafID") != null) {
					itemDef.put("leafID", atts.getValue("leafID"));
				}
			} else if (itemDef.containsKey("leafID")) {
				itemDef.put("leafID", itemDef.get("leafID")+","+atts.getValue("leafID"));
				isNotOneDocumentFlag = true;
			}

			/*ItemDef/def:Origin/def:PDFPageRef*/
		} else if (isItemDefFlag && qName.equals("def:PDFPageRef")) {
			if (!isNotOneDocumentFlag) {
				if (atts.getValue("PageRefs") != null) {
					itemDef.put("PageRefs", atts.getValue("PageRefs"));
				}
				if (atts.getValue("Type") != null) {
					itemDef.put("PageType", atts.getValue("Type"));
				}
			} else if (isNotOneDocumentFlag) {
				if(itemDef.containsKey("PageRefs")) {
					itemDef.put("PageRefs", itemDef.get("PageRefs")+",");
					if (atts.getValue("PageRefs") != null) {
						itemDef.put("PageRefs", itemDef.get("PageRefs")+atts.getValue("PageRefs"));
					}
				} else if (!itemDef.containsKey("PageRefs")) {
					itemDef.put("PageRefs", ",");
					if (atts.getValue("PageRefs") != null) {
						itemDef.put("PageRefs", itemDef.get("PageRefs")+atts.getValue("PageRefs"));
					}
				}
				if(itemDef.containsKey("PageType")) {
					itemDef.put("PageType", itemDef.get("PageType")+",");
					if (atts.getValue("Type") != null) {
						itemDef.put("PageType", itemDef.get("PageType")+atts.getValue("Type"));
					}
				} else if (!itemDef.containsKey("PageType")) {
					itemDef.put("PageType", ",");
					if (atts.getValue("Type") != null) {
						itemDef.put("PageType", itemDef.get("PageType")+atts.getValue("Type"));
					}
				}
			}

			/*ItemDef/def:ValueListRef*/
		} else if (isItemDefFlag && qName.equals("def:ValueListRef")) {
			if (atts.getValue("ValueListOID") != null) {
				itemDef.put("ValueListOID", atts.getValue("ValueListOID"));
				//To set ItemDef attribute(Domain,DatasetName,VariableName) to each valueItemRef.
				if (container.valueItemDefList.get(atts.getValue("ValueListOID")) != null) {
					ArrayList<Hashtable<String, String>> array = container.valueItemDefList.get(atts.getValue("ValueListOID"));
					for (int i=0; i<array.size(); i++) {
						for (int j=0; j<container.variableItemRefList.size(); j++){
							if (container.variableItemRefList.get(j).get("ItemOID") != null) {
								if (container.variableItemRefList.get(j).get("ItemOID").equals(oid)) {
									if ((String)container.variableItemRefList.get(j).get("Domain") != null) {
										array.get(i).put("Domain", (String)container.variableItemRefList.get(j).get("Domain"));
									} else {
										array.get(i).put("Domain", (String)container.variableItemRefList.get(j).get("DatasetName"));
									}
									array.get(i).put("DatasetName", (String)container.variableItemRefList.get(j).get("DatasetName"));
									array.get(i).put("VariableName", itemDef.get("Name"));
								}
							}
						}
					}
				}
			}

			/*MethodDef*/
		} else if (qName.equals("MethodDef")) {
			isMethodDefFlag = true;
			methodDef = new Hashtable<String, String>();
			oid = atts.getValue("OID");
			if (atts.getValue("Name") != null) {
				methodDef.put("Name", atts.getValue("Name"));
			}
			if (atts.getValue("Type") != null) {
				methodDef.put("Type", atts.getValue("Type"));
			}

			/*MethodDef/Description/TranslatedText*/
		} else if (isMethodDefFlag && qName.equals("TranslatedText")) {
			if (atts.getValue("xml:lang") != null) {
				methodDef.put("xml:lang", atts.getValue("xml:lang"));
			}
			/*MethodDef/def:DocumentRef*/
		} else if (isMethodDefFlag && qName.equals("def:DocumentRef")) {
			//MethodDef can have some leafID
			if (!methodDef.containsKey("leafID")) {
				if (atts.getValue("leafID") != null) {
					methodDef.put("leafID", atts.getValue("leafID"));
				}
			} else if (methodDef.containsKey("leafID")) {
				if (atts.getValue("leafID") != null) {
					methodDef.put("leafID", methodDef.get("leafID")+","+atts.getValue("leafID"));
					isNotOneDocumentFlag = true;
				}
			}

			/*MethodDef/Description/TransratedText/def:PDFPageRef*/
		} else if (isMethodDefFlag && qName.equals("def:PDFPageRef")) {
			//if MethodDef has some leafID, PegeRefs and Type have some .
			if (!isNotOneDocumentFlag) { //first one
				if (atts.getValue("PageRefs") != null) {
					methodDef.put("PageRefs", atts.getValue("PageRefs"));
				}
				if (atts.getValue("Type") != null) {
					methodDef.put("PageType", atts.getValue("Type"));
				}
			} else if (isNotOneDocumentFlag) { //not first one
				if(methodDef.containsKey("PageRefs")) {
					methodDef.put("PageRefs", methodDef.get("PageRefs")+",");
					if (atts.getValue("PageRefs") != null) {
						methodDef.put("PageRefs", methodDef.get("PageRefs")+atts.getValue("PageRefs"));
					}
				} else if (!methodDef.containsKey("PageRefs")) {
					methodDef.put("PageRefs", ",");
					if (atts.getValue("PageRefs") != null) {
						methodDef.put("PageRefs", methodDef.get("PageRefs")+atts.getValue("PageRefs"));
					}
				}
				if(methodDef.containsKey("PageType")) {
					methodDef.put("PageType", methodDef.get("PageType")+",");
					if (atts.getValue("Type") != null) {
						methodDef.put("PageType", methodDef.get("PageType")+atts.getValue("Type"));
					}
				} else if (!methodDef.containsKey("PageType")) {
					methodDef.put("PageType", ",");
					if (atts.getValue("Type") != null) {
						methodDef.put("PageType", methodDef.get("PageType")+atts.getValue("Type"));
					}
				}
			}


			/*MethodDef/FormalExpression*/
		} else if (isMethodDefFlag && qName.equals("FormalExpression")) {
			if (atts.getValue("Context") != null) {
				methodDef.put("Context", atts.getValue("Context"));
			}

			/*CodeList*/
		} else if (qName.equals("CodeList")) {
			isCodeListDefFlag = true;
			codeListDef = new Hashtable<String, String>();
			//keeping Codelist attributes to set them for each CodeListItem or EnumeratedItem
			oid = null;
			codeListName = null;
			dataType = null;
			sasFormatName = null;
			oid = atts.getValue("OID");
			if (atts.getValue("Name") != null) {
				codeListName = atts.getValue("Name");
				codeListDef.put("Name", codeListName);
			}
			if (atts.getValue("DataType") != null) {
				dataType = atts.getValue("DataType");
				codeListDef.put("DataType", dataType);
			}
			if (atts.getValue("SASFormatName") != null) {
				sasFormatName = atts.getValue("SASFormatName");
				codeListDef.put("SASFormatName", sasFormatName);
			}

			/*CodeList/CodeListItem*/
		} else if (isCodeListDefFlag && qName.equals("CodeListItem")) {
			isCodeListItemFlag = true;
			codeListItemDef = new Hashtable<String, String>();
			//from CodeList attribute
			if (oid != null) {
				codeListItemDef.put("OID", oid);
			}
			if (codeListName != null) {
				codeListItemDef.put("CodeListName", codeListName);
			}
			if (dataType != null) {
				codeListItemDef.put("DataType", dataType);
			}
			if (sasFormatName != null) {
				codeListItemDef.put("SASFormatName", sasFormatName);
			}
			if (atts.getValue("CodedValue") != null) {
				codeListItemDef.put("CodedValue", atts.getValue("CodedValue"));
			}
			if (atts.getValue("Rank") != null) {
				codeListItemDef.put("Rank", atts.getValue("Rank"));
			}
			if (atts.getValue("OrderNumber") != null) {
				codeListItemDef.put("OrderNumber", atts.getValue("OrderNumber"));
			}
			if (atts.getValue("def:ExtendedValue") != null) {
				codeListItemDef.put("def:ExtendedValue", atts.getValue("def:ExtendedValue"));
			}

			/*CodeList/CodeListItem/TranslatedText*/
		} else if (isCodeListDefFlag && isCodeListItemFlag && qName.equals("TranslatedText")) {
			if (atts.getValue("xml:lang") != null) {
				codeListItemDef.put("xml:lang", atts.getValue("xml:lang"));
			}

			/*CodeList/CodeListItem/Alias*/
		} else if (isCodeListDefFlag && isCodeListItemFlag && qName.equals("Alias")) {
			if (atts.getValue("Name") != null) {
				codeListItemDef.put("Name", atts.getValue("Name"));
			}
			if (atts.getValue("Context") != null) {
				codeListItemDef.put("Context", atts.getValue("Context"));
			}

			/*CodeList/EnumeratedItem*/
		} else if (isCodeListDefFlag && qName.equals("EnumeratedItem")) {
//			isCodeListItemFlag = true;
			codeListItemDef = new Hashtable<String, String>();
			isEnumeratedItemFlag = true;
//			enumeratedItemDef = new Hashtable<String, String>();
			//from CodeList attribute
			if (oid != null) {
//			enumeratedItemDef.put("OID", oid);
				codeListItemDef.put("OID", oid);
			}
			if (codeListName != null) {
//			enumeratedItemDef.put("CodeListName", codeListName);
				codeListItemDef.put("CodeListName", codeListName);
			}
			if (dataType != null) {
//			enumeratedItemDef.put("DataType", dataType);
				codeListItemDef.put("DataType", dataType);
			}
			if (sasFormatName != null) {
//			enumeratedItemDef.put("SASFormatName", sasFormatName);
				codeListItemDef.put("SASFormatName", sasFormatName);
			}
			if (atts.getValue("CodedValue") != null) {
//				enumeratedItemDef.put("CodedValue", atts.getValue("CodedValue"));
				codeListItemDef.put("CodedValue", atts.getValue("CodedValue"));
			}
			if (atts.getValue("Rank") != null) {
//				enumeratedItemDef.put("Rank", atts.getValue("Rank"));
				codeListItemDef.put("Rank", atts.getValue("Rank"));
			}
			if (atts.getValue("OrderNumber") != null) {
				codeListItemDef.put("OrderNumber", atts.getValue("OrderNumber"));
			}
			if (atts.getValue("def:ExtendedValue") != null) {
//				enumeratedItemDef.put("def:ExtendedValue", atts.getValue("def:ExtendedValue"));
				codeListItemDef.put("def:ExtendedValue", atts.getValue("def:ExtendedValue"));
			}

			/*CodeList/EnumeratedItem/Alias*/
		} else if (isCodeListDefFlag && isEnumeratedItemFlag && qName.equals("Alias")) {
//			enumeratedItemDef.put("Name", atts.getValue("Name"));
//			enumeratedItemDef.put("Context", atts.getValue("Context"));
			if (atts.getValue("Name") != null) {
				codeListItemDef.put("Name", atts.getValue("Name"));
			}
			if (atts.getValue("Context") != null) {
				codeListItemDef.put("Context", atts.getValue("Context"));
			}

			/*CodeList/ExternalCodeList*/
		} else if (isCodeListDefFlag && qName.equals("ExternalCodeList")) {
			dictionaryDef = new Hashtable<String, String>();
			//from CodeList attribute
			if (oid != null) {
				dictionaryDef.put("DictionaryID", oid);
			}
			if (codeListName != null) {
				dictionaryDef.put("CodeListName", codeListName);
			}
			if (dataType != null) {
				dictionaryDef.put("DataType", dataType);
			}
			if (sasFormatName != null) {
				dictionaryDef.put("SASFormatName", sasFormatName);
			}
			if (atts.getValue("Dictionary") != null) {
				if (oid != null) {
					container.dictionaryIdList.put(oid, atts.getValue("Dictionary"));
				}
				dictionaryDef.put("Dictionary", atts.getValue("Dictionary"));
			}
			if (atts.getValue("Version") != null) {
				dictionaryDef.put("Version", atts.getValue("Version"));
			}
			if (atts.getValue("ref") != null) {
				dictionaryDef.put("ref", atts.getValue("ref"));
			}
			if (atts.getValue("href") != null) {
				dictionaryDef.put("href", atts.getValue("href"));
			}
			container.dictionaryDefList.add(dictionaryDef);

			/*CodeList/Alias*/
		} else if (isCodeListDefFlag && !isCodeListItemFlag && !isEnumeratedItemFlag && qName.equals("Alias")) {
//			code = null;
//			context = null;
			if (atts.getValue("Name") != null) {
//				code = atts.getValue("Name");
				codeListDef.put("Code", atts.getValue("Name"));
			}
			if (atts.getValue("Context") != null) {
//				context = atts.getValue("Context");
				codeListDef.put("Context", atts.getValue("Context"));
			}

			/*def:leaf*/
		} else if (qName.equals("def:leaf")) {
			isLeafDefFlag = true;
			leafDef = new Hashtable<String, String>();
			oid = atts.getValue("ID");
			if(atts.getValue("ID") != null) {
				leafDef.put("ID", atts.getValue("ID"));
			}
			if (atts.getValue("xlink:href") != null) {
				leafDef.put("xlink:href", atts.getValue("xlink:href"));
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay*/
		} else if (qName.equals("arm:ResultDisplay")) {
			isResultDesplayFlag = true;
			displayName = null;
			displayXmlLang = null;
			displayDescription = null;
			displayLeafID = null;
			displayPageType = null;
			displayPageRefs = null;
			if (atts.getValue("Name") != null) {
				displayName = atts.getValue("Name");
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/Description/TranslatedText*/
		} else if (isResultDesplayFlag && !isAnalysisResultFlag && !isDocumentationFlag && qName.equals("TranslatedText")) {
			if (atts.getValue("xml:lang") != null) {
				displayXmlLang = atts.getValue("xml:lang");
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/def:DocumentRef*/
		} else if (isResultDesplayFlag && !isAnalysisResultFlag && !isDocumentationFlag && qName.equals("def:DocumentRef")) {
			if (displayLeafID == null) {
				if (atts.getValue("leafID") != null) {
					displayLeafID = atts.getValue("leafID");
				}
			} else if (displayLeafID != null) {
				if (atts.getValue("leafID") != null) {
					displayLeafID = displayLeafID + "," + atts.getValue("leafID");
					isNotOneDocumentFlag = true;
				}
			}


			/*arm:AnalysisResultDisplays/arm:ResultDisplay/def:DocumentRef/def:PDFPageRef*/
		} else if (isResultDesplayFlag && !isAnalysisResultFlag && !isDocumentationFlag && qName.equals("def:PDFPageRef")) {
			if (atts.getValue("PageRefs") != null) {
				displayPageRefs = atts.getValue("PageRefs");
			}
			if (atts.getValue("Type") != null) {
				displayPageType = atts.getValue("Type");
			}
			//ResultDesplay can have some DocumentRef
			if (!isNotOneDocumentFlag) { //first
				if (atts.getValue("PageRefs") != null) {
					displayPageType = atts.getValue("PageRefs");
				}
				if (atts.getValue("Type") != null) {
					displayPageType = atts.getValue("Type");
				}
			} else if (isNotOneDocumentFlag) { // not first
				if(displayPageRefs != null) {
					displayPageRefs = displayPageRefs + ",";
					if (atts.getValue("PageRefs") != null) {
						displayPageRefs = displayPageRefs + atts.getValue("PageRefs");
					}
				} else if (displayPageRefs == null) {
					displayPageRefs = ",";
					if (atts.getValue("PageRefs") != null) {
						displayPageRefs = displayPageRefs + atts.getValue("PageRefs");
					}
				}
				if(displayPageType != null) {
					displayPageType = displayPageType + ",";
					if (atts.getValue("Type") != null) {
						displayPageType = displayPageType + atts.getValue("Type");
					}
				} else if (displayPageType == null) {
					displayPageType = ",";
					if (atts.getValue("Type") != null) {
						displayPageType = displayPageType + atts.getValue("Type");
					}
				}
			}


			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult*/
		} else if (isResultDesplayFlag && qName.equals("arm:AnalysisResult")) {
			isAnalysisResultFlag = true;
			analysisOID = null;
			analysisResult = new Hashtable<String, String>();
			if (displayName != null) {
				analysisResult.put("DisplayName", displayName);
			}
			if (displayXmlLang != null) {
				analysisResult.put("DisplayXmlLang", displayXmlLang);
			}
			if (displayLeafID != null) {
				analysisResult.put("DisplayLeafID", displayLeafID);
			}
			if (displayPageRefs != null) {
				analysisResult.put("DisplayPageRefs", displayPageRefs);
			}
			if (displayPageType != null) {
				analysisResult.put("DisplayPageType", displayPageType);
			}
			if (displayDescription != null) {
				analysisResult.put("DisplayDescription", displayDescription);
			}
			if (atts.getValue("OID") != null) {
				analysisResult.put("AnalysisOID", atts.getValue("OID"));
				analysisOID = atts.getValue("OID");
			}
			if (atts.getValue("ParameterOID") != null) {
				analysisResult.put("ParameterOID", atts.getValue("ParameterOID"));
			}
			if (atts.getValue("AnalysisReason") != null) {
				analysisResult.put("AnalysisReason", atts.getValue("AnalysisReason"));
			}
			if (atts.getValue("AnalysisPurpose") != null) {
				analysisResult.put("AnalysisPurpose", atts.getValue("AnalysisPurpose"));
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/Description/TranslatedText*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && !isDocumentationFlag && qName.equals("TranslatedText")) {
			if (atts.getValue("xml:lang") != null) {
				analysisResult.put("xml:lang", atts.getValue("xml:lang"));
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:AnalysisDatasets*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && qName.equals("arm:AnalysisDatasets")) {
			if (atts.getValue("def:CommentOID") != null) {
				analysisResult.put("def:CommentOID", atts.getValue("def:CommentOID"));
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:AnalysisDatasets/arm:AnalysisDataset*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && qName.equals("arm:AnalysisDataset")) {
			analysisDataset = new Hashtable<String, String>();
			if (displayName != null) {
				analysisDataset.put("DisplayName", displayName);
			}
			if (analysisOID != null) {
				analysisDataset.put("AnalysisOID", analysisOID);
			}
//			if (atts.getValue("ItemGroupOID") != null) {
//				analysisResult.put("ItemGroupOID", atts.getValue("ItemGroupOID"));
//			}
			if (atts.getValue("ItemGroupOID") != null) {
			analysisDataset.put("ItemGroupOID", atts.getValue("ItemGroupOID"));
			for (int i=0; i<container.itemGroupDefList.size(); i++) {
				if (atts.getValue("ItemGroupOID").equals(container.itemGroupDefList.get(i).get("OID"))) {
					analysisDataset.put("DatasetName", (String)container.itemGroupDefList.get(i).get("DatasetName"));
				}
			}
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:AnalysisDatasets/arm:AnalysisDataset/def:WhereClauseRef*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && qName.equals("def:WhereClauseRef")) {
			if (atts.getValue("WhereClauseOID") != null) {
				analysisDataset.put("WhereClauseOID", atts.getValue("WhereClauseOID"));
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:AnalysisDatasets/arm:AnalysisDataset/arm:AnalysisVariable*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && qName.equals("arm:AnalysisVariable")) {
			if (analysisDataset.containsKey("Analysis Variable")) {
				if (atts.getValue("ItemOID") != null) {
					analysisDataset.put("Analysis Variable", analysisDataset.get("Analysis Variable")+","+container.itemDefList.get(atts.getValue("ItemOID")).get("Name"));
				}
			} else {
				if (atts.getValue("ItemOID") != null) {
					analysisDataset.put("Analysis Variable", container.itemDefList.get(atts.getValue("ItemOID")).get("Name"));
				}
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:Documentation*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && qName.equals("arm:Documentation")) {
			isDocumentationFlag = true;

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:Documentation/Discription/TranslatedText*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && isDocumentationFlag && qName.equals("TranslatedText")) {
			if (atts.getValue("xml:lang") != null) {
				analysisResult.put("Documentation xml:lang", atts.getValue("xml:lang"));
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:Documentation/def:DocumentRef*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && isDocumentationFlag && qName.equals("def:DocumentRef")) {
			if (!analysisResult.containsKey("Documentation leafID")) {
				if (atts.getValue("leafID") != null) {
					analysisResult.put("Documentation leafID", atts.getValue("leafID"));
				}
			} else if (analysisResult.containsKey("Documentation leafID")) {
				if (atts.getValue("leafID") != null) {
					analysisResult.put("Documentation leafID", analysisResult.get("Documentation leafID")+","+atts.getValue("leafID"));
					isNotOneDocumentFlag = true;
				}
			}


			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:Documentation/def:DocumentRef/def:PDFPageRef*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && isDocumentationFlag && qName.equals("def:PDFPageRef")) {
			if (atts.getValue("PageRefs") != null) {
				analysisResult.put("Documentation PageRefs", atts.getValue("PageRefs"));
			}
			if (atts.getValue("Type") != null) {
				analysisResult.put("Documentation PageType", atts.getValue("Type"));
			}
			//arm:Documentation can have some DocumentRef
			if (!isNotOneDocumentFlag) { //first
				if (atts.getValue("PageRefs") != null) {
					analysisResult.put("Documentation PageRefs", atts.getValue("PageRefs"));
				}
				if (atts.getValue("Type") != null) {
					analysisResult.put("Documentation PageType", atts.getValue("Type"));
				}
			} else if (isNotOneDocumentFlag) { //not first
				if(analysisResult.containsKey("Documentation PageRefs")) {
					analysisResult.put("Documentation PageRefs", analysisResult.get("Documentation PageRefs")+",");
					if (atts.getValue("PageRefs") != null) {
						analysisResult.put("Documentation PageRefs", analysisResult.get("Documentation PageRefs")+atts.getValue("PageRefs"));
					}
				} else if (!analysisResult.containsKey("PageRefs")) {
					analysisResult.put("Documentation PageRefs", ",");
					if (atts.getValue("PageRefs") != null) {
						analysisResult.put("Documentation PageRefs", analysisResult.get("Documentation PageRefs")+atts.getValue("PageRefs"));
					}
				}
				if(analysisResult.containsKey("Documentation PageType")) {
					analysisResult.put("Documentation PageType", analysisResult.get("Documentation PageType")+",");
					if (atts.getValue("Type") != null) {
						analysisResult.put("Documentation PageType", analysisResult.get("Documentation PageType")+atts.getValue("Type"));
					}
				} else if (!analysisResult.containsKey("Documentation PageType")) {
					analysisResult.put("Documentation PageType", ",");
					if (atts.getValue("Type") != null) {
						analysisResult.put("Documentation PageType", analysisResult.get("Documentation PageType")+atts.getValue("Type"));
					}
				}
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:ProgrammingCode*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && qName.equals("arm:ProgrammingCode")) {
			isProgrammingCodeFlag = true;
			if (atts.getValue("Context") != null) {
				analysisResult.put("ProgrammingCodeContext", atts.getValue("Context"));
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:ProgrammingCode/def:DocumentRef*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && isProgrammingCodeFlag && qName.equals("def:DocumentRef")) {
			if (!analysisResult.containsKey("ProgrammingCodeleafID")) {
				if (atts.getValue("leafID") != null) {
					analysisResult.put("ProgrammingCodeleafID", atts.getValue("leafID"));
				}
			}else if (analysisResult.containsKey("ProgrammingCodeleafID")) {
				if (atts.getValue("leafID") != null) {
					analysisResult.put("ProgrammingCodeleafID", analysisResult.get("ProgrammingCodeleafID")+","+atts.getValue("leafID"));
					isNotOneDocumentFlag = true;
				}
			}

			/*arm:AnalysisResultDisplays/arm:ResultDisplay/arm:AnalysisResult/arm:ProgrammingCode/def:DocumentRef/def:PDFPageRef*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && isProgrammingCodeFlag && qName.equals("def:PDFPageRef")) {
			if (!isNotOneDocumentFlag) { //first one
				if (atts.getValue("PageRefs") != null) {
					analysisResult.put("ProgrammingPageRefs", atts.getValue("PageRefs"));
				}
				if (atts.getValue("Type") != null) {
					analysisResult.put("ProgrammingPageType", atts.getValue("Type"));
				}
			} else if (isNotOneDocumentFlag) { //not first one
				if(analysisResult.containsKey("ProgrammingPageRefs")) {
					analysisResult.put("ProgrammingPageRefs", analysisResult.get("ProgrammingPageRefs")+",");
					if (atts.getValue("PageRefs") != null) {
						analysisResult.put("ProgrammingPageRefs", analysisResult.get("ProgrammingPageRefs")+atts.getValue("PageRefs"));
					}
				} else if (!methodDef.containsKey("PageRefs")) {
					analysisResult.put("ProgrammingPageRefs", ",");
					if (atts.getValue("PageRefs") != null) {
						analysisResult.put("ProgrammingPageRefs", analysisResult.get("ProgrammingPageRefs")+atts.getValue("PageRefs"));
					}
				}
				if(analysisResult.containsKey("ProgrammingPageType")) {
					analysisResult.put("ProgrammingPageType", methodDef.get("ProgrammingPageType")+",");
					if (atts.getValue("Type") != null) {
						analysisResult.put("ProgrammingPageType", analysisResult.get("ProgrammingPageType")+atts.getValue("Type"));
					}
				} else if (!analysisResult.containsKey("ProgrammingPageType")) {
					analysisResult.put("ProgrammingPageType", ",");
					if (atts.getValue("Type") != null) {
						analysisResult.put("ProgrammingPageType", analysisResult.get("ProgrammingPageType")+atts.getValue("Type"));
					}
				}
			}
		}
	}

	/*
	 * get text()
	 */

	public void characters(char[] ch, int start, int length) {

		if( leaf != null ){
			     leaf.append( new String(ch, start, length) );
			   }
	}


	/*
	 * operating EndTag. -set text,add List
	 */
	public void endElement(String namespaceURI, String localName, String qName) {
//
//		  if( leaf != null ){
//			     System.out.println( tagStack + ":" + leaf.toString().trim() );
//			     leaf = null;
//			   }
//
		/*ODM/Study/GlobalVariables/StudyName/text()*/
		if (qName.equals("StudyName")) {
			container.studyDef.put("StudyName", leaf.toString().trim());

			/*ODM/Study/GlobalVariables/StudyDescription/text()*/
		} else if (qName.equals("StudyDescription")) {
			container.studyDef.put("StudyDescription", leaf.toString().trim());

			/*ODM/Study/GlobalVariables/ProtocolName/text()*/
		} else if (qName.equals("ProtocolName")) {
			container.studyDef.put("ProtocolName", leaf.toString().trim());

			/*</def:AnnotatedCRF> - add to documentDefList*/
		} else if (isAnnotatedCRFFlag && qName.equals("def:AnnotatedCRF")) {
			isAnnotatedCRFFlag = false;

			/*</def:SupplementalDoc> - add to documentDefList*/
		} else if (isSupplementalDocFlag && qName.equals("def:SupplementalDoc")) {
			isSupplementalDocFlag = false;

			/*ValueListDef*/
		} else if (isValueListDefFlag && qName.equals("def:ValueListDef")) {
			isValueListDefFlag = false;
			if (oid != null) {
				container.valueItemDefList.put(oid, container.valueItemRefList);
				container.valueItemDefIdList.add(oid);
			}

			/*</ItemRef> - add to valueItemRefList*/
		} else if (isValueListDefFlag && qName.equals("ItemRef")) {
			container.valueItemRefList.add(valueItemRef);

			/*</def:WhereClauseDef> - add to whereClauseDefList*/
		} else if (isWhereClauseDefFlag && qName.equals("def:WhereClauseDef")) {
			if (oid != null) {
				container.whereClauseDefList.put(oid, container.rankCheckList);
			}
			isWhereClauseDefFlag = false;

			/*</RangeCheck> - add to rankCheckList*/
		} else if (isWhereClauseDefFlag && qName.equals("RangeCheck")) {
			container.rankCheckList.add(whereClauseDef);

			/*def:WhereClauseDef/RangeCheck/CheckValue*/
		} else if (isWhereClauseDefFlag && qName.equals("CheckValue")) {
			if (whereClauseDef.containsKey("CheckValue")) {
				whereClauseDef.put("CheckValue",whereClauseDef.get("CheckValue")+","+leaf.toString().trim());
			} else {
			whereClauseDef.put("CheckValue", leaf.toString().trim());
			}

			/*add to ItemGroupList*/
		} else if (isItemGroupDefFlag && qName.equals("ItemGroupDef")) {
			container.itemGroupDefList.add(itemGroupDef);
			isItemGroupDefFlag = false;

			/*def:itemGroupDef/Description/TransratedText/text()*/
		} else if (isItemGroupDefFlag && qName.equals("TranslatedText")) {
			itemGroupDef.put("Comment",leaf.toString().trim());

			/*def:CommentDef/def:leaf/def:title/text()*/
		} else if (isItemGroupDefFlag && qName.equals("def:title")){
			itemGroupDef.put("Title", leaf.toString().trim());

			/*add  to commentDefList*/
		} else if (isCommentDefFlag && qName.equals("def:CommentDef")) {
			if (oid != null) {
				container.commentDefList.put(oid, commentDef);
			}
			isCommentDefFlag = false;
			isNotOneDocumentFlag = false;

			/*def:CommentDef/Description/TransratedText/text()*/
		} else if (isCommentDefFlag && qName.equals("TranslatedText")) {
			if (!commentDef.containsKey("TranslatedText")) {
				commentDef.put("TranslatedText", leaf.toString().trim());
			}

			/*add to itemDefList*/
		} else if (isItemDefFlag && qName.equals("ItemDef")) {
			if (oid != null) {
				container.itemDefList.put(oid, itemDef);
			}
			isItemDefFlag = false;
			isNotOneDocumentFlag = false;
			isItemDefOriginFlag = false;

			/*ItemDef/Description/TranslatedText/text()*/
		} else if (isItemDefFlag && !isItemDefOriginFlag && qName.equals("TranslatedText")) {
			if (!itemDef.containsKey("TranslatedText")) {
				itemDef.put("TranslatedText", leaf.toString().trim());
			}

			/*ItemDef/def:Origin/Description/TranslatedText/text()*/
		} else if (isItemDefFlag && isItemDefOriginFlag && qName.equals("TranslatedText")) {
			if (!itemDef.containsKey("Origin TranslatedText")) {
				itemDef.put("Origin TranslatedText", leaf.toString().trim());
			}

		} else if  (isItemDefFlag && isItemDefOriginFlag && qName.equals("def:Origin")) {
			isItemDefOriginFlag = false;

			/*add to methodDefList*/
		} else if (isMethodDefFlag && qName.equals("MethodDef")) {
			if (oid != null) {
				container.methodDefList.put(oid, methodDef);
			}
			isMethodDefFlag = false;
			isNotOneDocumentFlag = false;

			/*methodDef/Description/TranslatedText/text()*/
		} else if (isMethodDefFlag && qName.equals("TranslatedText")) {
			if (!methodDef.containsKey("TranslatedText")) {
				methodDef.put("TranslatedText", leaf.toString().trim());
			}

			/*methodDef/FormalExpression/text()*/
		} else if (isMethodDefFlag && qName.equals("FormalExpression")) {
			methodDef.put("FormalExpression", leaf.toString().trim());

			/*add to codeListDefList*/
		} else if (isCodeListDefFlag && qName.equals("CodeList")) {
			isCodeListDefFlag = false;
			if (oid != null) {
				container.codeListDefList.put(oid, codeListDef);
			}

			/*add to codeListItemDefList*/
		} else if (isCodeListDefFlag && isCodeListItemFlag && qName.equals("CodeListItem")) {
			isCodeListItemFlag = false;
			container.codeListItemDefList.add(codeListItemDef);

			/*CodeList/CodeListItem/Decode/TranslatedText/text()*/
		} else if (isCodeListDefFlag && isCodeListItemFlag && qName.equals("TranslatedText")) {
			if (!codeListItemDef.containsKey("TranslatedText")) {
				codeListItemDef.put("TranslatedText", leaf.toString().trim());
			}

			/*add to enumeratedItemDefList*/
		} else if (isCodeListDefFlag && isEnumeratedItemFlag && qName.equals("EnumeratedItem")) {
			isEnumeratedItemFlag = false;
//			container.enumeratedItemDefList.add(enumeratedItemDef);
			container.codeListItemDefList.add(codeListItemDef);

			/*add to leafDefList*/
		} else if (isLeafDefFlag && qName.equals("def:leaf")) {
			isLeafDefFlag = false;
			container.leafDefList.add(leafDef);

			/*def:leaf/def:title*/
		} else if (isLeafDefFlag && qName.equals("def:title")) {
			leafDef.put("def:title", leaf.toString().trim());

			/*arm:ResultDisplay*/
		} else if (isResultDesplayFlag && qName.equals("arm:ResultDisplay")) {
			isResultDesplayFlag = false;
			isNotOneDocumentFlag = false;

			/*arm:ResultDisplay/Description/TranslatedText/text()*/
		} else if (isResultDesplayFlag && !isAnalysisResultFlag && !isDocumentationFlag && !isProgrammingCodeFlag && qName.equals("TranslatedText")) {
			displayDescription = leaf.toString().trim();

			/*</arm:AnalysisResult>*/
		} else if (qName.equals("arm:AnalysisResult")) {
			isAnalysisResultFlag = false;
			container.analysisResultList.add(analysisResult);

			/*arm:AnalysisResult/Description/TranslatedText/text() */
		} else if (isResultDesplayFlag && isAnalysisResultFlag && !isDocumentationFlag && !isProgrammingCodeFlag && qName.equals("TranslatedText")) {
			analysisResult.put("AnalysisDescription", leaf.toString().trim());

			/*</arm;AnalysisDataset>*/
		} else if (qName.equals("arm:AnalysisDataset")) {
			container.analysisDatasetList.add(analysisDataset);

			/*arm:AnalysisResult/arm:Documentation/Description/TranslatedText/text()*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && isDocumentationFlag && !isProgrammingCodeFlag && qName.equals("TranslatedText")) {
			analysisResult.put("Documentation Description", leaf.toString().trim());

			/*</Documentation>*/
		} else if (qName.equals("arm:Documentation")) {
			isDocumentationFlag = false;
			isNotOneDocumentFlag = false;

			/*arm:AnalysisResult/arm:ProgrammingCode/Description/TranslatedText/text()*/
		} else if (isResultDesplayFlag && isAnalysisResultFlag && !isDocumentationFlag && isProgrammingCodeFlag && qName.equals("arm:Code")) {
			analysisResult.put("ProgrammingCodeDescription", leaf.toString().trim());

			/*arm:AnalysisResult/arm:ProgrammingCode/arm:Code/text()*/
		} else if (qName.equals("arm:Code")) {
			analysisResult.put("Code", leaf.toString().trim());

			/*</arm:ProgrammingCode>*/
		} else if (qName.equals("arm:ProgrammingCode")) {
			isProgrammingCodeFlag = false;
		}

	}

	public void endDocument()  {
//		System.out.println("endDocument");
	}

	public void checkOid() throws NotOidConnectException, SAXException {
		try {
			for (int i=0; i<container.itemGroupDefList.size();i++) {
				if (container.itemGroupDefList.get(i).get("CommentOID") != null) {
					checkOidReference((String)container.itemGroupDefList.get(i).get("CommentOID"), ListType.COMMENTDEF);
				}
			}
			for (int i=0; i<container.variableItemRefList.size();i++) {
				if (container.variableItemRefList.get(i).get("ItemOID") != null) { // all have?
					checkOidReference((String)container.variableItemRefList.get(i).get("ItemOID"), ListType.ITEMDEF);
					if (container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")) != null) {
						if (container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CommentOID") != null) {
							checkOidReference((String)container.itemDefList.get((String)container.variableItemRefList.get(i).get("ItemOID")).get("CommentOID"), ListType.COMMENTDEF);
						}
					}
				}
				if (container.variableItemRefList.get(i).get("MethodOID") != null) {
					checkOidReference((String)container.variableItemRefList.get(i).get("MethodOID"), ListType.METHODDEF);
				}
			}
			for (int m=0; m<container.valueItemDefIdList.size(); m++) {
				String key = container.valueItemDefIdList.get(m);
				for (int n=0; n<container.valueItemDefList.get(key).size(); n++) {
					if (container.valueItemDefList.get(key).get(n).get("ItemOID") != null) { //all have?
						checkOidReference((String)container.valueItemDefList.get(key).get(n).get("ItemOID"), ListType.ITEMDEF);
					}
					if (container.valueItemDefList.get(key).get(n).get("MethodOID") != null) {
						checkOidReference((String)container.valueItemDefList.get(key).get(n).get("MethodOID"), ListType.METHODDEF);
					}
					if (container.valueItemDefList.get(key).get(n).get("CommentOID") != null) {
						checkOidReference((String)container.valueItemDefList.get(key).get(n).get("CommentOID"), ListType.COMMENTDEF);
					}
					if (container.valueItemDefList.get(key).get(n).get("WhereClauseOID") != null) { //all have?
						checkOidReference((String)container.valueItemDefList.get(key).get(n).get("WhereClauseOID"), ListType.WHERECLAUSEDEF);
					}
					if (container.valueItemDefList.get(key).get(n).get("ItemOID") != null) {
						if (container.itemDefList.get((String)container.valueItemDefList.get(key).get(n).get("ItemOID")) != null) {
							if (container.itemDefList.get((String)container.valueItemDefList.get(key).get(n).get("ItemOID")).get("CommentOID") != null) {
								checkOidReference((String)container.itemDefList.get((String)container.valueItemDefList.get(key).get(n).get("ItemOID")).get("CommentOID"), ListType.COMMENTDEF);
							}
						}
					}
					if (container.whereClauseDefList.get(container.valueItemDefList.get(key).get(n).get("WhereClauseOID")) != null) {
						ArrayList<Hashtable<String, String>> array = container.whereClauseDefList.get(container.valueItemDefList.get(key).get(n).get("WhereClauseOID"));
						for (int k=0; k<array.size(); k++) {
							if (array.get(k).get("def:ItemOID") != null) {
								checkOidReference((String)array.get(k).get("def:ItemOID"), ListType.ITEMDEF);
								if (container.itemDefList.get(array.get(k).get("def:ItemOID")) != null) {
									if (container.itemDefList.get(array.get(k).get("def:ItemOID")).get("def:CommentOID") != null) {
										checkOidReference((String)container.itemDefList.get(array.get(k).get("def:ItemOID")).get("def:CommentOID"), ListType.COMMENTDEF);
									}
								}
							}
						}
					}
				}
			}

			for (int i=0; i<container.codeListItemDefList.size();i++) { // all true?
				if (container.codeListItemDefList.get(i).get("OID") != null) {
					checkOidReference((String)container.codeListItemDefList.get(i).get("OID"), ListType.CODELISTDEF);
				}
			}
//			for (int i=0; i<container.enumeratedItemDefList.size();i++) { // all true?
//				if (container.enumeratedItemDefList.get(i).get("OID") != null) {
//					checkOidReference((String)container.enumeratedItemDefList.get(i).get("OID"), ListType.CODELISTDEF);
//				}
//			}
			for (int i=0; i<container.analysisResultList.size();i++) {
				if (container.analysisResultList.get(i).get("ParameterOID") != null) {
					checkOidReference((String)container.analysisResultList.get(i).get("ParameterOID"), ListType.PARAMETERDEF);
				}
				if (container.analysisResultList.get(i).get("def:CommentOID") != null) {
					checkOidReference((String)container.analysisResultList.get(i).get("def:CommentOID"), ListType.COMMENTDEF);
				}
				if (container.analysisResultList.get(i).get("WhereClauseOID") != null) {
					checkOidReference((String)container.analysisDatasetList.get(i).get("WhereClauseOID"), ListType.WHERECLAUSEDEF);
				}
			}
		}catch(NotOidConnectException ex) {
//			System.out.println("error");
//	               System.out.println(ex.getMessage());
//	               System.out.println();
	               ErrorInfo error = new ErrorInfo();
	               error.setMessage(ex.getMessage());

	       errors.add(error);
	       vResult.setResult(false);
		}
	}


	/*
	 * schema - warning
	 */
	public void warning(SAXParseException exception) throws SAXException {
//		System.out.println("warning");
		// Do nothing
	}


	/*
	 * schema - error
	 * if this method is called, set errors in result object.
	 */
	public void error(SAXParseException exception) throws SAXException {
//		System.out.println("error");
//		 System.out.println(
//                 "Error: URI=" + exception.getSystemId()
//                  + ", Line=" + exception.getLineNumber()
//                  + ", Column=" + exception.getColumnNumber());
//               System.out.println(exception.getMessage());
//               System.out.println();

               ErrorInfo error = new ErrorInfo();
               error.setId(exception.getSystemId());
               error.setLine(exception.getLineNumber());
               error.setColumn(exception.getColumnNumber());
               error.setMessage(exception.getMessage());

       errors.add(error);
       }


	/*
	 * schema - fatalError
	 * if this method is called, soon make error
	 */
	public void fatalError(SAXParseException exception) throws SAXException{
//		 System.out.println(
//                 "Error: URI=" + exception.getSystemId()
//                  + ", Line=" + exception.getLineNumber()
//                  + ", Column=" + exception.getColumnNumber());
//               System.out.println(exception.getMessage());
//               System.out.println();

               ErrorInfo error = new ErrorInfo();
               error.setId(exception.getSystemId());
               error.setLine(exception.getLineNumber());
               error.setColumn(exception.getColumnNumber());
               error.setMessage(exception.getMessage());

       vResult.setResult(false);
       errors.add(error);

//       throw new SAXException("fatalError");

	}

	public ValidationResult getResult() {
		vResult.setContainer(container);
		vResult.setErrors(errors);
		return vResult;
	}

	private enum ListType {
		ITEMDEF, COMMENTDEF, METHODDEF, WHERECLAUSEDEF, CODELISTDEF, PADEF, PARAMETERDEF
	}


	public void checkOidReference(String oid, ListType list) throws NotOidConnectException, SAXException {
		String str = "An item referenced by the following OID does not exist : ";
		switch (list) {
		case COMMENTDEF:
			if (container.commentDefList.containsKey(oid)) {
			} else {
				throw new NotOidConnectException("[CommentOID]"+oid);
			}
			break;
		case METHODDEF:
			if (container.methodDefList.containsKey(oid)) {
			} else {
				throw new NotOidConnectException("[MethodOID]"+oid);
			}
			break;
		case ITEMDEF:
			if (container.itemDefList.containsKey(oid)) {
			} else {
				throw new NotOidConnectException("[ItemOID]"+oid);
			}
			break;
		case WHERECLAUSEDEF:
			if (container.whereClauseDefList.containsKey(oid)) {
			} else {
				throw new NotOidConnectException("[WhereClauseOID]"+oid);
			}
			break;
		case CODELISTDEF:
			if (container.codeListDefList.containsKey(oid)) {
			} else {
				throw new NotOidConnectException("[CodeListOID]"+oid);
			}
			break;
		case PARAMETERDEF:
			if (container.itemDefOIDandDatasetNameList.containsKey(oid)) {
			} else {
				throw new NotOidConnectException("[ParameterOID]"+oid);
			}
			break;
		default:
		}
	}

}
