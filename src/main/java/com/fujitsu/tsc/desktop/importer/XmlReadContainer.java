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

public class XmlReadContainer {

	Hashtable<String, String> studyDef;
	Hashtable<String, Hashtable<String, String>> documentDefList;
	ArrayList<Hashtable<String, String>> itemGroupDefList;
	ArrayList<Hashtable<String, String>> variableItemRefList;
	ArrayList<String> valueItemDefIdList;
	Hashtable<String, ArrayList<Hashtable<String, String>>> valueItemDefList;
	ArrayList<Hashtable<String, String>> valueItemRefList;
	Hashtable<String, Hashtable<String, String>> commentDefList;
	Hashtable<String, Hashtable<String, String>> itemDefList;
	Hashtable<String, Hashtable<String, String>> methodDefList;
	ArrayList<Hashtable<String, String>> rankCheckList;
	Hashtable<String, ArrayList<Hashtable<String, String>>> whereClauseDefList;
	Hashtable<String, Hashtable<String, String>> codeListDefList;
	ArrayList<Hashtable<String,String>> codeListItemDefList;
//	ArrayList<Hashtable> enumeratedItemDefList;
	ArrayList<Hashtable<String,String>> dictionaryDefList;
	ArrayList<Hashtable<String,String>> leafDefList;
	ArrayList<Hashtable<String,String>> analysisResultList;
	ArrayList<Hashtable<String,String>> analysisDatasetList;

	Hashtable<String, String> itemDefOIDandDatasetNameList;
	Hashtable<String, String> dictionaryIdList;

	public void setStudyDef(Hashtable<String, String> studyDef) {
		this.studyDef = studyDef;
	}

	public void setDocumentDefList(Hashtable<String, Hashtable<String, String>> documentDefList) {
		this.documentDefList = documentDefList;
	}

	public void setItemGroupDefList(ArrayList<Hashtable<String, String>> itemGroupDefList) {
		this.itemGroupDefList = itemGroupDefList;
	}

	public void setVariableItemRefList(ArrayList<Hashtable<String, String>> variableItemRefList) {
		this.variableItemRefList = variableItemRefList;
	}

	public void setValueItemDefIdList(ArrayList<String> valueItemDefIdList) {
		this.valueItemDefIdList = valueItemDefIdList;
	}

	public void setValueItemDefList(
			Hashtable<String, ArrayList<Hashtable<String, String>>> valueItemDefList) {
		this.valueItemDefList = valueItemDefList;
	}

	public void setValueItemRefList(
			ArrayList<Hashtable<String, String>> valueItemRefList) {
		this.valueItemRefList = valueItemRefList;
	}

	public void setCommentDefList(
			Hashtable<String, Hashtable<String, String>> commentDefList) {
		this.commentDefList = commentDefList;
	}

	public void setItemDefList(
			Hashtable<String, Hashtable<String, String>> itemDefList) {
		this.itemDefList = itemDefList;
	}

	public void setMethodDefList(
			Hashtable<String, Hashtable<String, String>> methodDefList) {
		this.methodDefList = methodDefList;
	}

	public void setRankCheckList(ArrayList<Hashtable<String, String>> rankCheckList) {
		this.rankCheckList = rankCheckList;
	}

	public void setWhereClauseDefList(
			Hashtable<String, ArrayList<Hashtable<String, String>>> whereClauseDefList) {
		this.whereClauseDefList = whereClauseDefList;
	}

	public void setCodeListDefList(
			Hashtable<String, Hashtable<String, String>> codeListDefList) {
		this.codeListDefList = codeListDefList;
	}

	public void setCodeListItemDefList(ArrayList<Hashtable<String,String>> codeListItemDefList) {
		this.codeListItemDefList = codeListItemDefList;
	}

//	public void setEnumeratedItemDefList(ArrayList<Hashtable> enumeratedItemDefList) {
//		this.enumeratedItemDefList = enumeratedItemDefList;
//	}

	public void setDictionaryDefList(ArrayList<Hashtable<String,String>> dictionaryDefList) {
		this.dictionaryDefList = dictionaryDefList;
	}

	public void setLeafDefList(ArrayList<Hashtable<String,String>> leafDefList) {
		this.leafDefList = leafDefList;
	}

	public void setAnalysisResultList(ArrayList<Hashtable<String,String>> analysisResultList) {
		this.analysisResultList = analysisResultList;
	}

