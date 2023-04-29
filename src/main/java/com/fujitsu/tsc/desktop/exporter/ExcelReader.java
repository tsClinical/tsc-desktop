/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.exporter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fujitsu.tsc.desktop.util.MetaDataReader;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

public class ExcelReader implements MetaDataReader {

	private static Logger logger;
	private FileInputStream in;
	private Workbook workbook;
	private Hashtable<String, Sheet> sheetHash;	//Allows to cache multiple excel worksheets
	private Hashtable<String, Iterator<Row>> iteratorHash;	//Allows to cache multiple excel worksheets
	private Hashtable<String, List<String>> headerListHash;	//Stores headers of cached whorksheets
//	private List<String> headerList;
	private String currentTableName;
// Changed for v1.1.0 - now MetaDataReader allows a WhereClause array.
//	private WhereClause clause;
//	private WhereClause clauseArray[];
	private Hashtable<String, WhereClause[]> clauseArrayHash;
	private Hashtable<String, HashSet<String>> uniqueKeysHash;	//Table keys - used to read unique records only  
	private Hashtable<String, HashSet<String>> actualKeysHash;	//Key sets found in actual table records

	public ExcelReader(String dataSourceLocation, String tableName)
			throws TableNotFoundException, InvalidFormatException, IOException {
		logger = LogManager.getLogger();
		init(dataSourceLocation, tableName);
	}

// Changed for v1.1.0 - now MetaDataReader allows a WhereClause array.
//	public ExcelReader(String dataSourceLocation, String tableName, WhereClause clause)
//			throws FileNotFoundException, TableNotFoundException {
//		logger = LogManager.getLogger();
//		init(dataSourceLocation, tableName);
//		setWhereClause(clause);
//	}
	public ExcelReader(String dataSourceLocation, String tableName, WhereClause clauseArray[])
			throws TableNotFoundException, InvalidFormatException, IOException {
		logger = LogManager.getLogger();
		init(dataSourceLocation, tableName);
		setWhereClause(tableName, clauseArray);
	}
	
// Changed for v1.1.0 - now MetaDataReader allows a WhereClause array.
//	public void setTable(String tableName, WhereClause clause) throws TableNotFoundException {
//		init(tableName);
//		setWhereClause(clause);
//	}
	public void setTable(String tableName, WhereClause clauseArray[]) throws TableNotFoundException {
		init(tableName);
		setWhereClause(tableName, clauseArray);
	}
	
	public void setTable(String tableName) throws TableNotFoundException {
		init(tableName);
		clearWhereClause(tableName);
	}
	
// Changed for v1.1.0 - now MetaDataReader allows a WhereClause array.
//	private void setWhereClause(WhereClause clause) {
//		this.clause = clause;
//	}
	private void setWhereClause(String tableName, WhereClause clauseArray[]) {
//		this.clauseArray = clauseArray;
		this.clauseArrayHash.put(tableName, clauseArray);
	}

	private void clearWhereClause(String tableName) {
// Changed for v1.1.0 - now MetaDataReader allows a WhereClause array.
//		this.clause = null;
//		this.clauseArray = null;
		this.clauseArrayHash.remove(tableName);
	}
	
	public void setUniqueKeys(String tableName, HashSet keys) {
		this.uniqueKeysHash.put(tableName, keys);
		this.actualKeysHash.put(tableName, new HashSet<String>());
	}
	
	public void clearUniqueKeys(String tableName) {
		this.uniqueKeysHash.remove(tableName);
		this.actualKeysHash.put(tableName, new HashSet<String>());
	}

	/**
	 * This method reads a row from the specified table of the specified data source and returns a Hashtable object.
	 */
	public Hashtable<String, String> read() {
		return read(this.currentTableName);
	}

	public Hashtable<String, String> read(String sheetName) {

		Hashtable<String, String> hash = null;
		Row r = null;
		
		/*
		 * Put an entire row into Hashtable.
		 * Skip if the row does not meet criteria in WhereClause, and repeat.
		 */
		while (iteratorHash.get(sheetName).hasNext()) {
			hash = new Hashtable<String, String>();
			r = iteratorHash.get(sheetName).next();
			for (int i = 0; i < headerListHash.get(sheetName).size(); i++) {
				/*
				 *  Ignore a column if its name is the same as those of any existing columns.
				 *  A column on the left is put into hash while any columns on the right are ignored. 
				 */
				if (!hash.containsKey(headerListHash.get(sheetName).get(i))) {
					hash.put(headerListHash.get(sheetName).get(i), new DataFormatter().formatCellValue(r.getCell(i)));
				}
				
				/* 
				 * If you want to change procedures based on data type,
				 * uncomment the following lines and add statements.
				 */
				
//				switch (r.getCell(i).getCellType()) {
//				case Cell.CELL_TYPE_BLANK:
//					hash.put(list.get(i), r.getCell(i).getStringCellValue());
//					break;
//				case Cell.CELL_TYPE_BOOLEAN:
//					hash.put(list.get(i), new Boolean(r.getCell(i).getBooleanCellValue()).toString());
//					break;
//				case Cell.CELL_TYPE_ERROR:
//					hash.put(list.get(i), new Byte(r.getCell(i).getErrorCellValue()).toString());
//					break;
//				case Cell.CELL_TYPE_FORMULA:
//					hash.put(list.get(i), r.getCell(i).getCellFormula());
//					break;
//				case Cell.CELL_TYPE_NUMERIC:
//					hash.put(list.get(i), new Double(r.getCell(i).getNumericCellValue()).toString());
//					break;
//				case Cell.CELL_TYPE_STRING:
//					hash.put(list.get(i), r.getCell(i).getStringCellValue());
//					break;
//				}
			}
// Changed for v1.1.0 - now MetaDataReader allows a WhereClause array.
//			if (clause == null || meetsWhereClause(hash)) {
			//If the record meets WhereClause, then return the record. Otherwise return null.
			if (clauseArrayHash.get(sheetName) == null || meetsWhereClause(sheetName, hash)) {
				//If the record is unique, then return the record. Otherwise return null.
				if (isUniqueRecord(sheetName, hash)) {
					break;
				}
			}
			hash = null;
		}
		
		return hash;
	}
	
