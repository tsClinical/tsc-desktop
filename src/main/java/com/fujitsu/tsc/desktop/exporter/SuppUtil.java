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

	private boolean is_autosupp_active = false;
	private ArrayList<SuppDataset> supp_datasets = new ArrayList<>();	// A list of parent datasets
	private final Set<String> supp_dataset_names;	//To be mapped from supp_datasets
	private ArrayList<Variable> qval_values = new ArrayList<>();	// A list of QVAL values
	private final Set<String> qval_value_keys;	//To be mapped from qval_values
	private Hashtable seqList = new Hashtable<>(); //Pairs of a parent dataset name and -SEQ lengh of the dataset for IDVARVAL length;

	public enum IsEmpty {	//Indicates whether a dataset is empty
		Yes, No, SUPP;
		
		public static IsEmpty parse(String isEmpty) {
			if (isEmpty == null) {
				return IsEmpty.No;
			}
			if ("YES".equals(isEmpty.toUpperCase())) {
				return IsEmpty.Yes;
			} else if ("SUPP".equals(isEmpty.toUpperCase())) {
				return IsEmpty.SUPP;
			} else {
				return IsEmpty.No;
			}
		}
	}

	public class SuppDataset {
		public String domain = "";
		public String dataset_name = "";
		public IsEmpty is_empty = IsEmpty.No;
	}
	
	public class Variable {
		public String dataset_name = "";
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
	
	/**
	 * Constructor
	 * @param reader1 Reader of the DATASET sheet
	 * @param reader2 Reader of the VARIABLE sheet
	 */
	public SuppUtil(MetaDataReader reader1, MetaDataReader reader2) {
		Hashtable<String, String> hash;
		/* For each variable */
		while ((hash = reader2.read()) != null) {
			String variable_name = hash.get("Variable Name");
			String str_is_supp = hash.get("Is SUPP");
			String str_repeat_n = hash.get("Repeat N");
			int repeat_n = parseRepeatN(str_repeat_n);
			/* A SUPP variable is either Is SUPP="Yes" or Repeat N > 0 (excluding TSVAL and COVAL) */
			if((!StringUtils.isEmpty(str_is_supp) && "Yes".equals(str_is_supp))
					|| (repeat_n > 0 && !"TSVAL".equals(variable_name) && !"COVAL".equals(variable_name))) {
				int start = ("Yes".equals(str_is_supp) ? 0 : 1);	//0: this variable and repeating variables, 1: repeating variables
				for (int i = start; i <= repeat_n; i++) {
					Variable qval_value = new Variable();
					qval_value.dataset_name = hash.get("Dataset Name");
					qval_value.variable_name = getRepeatVariableName(variable_name, i);
					qval_value.label = getRepeatVariableLabel(hash.get("Label"), i);
					qval_value.data_type = hash.get("DataType");
					qval_value.length = getRepeatLength(hash.get("Length"), i);
					qval_value.sig_digits = hash.get("SignificantDigits");
					qval_value.origin.origin = hash.get("Origin");
					qval_value.origin.crf_id = hash.get("CRF ID");
					qval_value.origin.crf_page_type = hash.get("CRF Page Type");
					qval_value.origin.str_crf_page_reference = hash.get("CRF Page Reference");
					qval_value.mandatory = hash.get("Mandatory");
					qval_values.add(qval_value);
				}
			}
			/* Used for length of IDVARVAL */
			if (str_is_supp != null && !str_is_supp.equals("Yes")) {
				if (variable_name.endsWith("SEQ") && variable_name.length() == 5 && hash.get("Length") != null) {
					seqList.put(hash.get("Dataset Name"), hash.get("Length"));
				}
			}
		}
		Set<String> supp_dataset_set = qval_values.stream().map(o -> o.dataset_name).collect(Collectors.toSet());
		/* For each dataset */
		while ((hash = reader1.read()) != null) {
			String dataset_name = hash.get("Dataset Name");
			IsEmpty is_empty = IsEmpty.parse(hash.get("Is Empty"));
			if (supp_dataset_set.contains(dataset_name) && is_empty == IsEmpty.No) {
				SuppDataset dataset = new SuppDataset();
				dataset.domain = hash.get("Domain");
				dataset.dataset_name = dataset_name;
				this.supp_datasets.add(dataset);
			}
		}
		if (!this.supp_datasets.isEmpty()) {
			this.is_autosupp_active = true;	//The source spreadsheet is in the Auto-SUPP format
		}
		this.supp_dataset_names = this.supp_datasets.stream().map(o -> o.dataset_name).collect(Collectors.toSet());
		this.qval_value_keys = this.qval_values.stream().map(o -> o.dataset_name + "/" + o.variable_name).collect(Collectors.toSet());
	}
	
	public boolean isAutoSuppActive() {
		return this.is_autosupp_active;
	}
	
	/**
	 *  A list of parent datasets that have NSVs (Is SUPP=Yes or Repeat N (excluding TSVAL/COVAL) ).
	 * @return Parent datasets excluding those with Is Empty = Yes/SUPP
	 */
	public List<SuppDataset> listSuppDatasets() {
		return this.supp_datasets;
	}
	public Set<String> getSuppDatasetNames() {
		return this.supp_dataset_names;
	}
	
	/**
	 * A list of NSVs (Is SUPP=Yes or Repeat N (excluding TSVAL/COVAL) ) including empty datasets.
	 * @return
	 */
	public List<Variable> listSuppVariables() {
		return this.qval_values;
	}
	public List<Variable> listSuppVariables(String dataset_name) {
		if (StringUtils.isEmpty(dataset_name)) {
			return new ArrayList<>();
		}
		return this.qval_values.stream().filter(o -> StringUtils.equals(dataset_name, o.dataset_name)).collect(Collectors.toList());
	}
	public Set<String> getSuppVariableNames() {
		return this.qval_value_keys;
	}
	
	/* Return a number from 0 to 99 based on the given "Repeat N" attribute */
	public static int parseRepeatN(String str_repeat_n) {
		int repeat_n = 0;
		if (StringUtils.isEmpty(str_repeat_n)) {
			return repeat_n;
		}
		try {
			repeat_n = Integer.parseInt(str_repeat_n);
			if (repeat_n > 99) {	//0 < repeat_n < 100
				repeat_n = 0;
			}
		} catch (NumberFormatException ex) {
			//Do nothing.
		}
		return repeat_n;
	}

	public static String getRepeatIsSupp(String variable_name, String is_supp, int N) {
		if (StringUtils.isEmpty(variable_name)) {
			return "No";
		}
		if (StringUtils.isEmpty(is_supp)) {
			return "No";
		}
		if (N == 0) {
			return is_supp;
		}
		/* "No" for TSVAL or COVAL, "Yes" for other Repeat N variables */
		if (N > 0 && !"TSVAL".equals(variable_name) && !"COVAL".equals(variable_name)) {
				return "Yes";
		} else {
			return "No";
		}
	}
	
	/* Variable Name += N */
	public static String getRepeatVariableName(String variable_name, int N) {
		if (StringUtils.isEmpty(variable_name)) {
			return variable_name;
		}
		if (N == 0) {
			return variable_name;
		}
		if (variable_name.length() >= 8 && 1 <= N && N < 10) {
			variable_name = variable_name.substring(0, 7) + N;
		} else if (variable_name.length() == 7 && 10 <= N && N < 100) {
			variable_name = variable_name.substring(0, 6) + N;
		} else {
			variable_name = variable_name + N;
		}
		return variable_name;
	}
	
	/* Variable Label += " " + N */
	public static String getRepeatVariableLabel(String variable_label, int N) {
		if (StringUtils.isEmpty(variable_label)) {
			return variable_label;
		}
		if (N == 0) {
			return variable_label;
		}
		if (variable_label.length() >= 39 && 1 <= N && N < 10) {
			variable_label = variable_label.substring(0, 38) + " " + N;
		} else if (variable_label.length() == 38 && 10 <= N && N < 100) {
			variable_label = variable_label.substring(0, 37) + " " +  N;
		} else {
			variable_label = variable_label + " " + N;
		}
		return variable_label;
	}
	
	/* Length = Length - (200 * N) */
	public static String getRepeatLength(String strLength, int N) {
		if (StringUtils.isEmpty(strLength)) {
			return "";
		}
		if (N == 0) {
			return strLength;
		}
		try {
			int length = Integer.parseInt(strLength);
			length = length - (200 * N);
			if (length < 1) {
				length = 1;
			}
			return String.valueOf(length);
		} catch (NumberFormatException ex) {
			return "";
		}
	}

	/* SAS Field Name += N */
	public static String getRepeatSasFieldName(String r_variable_name, String sas_field_name, int N) {
		if (StringUtils.isEmpty(sas_field_name)) {
			return r_variable_name;
		} else {
			return getRepeatVariableName(sas_field_name, N);
		}
	}

	/* get maximum length of variable name */
	public int getQnamLength(String dataset) {
		List<Variable> variables = null;
		int length = 0;

		variables = (listSuppVariables(dataset));
		for (Variable var : variables) {
			if (var.variable_name != null && length < var.variable_name.length()){
				length = var.variable_name.length();
			}
		}
		return length;

	}
	
	/* get maximum length of Label */
	public int getQlabelLength(String dataset) {
		List<Variable> variables = null;
		int length = 0;

		variables = (listSuppVariables(dataset));
		for (Variable var : variables) {
			if (var.label != null && length < var.label.length()){
				length = var.label.length();
			}
		}
		return length;
	}

	/* get maximum value of length */
	public String getQvalDataType(String dataset) {
		List<Variable> variables = listSuppVariables(dataset);	//NSVs
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
		List<Variable> variables = null;
		int length = 0;

		variables = (listSuppVariables(dataset));
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
		List<Variable> variables = null;
		int sig_digits = 0;

		variables = (listSuppVariables(dataset));
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
		List<Variable> variables = null;
		Origin origin = new Origin();	//Origin of QVAL
		boolean isFirst = true;
		boolean notSame = false;	//The flag indicates origin of QVAL is blank.

		variables = (listSuppVariables(dataset));
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
		List<Variable> variables = null;
		int length = 0;

		variables = (listSuppVariables(dataset));
		for (Variable var : variables) {
			if (var.origin != null && length < StringUtils.length(var.origin.origin)) {
				length = StringUtils.length(var.origin.origin);
			}
		}
		return length;
	}

	/* get maximum length of Evaluator */
	public int getQevalLength(String dataset) {
		List<Variable> variables = null;
		int length = 1;

		variables = (listSuppVariables(dataset));
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