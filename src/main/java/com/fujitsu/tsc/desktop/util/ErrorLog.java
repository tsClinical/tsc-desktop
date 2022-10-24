/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.util;

import org.apache.commons.lang3.StringUtils;

public class ErrorLog {

	public static enum ErrorLevel {
		WARN, ERROR;
	}

	private String tab_name;	//Sheet name for Excel spreadsheet
	private int line_num;
	private int column_num;
	private String column_name;
	private ErrorLevel level;
	private String message;

	public ErrorLog(String tab_name, int line_num, int column_num, ErrorLevel level, String message) {
		this.tab_name = tab_name;
		this.line_num = line_num;
		this.column_num = column_num;
		this.column_name = "";
		this.level = level;
		this.message = message;
	}

	public ErrorLog(String tab_name, int line_num, String column_name, ErrorLevel level, String message) {
		this.tab_name = tab_name;
		this.line_num = line_num;
		this.column_num = 0;
		this.column_name = column_name;
		this.level = level;
		this.message = message;
	}

	/**
	 * Use this constructor if the error source does not know error position.
	 * @param level Error Level
	 * @param message Error Message
	 */
	public ErrorLog(ErrorLevel level, String message) {
		this.tab_name = "";
		this.line_num = 0;
		this.column_num = 0;
		this.column_name = "";
		this.level = level;
		this.message = message;
	}
	
	public String getTabName() {
		return this.tab_name;
	}
	
	public void setTabName(String tab_name) {
		this.tab_name = tab_name;
	}
	
	public int getLineNum() {
		return this.line_num;
	}
	
	public void setLineNum(int line_num) {
		this.line_num = line_num;
	}
	
	public int getColumnNum() {
		return this.column_num;
	}
	
	public void setColumnNum(int column_num) {
		this.column_num = column_num;
	}
	
	public String getColumnName() {
		return this.column_name;
	}
	
	public void setColumnName(String column_name) {
		this.column_name = column_name;
	}
	
	public ErrorLevel getErrorLevel() {
		return this.level;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public String print() {
		String rtn = "";
		if (StringUtils.isNotEmpty(this.tab_name)) {
			rtn += "Tab Name: " + this.tab_name;
		}
		if (this.line_num > 1) {
			if (!"".equals(rtn)) { rtn += ", "; }
			rtn += "Line Number: " + this.line_num;
		}
		if (StringUtils.isNotEmpty(this.column_name)) {
			if (!"".equals(rtn)) { rtn += ", "; }
			rtn += "Column Name: " + this.column_name;
		}
		if (!"".equals(rtn)) { rtn += "\n"; }
		rtn += this.message;
		return rtn;
	}
}
