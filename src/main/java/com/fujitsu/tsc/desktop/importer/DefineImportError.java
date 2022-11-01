/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer;

import com.fujitsu.tsc.desktop.importer.models.DefineARMDatasetModel.DefineARMDatasetPk;
import com.fujitsu.tsc.desktop.importer.models.DefineARMDisplayModel.DefineARMDisplayPk;
import com.fujitsu.tsc.desktop.importer.models.DefineCodelistModel.DefineCodelistPk;
import com.fujitsu.tsc.desktop.importer.models.DefineCommentModel.DefineCommentPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDatasetModel.DefineDatasetPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDictionaryModel.DefineDictionaryPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDocumentModel.DefineDocumentPk;
import com.fujitsu.tsc.desktop.importer.models.DefineMethodModel.DefineMethodPk;
import com.fujitsu.tsc.desktop.importer.models.DefineValueModel.DefineValuePk;
import com.fujitsu.tsc.desktop.importer.models.DefineVariableModel.DefineVariablePk;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.DefineWCPk;

public class DefineImportError {
	
	private Object key;
	private String message;
	
	public DefineImportError(DefineDocumentPk key) {
		this.key = key;
	}
	
	public DefineImportError(DefineDatasetPk key) {
		this.key = key;
	}
	
	public DefineImportError(DefineVariablePk key) {
		this.key = key;
	}
	
	public DefineImportError(DefineValuePk key) {
		this.key = key;
	}
	
	public DefineImportError(DefineDictionaryPk key) {
		this.key = key;
	}
	
	public DefineImportError(DefineCodelistPk key) {
		this.key = key;
	}
	
	public DefineImportError(DefineARMDisplayPk key) {
		this.key = key;
	}
	
	public DefineImportError(DefineARMDatasetPk key) {
		this.key = key;
	}
	
	public DefineImportError(DefineMethodPk key) {
		this.key = key;
	}
	
	public DefineImportError(DefineCommentPk key) {
		this.key = key;
	}
	
	public DefineImportError(DefineWCPk key) {
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
