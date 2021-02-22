/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.fujitsu.tsc.desktop.importer.ExcelWriter2.ExcelColumn;
import com.fujitsu.tsc.desktop.importer.models.EdcKeysModel;
import com.fujitsu.tsc.desktop.importer.models.OdmCodelistModel;
import com.fujitsu.tsc.desktop.importer.models.OdmCodelistModel.OdmCodelistPk;
import com.fujitsu.tsc.desktop.importer.models.OdmFieldModel;
import com.fujitsu.tsc.desktop.importer.models.OdmFieldModel.OdmFieldPk;
import com.fujitsu.tsc.desktop.importer.models.OdmFormModel;
import com.fujitsu.tsc.desktop.importer.models.OdmFormModel.OdmFormPk;

import com.fujitsu.tsc.desktop.importer.models.OdmModel;
import com.fujitsu.tsc.desktop.importer.models.OdmStudyModel;
import com.fujitsu.tsc.desktop.util.Config;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

/**
 * Generate CRF Spec
 *
 */
public class CrfSpecCreator {
	
	//Maximum records to read
	private final static int MAX_ROW_SIZE = 10000;
	
	/* Regular Expressions to match date formats
	 * YYYY/MM/DD
	 * YYYY/MM/DD HH24:MI:SS
	 * YYYY-MM-DD
	 * YYYY-MM-DD"T"HH24:MI:SS
	 * DDMONYYYY HH24:MI:SS
	 * DDMONYYYY
	 */
	private static List<RegexDateMatcher> dateMatchers = new ArrayList<>();
	static {
		dateMatchers.add(new RegexDateMatcher("^(\\d{4})/(\\d{2})/(\\d{2})$", "YYYY/MM/DD"));
		dateMatchers.add(new RegexDateMatcher("^(\\d{4})/(\\d{2})/(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})$", "YYYY/MM/DD HH24:MI:SS"));
		dateMatchers.add(new RegexDateMatcher("^(\\d{4})-(\\d{2})-(\\d{2})$", "YYYY-MM-DD"));
		dateMatchers.add(new RegexDateMatcher("^(\\d{4})-(\\d{2})-(\\d{2})\"T\"(\\d{2}):(\\d{2}):(\\d{2})$", "YYYY-MM-DD\"T\"HH24:MI:SS"));
		dateMatchers.add(new RegexDateMatcher("^(\\d{2})(\\D{3})(\\d{4}) (\\d{2}):(\\d{2}):(\\d{2})$", "DDMONYYYY HH24:MI:SS")); //UNK format should be handled separately
		dateMatchers.add(new RegexDateMatcher("^(\\d{2})(\\D{3})(\\d{4})$", "DDMONYYYY"));  //UNK format should be handled separately
	}
	/* Regular Expressions to match number formats
	 * Distinguish integer and float because integer values could be codelist values.
	 */
	private static Pattern integerRegex = Pattern.compile("^[1-9]+?\\d*?$");
	private static Pattern floatRegex = Pattern.compile("^[+-]?\\d+?\\.\\d+?$");

	private Config config;
	private OdmModel crf;
	private OdmStudyModel study;
	private File[] srcFiles;
	private List<String[]> lines = new ArrayList<String[]>();

	public CrfSpecCreator(Config config, OdmStudyModel params, File[] srcFiles) {
		this.config = config;
		this.crf = new OdmModel();
		crf.put(params);
		this.study = crf.getStudy();
		this.srcFiles = srcFiles;
	}

