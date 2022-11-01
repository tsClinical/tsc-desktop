/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontFamily;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelStyle {

	private final int COEFFICIENT_WIDTH = 265;
	private Config config;
	private CellStyle style_lightyellow;
	private CellStyle style_darkred;
	private CellStyle style_gray;
	private CellStyle style_lightyellow_bold;
	private CellStyle style_lightorange;
	private CellStyle style_orange_bold;
	private CellStyle style_lightgreen;
	private CellStyle style_white;
	private XSSFFont font_white;
	private XSSFFont font_black;

	public ExcelStyle(XSSFWorkbook wb, Config config) {

		this.config = config;
		DataFormat format = wb.createDataFormat(); //text

	    //font white
	    font_white = wb.createFont();
	    font_white.setFamily(FontFamily.MODERN);
	    font_white.setFontName(XSSFFont.DEFAULT_FONT_NAME);
	    font_white.setColor(IndexedColors.WHITE.getIndex());

	    //font_black
	    font_black = wb.createFont();
	    font_black.setFamily(FontFamily.MODERN);
	    font_black.setFontName(XSSFFont.DEFAULT_FONT_NAME);
	    font_black.setColor(IndexedColors.BLACK.getIndex());

	    //cell_white: Data Rows
		style_white = wb.createCellStyle(); //first row color
		style_white.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style_white.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		style_white.setBorderTop(BorderStyle.THIN);
		style_white.setBorderBottom(BorderStyle.THIN);
		style_white.setBorderLeft(BorderStyle.THIN);
		style_white.setBorderRight(BorderStyle.THIN);
		style_white.setFont(font_black);
		style_white.setAlignment(HorizontalAlignment.LEFT);
		style_white.setVerticalAlignment(VerticalAlignment.TOP);
		style_white.setDataFormat(format.getFormat("text"));
		style_white.setWrapText(true);

	    //cell_light_yerrow: Data Rows
		style_lightyellow = wb.createCellStyle(); //first row color
		style_lightyellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style_lightyellow.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		style_lightyellow.setBorderTop(BorderStyle.THIN);
		style_lightyellow.setBorderBottom(BorderStyle.THIN);
		style_lightyellow.setBorderLeft(BorderStyle.THIN);
		style_lightyellow.setBorderRight(BorderStyle.THIN);
		style_lightyellow.setFont(font_black);
		style_lightyellow.setAlignment(HorizontalAlignment.LEFT);
		style_lightyellow.setVerticalAlignment(VerticalAlignment.TOP);
		style_lightyellow.setDataFormat(format.getFormat("text"));
		style_lightyellow.setWrapText(true);

	    //cell_dark_red: Header Row
		style_darkred = wb.createCellStyle(); //first row color
		style_darkred.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style_darkred.setFillForegroundColor(IndexedColors.BROWN.getIndex());
	    style_darkred.setBorderTop(BorderStyle.THIN);
	    style_darkred.setBorderBottom(BorderStyle.THIN);
	    style_darkred.setBorderLeft(BorderStyle.THIN);
	    style_darkred.setBorderRight(BorderStyle.THIN);
	    style_darkred.setFont(font_white);
	    style_darkred.setAlignment(HorizontalAlignment.LEFT);
	    style_darkred.setVerticalAlignment(VerticalAlignment.TOP);
	    style_darkred.setDataFormat(format.getFormat("text"));
//	    style_darkred.setWrapText(true);

		//gray: Header Column
		style_gray = wb.createCellStyle(); //first row collor
		style_gray.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style_gray.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	    style_gray.setBorderTop(BorderStyle.THIN);
	    style_gray.setBorderBottom(BorderStyle.THIN);
	    style_gray.setBorderLeft(BorderStyle.THIN);
	    style_gray.setBorderRight(BorderStyle.THIN);
	    style_gray.setFont(font_black);
	    style_gray.setAlignment(HorizontalAlignment.LEFT);
	    style_gray.setVerticalAlignment(VerticalAlignment.TOP);
	    style_gray.setDataFormat(format.getFormat("text"));
	    style_gray.setWrapText(true);

	    /*
	     * The following styles are deprecated.
	     */
	    //cell_plane
//	    style_white = wb.createCellStyle();
//	    style_white.setAlignment(HorizontalAlignment.LEFT);
//	    style_white.setVerticalAlignment(VerticalAlignment.TOP);
//	    style_white.setDataFormat(format.getFormat("text"));
//	    style_white.setWrapText(true);

		//light_yerrow_bold
		style_lightyellow_bold = wb.createCellStyle(); //first row color
		style_lightyellow_bold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style_lightyellow_bold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		style_lightyellow_bold.setBorderTop(BorderStyle.THIN);
		style_lightyellow_bold.setBorderBottom(BorderStyle.THIN);
		style_lightyellow_bold.setBorderLeft(BorderStyle.THIN);
		style_lightyellow_bold.setBorderRight(BorderStyle.THIN);
		style_lightyellow_bold.setFont(font_black);
		style_lightyellow_bold.setDataFormat(format.getFormat("text"));
		style_lightyellow_bold.setWrapText(true);

		//orange
		style_orange_bold = wb.createCellStyle(); //first row collor
		style_orange_bold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style_orange_bold.setFillForegroundColor(IndexedColors.GOLD.getIndex());
	    style_orange_bold.setBorderTop(BorderStyle.THIN);
	    style_orange_bold.setBorderBottom(BorderStyle.THIN);
	    style_orange_bold.setBorderLeft(BorderStyle.THIN);
	    style_orange_bold.setBorderRight(BorderStyle.THIN);
		style_orange_bold.setFont(font_black);
	    style_orange_bold.setDataFormat(format.getFormat("text"));
//		style_orange_bold.setWrapText(true);

		//light_orange
		style_lightorange = wb.createCellStyle(); //first row collor
		style_lightorange.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style_lightorange.setFillForegroundColor(IndexedColors.TAN.getIndex());
	    style_lightorange.setBorderTop(BorderStyle.THIN);
	    style_lightorange.setBorderBottom(BorderStyle.THIN);
	    style_lightorange.setBorderLeft(BorderStyle.THIN);
	    style_lightorange.setBorderRight(BorderStyle.THIN);
	    style_lightorange.setDataFormat(format.getFormat("text"));
		style_lightorange.setWrapText(true);

		//light_green
		style_lightgreen = wb.createCellStyle(); //first row collor
		style_lightgreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style_lightgreen.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
	    style_lightgreen.setBorderTop(BorderStyle.THIN);
	    style_lightgreen.setBorderBottom(BorderStyle.THIN);
	    style_lightgreen.setBorderLeft(BorderStyle.THIN);
	    style_lightgreen.setBorderRight(BorderStyle.THIN);
	    style_lightgreen.setDataFormat(format.getFormat("text"));
		style_lightgreen.setWrapText(true);
	}

	public XSSFSheet setStyleSheet_FreeStyle(XSSFSheet sheet, CellStyle[] styles) {
		XSSFRow topOfRow = sheet.getRow(0);
		for (int x=0; x < styles.length; x++) {
			for (int y=1; y < sheet.getLastRowNum() + 1; y++) {	// Header(+1)
				XSSFRow row = sheet.getRow(y);
				XSSFCell cell = row.getCell(x);
				if (cell == null) {
					cell = row.createCell(x);
				}
				cell.setCellStyle(styles[x]);
			}
			XSSFCell topCell = topOfRow.getCell(x);
			if (topCell == null) {
				topCell = topOfRow.createCell(x);
			}
			topCell.setCellStyle(style_darkred);
		}
		return sheet;
	}

	public XSSFSheet setStyleOdm_StudySheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[]{
				style_gray,
				style_white
		};
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_UnitSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[8];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}
	
	public XSSFSheet setStyleOdm_EventSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[13];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_EventFormSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[6];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_FormSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[10];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_FieldSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[35];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_CodelistSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[17];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_MethodSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[11];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_ConditionSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[10];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_EdcKeysSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[]{
				style_gray,
				style_white
		};
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleDefine_StudySheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[]{
				style_gray,
				style_white
		};
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleDefine_StandardSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[16];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleDefine_DocumentSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[6];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleDefine_DatasetSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[27];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleDefine_VariableSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[46];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleDefine_ValueSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[47];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleDefine_Result1Sheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[38];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleDefine_Result2Sheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[13];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleDefine_DictionarySheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[17];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleDefine_MethodSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[15];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleDefine_CommentSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[11];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleDefine_CodelistSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[27];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_white, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setColumnWidth(XSSFSheet sheet) {

		String sheet_name = sheet.getSheetName();
		XSSFRow column = sheet.getRow(0);
		for (int i = 0; i < column.getLastCellNum(); i++) {
			XSSFCell cell = column.getCell(i);
			if (cell == null) {
				continue;
			}
			String column_name = cell.getStringCellValue();
			if (StringUtils.isEmpty(column_name)) {
				continue;
			}
			/* Common Columns */
			if ("CommentOID".equals(column_name) || "OID".equals(column_name)) {
				sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
			} else if ("Comment".equals(column_name)) {
				sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
			} else if ("Language".equals(column_name) || "xml:lang".equals(column_name)) {
				sheet.setColumnWidth(i, 8 * COEFFICIENT_WIDTH);
			} else if ("DocumentID".equals(column_name)) {
				sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
			} else if ("Document Page Type".equals(column_name) || "CRF Page Type".equals(column_name)) {
				sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
			} else if ("Document Page Reference".equals(column_name) || "CRF Page Reference".equals(column_name)) {
				sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
			} else if ("Document First Page".equals(column_name) || "CRF First Page".equals(column_name)) {
				sheet.setColumnWidth(i, 16 * COEFFICIENT_WIDTH);
			} else if ("Document Last Page".equals(column_name) || "CRF Last Page".equals(column_name)) {
				sheet.setColumnWidth(i, 16 * COEFFICIENT_WIDTH);
			} else if ("Document Page Title".equals(column_name) || "CRF Page Title".equals(column_name)) {
				sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
			} else if ("Dataset Name".equals(column_name)) {
				sheet.setColumnWidth(i, 11 * COEFFICIENT_WIDTH);
			} else if ("No Data".equals(column_name)) {
				sheet.setColumnWidth(i, 7 * COEFFICIENT_WIDTH);
			} else if ("Standard".equals(column_name)) {
				sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
			} else if ("Repeating".equals(column_name)) {
				sheet.setColumnWidth(i, 8 * COEFFICIENT_WIDTH);
			} else if ("IsReferenceData".equals(column_name)) {
				sheet.setColumnWidth(i, 13 * COEFFICIENT_WIDTH);
			} else if ("Variable Name".equals(column_name)) {
				sheet.setColumnWidth(i, 11 * COEFFICIENT_WIDTH);
			} else if ("Label".equals(column_name)) {
				sheet.setColumnWidth(i, 33 * COEFFICIENT_WIDTH);
			} else if ("Mandatory".equals(column_name)) {
				sheet.setColumnWidth(i, 9 * COEFFICIENT_WIDTH);
			} else if ("Key Sequence".equals(column_name)) {
				sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
			} else if ("DataType".equals(column_name)) {
				sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
			} else if ("Length".equals(column_name)) {
				sheet.setColumnWidth(i, 12 * COEFFICIENT_WIDTH);
			} else if ("SignificantDigits".equals(column_name)) {
				sheet.setColumnWidth(i, 12 * COEFFICIENT_WIDTH);
			} else if ("SASFieldName".equals(column_name)) {
				sheet.setColumnWidth(i, 11 * COEFFICIENT_WIDTH);
			} else if ("DisplayFormat".equals(column_name)) {
				sheet.setColumnWidth(i, 12 * COEFFICIENT_WIDTH);
			} else if ("Codelist".equals(column_name)) {
				sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
			} else if ("Origin".equals(column_name)) {
				sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
			} else if ("Source".equals(column_name)) {
				sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
			} else if ("CRF ID".equals(column_name)) {
				sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
			} else if ("MethodOID".equals(column_name)) {
				sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
			} else if ("Derivation Type".equals(column_name)) {
				sheet.setColumnWidth(i, 12 * COEFFICIENT_WIDTH);
			} else if ("Predecessor/Derivation".equals(column_name)) {
				sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
			} else if ("FormalExpression Context".equals(column_name)) {
				sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
			} else if ("FormalExpression Text".equals(column_name)) {
				sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
			} else if ("Alias Context".equals(column_name)) {
				sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
			} else if ("Alias Name".equals(column_name) || "Alias".equals(column_name)) {
				sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
			} else if ("WhereClauseDataset".equals(column_name)) {
				sheet.setColumnWidth(i, 16 * COEFFICIENT_WIDTH);
			} else if ("WhereClauseVariable".equals(column_name)) {
				sheet.setColumnWidth(i, 17 * COEFFICIENT_WIDTH);
			} else if ("WhereClauseOperator".equals(column_name)) {
				sheet.setColumnWidth(i, 17 * COEFFICIENT_WIDTH);
			} else if ("WhereClauseValue".equals(column_name)) {
				sheet.setColumnWidth(i, 30 * COEFFICIENT_WIDTH);
			} else if ("WhereClause CommentOID".equals(column_name)) {
				sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
			} else if ("WhereClause Comment".equals(column_name)) {
				sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
			} else if ("WhereClause Language".equals(column_name)) {
				sheet.setColumnWidth(i, 18 * COEFFICIENT_WIDTH);
			} else if ("Display Name".equals(column_name)) {
				sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
			} else if ("Result Key".equals(column_name)) {
				sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
			} else if ("User Note 1".equals(column_name)) {
				sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
			} else if ("User Note 2".equals(column_name)) {
				sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
			/* STUDY */
			} else if (StringUtils.equals(sheet_name, config.defineStudyTableName) || StringUtils.equals(sheet_name, config.odmStudyTableName)) {
				if ("Property Name".equals(column_name)) {
				    sheet.setColumnWidth(i, 25 * COEFFICIENT_WIDTH);
				} else if ("Property Value".equals(column_name)) {
					sheet.setColumnWidth(i, 80 * COEFFICIENT_WIDTH);
				}
			/* STANDARD */
			} else if (StringUtils.equals(sheet_name, config.defineStandardTableName)) {
				if ("Name".equals(column_name)) {
				    sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Type".equals(column_name)) {
					sheet.setColumnWidth(i, 5 * COEFFICIENT_WIDTH);
				} else if ("Publishing Set".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				} else if ("Version".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				} else if ("Status".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				}
			/* DOCUMENT */
			} else if (StringUtils.equals(sheet_name, config.defineDocumentTableName)) {
				if ("ID".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Type".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("href".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Title".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				}
			/* DATASET */
			} else if (StringUtils.equals(sheet_name, config.defineDatasetTableName)) {
				if ("Domain".equals(column_name)) {
					sheet.setColumnWidth(i, 11 * COEFFICIENT_WIDTH);
				} else if ("Has SUPP".equals(column_name)) {
						sheet.setColumnWidth(i, 7 * COEFFICIENT_WIDTH);
				} else if ("Description".equals(column_name) || "TranslatedText".equals(column_name)) {
					sheet.setColumnWidth(i, 25 * COEFFICIENT_WIDTH);
				} else if ("SASDatasetName".equals(column_name)) {
					sheet.setColumnWidth(i, 11 * COEFFICIENT_WIDTH);
				} else if ("Purpose".equals(column_name)) {
					sheet.setColumnWidth(i, 11 * COEFFICIENT_WIDTH);
				} else if ("Structure".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				} else if ("Class".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Subclass".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Leaf href".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				} else if ("Leaf Title".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				}
			/* VARIABLE */
			} else if (StringUtils.equals(sheet_name, config.defineVariableTableName)) {
				if ("Is SUPP".equals(column_name)) {
					sheet.setColumnWidth(i, 6 * COEFFICIENT_WIDTH);
				} else if ("Repeat N".equals(column_name)) {
					sheet.setColumnWidth(i, 7 * COEFFICIENT_WIDTH);
				} else if ("Non Standard".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				} else if ("Sort Order".equals(column_name)) {
					sheet.setColumnWidth(i, 8 * COEFFICIENT_WIDTH);
				} else if ("Evaluator".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				} else if ("Has VLM".equals(column_name)) {
					sheet.setColumnWidth(i, 7 * COEFFICIENT_WIDTH);
				} else if ("Role".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Role Codelist".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				}
			/* VALUE */
			} else if (StringUtils.equals(sheet_name, config.defineValueTableName)) {
				if ("Value Name".equals(column_name)) {
					sheet.setColumnWidth(i, 11 * COEFFICIENT_WIDTH);
				} else if ("Value Key".equals(column_name)) {
					sheet.setColumnWidth(i, 11 * COEFFICIENT_WIDTH);
				} else if ("WhereClauseGroupID".equals(column_name)) {
					sheet.setColumnWidth(i, 17 * COEFFICIENT_WIDTH);
				}
			/* DICTIONARY */
			} else if (StringUtils.equals(sheet_name, config.defineDictionaryTableName)) {
				if ("Dictionary ID".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Name".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("DataType".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Version".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				} else if ("ref".equals(column_name)) {
					sheet.setColumnWidth(i, 30 * COEFFICIENT_WIDTH);
				} else if ("href".equals(column_name)) {
					sheet.setColumnWidth(i, 30 * COEFFICIENT_WIDTH);
				}
			/* CODELIST */
			} else if (StringUtils.equals(sheet_name, config.defineCodelistTableName) || StringUtils.equals(sheet_name, config.odmCodelistTableName)) {
				if ("Codelist ID".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Codelist Code".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				} else if ("Codelist Label".equals(column_name)) {
					sheet.setColumnWidth(i, 33 * COEFFICIENT_WIDTH);
				} else if ("SASFormatName".equals(column_name)) {
					sheet.setColumnWidth(i, 12 * COEFFICIENT_WIDTH);
				} else if ("Code".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				} else if ("User Code".equals(column_name)) {
					sheet.setColumnWidth(i, 30 * COEFFICIENT_WIDTH);
				} else if ("Order Number".equals(column_name)) {
					sheet.setColumnWidth(i, 11 * COEFFICIENT_WIDTH);
				} else if ("Rank".equals(column_name)) {
					sheet.setColumnWidth(i, 5 * COEFFICIENT_WIDTH);
				} else if ("ExtendedValue".equals(column_name)) {
					sheet.setColumnWidth(i, 11 * COEFFICIENT_WIDTH);
				} else if ("Submission Value".equals(column_name)) {
					sheet.setColumnWidth(i, 30 * COEFFICIENT_WIDTH);
				} else if ("Decode".equals(column_name)) {
					sheet.setColumnWidth(i, 30 * COEFFICIENT_WIDTH);
				} else if ("Decode Language".equals(column_name)) {
					sheet.setColumnWidth(i, 14 * COEFFICIENT_WIDTH);
				}
			/* METHOD */
			} else if (StringUtils.equals(sheet_name, config.defineMethodTableName) || StringUtils.equals(sheet_name, config.odmMethodTableName)) {
				if ("Name".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Type".equals(column_name)) {
					sheet.setColumnWidth(i, 12 * COEFFICIENT_WIDTH);
				} else if ("Description".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				}
			/* RESULT1 */
			} else if (StringUtils.equals(sheet_name, config.defineResult1TableName)) {
				if ("Display Name".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Display Description".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				} else if ("Display Language".equals(column_name) || "Result Language".equals(column_name)
						 || "Documentation Language".equals(column_name) || "Datasets Language".equals(column_name)) {
					sheet.setColumnWidth(i, 8 * COEFFICIENT_WIDTH);
				} else if ("Leaf ID".equals(column_name) || "Documentation ID".equals(column_name)
						|| "Programming Code Document ID".equals(column_name) || "Datasets DocumentID".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Leaf Page Type".equals(column_name) || "Documentation Page Type".equals(column_name)
						|| "Programming Code Document Page Type".equals(column_name) || "Datasets Document Page Type".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Leaf Page Reference".equals(column_name) || "Documentation Page Reference".equals(column_name)
						|| "Programming Code Document Page Reference".equals(column_name) || "Datasets Document Page Reference".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Leaf First Page".equals(column_name) || "Documentation First Page".equals(column_name)
						|| "Programming Code Document First Page".equals(column_name) || "Datasets Document First Page".equals(column_name)) {
					sheet.setColumnWidth(i, 16 * COEFFICIENT_WIDTH);
				} else if ("Leaf Last Page".equals(column_name) || "Documentation Last Page".equals(column_name)
						|| "Programming Code Document Last Page".equals(column_name) || "Datasets Document Last Page".equals(column_name)) {
					sheet.setColumnWidth(i, 16 * COEFFICIENT_WIDTH);
				} else if ("Result Key".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Result Description".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				} else if ("ParameterOID Dataset".equals(column_name)) {
					sheet.setColumnWidth(i, 18 * COEFFICIENT_WIDTH);
				} else if ("Analysis Reason".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Analysis Purpose".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Documentation Text".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				} else if ("Programming Code Context".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				} else if ("Programming Code Text".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				} else if ("Datasets CommentOID".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Datasets Comment".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				}
			/* RESULT2 */
			} else if (StringUtils.equals(sheet_name, config.defineResult2TableName)) {
				if ("Analysis Variable".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				}
			/* ODM - UNIT */
			} else if (StringUtils.equals(sheet_name, config.odmUnitTableName)) {
				if ("ID".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Name".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Symbol".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				}
			/* ODM - EVENT */
			} else if (StringUtils.equals(sheet_name, config.odmEventTableName)) {
				if ("ID".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Name".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Type".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				} else if ("Category".equals(column_name)) {
					sheet.setColumnWidth(i, 10 * COEFFICIENT_WIDTH);
				} else if ("Description".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				}
			/* ODM - EVENTxFORM */
			} else if (StringUtils.equals(sheet_name, config.odmEventFormTableName)) {
				if ("Event Name".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Form Name".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("CollectionExceptionCondition".equals(column_name)) {
					sheet.setColumnWidth(i, 30 * COEFFICIENT_WIDTH);
				}
			/* ODM - FORM */
			} else if (StringUtils.equals(sheet_name, config.odmFormTableName)) {
				if ("ID".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Name".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Description".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				} else if ("xml:lang".equals(column_name)) {
					sheet.setColumnWidth(i, 7 * COEFFICIENT_WIDTH);
				} else if ("PdfFileName".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				}
			/* ODM - FIELD */
			} else if (StringUtils.equals(sheet_name, config.odmFieldTableName)) {
				if ("Form Name".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("ID".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Item Name".equals(column_name)) {
					sheet.setColumnWidth(i, 30 * COEFFICIENT_WIDTH);
				} else if ("Level".equals(column_name)) {
					sheet.setColumnWidth(i, 5 * COEFFICIENT_WIDTH);
				} else if ("Question".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				} else if ("Question xml:lang".equals(column_name)) {
					sheet.setColumnWidth(i, 14 * COEFFICIENT_WIDTH);
				} else if ("ControlType".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("IsLog".equals(column_name)) {
					sheet.setColumnWidth(i, 5 * COEFFICIENT_WIDTH);
				} else if ("Derived From".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Section Label".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("SAS Name".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Description".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				} else if ("Description xml:lang".equals(column_name)) {
					sheet.setColumnWidth(i, 7 * COEFFICIENT_WIDTH);
				} else if ("Unit Name".equals(column_name)) {
					sheet.setColumnWidth(i, 15 * COEFFICIENT_WIDTH);
				} else if ("Codelist".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("RangeCheck".equals(column_name)) {
					sheet.setColumnWidth(i, 30 * COEFFICIENT_WIDTH);
				} else if ("SoftHard".equals(column_name)) {
					sheet.setColumnWidth(i, 7 * COEFFICIENT_WIDTH);
				} else if ("RangeCheck Error Message".equals(column_name)) {
					sheet.setColumnWidth(i, 30 * COEFFICIENT_WIDTH);
				} else if ("Method ID".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Derivation".equals(column_name)) {
					sheet.setColumnWidth(i, 30 * COEFFICIENT_WIDTH);
				} else if ("Condition ID".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("CollectionExceptionCondition".equals(column_name)) {
					sheet.setColumnWidth(i, 30 * COEFFICIENT_WIDTH);
			}
			/* ODM - CONDITION */
			} else if (StringUtils.equals(sheet_name, config.odmMethodTableName)) {
				if ("Condition ID".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Condition Name".equals(column_name)) {
					sheet.setColumnWidth(i, 20 * COEFFICIENT_WIDTH);
				} else if ("Description".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				} else if ("xml:lang".equals(column_name)) {
					sheet.setColumnWidth(i, 7 * COEFFICIENT_WIDTH);
				}
			/* ODM - EDC_KEYS */
			} else if ("EDC_KEYS".equals(sheet_name)) {
				if ("ODM Key".equals(column_name)) {
					sheet.setColumnWidth(i, 24 * COEFFICIENT_WIDTH);
				} else if ("EDC Ky".equals(column_name)) {
					sheet.setColumnWidth(i, 40 * COEFFICIENT_WIDTH);
				}
			} else {
				if (column.getCell(i).toString().equals("Message")) {
					sheet.setColumnWidth(i, 12000);
				} else if (column.getCell(i).toString().equals("Additional Rules")) {
					sheet.setColumnWidth(i, 12000);
				}
			}
		}
		return sheet;
	}
}
