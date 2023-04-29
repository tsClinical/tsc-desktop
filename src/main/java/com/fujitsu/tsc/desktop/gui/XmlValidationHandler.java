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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlValidationHandler extends DefaultHandler {

	private static Logger logger = LogManager.getLogger();
	private XmlValidationAppender appender;
	private int errCount;

	JTable out;
    int row;
    DefaultTableModel model;
    
    public XmlValidationHandler(XmlValidationAppender appender) {
    	this.appender = appender;
    	errCount = 0;
    }
    
	public void error(final SAXParseException ex) {
		appender.writeNext(ex);
		errCount++;
	}
	
	public XmlValidationAppender getAppender() {
		return appender;
	}
	
	public int getErrorCount() {
		return errCount;
	}
}
