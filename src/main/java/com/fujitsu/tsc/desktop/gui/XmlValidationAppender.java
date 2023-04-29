/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.LogEvent;
import org.xml.sax.SAXParseException;

import com.fujitsu.tsc.desktop.util.ErrorInfo;

public class XmlValidationAppender {
	private JTable table;
	private int nextLineNum;	//Line number in the table where the next validation error should be added.

	public XmlValidationAppender(JTable table) {
		this.table = table;
		nextLineNum = 0;
	}

	public void writeNext(SAXParseException ex) {
		String br = System.lineSeparator();
		TableModel model = table.getModel();
		if (model.getRowCount() <= nextLineNum) {
			((DefaultTableModel)model).addRow(new String[] {null, null});
		}
		model.setValueAt("Line: " + ex.getLineNumber() + br + "Column: " + ex.getColumnNumber(), nextLineNum, 0);
		model.setValueAt(ex.getMessage(), nextLineNum, 1);
		nextLineNum++;
	}

	public void writeNext(ErrorInfo ex) {
		String br = System.getProperty("line.separator");
		TableModel model = table.getModel();
		if (model.getRowCount() <= nextLineNum) {
			((DefaultTableModel)model).addRow(new String[] {null, null});
		}
		if (ex.getLine() == 0) {
		} else {
			model.setValueAt("Line: " + ex.getLine() + br + "Column: " + ex.getColumn(), nextLineNum, 0);
		}
			model.setValueAt(ex.getMessage(), nextLineNum, 1);
			nextLineNum++;
	}


	/**
	 * Write a validation success message to the top row of the table.
	 */
	public void writeSuccessMessage() {
		TableModel model = table.getModel();
		model.setValueAt("No errors have been found. The define.xml has been validated against the specified schema successfully.", nextLineNum++, 1);
	}

	/**
	 * Write error messages (e.g. IOException) to the table.
	 * @param err An error message
	 */
	public void writeErrorMessage(String err) {
		TableModel model = table.getModel();
		if (model.getRowCount() <= nextLineNum) {
			((DefaultTableModel)model).addRow(new String[] {null, null});
		}
		if (err == null) {
			model.setValueAt("Failed to import define.xml. Exiting the program... ", nextLineNum++, 1);
		} else {
			model.setValueAt(err, nextLineNum++, 1);
		}
	}

	/**
	 * Remove all rows, and then add a blank row.
	 */
	public void clear() {
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		while (model.getRowCount() > 0) {
			model.removeRow(0);
		}
		model.addRow(new String[] {null, null});
	}

	public void writeMessage(int i) {
		TableModel model = table.getModel();
		if (model.getRowCount() <= nextLineNum) {
			((DefaultTableModel)model).addRow(new String[] {null, null});
		}
		if (i == 0) {
			model.setValueAt("The above fatal errors have been found in define.xml.", nextLineNum++, 1);
		} else if (i == 1) {
			model.setValueAt("No fatal errors have been found in define.xml. Importing...", nextLineNum++, 1);
		} else if (i == 2) {
			model.setValueAt("An Excel file has been created. But the above warning(s) exist.", nextLineNum++, 1);
		} else if (i == 3) {
			model.setValueAt("An Excel file has been created. No warnings have been found.", nextLineNum++, 1);
		}
	}

	public void writeMessage(String message) {
		TableModel model = table.getModel();
		if (model.getRowCount() <= nextLineNum) {
			((DefaultTableModel)model).addRow(new String[] {null, null});
		}
		model.setValueAt(message, nextLineNum++, 1);
	}

	public void writeMessage(Exception e) {
		TableModel model = table.getModel();
		if (model.getRowCount() <= nextLineNum) {
			((DefaultTableModel)model).addRow(new String[] {null, null});
		}
		model.setValueAt(e.getMessage(), nextLineNum++, 1);
	}
}
