/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.fujitsu.tsc.desktop.util.ErrorInfo;

/**
 * This class scans an XML file and holds validation errors.
 */
public class DefaultValidationHandler extends DefaultHandler {
	
	private static Logger logger;
	private Locator locator;
	private List<ErrorInfo> errors = new ArrayList<>();
 
	public DefaultValidationHandler(){
		logger = Logger.getLogger("com.fujitsu.tsc.desktop");
	}

	public void startDocument() {
	}
	
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) {
	}

	public void characters(char[] ch, int start, int length) {
	}

	public void endElement(String namespaceURI, String localName, String qName) {
	}

	public void endDocument()  {
	}

	public void warning(SAXParseException exception) throws SAXException {
	}

	public void error(SAXParseException exception) throws SAXException {
		ErrorInfo error = new ErrorInfo();
	    error.setId(exception.getSystemId());
	    error.setLine(exception.getLineNumber());
	    error.setColumn(exception.getColumnNumber());
	    error.setMessage(exception.getMessage());
	    error.setMessage(exception.getMessage() + "\nnear the line number " + locator.getLineNumber() + ".");
	    errors.add(error);
    }

	public void fatalError(SAXParseException exception) throws SAXException{
		ErrorInfo error = new ErrorInfo();
		error.setId(exception.getSystemId());
		error.setLine(exception.getLineNumber());
		error.setColumn(exception.getColumnNumber());
		error.setMessage(exception.getMessage() + "\nnear the line number " + locator.getLineNumber() + ".");
		errors.add(error);
	}

	public List<ErrorInfo> getErrors() {
		return errors;
	}
}