	public String getTableName() {
		return this.currentTableName;
	}
	
	public WhereClause[] getWhereClause() {
//		return this.clause;
//		return this.clauseArray;
		return getWhereClause(this.currentTableName);
	}
	
	public WhereClause[] getWhereClause(String tableName) {
		return this.clauseArrayHash.get(tableName);
	}
	
	public void close() {
		try {
			in.close();
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		}
	}
	
	private void init(String dataSourceLocation, String tableName)
			throws FileNotFoundException, TableNotFoundException {
		try {
			if (in != null) {
				in.close();
			}
			in = new FileInputStream(dataSourceLocation);
			workbook = new XSSFWorkbook(in);
			sheetHash = new Hashtable<String, Sheet>();
			iteratorHash = new Hashtable<String, Iterator<Row>>();
			headerListHash = new Hashtable<String, List<String>>();
			clauseArrayHash = new Hashtable<String, WhereClause[]>();
			uniqueKeysHash = new Hashtable<>();
			actualKeysHash = new Hashtable<>();
			init(tableName);
		} catch (FileNotFoundException ex) {
			throw ex;
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		}
	}

	private void init(String tableName) throws TableNotFoundException {
		Sheet sheet;
		List<String> headerList;
		clearUniqueKeys(tableName);
		if (sheetHash.get(tableName) != null) {
			this.currentTableName = tableName;
			this.iteratorHash.put(tableName, this.sheetHash.get(tableName).rowIterator());
			this.iteratorHash.get(tableName).next();	//initialize iterator and ignore header row
//			this.sheetHash.get(tableName).rowIterator().next();	//initialize iterator and ignore header row
		} else {
			sheet = workbook.getSheet(tableName);
			if (sheet != null) {
				/*
				 *  Save the column header in List
				 */
				this.currentTableName = tableName;
				this.sheetHash.put(tableName, sheet);
				this.iteratorHash.put(tableName, sheet.rowIterator());
				Row r = this.iteratorHash.get(tableName).next();
				headerList = new ArrayList<String>();
				if (r != null) {
					Cell cell = null;
					for (int i = 0; (cell = r.getCell(i)) != null; i++) {
						headerList.add(cell.getStringCellValue());
					}
					headerListHash.put(tableName, headerList);
				}
			} else {
				throw new TableNotFoundException(tableName);
			}
		}
	}
	
	private boolean meetsWhereClause(String sheetName, Hashtable<String, String> hash) {
// Changed for v1.1.0 - now MetaDataReader allows a WhereClause array.
//		if (clause.getOperator() == WhereClause.Operator.EQ) {
//			if ((hash.get(clause.getVariable())).equals(clause.getValue())) {
//				return true;
//			} else {
//				return false;
//			}
//		} else if (clause.getOperator() == WhereClause.Operator.NE) {
//			if ((hash.get(clause.getVariable())).equals(clause.getValue())) {
//				return false;
//			} else {
//				return true;
//			}
//		} else {
//			return false;		//Default
//		}
		WhereClause clauseArray[] = clauseArrayHash.get(sheetName);
		for (int i = 0; i < clauseArray.length; i++) {
			if (clauseArray[i].getOperator() == WhereClause.Operator.EQ) {
				if ((hash.get(clauseArray[i].getVariable())).equals(clauseArray[i].getValue())) {
					//Do nothing and continue.
				} else {
					return false;
				}
			} else if (clauseArray[i].getOperator() == WhereClause.Operator.NE) {
				if ((hash.get(clauseArray[i].getVariable())).equals(clauseArray[i].getValue())) {
					return false;
				} else {
					//Do nothing and continue.
				}
			} else {
				return false;		//Default
			}
		}
		return true;	//If all WhereClauses are met, return true.
	}
	
	/*
	 * This method returns true if the record in the table should be processed.
	 *  - If Unique Keys are not set, then return true.
	 *  - If Unique Keys are set, then return true if the record is unique in the table. Otherwise, return false.
	 */
	private boolean isUniqueRecord(String sheetName, Hashtable<String, String> hash) {
		Set<String> uniqueKeys = this.uniqueKeysHash.get(sheetName);
		if (uniqueKeys == null || uniqueKeys.isEmpty()) {
			return true;
		} else {
			String keyString = "";
			for (String key : uniqueKeys) {
				if ("".equals(keyString)) {
					keyString = hash.get(key);
				} else {
					keyString += "/" + hash.get(key);
				}
			}
			Set<String> actualKeys = this.actualKeysHash.get(sheetName);
			if (actualKeys.contains(keyString)) {
				return false;
			} else {
				actualKeys.add(keyString);
				return true;
			}
		}
	}
}