	/**
	 * Scan datasets
	 * @return
	 * @throws IOException
	 * @throws CsvException 
	 */
	public OdmModel create() throws IOException, CsvException {
		for (int i = 0; i < srcFiles.length; i++) {
			File srcFile = srcFiles[i];
			/* Create Form based on the source file name.
			 * Datasates are usually named using lower cases, but a CRF spec usually defines using upper cases. */
			String form_id = srcFile.getName().substring(0, srcFile.getName().lastIndexOf('.')).toUpperCase();
			OdmFormPk formKey = new OdmFormPk(form_id);
			OdmFormModel form = new OdmFormModel(formKey);
			form.ordinal = i + 1;
			form.name = form_id;
			crf.put(formKey, form);
			
			/* Create Field */
			lines = EdtSpecCreator.readToStringArray(srcFile, study.encoding, study.delimiter, study.text_qualifier);
			List<Integer> codeColumns = new ArrayList<>();
			for (int j = 0; j < columnSize(); j++) {
				String field_id = getColumnName(j);
				OdmFieldPk fieldKey = new OdmFieldPk(form_id, "", field_id);
				OdmFieldModel field = new OdmFieldModel(fieldKey);
				field.ordinal = j + 1;
				field.form_name = form.name;
				field.name = field_id; 
				field.level = 0;
				field.mandatory = "No";
				if (lines != null && lines.size() > study.header_line) {	//The dataset could have no records.
					ColumnType columnType = getColumnType(j);
					switch (columnType.getType()) {
					case DATE:
						field.data_type = "datetime";
						updateEdcDateFormat(columnType.detail);
						updateEdcUnkDateTimeText(StringUtils.defaultString(seaechUnkDateTime(j, columnType.getDetail())));
						break;
					case STRING:
						field.data_type = "text";
						codeColumns.add(j);
						field.crf_codelist = field_id;
						break;
					case INTEGER:
						field.data_type = "integer";
						codeColumns.add(j);
						field.crf_codelist = field_id;
						break;
					case FLOAT:
						field.data_type = "float";
						break;
					default:
						break;
					}
				} else {
					field.data_type = "text";
				}
				crf.put(fieldKey, field);
			}
			/* Create Codelist */
			for (Integer colIndex : codeColumns) {
				Set<String> userCodes = new LinkedHashSet<String>();
				for (int r = study.header_line; r < lines.size(); r++) {
					String txt = getStr(r, colIndex);
					if (StringUtils.isNotBlank(txt)) {
						userCodes.add(txt);
					}
				}
				/* If values are all empty, do not create a codelist. */
				boolean all_empty = true;
				for (String userCode : userCodes) {
					if (!StringUtils.isEmpty(userCode)) {
						all_empty = false;
						break;
					}
				}
				if (all_empty) {
					OdmFieldPk fieldKey = new OdmFieldPk(form_id, "", getColumnName(colIndex));
					OdmFieldModel field = crf.get(fieldKey);
					if (field != null)
						field.crf_codelist = "";
					continue;
				}
				/* Create a codelist. */
				Iterator<String> iterator = userCodes.iterator();
				while (iterator.hasNext()) {
					String userCode = iterator.next();
					OdmCodelistPk codelistKey = new OdmCodelistPk(getColumnName(colIndex), userCode);
					OdmCodelistModel codelist = new OdmCodelistModel(codelistKey);
					codelist.codelist_label = codelist.codelist;
					if (getColumnType(colIndex).getType() == CType.STRING) {
						codelist.data_type = "text";
					} else {
						codelist.data_type = "integer";
					}
					codelist.submission_value = codelist.user_code;
					crf.put(codelistKey, codelist);
				}
			}
		}
		/* Create EDC_Keys */
		/* (1) Summarize each OdmFieldModel for each field_id (field_id_map) */
		Map<String, List<OdmFieldModel>> field_id_map = new HashMap<>();
		List<OdmFieldModel> fields = crf.listField();
		for (OdmFieldModel field : fields) {
			List<OdmFieldModel> cached_fields = field_id_map.get(field.field_id);
			if (cached_fields == null) {
				List<OdmFieldModel> new_list = new ArrayList<>();
				new_list.add(field);
				field_id_map.put(field.field_id, new_list);
			} else {
				cached_fields.add(field);
			}
		}
		/* (2) Find field_ids that appear in many Forms (common_fields) */
		List<OdmFormModel> forms = crf.listForm();
		int form_count = forms.size();
		Set<String> common_fields = field_id_map.keySet();
		Iterator<String> iterator = common_fields.iterator();
		while (iterator.hasNext()) {
			String common_field = iterator.next();
			int count = field_id_map.get(common_field).size();
			if (count < form_count / 2) {	//Filters Fields that appear more than half Forms
				iterator.remove();
			}
		}
		/* (3) Mark field_ids that appear in many Forms as common_vars. */
		List<String> common_fields_list = new ArrayList<String>(common_fields).stream().sorted().collect(Collectors.toList());
		EdcKeysModel edc_keys = new EdcKeysModel();
		edc_keys.form_id = "<filename>";
		for (int i = 0; i < common_fields_list.size(); i++) {
			if (i == 0) {
				edc_keys.common_vars = common_fields_list.get(0);
			} else {
				edc_keys.common_vars += this.config.valueDelimiter + common_fields_list.get(i);
			}
		}
		crf.put(edc_keys);
		/* Set true to IsLog of Common Variables */
		Iterator<OdmFieldModel> fld_iterator = fields.iterator();
		while (fld_iterator.hasNext()) {
			OdmFieldModel field = fld_iterator.next(); 
			if (common_fields.contains(field.field_id)) {
				field.is_log = "TRUE";
			}
		}
		return crf;
	}