	public void setAnalysisDatasetList(ArrayList<Hashtable<String,String>> analysisDatasetList) {
		this.analysisDatasetList = analysisDatasetList;
	}

	public void setItemDefOIDandDatasetNameList(
			Hashtable<String, String> itemDefOIDandDatasetNameList) {
		this.itemDefOIDandDatasetNameList = itemDefOIDandDatasetNameList;
	}

	public Hashtable<String, String> getStudyDef() {
		return studyDef;
	}

	public Hashtable<String, Hashtable<String, String>> getDocumentDefList() {
		return documentDefList;
	}

	public ArrayList<Hashtable<String, String>> getItemGroupDefList() {
		return itemGroupDefList;
	}

	public ArrayList<Hashtable<String, String>> getVariableItemRefList() {
		return variableItemRefList;
	}

	public ArrayList<String> getValueItemDefIdList() {
		return valueItemDefIdList;
	}

	public Hashtable<String, ArrayList<Hashtable<String, String>>> getValueItemDefList() {
		return valueItemDefList;
	}

	public ArrayList<Hashtable<String, String>> getValueItemRefList() {
		return valueItemRefList;
	}

	public Hashtable<String, Hashtable<String, String>> getCommentDefList() {
		return commentDefList;
	}

	public Hashtable<String, Hashtable<String, String>> getItemDefList() {
		return itemDefList;
	}

	public Hashtable<String, Hashtable<String, String>> getMethodDefList() {
		return methodDefList;
	}

	public ArrayList<Hashtable<String, String>> getRankCheckList() {
		return rankCheckList;
	}

	public Hashtable<String, ArrayList<Hashtable<String, String>>> getWhereClauseDefList() {
		return whereClauseDefList;
	}

	public Hashtable<String, Hashtable<String, String>> getCodeListDefList() {
		return codeListDefList;
	}

	public ArrayList<Hashtable<String,String>> getCodeListItemDefList() {
		return codeListItemDefList;
	}

//	public ArrayList<Hashtable> getEnumeratedItemDefList() {
//		return enumeratedItemDefList;
//	}

	public ArrayList<Hashtable<String,String>> getDictionaryDefList() {
		return dictionaryDefList;
	}

	public ArrayList<Hashtable<String,String>> getLeafDefList() {
		return leafDefList;
	}

	public ArrayList<Hashtable<String,String>> getAnalysisResultList() {
		return analysisResultList;
	}

	public ArrayList<Hashtable<String,String>> getAnalysisDatasetList() {
		return analysisDatasetList;
	}

	public Hashtable<String, String> getItemDefOIDandDatasetNameList() {
		return itemDefOIDandDatasetNameList;
	}

	public Hashtable<String, String> getDictionaryIdList() {
		return dictionaryIdList;
	}

	public XmlReadContainer() {
		studyDef = new Hashtable<String, String>();
		documentDefList = new Hashtable<String, Hashtable<String, String>>();
		itemGroupDefList = new ArrayList<Hashtable<String, String>>();
		variableItemRefList = new ArrayList<Hashtable<String, String>>();
		valueItemDefIdList = new ArrayList<String>();
		valueItemDefList = new Hashtable<String, ArrayList<Hashtable<String,String>>>();
		valueItemRefList = new ArrayList<Hashtable<String,String>>();
		commentDefList = new Hashtable<String, Hashtable<String,String>>();
		itemDefList = new Hashtable<String, Hashtable<String,String>>();
		methodDefList = new Hashtable<String, Hashtable<String,String>>();
		rankCheckList = new ArrayList<Hashtable<String,String>>();
		whereClauseDefList = new Hashtable<String, ArrayList<Hashtable<String,String>>>();
		codeListDefList = new Hashtable<String, Hashtable<String,String>>();
		codeListItemDefList = new ArrayList<Hashtable<String,String>>();
//		enumeratedItemDefList = new ArrayList<Hashtable>();
		dictionaryDefList = new ArrayList<Hashtable<String,String>>();
		leafDefList = new ArrayList<Hashtable<String,String>>();
		analysisResultList = new ArrayList<Hashtable<String,String>>();
		analysisDatasetList = new ArrayList<Hashtable<String,String>>();

		itemDefOIDandDatasetNameList = new Hashtable<String, String>();
		dictionaryIdList = new Hashtable<String, String>();

	}
}
