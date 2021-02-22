/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.util;

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

	public ExcelStyle(XSSFWorkbook wb) {

		DataFormat format = wb.createDataFormat(); //text

	    //font white
	    font_white = wb.createFont();
	    font_white.setFamily(FontFamily.MODERN);
	    font_white.setColor(IndexedColors.WHITE.getIndex());

	    //font_black
	    font_black = wb.createFont();
	    font_black.setFamily(FontFamily.MODERN);
	    font_black.setColor(IndexedColors.BLACK.getIndex());

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
	    style_white = wb.createCellStyle();
	    style_white.setAlignment(HorizontalAlignment.LEFT);
	    style_white.setVerticalAlignment(VerticalAlignment.TOP);
	    style_white.setDataFormat(format.getFormat("text"));
	    style_white.setWrapText(true);

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
				style_lightyellow
		};
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_UnitSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[8];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_lightyellow, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}
	
	public XSSFSheet setStyleOdm_EventSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[13];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_lightyellow, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_EventFormSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[6];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_lightyellow, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_FormSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[10];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_lightyellow, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_FieldSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[29];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_lightyellow, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_CodelistSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[17];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_lightyellow, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_MethodSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[11];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_lightyellow, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleOdm_ConditionSheet(XSSFSheet sheet) {
		CellStyle[] cellStyle = new CellStyle[10];

		for(int i = 0; i < cellStyle.length; cellStyle[i] = style_lightyellow, i++);
		return setStyleSheet_FreeStyle(sheet, cellStyle);
	}

	public XSSFSheet setStyleUnitSheet(XSSFSheet sheet) {
		for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
			sheet.getRow(0).getCell(i).setCellStyle(style_darkred);
		}
		for (int j=1; j<=sheet.getLastRowNum(); j++) {
			sheet.getRow(j).getCell(0).setCellStyle(style_lightyellow);
			sheet.getRow(j).getCell(1).setCellStyle(style_lightyellow);
			sheet.getRow(j).getCell(2).setCellStyle(style_lightyellow);
			sheet.getRow(j).getCell(3).setCellStyle(style_lightyellow);
			sheet.getRow(j).getCell(4).setCellStyle(style_lightyellow);
			sheet.getRow(j).getCell(5).setCellStyle(style_lightyellow);
		}
		return sheet;
	}

	public XSSFSheet setStyleStudySheet(XSSFSheet sheet) {
		for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
			sheet.getRow(0).getCell(i).setCellStyle(style_darkred);
		}
		for (int j=1; j<=sheet.getLastRowNum(); j++) {
			sheet.getRow(j).getCell(0).setCellStyle(style_gray);
			sheet.getRow(j).getCell(1).setCellStyle(style_lightyellow);
		}
		return sheet;
	}

	public XSSFSheet setStyleDocumentSheet(XSSFSheet sheet) {
		for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
			sheet.getRow(0).getCell(i).setCellStyle(style_darkred);
		}
		for (int j=1; j<=sheet.getLastRowNum(); j++) {
			for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
				if (sheet.getRow(j).getCell(i) == null) {
					sheet.getRow(j).createCell(i);
				}
			sheet.getRow(j).getCell(i).setCellStyle(style_lightyellow);
			}
			}
		return sheet;
	}

	public XSSFSheet setStyleDatasetSheet(XSSFSheet sheet) {
		for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
			sheet.getRow(0).getCell(i).setCellStyle(style_darkred);
		}
		for (int j=1; j<=sheet.getLastRowNum(); j++) {
			for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
				if (sheet.getRow(j).getCell(i) == null) {
					sheet.getRow(j).createCell(i);
				}
			sheet.getRow(j).getCell(i).setCellStyle(style_lightyellow);
			}
			}
		return sheet;
	}

	public XSSFSheet setStyleVariableSheet(XSSFSheet sheet) {
		for (int i=0; i<19; i++) {
			sheet.getRow(0).getCell(i).setCellStyle(style_lightyellow);
		}
		for (int i=19; i<sheet.getRow(0).getLastCellNum(); i++) {
			sheet.getRow(0).getCell(i).setCellStyle(style_orange_bold);
		}
		for (int j=1; j<=sheet.getLastRowNum(); j++) {
			for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
				if (sheet.getRow(j).getCell(i) == null) {
					sheet.getRow(j).createCell(i);
				}
			sheet.getRow(j).getCell(i).setCellStyle(style_white);
			}
		}
		return sheet;
	}

	public XSSFSheet setStyleValueSheet(XSSFSheet sheet) {
		for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
			sheet.getRow(0).getCell(i).setCellStyle(style_darkred);
		}
		for (int j=1; j<=sheet.getLastRowNum(); j++) {
			if (sheet.getRow(j).getCell(0) == null) {
				for (int i=0; i<29; i++) {
					if (sheet.getRow(j).getCell(i) == null) {
						sheet.getRow(j).createCell(i);
					}
					sheet.getRow(j).getCell(i).setCellStyle(style_gray);
				}
			} else {
				for (int i=0; i<29; i++) {
					if (sheet.getRow(j).getCell(i) == null) {
						sheet.getRow(j).createCell(i);
					}
					sheet.getRow(j).getCell(i).setCellStyle(style_lightyellow);
				}
			}
			for (int i=29; i<sheet.getRow(0).getLastCellNum(); i++) {
				if (sheet.getRow(j).getCell(i) == null) {
					sheet.getRow(j).createCell(i);
				}
				sheet.getRow(j).getCell(i).setCellStyle(style_lightorange);
			}
		}
		return sheet;
	}

	public XSSFSheet setStyleDictionarySheet(XSSFSheet sheet) {
		for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
			sheet.getRow(0).getCell(i).setCellStyle(style_darkred);
		}
		for (int j=1; j<=sheet.getLastRowNum(); j++) {
			for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
				if (sheet.getRow(j).getCell(i) == null) {
					sheet.getRow(j).createCell(i);
				}
			sheet.getRow(j).getCell(i).setCellStyle(style_lightyellow);
			}
		}
		return sheet;
	}

	public XSSFSheet setStyleCodelistSheet(XSSFSheet sheet) {
		for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
			sheet.getRow(0).getCell(i).setCellStyle(style_lightyellow_bold);
		}
		for (int j=1; j<=sheet.getLastRowNum(); j++) {
			for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
				if (sheet.getRow(j).getCell(i) == null) {
					sheet.getRow(j).createCell(i);
				}
			sheet.getRow(j).getCell(i).setCellStyle(style_white);
			}
		}

		return sheet;
	}

	public XSSFSheet setStyleResult1Sheet(XSSFSheet sheet) {
		for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
			sheet.getRow(0).getCell(i).setCellStyle(style_darkred);
		}
		for (int j=1; j<=sheet.getLastRowNum(); j++) {
			if (sheet.getRow(j).getCell(0) == null) {
				for (int i=0; i<8; i++) {
					if (sheet.getRow(j).getCell(i) == null) {
						sheet.getRow(j).createCell(i);
					}
					sheet.getRow(j).getCell(i).setCellStyle(style_gray);
				}
			} else {
				for (int i=0; i<8; i++) {
					if (sheet.getRow(j).getCell(i) == null) {
						sheet.getRow(j).createCell(i);
					}
					sheet.getRow(j).getCell(i).setCellStyle(style_lightgreen);
				}
			}
			for (int i=8; i<sheet.getRow(0).getLastCellNum(); i++) {
				if (sheet.getRow(j).getCell(i) == null) {
					sheet.getRow(j).createCell(i);
				}
				sheet.getRow(j).getCell(i).setCellStyle(style_lightyellow);
			}
		}

		return sheet;
	}

	public XSSFSheet setStyleResult2Sheet(XSSFSheet sheet) {
		for (int i=0; i<sheet.getRow(0).getLastCellNum(); i++) {
			sheet.getRow(0).getCell(i).setCellStyle(style_darkred);
		}
		for (int j=1; j<=sheet.getLastRowNum(); j++) {
			if (sheet.getRow(j).getCell(0) == null) {
				for (int i=0; i<6; i++) {
					if (sheet.getRow(j).getCell(i) == null) {
						sheet.getRow(j).createCell(i);
					}
					sheet.getRow(j).getCell(i).setCellStyle(style_gray);
				}
			} else {
				for (int i=0; i<6; i++) {
					if (sheet.getRow(j).getCell(i) == null) {
						sheet.getRow(j).createCell(i);
					}
					sheet.getRow(j).getCell(i).setCellStyle(style_lightyellow);
				}
			}
			for (int i=6; i<sheet.getRow(0).getLastCellNum(); i++) {
				if (sheet.getRow(j).getCell(i) == null) {
					sheet.getRow(j).createCell(i);
				}
				sheet.getRow(j).getCell(i).setCellStyle(style_lightorange);
			}
		}
		return sheet;
	}


	public XSSFSheet setColumnWidth(XSSFSheet sheet) {

		XSSFRow column = sheet.getRow(0);
		for (int i=0; i<column.getLastCellNum(); i++) {

			if (column.getCell(i).toString().equals("Property Name")) {
			    sheet.setColumnWidth(i, 7000);
			    
			} else if (column.getCell(i).toString().equals("Property Value")) {
				sheet.setColumnWidth(i, 10000);

			} else if (column.getCell(i).toString().equals("ID")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("Type")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("href")) {
				sheet.setColumnWidth(i, 8000);

			  } else if (column.getCell(i).toString().equals("Title")) {
				sheet.setColumnWidth(i, 9000);

			  } else if (column.getCell(i).toString().equals("Domain")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Dataset Name")) {
				sheet.setColumnWidth(i, 3500);

			  } else if (column.getCell(i).toString().equals("Has SUPP")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Repeating")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("IsReferenceData")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Purpose")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Structure")) {
				sheet.setColumnWidth(i, 20000);

			  } else if (column.getCell(i).toString().equals("Class")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("Comment")) {
				sheet.setColumnWidth(i, 10000);

			  } else if (column.getCell(i).toString().equals("xml:lang")) {
				sheet.setColumnWidth(i, 2500);

			  } else if (column.getCell(i).toString().equals("DocumentID")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Document Page Type")) {
				sheet.setColumnWidth(i, 5000);

			  } else if (column.getCell(i).toString().equals("Document Page Reference")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("Description")) {
				sheet.setColumnWidth(i, 10000);

			  } else if (column.getCell(i).toString().equals("Alias")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Variable Name")) {
				sheet.setColumnWidth(i, 4500);

			  } else if (column.getCell(i).toString().equals("Is SUPP")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Label")) {
				sheet.setColumnWidth(i, 12000);

			  } else if (column.getCell(i).toString().equals("Mandatory")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Key Sequence")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("DataType")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Length")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("SignificantDigits")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("SASFieldName")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("DisplayFormat")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Codelist")) {
				sheet.setColumnWidth(i, 5000);

			  } else if (column.getCell(i).toString().equals("Origin")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Derivation Type")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("CRF ID")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("CRF Page Type")) {
				sheet.setColumnWidth(i, 5000);

			  } else if (column.getCell(i).toString().equals("CRF Page Reference")) {
				sheet.setColumnWidth(i, 5000);

			  } else if (column.getCell(i).toString().equals("Has Value Metadata")) {
				sheet.setColumnWidth(i, 4500);

			  } else if (column.getCell(i).toString().equals("Predecessor/Derivation")) {
				sheet.setColumnWidth(i, 17000);

			  } else if (column.getCell(i).toString().equals("Role")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Role codelist")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Formal expression context")) {
				sheet.setColumnWidth(i, 7000);

			  } else if (column.getCell(i).toString().equals("Formal expression")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("Value Name")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Value Key")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("W Domain")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("W Dataset Name")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("W Variable Name")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("W Value Key")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("WhereClauseDataset")) {
				sheet.setColumnWidth(i, 5000);

			  } else if (column.getCell(i).toString().equals("WhereClauseVariable")) {
				sheet.setColumnWidth(i, 5500);

			  } else if (column.getCell(i).toString().equals("WhereClauseOperator")) {
				sheet.setColumnWidth(i, 5500);

			  } else if (column.getCell(i).toString().equals("WhereClauseValue")) {
				sheet.setColumnWidth(i, 7000);

			  } else if (column.getCell(i).toString().equals("WhereClause Comment")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("W xml:lang")) {
				sheet.setColumnWidth(i, 2500);

			  } else if (column.getCell(i).toString().equals("Dictionary ID")) {
				sheet.setColumnWidth(i, 5000);

			  } else if (column.getCell(i).toString().equals("Name")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("Version")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("ref")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Codelist ID")) {
				sheet.setColumnWidth(i, 5000);

			  } else if (column.getCell(i).toString().equals("Codelist Code")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Codelist Label")) {
				sheet.setColumnWidth(i, 10000);

			  } else if (column.getCell(i).toString().equals("Code")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("SASFormatName")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Order Number")) {
				sheet.setColumnWidth(i, 2500);

			  } else if (column.getCell(i).toString().equals("Rank")) {
				sheet.setColumnWidth(i, 2500);

			  } else if (column.getCell(i).toString().equals("ExtendedValue")) {
				sheet.setColumnWidth(i, 2500);

			  } else if (column.getCell(i).toString().equals("Submission Value")) {
				sheet.setColumnWidth(i, 7000);

			  } else if (column.getCell(i).toString().equals("Translated Text")) {
				sheet.setColumnWidth(i, 8000);

			  } else if (column.getCell(i).toString().equals("Display Name")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Display Description")) {
				sheet.setColumnWidth(i, 8000);

			  } else if (column.getCell(i).toString().equals("Display xmllang")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Leaf ID")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Leaf Page Type")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Leaf Page Reference")) {
				sheet.setColumnWidth(i, 3500);

			  } else if (column.getCell(i).toString().equals("W Display Name")) {
				sheet.setColumnWidth(i, 3500);

			  } else if (column.getCell(i).toString().equals("Result Key")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("Result Description")) {
				sheet.setColumnWidth(i, 8000);

			  } else if (column.getCell(i).toString().equals("Result xml:lang")) {
				sheet.setColumnWidth(i, 2500);

			  } else if (column.getCell(i).toString().equals("ParameterOID Dataset")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Analysis Reason")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Analysis Purpose")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Documentation ID")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("Documentation Page Type")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Documentation Page Reference")) {
				sheet.setColumnWidth(i, 5000);

			  } else if (column.getCell(i).toString().equals("Documentation Text")) {
				sheet.setColumnWidth(i, 10000);

			  } else if (column.getCell(i).toString().equals("Documentation xml:lang")) {
				sheet.setColumnWidth(i, 2500);

			  } else if (column.getCell(i).toString().equals("Programming Code Context")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Programming Code Text")) {
				sheet.setColumnWidth(i, 10000);

			  } else if (column.getCell(i).toString().equals("Programming Code Document ID")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Programming Code Document Page Type")) {
				sheet.setColumnWidth(i, 5000);

			  } else if (column.getCell(i).toString().equals("Programming Code Document Page Reference")) {
				sheet.setColumnWidth(i, 5000);

			  } else if (column.getCell(i).toString().equals("Datasets Comment")) {
				sheet.setColumnWidth(i, 10000);

			  } else if (column.getCell(i).toString().equals("Datasets xml:lang")) {
				sheet.setColumnWidth(i, 2500);

			  } else if (column.getCell(i).toString().equals("Analysis Variable")) {
				sheet.setColumnWidth(i, 3000);

			  } else if (column.getCell(i).toString().equals("W Result Key")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("User Note 1")) {
				sheet.setColumnWidth(i, 10000);

			  } else if (column.getCell(i).toString().equals("User Note 2")) {
				sheet.setColumnWidth(i, 10000);

			  } else if (column.getCell(i).toString().equals("Decode")) {
				sheet.setColumnWidth(i, 10000);
				
			  } else if (column.getCell(i).toString().equals("Symbol")) {
				sheet.setColumnWidth(i, 6000);
				
			  } else if (column.getCell(i).toString().equals("Category")) {
				sheet.setColumnWidth(i, 6000);
				
			  } else if (column.getCell(i).toString().equals("Alias Context")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Alias Name")) {
				sheet.setColumnWidth(i, 10000);

			  } else if (column.getCell(i).toString().equals("CollectionExceptionCondition")) {
				sheet.setColumnWidth(i, 8000);
				
			  } else if (column.getCell(i).toString().equals("Event Name")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("Form Name")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("PdfFileName")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Item Name")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("Level")) {
				sheet.setColumnWidth(i, 2000);

			  } else if (column.getCell(i).toString().equals("Question")) {
				sheet.setColumnWidth(i, 10000);

			  } else if (column.getCell(i).toString().equals("Question xml:lang")) {
				sheet.setColumnWidth(i, 2500);

			  } else if (column.getCell(i).toString().equals("Description xml:lang")) {
				sheet.setColumnWidth(i, 2500);

			  } else if (column.getCell(i).toString().equals("SAS Name")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Unit Name")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("RangeCheck")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("SoftHard")) {
				sheet.setColumnWidth(i, 2000);

			  } else if (column.getCell(i).toString().equals("RangeCheck Error Message")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("Formal Expression Context")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("Formal Expression")) {
				sheet.setColumnWidth(i, 6000);

			  } else if (column.getCell(i).toString().equals("Derivation")) {
				sheet.setColumnWidth(i, 4000);

			  } else if (column.getCell(i).toString().equals("User Code")) {
				sheet.setColumnWidth(i, 7000);

			  } else if (column.getCell(i).toString().equals("Message")) {
					sheet.setColumnWidth(i, 12000);
			  } else if (column.getCell(i).toString().equals("Additional Rules")) {
					sheet.setColumnWidth(i, 12000);
			}
		}
		return sheet;
	}


}
