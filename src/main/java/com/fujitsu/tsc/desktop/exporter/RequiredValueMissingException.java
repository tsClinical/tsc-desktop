/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.exporter;

public class RequiredValueMissingException extends Exception {
	
	private Exception ex;
	private DefineXmlWriter.ErrorHint errHint;
	private OdmXmlWriter.ErrorHint odmErrHint;
	private String message;
	private final String BR = System.getProperty("line.separator");

	public RequiredValueMissingException(NullPointerException ex, DefineXmlWriter.ErrorHint errHint) {
		super();
		this.ex = ex;
		this.errHint = errHint;
		message = "An error has occurred likely due to a missing required value. Modify your input data and try again. Below is the hint about the error:" + BR
				+ "    XML Tag: " + errHint.tagType.toString() + BR
				+ "    Domain/Dataset: " + errHint.domainKey + BR
				+ "    Variable: " + errHint.variableName + BR
				+ "    Value: " + errHint.valueKey + BR
				+ "    Parameter: " + errHint.param;
	}
	
	public RequiredValueMissingException(NullPointerException ex, OdmXmlWriter.ErrorHint odmErrHint) {
		super();
		this.ex = ex;
		this.odmErrHint = odmErrHint;
		message = "An error has occurred likely due to a missing required value. Modify your input data and try again. Below is the hint about the error:" + BR
				+ "    XML Tag: " + odmErrHint.odmTagType.toString() + BR
				+ "    Event: " + odmErrHint.eventName + BR
				+ "    Form: " + odmErrHint.formName + BR
				+ "    Group: " + odmErrHint.groupName + BR
				+ "    Field: " + odmErrHint.fieldName + BR
				+ "    Parameter: " + odmErrHint.param;
	}
	
	public RequiredValueMissingException(ArrayIndexOutOfBoundsException ex, DefineXmlWriter.ErrorHint errHint) {
		super();
		this.ex = ex;
		this.errHint = errHint;
		message = "An error has occurred likely due to a missing required value. Modify your input data and try again. Below is the hint about the error:" + BR
				+ "    XML Tag: " + errHint.tagType.toString() + BR
				+ "    Domain/Dataset: " + errHint.domainKey + BR
				+ "    Variable: " + errHint.variableName + BR
				+ "    Value: " + errHint.valueKey + BR
				+ "    Parameter: " + errHint.param;
	}

	public RequiredValueMissingException(ArrayIndexOutOfBoundsException ex, OdmXmlWriter.ErrorHint odmErrHint) {
		super();
		this.ex = ex;
		this.odmErrHint = odmErrHint;
		message = "An error has occurred likely due to a missing required value. Modify your input data and try again. Below is the hint about the error:" + BR
				+ "    XML Tag: " + odmErrHint.odmTagType.toString() + BR
				+ "    Event: " + odmErrHint.eventName + BR
				+ "    Form: " + odmErrHint.formName + BR
				+ "    Group: " + odmErrHint.groupName + BR
				+ "    Field: " + odmErrHint.fieldName + BR
				+ "    Parameter: " + odmErrHint.param;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getSourceClass() {
		return ex.getClass().toString();
	}
}
