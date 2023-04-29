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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.fujitsu.tsc.desktop.importer.ExcelWriter2.ExcelColumn;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

/**
 * Generate eDT Spec
 *
 */
public class EdtSpecCreator {
	
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
		dateMatchers.add(new RegexDateMatcher("^(\\d{2})(\\D{3})(\\d{4}) (\\d{2}):(\\d{2}):(\\d{2})$", "DDMONYYYY HH24:MI:SS"));  //UNK format should be handled separately
		dateMatchers.add(new RegexDateMatcher("^(\\d{2})(\\D{3})(\\d{4})$", "DDMONYYYY"));  //UNK format should be handled separately
	}
	/* Regular Expressions to match number formats
	 * Distinguish integer and float because integer values could be codelist values.
	 */
	private static Pattern integerRegex = Pattern.compile("^[1-9]+?\\\\d*?$");
	private static Pattern floatRegex = Pattern.compile("^[+-]?\\d+?\\.\\d+?$");

	private StudyEdtGeneral param;
	private File srcFile;
	private List<String[]> lines = new ArrayList<String[]>();

	public EdtSpecCreator(StudyEdtGeneral param, File srcFile) {
		this.param = param;
		this.srcFile = srcFile;
	}

	/**
	 * Scan datasets
	 * @return Pair of StudyEdtColumns and StudyEdtCodelists
	 * @throws IOException
	 * @throws CsvException 
	 */
	public Pair<List<StudyEdtColumn>, List<StudyEdtCodelist>> create() throws IOException, CsvException {
		List<StudyEdtColumn> rtnColumn = new ArrayList<>();
		List<StudyEdtCodelist> rtnCodelist = new ArrayList<>();
		Pair<List<StudyEdtColumn>, List<StudyEdtCodelist>> rtn = Pair.of(rtnColumn, rtnCodelist);
		lines = readToStringArray(srcFile, param.encoding, param.delimiter, param.text_qualifier);
		List<Integer> codeColumns = new ArrayList<>();
		for (int i = 0; i < columnSize(); i++) {
			StudyEdtColumn column = new StudyEdtColumn();
			column.column_no = i + 1;
			column.name = getColumnName(i);
			if (lines != null && lines.size() > param.header_line) {	//The dataset could have no records.
				ColumnType columnType = getColumnType(i);
				switch (columnType.getType()) {
				case DATE:
					column.date_time_format = columnType.detail;
					column.unk_date_time_text = StringUtils.defaultString(searchUnkDateTime(i, columnType.getDetail()));
					break;
				case STRING:
				case INTEGER:
					codeColumns.add(i);
					column.controlled_terms = getColumnName(i);
					break;
				case FLOAT:
				default:
					break;
				}
			}
			rtnColumn.add(column);
		}
		for (Integer colIndex : codeColumns) {
			//Obtain all values
			Set<String> userCodes = new LinkedHashSet<String>();
			for (int r = param.header_line; r < lines.size(); r++) {
				String txt = getStr(r, colIndex);
				if (StringUtils.isNotBlank(txt)) {
					userCodes.add(txt);
				}
			}
			for (String userCode : userCodes) {
				StudyEdtCodelist codelist = new StudyEdtCodelist();
				codelist.codelist = codelist.codelist_label = getColumnName(colIndex);
				if (getColumnType(colIndex).getType() == CType.STRING) {
					codelist.data_type = "text";
				} else {
					codelist.data_type = "integer";
				}
				codelist.user_code = userCode;
				codelist.submission_value = codelist.user_code;
				codelist.extended_value = YorN.No;
				rtnCodelist.add(codelist);
			}
		}
		//Remove column.controlled_terms when all values are blank
		Set<String> codelists = new HashSet<>();
		for (StudyEdtCodelist codelist : rtnCodelist) {
			codelists.add(codelist.codelist);
		}
		for (StudyEdtColumn column : rtnColumn) {
			if (!codelists.contains(column.controlled_terms)) {
				column.controlled_terms = "";
			}
		}
		return rtn;
	}

	private String getStr(int rowIndex, int colIndex) {
		String rtn = lines.get(rowIndex)[colIndex];
//		if (StringUtils.isNoneEmpty(param.text_qualifier)) {
//			if (rtn.startsWith(param.text_qualifier)) {
//				rtn = StringUtils.substringAfter(rtn, param.text_qualifier);
//			}
//			if (rtn.endsWith(param.text_qualifier)) {
//				rtn = StringUtils.substringBeforeLast(rtn, param.text_qualifier);
//			}
//		}
		return rtn;
	}

	//Find UNK expressions
	private String searchUnkDateTime(int columnIdx, String format) {
		List<String> MON = Arrays.asList(new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" });
		RegexDateMatcher matcher = null;
		for (RegexDateMatcher dateMatcher : dateMatchers) {
			if (dateMatcher.getFormat().equals(format)) {
				matcher = dateMatcher;
				break;
			}
		}
		//Scan all columns and find UNK expressions
		for (int i = param.header_line; i < lines.size(); i++) {
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

	//Obtain ColumnType of the designated column
	private ColumnType getColumnType(int columnIdx) {
		String txt = getStr(param.header_line, columnIdx);
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

		/*
		 * Return parts of date string (i.e. {"yyyy", "MM", "dd"}) that matches the pattern.
		 * If the string is different from the pattern, then return empty array. 
		 * TODO: This code simply returns empty array if date parts are text.
		 */
		List<String> getMatchStrings(String str) {
			Matcher matcher = pattern.matcher(str);
			List<String> rtn = new ArrayList<>();
			boolean res = matcher.find();
			if (res) {
				for (int i = 0; i < matcher.groupCount(); i++) {
					rtn.add(matcher.group(i + 1));
				}
			}
			return rtn;
		}

		public String getFormat() {
			return format;
		}
	}



	private String getColumnName(int columnIdx) {
		if (param.header_row_num >= 1) {
			return getStr(param.header_row_num - 1, columnIdx);
		} else {
			return "Column" + columnIdx;
		}
	}

	private int columnSize() {
		return lines.get(0).length;
	}

	//Load the dataset file on the memory (up to MAX_ROW_SIZE)
	public static List<String[]> readToStringArray(File srcFile, String encoding, String delimiter, String text_qualifier) throws IOException, CsvException {
		char separator = ',';	//default
		if (StringUtils.length(delimiter) > 0) {
			if ("\\t".equals(delimiter)) {
				separator = '\t';
			} else {
				separator = delimiter.toCharArray()[0];
			}
		}
		char qualifier = '"';	//default
		if (StringUtils.isEmpty(text_qualifier) || "(None)".equals(text_qualifier)) {
			qualifier = '\b';	//Backspace - use a character that is very unlikely to appear because 'blank' character is not supported by CSVParserBuilder 
		} else {
			qualifier = text_qualifier.toCharArray()[0];
		}
		char escape = '\\';	//default
		CSVParserBuilder builder = new CSVParserBuilder().withSeparator(separator).withQuoteChar(qualifier).withEscapeChar(escape);
		final CSVParser parser = builder.build();
		CSVReader reader = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(srcFile), Charset.forName(encoding)))
				.withCSVParser(parser).build();
		reader.setErrorLocale(new Locale("en"));
		List<String[]> rtn = new ArrayList<>();
		for (int i = 0; i < MAX_ROW_SIZE; i++) {
			String[] line = reader.readNext();
			if (line == null) {
				break;
			} else {
				rtn.add(line);
			}
		}
		reader.close();
		return rtn;
	}

	public static class StudyEdtGeneral {
		
		@ExcelColumn( name = "Source ID" , ordinal = 1)
		public String soruce_id;
		@ExcelColumn( name = "Source Name" , ordinal = 2)
		public String soruce_name;
		@ExcelColumn( name = "STUDYID" , ordinal = 3)
		public String study_id;
		

		/** Type */
		@ExcelColumn( name = "Type" , ordinal = 4)
		public String type_id;
		/** # of Header Lines */
		@ExcelColumn( name = "# of Header Lines" , ordinal = 5)
		public int header_line;
		public int header_row_num;
		/** Character Encoding */
		@ExcelColumn( name = "Character Encoding" , ordinal = 6)
		public String encoding;
		/** Delimited/Fixed Width */
		@ExcelColumn( name = "Delimited/Fixed Width" , ordinal = 7)
		public String separating_method;
		/** Delimiter */
		@ExcelColumn( name = "Delimiter" , ordinal = 8)
		public String delimiter;
		/** Text Qualifier */
		@ExcelColumn( name = "Text Qualifier" , ordinal = 9)
		public String text_qualifier;
		/** User Note 1 */
		@ExcelColumn( name = "User Note 1" , ordinal = 10)
		public String user_note1;
		/** User Note 2 */
		@ExcelColumn( name = "User Note 2" , ordinal = 11)
		public String user_note2;
		//
	}

	public class StudyEdtColumn {

		/** Company ID */
		public String company_id;
		/** Dataspec ID */
		public int dataspec_id;
		/** Column # */
		@ExcelColumn( name = "Column #" , ordinal = 1)
		public int column_no;
		/** Ordinal */
		public int ordinal;
		/** Column Name */
		@ExcelColumn( name = "Column Name" , ordinal = 2)
		public String name;
		/** Column Alias */
		@ExcelColumn( name = "Column Alias" , ordinal = 3)
		public String alias;
		
		@ExcelColumn( name = "Source Test Name" , ordinal = 4)
		public String source_test_name;
		
		/** Key Sequence */
		@ExcelColumn( name = "Key Sequence" , ordinal = 5)
		public String key_sequence;
		/** Date/Time Format */
		@ExcelColumn( name = "Date/Time Format" , ordinal = 6)
		public String date_time_format;
		/** Unknown Date/Time Text */
		@ExcelColumn( name = "Unknown Date/Time Text" , ordinal = 7)
		public String unk_date_time_text;
		/** Start Position */
		@ExcelColumn( name = "Start Position" , ordinal = 8)
		public Integer start_position;
		/** Width */
		@ExcelColumn( name = "Width" , ordinal = 9)
		public Integer width;
		/** Codelist ID */
		@ExcelColumn( name = "Codelist ID" , ordinal = 10)
		public String controlled_terms;
		/** Inclusion Selection Criteria */
		@ExcelColumn( name = "Inclusion Selection Criteria" , ordinal = 11)
		public String inclusion_selection_criteria;
		/** Exclusion Selection Criteria */
		@ExcelColumn( name = "Exclusion Selection Criteria" , ordinal = 12)
		public String exclusion_selection_criteria;
		/** User Note 1 */
		@ExcelColumn( name = "User Note 1" , ordinal = 13)
		public String user_note1;
		/** User Note 2 */
		@ExcelColumn( name = "User Note 2" , ordinal = 14)
		public String user_note2;
	}

	public class StudyEdtCodelist {

		/** Company ID */
		public String company_id;
		/** Dataspec ID */
		public int dataspec_id;
		/** Ordinal */
		public int ordinal;
		/** Codelist ID */
		@ExcelColumn( name = "Codelist ID" , ordinal = 1)
		public String codelist;
		/** Codelist Code */
		@ExcelColumn( name = "Codelist Code" , ordinal = 2)
		public String codelist_code;
		/** Codelist Label */
		@ExcelColumn( name = "Codelist Label" , ordinal = 3)
		public String codelist_label;
		/** DataType */
		@ExcelColumn( name = "DataType" , ordinal = 4)
		public String data_type;
		/** SASFormatName */
		@ExcelColumn( name = "SASFormatName" , ordinal = 5)
		public String sas_format_name;
		/** Code */
		@ExcelColumn( name = "Code" , ordinal = 6)
		public String code;
		/** User Code */
		@ExcelColumn( name = "User Code" , ordinal = 7)
		public String user_code;
		/** Order Number */
		@ExcelColumn( name = "Order Number" , ordinal = 10)
		public String order_number;
		/** Rank */
		@ExcelColumn( name = "Rank" , ordinal = 11)
		public String rank;
		/** ExtendedValue */
		@ExcelColumn( name = "ExtendedValue" , ordinal = 12)
		public YorN extended_value;
		/** Submission Value */
		@ExcelColumn( name = "Submission Value" , ordinal = 13)
		public String submission_value;
		/** Decode */
		@ExcelColumn( name = "Decode" , ordinal = 8)
		public String decode;
		/** xml:lang */
		@ExcelColumn( name = "xml:lang" , ordinal = 9)
		public String xml_lang;
		/** Alias Context */
		@ExcelColumn( name = "Alias Context" , ordinal = 14)
		public String alias_context;
		/** Alias Name */
		@ExcelColumn( name = "Alias Name" , ordinal = 15)
		public String alias_name;
		/** User Note 1 */
		@ExcelColumn( name = "User Note 1" , ordinal = 16)
		public String user_note1;
		/** User Note 2 */
		@ExcelColumn( name = "User Note 2" , ordinal = 17)
		public String user_note2;
	}

	public static enum YorN {
		Yes, No;
	}
	

}
