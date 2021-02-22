/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.exporter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.fujitsu.tsc.desktop.util.MetaDataReader;

public class SuppUtil {

	private ArrayList<String> suppDatasets;	// A list of parent datasets
	private Hashtable<String, ArrayList<Variable>> suppVariables; // Pairs of a parent dataset and a supp variable.
	private Hashtable seqList; //Pairs of a parent dataset name and -SEQ lengh of the dataset for IDVARVAL length;
//Change for v1.3.0
	private Hashtable datasetAndDomainSet;//Set of Dataset and Domain when Has SUPP.

	SuppUtil() {
		suppDatasets = new ArrayList<String>();
		suppVariables = new Hashtable<String, ArrayList<Variable>>();
		seqList = new Hashtable <String, String>();
//Change for v1.3.0
        datasetAndDomainSet = new Hashtable<String, String>();
	}

	public class Variable {
		public String variable_name = "";
		public String label = "";
		public String data_type = "";
		public String length = "";
		public String sig_digits = "";
		public Origin origin = new Origin();
		public String mandatory = "";
		public String evaluator = "";
	}
	public class Origin {
		public String origin = "";	//required
		public String crf_id = "";
		public String crf_page_type = "";
		public Set<Integer> crf_page_reference = new HashSet<>();
		public String str_crf_page_reference = "";
		
		public String pagesInString() {
			if (this.crf_page_reference == null || this.crf_page_reference.size() == 0) {
				return "";
			}
			List<Integer> pages = crf_page_reference.stream().sorted((num1, num2) -> Integer.compare(num1, num2)).collect(Collectors.toList());
			String str_pages = "";
			for (int i = 0; i < pages.size(); i++) {
				if (i > 0) {
					str_pages += " ";
				}
				str_pages += pages.get(i);
			}
			return str_pages;
		}
	}
	
	/*
	 * Scan the dataset table to identify SUPP-- datasets.
	 */
	public boolean scanDatasets(MetaDataReader reader) {
		Hashtable<String, String> hash;
		boolean hasSuppFlag = false;

		while ((hash = reader.read()) != null) {
			if(hash.get("Has SUPP") != null && hash.get("Has SUPP").equals("Yes")){
				suppDatasets.add(hash.get("Dataset Name"));
				hasSuppFlag = true;
//Change for v1.3.0
				datasetAndDomainSet.put(hash.get("Dataset Name"), hash.get("Domain"));
			}
		}
		return hasSuppFlag;
	}

	public boolean scanVariables(MetaDataReader reader) {
		Hashtable<String, String> hash;
		boolean isSuppFlag = false;
		ArrayList<Variable> variables = null;

		while ((hash = reader.read()) != null) {
			/* If this is a SUPP variable */
			if (hash.get("Is SUPP") != null && hash.get("Is SUPP").equals("Yes")) {
				Variable var = new Variable();
				/* If the dataset already exists in the list of SUPP datasets */
				if (suppVariables.containsKey(hash.get("Dataset Name"))) {
					variables = suppVariables.get(hash.get("Dataset Name"));
					var.variable_name = hash.get("Variable Name");
					var.label = hash.get("Label");
					var.data_type = hash.get("DataType");
					var.length = hash.get("Length");
					var.sig_digits = hash.get("SignificantDigits");
					var.origin.origin = hash.get("Origin");
					var.origin.crf_id = hash.get("CRF ID");
					var.origin.crf_page_type = hash.get("CRF Page Type");
					var.origin.str_crf_page_reference = hash.get("CRF Page Reference");
					var.mandatory = hash.get("Mandatory");
					variables.add(var);
					suppVariables.put(hash.get("Dataset Name"), variables);
				/* If this variable is for a new dataset */
				} else {
					variables = new ArrayList<Variable>();
					var.variable_name = hash.get("Variable Name");
					var.label = hash.get("Label");
					var.data_type = hash.get("DataType");
					var.length = hash.get("Length");
					var.sig_digits = hash.get("SignificantDigits");
					var.origin.origin = hash.get("Origin");
					var.origin.crf_id = hash.get("CRF ID");
					var.origin.crf_page_type = hash.get("CRF Page Type");
					var.origin.str_crf_page_reference = hash.get("CRF Page Reference");
					var.mandatory = hash.get("Mandatory");
					variables.add(var);
					suppVariables.put(hash.get("Dataset Name"), variables);
				}
				isSuppFlag = true;
				/* set domain-SEQ Length of parent domain to seqList for IDVARVAL */
			} else if (hash.get("Is SUPP") != null && !hash.get("Is SUPP").equals("Yes")) {
				if (hash.get("Variable Name").endsWith("SEQ") && hash.get("Variable Name").length() == 5 && hash.get("Length") != null) {
					seqList.put(hash.get("Dataset Name"), hash.get("Length"));
				}
			}
		}
		return isSuppFlag;
	}

	public List<String> getSuppDatasets() {
		return suppDatasets.stream().sorted((s1, s2) -> s1.compareTo(s2)).collect(Collectors.toList());
	}

//Change for v1.3.0
	public Hashtable<String, String> getDatasetAndDomainSet() {
		return datasetAndDomainSet;
	}

	public ArrayList<Variable> getSuppVariables(String dataset) {
		ArrayList<Variable> variables = null;

		variables = suppVariables.get(dataset);
		if (variables == null) {
			variables = new ArrayList<>();
		}
		return variables;
	}

