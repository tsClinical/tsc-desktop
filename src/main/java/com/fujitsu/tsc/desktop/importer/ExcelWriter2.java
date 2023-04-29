/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Export designated objects to Excel
 */
public class ExcelWriter2 {

	private XSSFWorkbook wb;
	@SuppressWarnings("unused")
	private XSSFCellStyle cellStyle;
	private XSSFCellStyle cellStyleGray;
	private XSSFCellStyle cellStyleRed;
	private XSSFCellStyle cellStyleYellow;

	public ExcelWriter2() {
		wb = new XSSFWorkbook();
		cellStyle = createCellStyle(wb);
		cellStyleGray = createCellStyleGray(wb);
		cellStyleRed = createCellStyleRed(wb);
		cellStyleYellow = createCellStyleYellow(wb);
	}

	//For spreadsheet in the Property Name/Value format
	public <T> void addData(String sheetName, T obj, Class<T> clz) throws IllegalArgumentException, IllegalAccessException {
		Sheet sheet = wb.createSheet(sheetName);
		//Create Header
		Row row = sheet.createRow(0);
		setCellValue(row, 0, cellStyleRed, "Property Name");
		setCellValue(row, 1, cellStyleRed, "Property Value");
		//Sort by Ordinal
		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clz, ExcelColumn.class);
		Collections.sort(fields, (o1, o2) -> Integer.compare(o1.getAnnotation(ExcelColumn.class).ordinal(), o2.getAnnotation(ExcelColumn.class).ordinal()));
		//Set PropertyName/Value
		int rowCount = 1;
		for (Field field : fields) {
			String propName = field.getAnnotation(ExcelColumn.class).name();
			String propValue = field.get(obj) == null ? "" : field.get(obj).toString();
			row = sheet.createRow(rowCount);
			setCellValue(row, 0, cellStyleGray, propName);
			setCellValue(row, 1, cellStyleYellow, propValue);
			rowCount++;
		}
		autoSizeColumn(sheet, 20000);
	}

	//For spreadsheet in the MultiRow format
	public <T> void addData(String sheetName, List<T> objs, Class<T> clz) throws IllegalArgumentException, IllegalAccessException {
		Sheet sheet = wb.createSheet(sheetName);
		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clz, ExcelColumn.class);
		Collections.sort(fields, (o1, o2) -> Integer.compare(o1.getAnnotation(ExcelColumn.class).ordinal(), o2.getAnnotation(ExcelColumn.class).ordinal()));
		//Create Header
		Row row = sheet.createRow(0);
		for (int i = 0; i < fields.size(); i++) {
			//sheet.setDefaultColumnStyle(i, cellStyleYellow);
			Field field = fields.get(i);
			String propName = field.getAnnotation(ExcelColumn.class).name();
			setCellValue(row, i, cellStyleRed, propName);
		}
		//Export data
		for (int i = 0; i < objs.size(); i++) {
			T obj = objs.get(i);
			row = sheet.createRow(i + 1);
			for (int j = 0; j < fields.size(); j++) {
				Field field = fields.get(j);
				String propValue = null;
				String name = field.getName();
				if ("length".equals(field.getName()) || "significant_digits".equals(field.getName())) {
					if ((Integer)field.get(obj) < 1) {
						propValue = "";
					} else {
						propValue = field.get(obj).toString();
					}
				} else {
					propValue = (field.get(obj) == null ? "" : field.get(obj).toString());
				}
				//setCellValue(row, j, null, propValue);
				setCellValue(row, j, cellStyleYellow, propValue);
			}
		}
		autoSizeColumn(sheet, 20000);
	}

	//Set value to the designated Cell
	private void setCellValue(Row row, int idx, XSSFCellStyle style, String value) {
		Cell cell = row.createCell(idx);
		if (style != null) {
			cell.setCellStyle(style);
		}
		cell.setCellValue(StringUtils.defaultString(value));
	}

	public void writeout(File outFile) throws FileNotFoundException, IOException {
		try (FileOutputStream out = new FileOutputStream(outFile)) {
			wb.write(out);
		}
	}

	/**
	 * Create CellStyle with only frame
	 *
	 * @param book
	 *            Book to which the CellStyle is applied
	 * @return CellStyle
	 */
	public XSSFCellStyle createCellStyle(XSSFWorkbook book) {
		XSSFCellStyle style = book.createCellStyle();
		style.setBorderTop(BorderStyle.THIN); // No frame without this
		style.setBorderBottom(BorderStyle.THIN); // No frame without this
		style.setBorderLeft(BorderStyle.THIN); // No frame without this
		style.setBorderRight(BorderStyle.THIN); // No frame without this
		XSSFFont font = book.createFont();
		font.setFontName("Calibri"); //Same font as the template
		style.setFont(font);
		return style;
	}

	/**
	 * Create redish CellStyle
	 *
	 * @param book
	 *            Book to which the CellStyle is applied
	 * @return CellStyle
	 */
	public XSSFCellStyle createCellStyleRed(XSSFWorkbook book) {
		XSSFCellStyle style = book.createCellStyle();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // No color without this 
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(192, 80, 77), new DefaultIndexedColorMap()));
		style.setBorderTop(BorderStyle.THIN); // No frame without this
		style.setBorderBottom(BorderStyle.THIN); // No frame without this
		style.setBorderLeft(BorderStyle.THIN); // No frame without this
		style.setBorderRight(BorderStyle.THIN); // No frame without this
		XSSFFont font = book.createFont();
		font.setFontName("Calibri"); // Same font as the template
		font.setColor(IndexedColors.WHITE.index);
		style.setFont(font);
		return style;
	}

	/**
	 * Create yellowish CellStyle
	 *
	 * @param book
	 *            Book to which the CellStyle is applied
	 * @return CellStyle
	 */
	public XSSFCellStyle createCellStyleYellow(XSSFWorkbook book) {
		XSSFCellStyle style = book.createCellStyle();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // No color without this
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 255, 153), new DefaultIndexedColorMap()));
		style.setBorderTop(BorderStyle.THIN); // No frame without this
		style.setBorderBottom(BorderStyle.THIN); // No frame without this
		style.setBorderLeft(BorderStyle.THIN); // No frame without this
		style.setBorderRight(BorderStyle.THIN); // No frame without this
		XSSFFont font = book.createFont();
		font.setFontName("Calibri"); //Same font as the template
		style.setFont(font);
		return style;
	}

	/**
	 * Create gray CellStyle
	 *
	 * @param book
	 *            Book to which the CellStyle is applied
	 * @return CellStyle
	 */
	public XSSFCellStyle createCellStyleGray(XSSFWorkbook book) {
		XSSFCellStyle style = book.createCellStyle();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // No color without this
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(217, 217, 217), new DefaultIndexedColorMap()));
		style.setBorderTop(BorderStyle.THIN); // No frame without this
		style.setBorderBottom(BorderStyle.THIN); // No frame without this
		style.setBorderLeft(BorderStyle.THIN); // No frame without this
		style.setBorderRight(BorderStyle.THIN); // No frame without this
		XSSFFont font = book.createFont();
		font.setFontName("Calibri"); //Same font as the template
		style.setFont(font);
		return style;
	}
	
	//Migrated from ExcelUtils
	public void autoSizeColumn(Sheet sheet, final int maxWidth) {
		int maxCellNum = 1;
		for (Row row : sheet) {
			maxCellNum = Math.max(maxCellNum, row.getLastCellNum());
		}
		for (int i = 0; i < maxCellNum; i++) {
			sheet.autoSizeColumn(i);
			int width = sheet.getColumnWidth(i) + 2000; // Some room
			width = Math.min(maxWidth, width);
			sheet.setColumnWidth(i, width);
		}
	}

	/**
	 * Annotations for Excel export
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ExcelColumn {

		String name();

		int ordinal();
	}
}