	//TODO Throw exception when dataset files are completely illegal (not a text file, not a table format)
	private String getStr(int rowIndex, int colIndex) {
		String rtn = lines.get(rowIndex)[colIndex];
//		if (StringUtils.isNoneEmpty(study.text_qualifier)) {
//			if (rtn.startsWith(study.text_qualifier)) {
//				rtn = StringUtils.substringAfter(rtn, study.text_qualifier);
//			}
//			if (rtn.endsWith(study.text_qualifier)) {
//				rtn = StringUtils.substringBeforeLast(rtn, study.text_qualifier);
//			}
//		}
		return rtn;
	}

	//Find UNK expressions
	private String seaechUnkDateTime(int columnIdx, String format) {
		List<String> MON = Arrays.asList(new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" });
		RegexDateMatcher matcher = null;
		for (RegexDateMatcher dateMatcher : CrfSpecCreator.dateMatchers) {
			if (dateMatcher.getFormat().equals(format)) {
				matcher = dateMatcher;
				break;
			}
		}
		//Scan all columns and find UNK expressions
		for (int i = study.header_line; i < lines.size(); i++) {
			String txt = getStr(i, columnIdx);
			if (StringUtils.isEmpty(txt)) {
				continue;
			}
			List<String> strs = matcher.getMatchStrings(txt);
			for (String str : strs) {
				if (StringUtils.isNumeric(str)) {
					continue;
				}
				if (MON.contains(str.toUpperCase())) {
					continue;
				}
				return str;
			}
		}
		return null;
	}
	
	private void updateEdcDateFormat(String str) {
		if (StringUtils.isEmpty(this.study.edc_date_format) && StringUtils.isNotEmpty(str)) {
			this.study.edc_date_format = str;
		}
	}

	private void updateEdcUnkDateTimeText(String str) {
		if (StringUtils.isEmpty(this.study.edc_unk_date_time_text) && StringUtils.isNotEmpty(str)) {
			this.study.edc_unk_date_time_text = str;
		}
	}

	private class ColumnType {

		private final CType ctype;
		private String detail;

		public ColumnType(CType cType) {
			this.ctype = cType;
		}

		public ColumnType(CType cType, String detail) {
			this.ctype = cType;
			this.detail = detail;
		}

		public CType getType() {
			return ctype;
		}

		public String getDetail() {
			return detail;
		}
	}

	private enum CType {
		DATE, STRING, INTEGER, FLOAT
	}

	//Identify ColumnType based on the first record
	private ColumnType getColumnType(int columnIdx) {
		String txt = getStr(study.header_line, columnIdx);
		if (integerRegex.matcher(txt).find()) {
			return new ColumnType(CType.INTEGER);
		}
		if (floatRegex.matcher(txt).find()) {
			return new ColumnType(CType.FLOAT);
		}
		for (RegexDateMatcher matcher : dateMatchers) {
			if (matcher.isMatch(txt)) {
				return new ColumnType(CType.DATE, matcher.getFormat());
			}
		}
		return new ColumnType(CType.STRING);
	}

	private static class RegexDateMatcher {

		private Pattern pattern;
		private String format;

		public RegexDateMatcher(String regex, String format) {
			this.format = format;
			pattern = Pattern.compile(regex);
		}

		boolean isMatch(String str) {
			return pattern.matcher(str).find();
		}

		List<String> getMatchStrings(String str) {
			Matcher matcher = pattern.matcher(str);
			List<String> rtn = new ArrayList<>();
			matcher.find();
			for (int i = 0; i < matcher.groupCount(); i++) {
				rtn.add(matcher.group(i + 1));
			}
			return rtn;
		}

		public String getFormat() {
			return format;
		}
	}



	private String getColumnName(int columnIdx) {
		if (study.header_line >= 1) {
			return getStr(0, columnIdx);
		} else {
			return "Column" + columnIdx;
		}
	}

	private int columnSize() {
		return lines.get(0).length;
	}

	public static enum YorN {
		Yes, No;
	}
}
