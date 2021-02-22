/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import java.io.*;



public class ExcelWriter {

	private Workbook wb;
	private Sheet sheet;

	public ExcelWriter() {
		wb = new XSSFWorkbook();
	}

	public void createSheet(String name) {
		sheet = wb.createSheet(name);
	}


	public void writeStudy(int rowNum, String property, String value) {
		sheet.createRow(rowNum).createCell(0).setCellValue(property);
		sheet.getRow(rowNum).createCell(1).setCellValue(value);
		System.out.println(value);
	}

	public void writeElement(int rowNum, int cellNum, String value) {
		if (cellNum == 0) {
//			System.out.println("value:"+value);
			sheet.createRow(rowNum).createCell(cellNum).setCellValue(value);
//			System.out.println(value);
		} else {
			sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
			System.out.println(value);
		}
	}

	public void writeElement(int rowNum, int cellNum, int value) {
		if (cellNum == 0) {
//			System.out.println("value:"+value);
			sheet.createRow(rowNum).createCell(cellNum).setCellValue(value);
//			System.out.println(value);
		} else {
			sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
			System.out.println(value);
		}
	}

	public void excelClose() {
		FileOutputStream out = null;
		try{
			out = new FileOutputStream("sample3_1.xlsx");
			wb.write(out);
		}catch(IOException e){
//			System.out.println(e.toString());
		}finally{
			try {
				out.close();
				System.out.println("close");
			}catch(IOException e){
				System.out.println(e.toString());
			}
		}

	}
}
