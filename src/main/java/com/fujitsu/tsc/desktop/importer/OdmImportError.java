/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer;

import com.fujitsu.tsc.desktop.importer.models.OdmCodelistModel.OdmCodelistPk;
import com.fujitsu.tsc.desktop.importer.models.OdmConditionModel.OdmConditionPk;
import com.fujitsu.tsc.desktop.importer.models.OdmEventFormModel.OdmEventFormPk;
import com.fujitsu.tsc.desktop.importer.models.OdmEventModel.OdmEventPk;
import com.fujitsu.tsc.desktop.importer.models.OdmFieldModel.OdmFieldPk;
import com.fujitsu.tsc.desktop.importer.models.OdmFormModel.OdmFormPk;
import com.fujitsu.tsc.desktop.importer.models.OdmMethodModel.OdmMethodPk;
import com.fujitsu.tsc.desktop.importer.models.OdmUnitModel.OdmUnitPk;

public class OdmImportError {
	
	private Object key;
	private String message;
	
	public OdmImportError(OdmCodelistPk key) {
		this.key = key;
	}
	
	public OdmImportError(OdmConditionPk key) {
		this.key = key;
	}
	
	public OdmImportError(OdmEventFormPk key) {
		this.key = key;
	}
	
	public OdmImportError(OdmEventPk key) {
		this.key = key;
	}
	
	public OdmImportError(OdmFieldPk key) {
		this.key = key;
	}
	
	public OdmImportError(OdmFormPk key) {
		this.key = key;
	}
	
	public OdmImportError(OdmMethodPk key) {
		this.key = key;
	}
	
	public OdmImportError(OdmUnitPk key) {
		this.key = key;
	}
	
	public void putMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		String rtn = this.message;
		rtn += key.toString();
		return rtn;
	}
}
