/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.validator;

import java.util.ArrayList;

import com.fujitsu.tsc.desktop.importer.KeyStorage;
import com.fujitsu.tsc.desktop.importer.XmlReadContainer;
import com.fujitsu.tsc.desktop.util.ErrorInfo;

public class ValidationResult {

	private boolean result;
	private ArrayList<ErrorInfo> errors;
	private XmlReadContainer container;
	private KeyStorage odmStorage;

	public ValidationResult () {
		result = true;
		errors = null;
		container = null;
	}

	public KeyStorage getOdmStorage() {
		return odmStorage;
	}

	public void setOdmStorage(KeyStorage odmStorage) {
		this.odmStorage = odmStorage;
	}

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public ArrayList<ErrorInfo> getErrors() {
		return errors;
	}

	public void setErrors(ArrayList<ErrorInfo> errors) {
		this.errors = errors;
	}

	public XmlReadContainer getContainer() {
		return container;
	}

	public void setContainer(XmlReadContainer container) {
		this.container = container;
	}
}