	/* get maximum length of variable name */
	public int getQnamLength(String dataset) {
		ArrayList<Variable> variables = null;
		int length = 0;

		variables = (suppVariables.get(dataset));
		for (Variable var : variables) {
			if (var.variable_name != null && length < var.variable_name.length()){
				length = var.variable_name.length();
			}
		}
		return length;

	}
	
	/* get maximum length of Label */
	public int getQlabelLength(String dataset) {
		ArrayList<Variable> variables = null;
		int length = 0;

		variables = (suppVariables.get(dataset));
		for (Variable var : variables) {
			if (var.label != null && length < var.label.length()){
				length = var.label.length();
			}
		}
		return length;
	}

	/* get maximum value of length */
	public String getQvalDataType(String dataset) {
		ArrayList<Variable> variables = suppVariables.get(dataset);	//NSVs
		String DEFAULT_TYPE = "text";
		if (variables == null) {
			return DEFAULT_TYPE;
		}
		String data_type = DEFAULT_TYPE;
		for (int i = 0; i < variables.size(); i++) {
			Variable var = variables.get(i);
			if (var == null) {
				continue;
			}
			if (i == 0) {
				data_type = var.data_type;
			} else {
				if (!StringUtils.equals(data_type, var.data_type)) {
					return DEFAULT_TYPE;
				}
			}
		}
		return data_type;
	}

	/* get maximum value of length */
	public String getQvalLength(String dataset) {
		ArrayList<Variable> variables = null;
		int length = 0;

		variables = (suppVariables.get(dataset));
		for (Variable var : variables) {
			try {
				if (StringUtils.isNotEmpty(var.length) && length < Integer.parseInt(var.length)) {
					length = Integer.parseInt(var.length);
				}
			} catch (NumberFormatException ex) {
				// Do nothing - simply ignore the exception and continue.
			}
		}

		if (length > 0) {
			return new Integer(length).toString();
		} else {
			return "";
		}
	}

	/* get maximum value of siginificant digits */
	public String getQvalSigDigits(String dataset){
		ArrayList<Variable> variables = null;
		int sig_digits = 0;

		variables = (suppVariables.get(dataset));
		for (Variable var : variables) {
			try {
				if (StringUtils.isNotEmpty(var.sig_digits) && sig_digits < Integer.parseInt(var.sig_digits)) {
					sig_digits = Integer.parseInt(var.sig_digits);
				}
			} catch (NumberFormatException ex) {
				// Do nothing - simply ignore the exception and continue.
			}
		}

		if (sig_digits > 0) {
			return new Integer(sig_digits).toString();
		} else {
			return "";
		}
	}

	/*
	 * If all Origins are same, return origin value. otherwise return null*/
	public Origin getQvalOrigin(String dataset) {
		ArrayList<Variable> variables = null;
		Origin origin = new Origin();	//Origin of QVAL
		boolean isFirst = true;
		boolean notSame = false;	//The flag indicates origin of QVAL is blank.

		variables = (suppVariables.get(dataset));
		for (Variable var : variables) {
			//For the first NSV
			if (var.origin != null && isFirst == true) {
				origin.origin = var.origin.origin;
				if ("CRF".equals(origin.origin)) {
					origin.crf_id = var.origin.crf_id;
					origin.crf_page_type = var.origin.crf_page_type;
					if (var.origin.str_crf_page_reference != null) {
						if ("PhysicalRef".equals(origin.crf_page_type)) {
							String[] pages = var.origin.str_crf_page_reference.split(" ");
							for (String page : pages) {
								origin.crf_page_reference.add(NumberUtils.toInt(page));
							}
						} else {
							origin.str_crf_page_reference = var.origin.str_crf_page_reference;
						}
					}
				}
				isFirst = false;
			//For the second NSV and after
			} else if (var.origin != null && isFirst == false){
				if (StringUtils.equals(origin.origin, var.origin.origin)) {
					if ("CRF".equals(origin.origin)) {
						//Both page types are PhysicalRef
						if (StringUtils.equals(origin.crf_id, var.origin.crf_id) && StringUtils.isEmpty(origin.str_crf_page_reference)
								&& "PhysicalRef".equals(var.origin.crf_page_type)) {
							String[] pages = var.origin.str_crf_page_reference.split(" ");
							for (String page : pages) {
								origin.crf_page_reference.add(NumberUtils.toInt(page));
							}
						} else {
							notSame = true;
						}
					}
				} else {
					notSame = true;
				}
			//Origin of the NSV is null
			} else {
				notSame = true;
			}
		}
		if (notSame) {
			return null;
		} else { 
			return origin;
		}
	}

	/* get maximum length of origin */
	public int getQorigLength(String dataset) {
		ArrayList<Variable> variables = null;
		int length = 0;

		variables = (suppVariables.get(dataset));
		for (Variable var : variables) {
			if (var.origin != null && length < StringUtils.length(var.origin.origin)) {
				length = StringUtils.length(var.origin.origin);
			}
		}
		return length;
	}

	/* get maximum length of Evaluator */
	public int getQevalLength(String dataset) {
		ArrayList<Variable> variables = null;
		int length = 1;

		variables = (suppVariables.get(dataset));
		for (Variable var : variables) {
			if (var.evaluator != null && length < StringUtils.length(var.evaluator)) {
				length = StringUtils.length(var.evaluator);
			}
		}
		return length;
	}

	/* get value of Length of "domain-SEQ" variable in parent dataset */
	public String getIdVarvalLength(String dataset) {
		String length = null;

		if (seqList.containsKey(dataset)) {
			length = seqList.get(dataset).toString();
		} else {
			length = "";
		}
		return length;
	}

}